package com.rendering.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AppearanceSpec {
	public FillSpec fill;
	public StrokeSpec stroke;
	public Object shadows;
	public Double opacity;
	public String blendMode;
	public Object effects;
	public String antialiasing;
	public String renderingIntent;
}
