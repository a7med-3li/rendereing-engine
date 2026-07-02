package com.rendering.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CanvasSpec {
	public int width;
	public int height;
	public String unit;
	public String colorMode;
	public BackgroundSpec background;
	public Integer dpi;
	public String coordinateOrigin;
}
