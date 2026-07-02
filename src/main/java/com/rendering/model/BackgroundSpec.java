package com.rendering.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BackgroundSpec {
	public FillSpec fill;
	public String shape;
	public BorderRadiusSpec borderRadius;
	public PaddingSpec padding;
	public StrokeSpec stroke;
	public Object shadows;
}
