package com.rendering.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TextElementSpec extends ElementSpec {
	public String content;
	public String language;
	public String locale;
	public TypographySpec typography;
	public ParagraphSpec paragraph;
	public AppearanceSpec appearance;
	public TextDecorationSpec textDecoration;
	public BackgroundSpec background;
	public PaddingSpec padding;
	public List<InlineSegmentSpec> inlineSegments;
	public AccessibilitySpec accessibility;
	public String notes;
}
