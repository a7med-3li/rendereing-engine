package com.rendering.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaddingSpec {
	public Double top;
	public Double right;
	public Double bottom;
	public Double left;
}
