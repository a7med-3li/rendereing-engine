package com.rendering.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
	@JsonSubTypes.Type(value = TextElementSpec.class, name = "text"),
	@JsonSubTypes.Type(value = DividerElementSpec.class, name = "divider"),
	@JsonSubTypes.Type(value = ShapeElementSpec.class, name = "shape")
})
public abstract class ElementSpec {
	public String id;
	public String type;
	public GeometrySpec geometry;
}
