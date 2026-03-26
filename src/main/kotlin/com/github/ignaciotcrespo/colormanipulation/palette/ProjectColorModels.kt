package com.github.ignaciotcrespo.colormanipulation.palette

import com.github.ignaciotcrespo.colormanipulation.model.ColorFormat
import com.github.ignaciotcrespo.colormanipulation.model.UnifiedColor
import com.intellij.openapi.vfs.VirtualFile

/**
 * A single color occurrence found in a project file.
 */
data class ProjectColor(
    val color: UnifiedColor,
    val format: ColorFormat,
    val file: VirtualFile,
    val line: Int,
    val column: Int,
    val matchText: String
)

/**
 * A group of identical colors (same RGB, possibly different formats/locations).
 */
data class ColorGroup(
    val color: UnifiedColor,
    val hexKey: String,
    val occurrences: List<ProjectColor>
) {
    val count: Int get() = occurrences.size
    val fileCount: Int get() = occurrences.map { it.file.path }.distinct().size
    val formats: Set<ColorFormat> get() = occurrences.map { it.format }.toSet()
}

/**
 * A cluster of similar colors that could potentially be unified.
 */
data class SimilarColorCluster(
    val colors: List<ColorGroup>,
    val maxDistance: Double
)

/**
 * Full analysis result for the project.
 */
data class ProjectColorAnalysis(
    val totalOccurrences: Int,
    val uniqueColors: Int,
    val filesScanned: Int,
    val filesWithColors: Int,
    val colorGroups: List<ColorGroup>,
    val similarClusters: List<SimilarColorCluster>,
    val formatInconsistencies: List<ColorGroup>,
    val scanTimeMs: Long
)
