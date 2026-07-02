# Rendering Engine

A powerful Java-based rendering engine that converts declarative JSON specifications into high-quality PNG images. This engine supports complex layouts with text, shapes, images, and comprehensive styling options.

## Features

- **JSON-based Specifications**: Define documents using a declarative JSON format
- **Rich Styling Support**: 
  - Text formatting (fonts, sizes, colors, decorations, transforms)
  - Background and fill colors with opacity
  - Border styling and border-radius
  - Stroke and fill for shapes
  - Padding and geometry specifications
- **Multiple Element Types**:
  - Text elements with advanced typography
  - Shape elements (rectangles, circles, ellipses)
  - Divider elements
  - Image elements with configurable dimensions
- **Advanced Typography**:
  - Font family and font source support
  - Text alignment and transformation
  - Inline segments for mixed content
  - RTL and bidirectional text support
- **Rendering Features**:
  - Anti-aliasing for smooth output
  - Transform and rotation support
  - Customizable canvas dimensions
  - High-quality PNG output

## Requirements

- Java 17 or higher
- Maven 3.6+

## Dependencies

- Jackson Databind 2.21.2 (JSON processing)

## Usage

### Command Line

```bash
# Using default input (1.json) and output (rendered2.png)
java -cp target/RenderingEngine-1.0-SNAPSHOT.jar com.Main

# With custom input and output paths
java -cp target/RenderingEngine-1.0-SNAPSHOT.jar com.Main input.json output.png
```

### Building

```bash
mvn clean package
```

## Project Structure

```
src/main/java/com/
├── Main.java                                    # Entry point
└── rendering/
    ├── RenderingEngine.java                     # Core rendering logic
    └── model/
        ├── RenderDocument.java                  # Document model
        ├── CanvasSpec.java                      # Canvas configuration
        ├── ElementSpec.java                     # Base element spec
        ├── TextElementSpec.java                 # Text element
        ├── ShapeElementSpec.java                # Shape element
        ├── DividerElementSpec.java              # Divider element
        ├── TypographySpec.java                  # Typography settings
        ├── GeometrySpec.java                    # Geometry settings
        ├── BackgroundSpec.java                  # Background configuration
        ├── StrokeSpec.java                      # Stroke properties
        ├── FillSpec.java                        # Fill properties
        ├── PaddingSpec.java                     # Padding settings
        ├── BorderRadiusSpec.java                # Border radius
        ├── AppearanceSpec.java                  # Appearance properties
        ├── MetadataSpec.java                    # Document metadata
        ├── AccessibilitySpec.java               # Accessibility features
        ├── FontSourceSpec.java                  # Font source configuration
        ├── FontFeaturesSpec.java                # Font features
        ├── TextDecorationSpec.java              # Text decoration
        ├── TextTransform.java                   # Text transformation
        ├── TransformOriginSpec.java             # Transform origin
        ├── InlineSegmentSpec.java               # Inline text segments
        └── SourceImageDimensionsSpec.java       # Image dimensions
```

## Example JSON Specification

```json
{
  "metadata": {
    "name": "Sample Document",
    "version": "1.0"
  },
  "canvas": {
    "width": 800,
    "height": 600,
    "backgroundColor": "#FFFFFF"
  },
  "elements": [
    {
      "type": "text",
      "content": "Hello, World!",
      "geometry": {
        "x": 50,
        "y": 50,
        "width": 700,
        "height": 100
      },
      "typography": {
        "fontSize": 48,
        "fontFamily": "Arial",
        "color": "#000000"
      }
    }
  ]
}
```

## License

This project is provided as-is for rendering and design purposes.

## Authors

- Ahmed Ali

## Contributing

Contributions are welcome! Please feel free to submit pull requests or open issues for bug reports and feature requests.
