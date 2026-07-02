package com.rendering.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MetadataSpec {
	public SourceImageDimensionsSpec sourceImageDimensions;
	public String description;
	public String primaryFontFamily;
	public List<String> fontVariantsPresent;
	public Integer totalElements;
	public Map<String, Integer> elementBreakdown;
	public List<String> colorPalette;
	public List<String> readingOrder;
	public List<String> encodingNotes;
}
