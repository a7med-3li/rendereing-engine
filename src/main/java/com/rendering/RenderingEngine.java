package com.rendering;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.core.type.TypeReference;
import com.rendering.model.BackgroundSpec;
import com.rendering.model.CanvasSpec;
import com.rendering.model.DividerElementSpec;
import com.rendering.model.ElementSpec;
import com.rendering.model.FillSpec;
import com.rendering.model.GeometrySpec;
import com.rendering.model.RenderDocument;
import com.rendering.model.ShapeElementSpec;
import com.rendering.model.StrokeSpec;
import com.rendering.model.TextElementSpec;
import com.rendering.model.TextTransform;
import com.rendering.model.TypographySpec;

import javax.imageio.ImageIO;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.AttributedString;
import java.text.Bidi;
import java.text.AttributedCharacterIterator;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class RenderingEngine {
	private final ObjectMapper mapper;
	private final Set<String> availableFonts;

	public RenderingEngine() {
		this.mapper = new ObjectMapper();
		this.mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
		this.mapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);
		this.availableFonts = new HashSet<>(List.of(GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames()));
		System.out.println("Available fonts: " + availableFonts.contains("Noto Naskh Arabic"));
	}

	public void render(Path input, Path output) throws IOException {
		String source = Files.readString(input);
		RenderDocument document = parseDocument(source);
		BufferedImage image = render(document);
		Path parent = output.toAbsolutePath().getParent();
		if (parent != null) {
			Files.createDirectories(parent);
		}
		ImageIO.write(image, "png", output.toFile());
	}

	public BufferedImage render(RenderDocument document) {
		CanvasSpec canvas = document.canvas;
		BufferedImage image = new BufferedImage(canvas.width, canvas.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = image.createGraphics();
		try {
			applyQualityHints(g2);
			fillCanvas(g2, canvas);
			List<ElementSpec> elements = new ArrayList<>(document.elements);
			elements.sort(Comparator.comparingInt(RenderingEngine::zIndex));
			for (ElementSpec element : elements) {
				renderElement(g2, element);
			}
		} finally {
			g2.dispose();
		}
		return image;
	}

	private RenderDocument parseDocument(String source) throws IOException {
		String trimmed = source.trim();
		if (trimmed.startsWith("[")) {
			List<ElementSpec> elements = mapper.readValue(trimmed, new TypeReference<List<ElementSpec>>() {});
			RenderDocument document = new RenderDocument();
			document.elements = elements;
			document.canvas = inferCanvas(elements);
			return document;
		}
		return mapper.readValue(trimmed, RenderDocument.class);
	}

	private CanvasSpec inferCanvas(List<ElementSpec> elements) {
		CanvasSpec canvas = new CanvasSpec();
		int width = 1;
		int height = 1;
		for (ElementSpec element : elements) {
			GeometrySpec geometry = element == null ? null : element.geometry;
			if (geometry == null) {
				continue;
			}
			width = Math.max(width, (int) Math.ceil(geometry.x + geometry.width));
			height = Math.max(height, (int) Math.ceil(geometry.y + geometry.height));
		}
		canvas.width = width;
		canvas.height = height;
		return canvas;
	}

	private static int zIndex(ElementSpec element) {
		GeometrySpec geometry = element.geometry;
		return geometry == null || geometry.zIndex == null ? 0 : geometry.zIndex;
	}

	private void applyQualityHints(Graphics2D g2) {
		g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
	}

	private void fillCanvas(Graphics2D g2, CanvasSpec canvas) {
		FillSpec background = canvas.background == null ? null : canvas.background.fill;
		Color fill = background == null ? Color.WHITE : color(background.color, background.opacity);
		g2.setPaint(fill);
		g2.fillRect(0, 0, canvas.width, canvas.height);
	}

	private void renderElement(Graphics2D g2, ElementSpec element) {
		if (element instanceof TextElementSpec text) {
			renderText(g2, text);
			return;
		}
		if (element instanceof DividerElementSpec divider) {
			renderDivider(g2, divider);
			return;
		}
		if (element instanceof ShapeElementSpec shape) {
			renderShape(g2, shape);
		}
	}

	private void renderText(Graphics2D g2, TextElementSpec text) {
		GeometrySpec geometry = text.geometry;
		if (geometry == null) {
			return;
		}

		Graphics2D local = (Graphics2D) g2.create();
		try {
			applyElementTransform(local, geometry);
			if (text.background != null && text.background.fill != null) {
				renderBackground(local, geometry, text.background);
			}
			String content = applyTextTransform(text.content, text.typography == null ? null : text.typography.textTransform);
			boolean rtl = isRtlDirection(text.typography == null ? null : text.typography.direction, content);
			Bidi bidi = new Bidi(content, rtl ? Bidi.DIRECTION_RIGHT_TO_LEFT : Bidi.DIRECTION_LEFT_TO_RIGHT);
			TextRunStyle style = new TextRunStyle(text.typography, text.appearance == null ? null : text.appearance.fill, rtl);
			AttributedCharacterIterator iterator = buildAttributedText(content, style, text.inlineSegments, rtl);
			FontRenderContext frc = local.getFontRenderContext();
			TextLayout layout = new TextLayout(iterator, frc);
			double drawX = text.typography != null && "center".equalsIgnoreCase(text.typography.textAlign)
				? (geometry.width - layout.getAdvance()) / 2.0
				: !bidi.baseIsLeftToRight() && text.typography != null && "right".equalsIgnoreCase(text.typography.textAlign)
				? geometry.width - layout.getAdvance()
				: 0.0;
			double baseline = geometry.baselineY != null ? geometry.baselineY - geometry.y : layout.getAscent();
			Color fill = text.appearance != null && text.appearance.fill != null
				? color(text.appearance.fill.color, text.appearance.fill.opacity)
				: Color.BLACK;
			local.setPaint(fill);
			layout.draw(local, (float) drawX, (float) baseline);
		} finally {
			local.dispose();
		}
	}

	private boolean isRtlDirection(String direction, String content) {
		if (direction != null) {
			String normalized = direction.trim().toLowerCase(Locale.ROOT);
			if ("rtl".equals(normalized) || "right-to-left".equals(normalized)) {
				return true;
			}
			if ("ltr".equals(normalized) || "left-to-right".equals(normalized)) {
				return false;
			}
		}
		return content != null && Bidi.requiresBidi(content.toCharArray(), 0, content.length());
	}

	private void renderBackground(Graphics2D g2, GeometrySpec geometry, BackgroundSpec background) {
		Graphics2D local = (Graphics2D) g2.create();
		try {
			Color fill = color(background.fill.color, background.fill.opacity);
			local.setPaint(fill);
			if ("circle".equalsIgnoreCase(background.shape)) {
				local.fill(new Ellipse2D.Double(0, 0, geometry.width, geometry.height));
			} else {
				local.fill(new Rectangle2D.Double(0, 0, geometry.width, geometry.height));
			}
		} finally {
			local.dispose();
		}
	}

	private void renderDivider(Graphics2D g2, DividerElementSpec divider) {
		GeometrySpec geometry = divider.geometry;
		if (geometry == null) {
			return;
		}

		Graphics2D local = (Graphics2D) g2.create();
		try {
			applyElementTransform(local, geometry);
			StrokeSpec stroke = divider.stroke;
			float width = stroke == null || stroke.width == null ? 1f : stroke.width.floatValue();
			float[] dash = null;
			if (divider.dashArray != null && divider.dashArray.length > 0) {
				dash = new float[divider.dashArray.length];
				for (int i = 0; i < divider.dashArray.length; i++) {
					dash[i] = (float) divider.dashArray[i];
				}
			} else if (stroke != null && "dotted".equalsIgnoreCase(stroke.style)) {
				float dotSize = divider.dotSize == null ? 2f : divider.dotSize.floatValue();
				float dotGap = divider.dotGap == null ? 4f : divider.dotGap.floatValue();
				dash = new float[]{dotSize, dotGap};
			}
			local.setColor(stroke == null ? Color.BLACK : color(stroke.color, stroke.opacity));
			if (dash != null) {
				local.setStroke(new BasicStroke(width, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 10f, dash, 0f));
			} else {
				local.setStroke(new BasicStroke(width));
			}
			int y = (int) Math.round(geometry.height / 2.0);
			local.drawLine(0, y, (int) Math.round(geometry.width), y);
		} finally {
			local.dispose();
		}
	}

	private void renderShape(Graphics2D g2, ShapeElementSpec shapeElement) {
		GeometrySpec geometry = shapeElement.geometry;
		if (geometry == null) {
			return;
		}

		Graphics2D local = (Graphics2D) g2.create();
		try {
			applyElementTransform(local, geometry);
			Color fill = shapeElement.appearance != null && shapeElement.appearance.fill != null
				? color(shapeElement.appearance.fill.color, shapeElement.appearance.fill.opacity)
				: Color.BLACK;
			local.setPaint(fill);
			Shape shape = switch (shapeElement.shape == null ? "" : shapeElement.shape.toLowerCase(Locale.ROOT)) {
				case "circle", "ellipse" -> new Ellipse2D.Double(0, 0, geometry.width, geometry.height);
				case "triangle" -> triangleShape(geometry.width, geometry.height);
				default -> new Rectangle2D.Double(0, 0, geometry.width, geometry.height);
			};
			local.fill(shape);
		} finally {
			local.dispose();
		}
	}

	private Shape triangleShape(double width, double height) {
		Path2D.Double triangle = new Path2D.Double();
		triangle.moveTo(width / 2.0, 0.0);
		triangle.lineTo(width, height);
		triangle.lineTo(0.0, height);
		triangle.closePath();
		return triangle;
	}

	private void applyElementTransform(Graphics2D g2, GeometrySpec geometry) {
		AffineTransform transform = new AffineTransform();
		transform.translate(geometry.x, geometry.y);

		double originX = geometry.transformOrigin == null || geometry.transformOrigin.x == null ? 0.0 : geometry.transformOrigin.x * geometry.width;
		double originY = geometry.transformOrigin == null || geometry.transformOrigin.y == null ? 0.0 : geometry.transformOrigin.y * geometry.height;

		boolean hasLocalTransform = geometry.rotation != null || geometry.skewX != null || geometry.skewY != null
			|| geometry.scaleX != null || geometry.scaleY != null
			|| Boolean.TRUE.equals(geometry.flipHorizontal) || Boolean.TRUE.equals(geometry.flipVertical);

		if (hasLocalTransform && (originX != 0.0 || originY != 0.0)) {
			transform.translate(originX, originY);
			applyLocalTransform(transform, geometry);
			transform.translate(-originX, -originY);
		} else if (hasLocalTransform) {
			applyLocalTransform(transform, geometry);
		}

		g2.transform(transform);
	}

	private void applyLocalTransform(AffineTransform transform, GeometrySpec geometry) {
		if (geometry.flipHorizontal != null && geometry.flipHorizontal) {
			transform.scale(-1, 1);
		}
		if (geometry.flipVertical != null && geometry.flipVertical) {
			transform.scale(1, -1);
		}
		double scaleX = geometry.scaleX == null ? 1.0 : geometry.scaleX;
		double scaleY = geometry.scaleY == null ? 1.0 : geometry.scaleY;
		if (scaleX != 1.0 || scaleY != 1.0) {
			transform.scale(scaleX, scaleY);
		}
		if (geometry.skewX != null || geometry.skewY != null) {
			transform.shear(geometry.skewX == null ? 0.0 : geometry.skewX, geometry.skewY == null ? 0.0 : geometry.skewY);
		}
		if (geometry.rotation != null) {
			transform.rotate(Math.toRadians(geometry.rotation));
		}
	}

	private String applyTextTransform(String content, TextTransform transform) {
		if (content == null || transform == null) {
			return content;
		}
		return switch (transform) {
			case UPPERCASE -> content.toUpperCase(Locale.ROOT);
			case LOWERCASE -> content.toLowerCase(Locale.ROOT);
			case CAPITALIZE, TITLE_CASE -> titleCase(content);
			default -> content;
		};
	}

	private String titleCase(String content) {
		StringBuilder out = new StringBuilder(content.length());
		boolean capitalize = true;
		for (char ch : content.toCharArray()) {
			if (Character.isLetterOrDigit(ch)) {
				out.append(capitalize ? Character.toTitleCase(ch) : Character.toLowerCase(ch));
				capitalize = false;
			} else {
				out.append(ch);
				capitalize = Character.isWhitespace(ch);
			}
		}
		return out.toString();
	}

	private AttributedCharacterIterator buildAttributedText(String content, TextRunStyle baseStyle, List<com.rendering.model.InlineSegmentSpec> inlineSegments, boolean rtl) {
		AttributedString attributed = new AttributedString(content);
		attributed.addAttribute(TextAttribute.RUN_DIRECTION, rtl ? TextAttribute.RUN_DIRECTION_RTL : TextAttribute.RUN_DIRECTION_LTR);
		applyStyle(attributed, 0, content.length(), baseStyle);
		if (inlineSegments != null) {
			for (com.rendering.model.InlineSegmentSpec segment : inlineSegments) {
				int start = Math.max(0, segment.range[0]);
				int end = Math.min(content.length(), segment.range[1]);
				if (start >= end) {
					continue;
				}
				String slice = segment.contentSlice == null ? content.substring(start, end) : segment.contentSlice;
				TextRunStyle style = new TextRunStyle(segment.typography, segment.appearance == null ? null : segment.appearance.fill, rtl);
				attributed.addAttribute(TextAttribute.FONT, resolveFont(style), start, end);
				attributed.addAttribute(TextAttribute.FOREGROUND, style.foreground(), start, end);
				attributed.addAttribute(TextAttribute.TRACKING, style.tracking(), start, end);
				attributed.addAttribute(TextAttribute.KERNING, TextAttribute.KERNING_ON, start, end);
				if (slice.length() != end - start) {
					// The schema's slice is informational; the source content remains authoritative.
				}
			}
		}
		return attributed.getIterator();
	}

	private void applyStyle(AttributedString attributed, int start, int end, TextRunStyle style) {
		attributed.addAttribute(TextAttribute.FONT, resolveFont(style), start, end);
		attributed.addAttribute(TextAttribute.FOREGROUND, style.foreground(), start, end);
		attributed.addAttribute(TextAttribute.TRACKING, style.tracking(), start, end);
		attributed.addAttribute(TextAttribute.KERNING, TextAttribute.KERNING_ON, start, end);
		attributed.addAttribute(TextAttribute.LIGATURES, TextAttribute.LIGATURES_ON, start, end);
	}

	private Font resolveFont(TextRunStyle style) {
		TypographySpec typography = style.typography;
		String family = typography != null ? typography.fontFamily : null;
		String resolvedFamily = chooseFontFamily(family, typography == null ? null : typography.fontFallbackStack, style.rtl);
		int awtStyle = Font.PLAIN;
		if (typography != null && typography.fontStyle != null && typography.fontStyle.equalsIgnoreCase("oblique")) {
			awtStyle = Font.ITALIC;
		}
		if (typography != null && typography.fontWeight != null && typography.fontWeight >= 600) {
			awtStyle |= Font.BOLD;
		}
		float size = typography != null && typography.fontSize != null ? typography.fontSize.floatValue() : 12f;
		Font font = new Font(resolvedFamily, awtStyle, Math.max(1, Math.round(size)));
		if (typography != null && typography.fontStyle != null && typography.fontStyle.equalsIgnoreCase("oblique") && typography.obliqueAngle != null) {
			font = font.deriveFont(AffineTransform.getShearInstance(Math.tan(Math.toRadians(typography.obliqueAngle)), 0));
		}
		if (typography != null && Boolean.TRUE.equals(typography.condensed)) {
			font = font.deriveFont(AffineTransform.getScaleInstance(0.85, 1.0));
		}
		if (typography != null && typography.fontSize != null) {
			font = font.deriveFont(size);
		}
		return font;
	}

	private String chooseFontFamily(String family, List<String> fallbacks, boolean rtl) {
		if (family != null && availableFonts.contains(family)) {
			System.out.println("Using specified font family: " + family);
			return family;
		}
		if (fallbacks != null) {
			for (String fallback : fallbacks) {
				if (fallback != null && availableFonts.contains(fallback)) {
					return fallback;
				}
			}
		}
		System.out.println("Specified font family not available. Falling back to default sans-serif font.");
		return Font.SANS_SERIF;
	}

	private Color color(String hex, Double opacity) {
		if (hex == null) {
			return new Color(0, 0, 0, alpha(opacity));
		}
		Color base = Color.decode(hex);
		return new Color(base.getRed(), base.getGreen(), base.getBlue(), alpha(opacity));
	}

	private int alpha(Double opacity) {
		double value = opacity == null ? 1.0 : opacity;
		return (int) Math.max(0, Math.min(255, Math.round(255 * value)));
	}

	private record TextRunStyle(TypographySpec typography, FillSpec fill, boolean rtl) {
		Color foreground() {
			return fill == null ? Color.BLACK : new Color(colorFromHex(fill.color).getRed(), colorFromHex(fill.color).getGreen(), colorFromHex(fill.color).getBlue(), alpha(fill.opacity));
		}

		float tracking() {
			if (typography == null || typography.letterSpacing == null || typography.fontSize == null || typography.fontSize == 0) {
				return 0f;
			}
			return (float) (typography.letterSpacing / typography.fontSize);
		}

		private Color colorFromHex(String hex) {
			return hex == null ? Color.BLACK : Color.decode(hex);
		}

		private int alpha(Double opacity) {
			double value = opacity == null ? 1.0 : opacity;
			return (int) Math.max(0, Math.min(255, Math.round(255 * value)));
		}
	}
}
