package com.rendering.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ParagraphSpec {
	public Integer maxLines;
	public String overflow;
	public String wrapMode;
	public Integer columns;
	public Integer columnGap;
	public Double paragraphSpacing;
	public Integer orphans;
	public Integer widows;
	public String textPath;
}
