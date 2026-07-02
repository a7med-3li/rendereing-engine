package com.rendering.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FontFeaturesSpec {
	public String ligatures;
	public String numerals;
	public Boolean smallCaps;
	public Boolean swashes;
	public String stylisticSet;
	public Boolean contextualAlts;
	public Boolean fraction;
	public Boolean ordinals;
	public Boolean superscript;
	public Boolean subscript;
	public List<String> rawFeatureTags;
}
