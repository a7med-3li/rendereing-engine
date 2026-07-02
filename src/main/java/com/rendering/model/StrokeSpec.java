package com.rendering.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StrokeSpec {
	public String color;
	public Double width;
	public String position;
	public String style;
	public Double opacity;
}
