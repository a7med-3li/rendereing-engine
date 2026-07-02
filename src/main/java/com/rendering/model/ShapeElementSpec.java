package com.rendering.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

public class ShapeElementSpec extends ElementSpec {
	public String shape;
	public AppearanceSpec appearance;
	public BorderRadiusSpec borderRadius;
	public String notes;
}
