package com.rendering.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RenderDocument {
	@JsonProperty("_schema_version")
	public String schemaVersion;

	@JsonProperty("_schema_ref")
	public String schemaRef;

	public CanvasSpec canvas;
	public List<ElementSpec> elements;
	public MetadataSpec metadata;
}
