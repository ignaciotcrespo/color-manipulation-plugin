package com.github.ignaciotcrespo.colormanipulation.palette

import com.github.ignaciotcrespo.colormanipulation.model.UnifiedColor

/**
 * Analyzes a list of [ProjectColor] occurrences and produces a [ProjectColorAnalysis]
 * with grouping, similarity clusters, and format inconsistency detection.
 */
object ColorAnalyzer {

    /** Squared Euclidean distance threshold for "similar" colors (approx ΔE ~10 in RGB). */
    private const val SIMILARITY_THRESHOLD_SQ = 300.0

    fun analyze(colors: List<ProjectColor>, filesScanned: Int, scanTimeMs: Long): ProjectColorAnalysis {
        // Group by normalized hex key (ignoring alpha for grouping)
        val groups = colors
            .groupBy { toHexKey(it.color) }
            .map { (hexKey, occurrences) ->
                ColorGroup(
                    color = occurrences.first().color,
                    hexKey = hexKey,
                    occurrences = occurrences
                )
            }
            .sortedByDescending { it.count }

        // Find clusters of similar colors
        val similarClusters = findSimilarClusters(groups)

        // Find format inconsistencies: same color used in multiple formats
        val formatInconsistencies = groups.filter { it.formats.size > 1 }

        return ProjectColorAnalysis(
            totalOccurrences = colors.size,
            uniqueColors = groups.size,
            filesScanned = filesScanned,
            filesWithColors = colors.map { it.file.path }.distinct().size,
            colorGroups = groups,
            similarClusters = similarClusters,
            formatInconsistencies = formatInconsistencies,
            scanTimeMs = scanTimeMs
        )
    }

    private fun findSimilarClusters(groups: List<ColorGroup>): List<SimilarColorCluster> {
        if (groups.size < 2) return emptyList()

        val used = mutableSetOf<Int>()
        val clusters = mutableListOf<SimilarColorCluster>()

        for (i in groups.indices) {
            if (i in used) continue
            val cluster = mutableListOf(groups[i])
            var maxDist = 0.0

            for (j in i + 1 until groups.size) {
                if (j in used) continue
                val dist = colorDistanceSq(groups[i].color, groups[j].color)
                if (dist in 1.0..SIMILARITY_THRESHOLD_SQ) {
                    cluster.add(groups[j])
                    used.add(j)
                    if (dist > maxDist) maxDist = dist
                }
            }

            if (cluster.size > 1) {
                used.add(i)
                clusters.add(SimilarColorCluster(cluster, kotlin.math.sqrt(maxDist)))
            }
        }

        return clusters.sortedByDescending { it.colors.sumOf { g -> g.count } }
    }

    private fun colorDistanceSq(a: UnifiedColor, b: UnifiedColor): Double {
        val dr = a.r - b.r
        val dg = a.g - b.g
        val db = a.b - b.b
        return dr * dr + dg * dg + db * db
    }

    private fun toHexKey(color: UnifiedColor): String {
        val r = color.r.toInt().coerceIn(0, 255)
        val g = color.g.toInt().coerceIn(0, 255)
        val b = color.b.toInt().coerceIn(0, 255)
        val a = (color.a * 255).toInt().coerceIn(0, 255)
        return "%02X%02X%02X%02X".format(r, g, b, a)
    }
}
