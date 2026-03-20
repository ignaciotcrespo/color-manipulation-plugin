# Color Manipulation

A powerful color toolkit for JetBrains IDEs. Select any color in your code, right-click, and convert, adjust, mix, or analyze it — all without leaving the editor.

Works in **all JetBrains IDEs** (IntelliJ IDEA, WebStorm, Android Studio, PyCharm, GoLand, CLion, Rider, PhpStorm, RubyMine, etc.) with **all file types**.

![Color Manipulation Plugin](docs/img/screenshot.png)

## Installation

- **From JetBrains Marketplace**: Settings > Plugins > Marketplace > Search "Color Manipulation"
- **Manual**: Download the `.zip` from [Releases](https://github.com/ignaciotcrespo/color-manipulation-plugin/releases) > Settings > Plugins > Install Plugin from Disk
- **Keyboard shortcut**: `Ctrl+Alt+C` (`Cmd+Alt+C` on Mac)

## Features

### Convert Format

Convert between **30+ color formats** across all major platforms:

| Platform | Formats |
|---|---|
| **Web / CSS** | `#RGB`, `#RRGGBB`, `#RRGGBBAA`, `#AARRGGBB`, `rgb()`, `rgba()`, `hsl()`, `hsla()`, `hwb()`, Named CSS Colors |
| **Android / Kotlin** | `0xRRGGBB`, `Color(0xAARRGGBB)`, `Color.rgb()`, `Color.argb()` |
| **iOS / Swift** | `UIColor(red:green:blue:alpha:)`, `Color(red:green:blue:)` |
| **Java** | `new Color(r, g, b)`, `new Color(r, g, b, a)` |
| **Generic** | Float RGB/RGBA `(0.0-1.0)`, with and without parentheses |

Also includes **Toggle #**, **Toggle 0x**, and **Swap RRGGBBAA/AARRGGBB** byte order.

### Convert from Name

Convert named colors from popular design systems to hex values:

- **CSS** — `red`, `cornflowerblue`, `darkslategray`, ...
- **Tailwind CSS** — `gray-800`, `blue-500`, `emerald-300`, ...
- **Bootstrap** — `primary`, `danger`, `blue-400`, ...
- **Material Design** — `Blue 500`, `Red 300`, `Deep Purple 700`, ...
- **iOS System** — `systemBlue`, `systemRed`, `systemGray2`, ...

Works on single names or multi-line selections — the plugin scans the text and converts every recognized name.

### Adjustments

| Category | Options |
|---|---|
| **Lighten / Darken** | 5%, 10%, 20%, or custom |
| **Saturate / Desaturate** | 10%, 20%, grayscale, or custom |
| **Adjust Alpha** | 100%, 75%, 50%, 25%, 0%, or custom |
| **Hue Rotate** | +30, +60, +90, +180 degrees, invert, or custom |
| **Temperature** | Warmer / Cooler at 10%, 20%, 40%, or custom |

### Mixing & Palette

- **Tint** — mix with white (10%, 25%, 50%, or custom)
- **Shade** — mix with black (10%, 25%, 50%, or custom)
- **Complementary** — source + 180 degrees hue rotation
- **Analogous** — three colors at -30, 0, +30 degrees
- **Triadic** — three colors at 0, 120, 240 degrees
- **Shades (100-900)** — nine lightness variations

### Accessibility

- **WCAG Contrast Check** — shows contrast ratio against white/black with AA/AAA pass/fail results
- **Auto-fix AA on White/Black** — adjusts lightness to meet 4.5:1 contrast ratio
- **Auto-fix AAA on White/Black** — adjusts lightness to meet 7:1 contrast ratio

### Color Blindness Simulation

Preview how colors appear to people with color vision deficiencies:

- **Protanopia** (red-blind)
- **Deuteranopia** (green-blind)
- **Tritanopia** (blue-blind)

### Closest Match

Find and convert to the nearest color in popular design systems:

- **Named CSS**, **Tailwind**, **Bootstrap**, **Material Design**, **iOS System**
- Two modes: convert to **name** (`cornflowerblue`) or to **value** (`#6495ED`)

### Practical Utils

- **Random Color** — generate a random color
- **Sort Lines** by Hue, Lightness, or Saturation — reorders entire lines by their color
- **Sort Colors** by Hue, Lightness, or Saturation — swaps color values in place
- **Normalize to Same Format** — convert all selected colors to match the first one's format

### Show Color Info

Popup with all format conversions, HSL values, alpha, and closest matches across all design systems.

### Repeat Last Action

Quickly re-apply any previous transform — including custom dialog values. Appears at the top of the menu after the first action.

## Multi-cursor & Embedded Colors

- **Multi-cursor**: select multiple colors individually and transform them all at once
- **Embedded colors**: select a block of text and the plugin finds all colors inside and transforms them
- **Live preview icons**: see the resulting color in the menu before clicking

## Building from Source

```bash
# Build
./gradlew buildPlugin

# Run in a sandboxed IDE
./gradlew runIde

# Verify plugin compatibility
./gradlew verifyPlugin
```

The plugin zip will be at `build/distributions/`.

## Contributing

Contributions are welcome. Please open an issue first to discuss what you'd like to change.

The architecture is data-driven — adding a new color preset is a single line of code:

```kotlin
ActionEntry.Transform("Lighten 7%") { c, _ -> ColorTransforms.lighten(c, 7.0) }
```

Adding a new category is one definition file + one line in `ColorManipulationGroup`.

## Support

If you find this plugin useful, consider [sponsoring its development](https://github.com/sponsors/ignaciotcrespo). Your support helps keep it free and actively maintained.

## License

[MIT License](LICENSE)
