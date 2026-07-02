package com.rendering.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class InlineSegmentSpec {
	public int[] range;
	public String contentSlice;
	public TypographySpec typography;
	public AppearanceSpec appearance;
	public TextDecorationSpec textDecoration;
	public String link;
}
