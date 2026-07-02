package com.rendering.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BorderRadiusSpec {
	public Double topLeft;
	public Double topRight;
	public Double bottomRight;
	public Double bottomLeft;
}
