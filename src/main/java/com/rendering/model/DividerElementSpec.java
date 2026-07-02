package com.rendering.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DividerElementSpec extends ElementSpec {
	public StrokeSpec stroke;
	public Double dotSize;
	public Double dotGap;
	public double[] dashArray;
	public String notes;
}
