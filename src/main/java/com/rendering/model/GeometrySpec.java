package com.rendering.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GeometrySpec {
	public double x;
	public double y;
	public double width;
	public double height;
	public Double rotation;
	public Double skewX;
	public Double skewY;
	public Double scaleX;
	public Double scaleY;
	public Boolean flipHorizontal;
	public Boolean flipVertical;
	public TransformOriginSpec transformOrigin;
	public Integer zIndex;
	public Double baselineY;
}
