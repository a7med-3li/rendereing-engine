package com.rendering.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TextDecorationSpec {
	public Object underline;
	public Object strikethrough;
	public Object overline;
	public Object highlight;
}
