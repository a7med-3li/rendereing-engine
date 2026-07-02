package com.rendering.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class SourceImageDimensionsSpec {
	public Integer width;
	public Integer height;
	public String unit;
}
