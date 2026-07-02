package com.rendering.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TypographySpec {
	public String fontFamily;
	public List<String> fontFallbackStack;
	public FontSourceSpec fontSource;
	public String fontVariant;
	public Integer fontWeight;
	public String fontStyle;
	public Double obliqueAngle;
	public Double fontSize;
	public String fontSizeUnit;
	public Boolean condensed;
	public Boolean expanded;
	public String fontStretch;
	public Double lineHeight;
	public String lineHeightUnit;
	public Double letterSpacing;
	public Double wordSpacing;
	public Double textIndent;
	public TextTransform textTransform;
	@JsonProperty("case")
	public String caseType;
	public String textAlign;
	public String verticalAlign;
	public String direction;
	public String writingMode;
	public String unicodeBidi;
	public Double baselineShift;
	public Boolean hangingPunctuation;
	public Boolean hyphenation;
	public FontFeaturesSpec fontFeatures;
	public String kerning;
	public Object kerningPairs;
	public Object ruby;
}
