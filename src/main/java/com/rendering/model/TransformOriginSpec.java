package com.rendering.model;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.IOException;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(using = TransformOriginSpec.Deserializer.class)
public class TransformOriginSpec {
	public Double x;
	public Double y;

	public static final class Deserializer extends JsonDeserializer<TransformOriginSpec> {
		@Override
		public TransformOriginSpec deserialize(JsonParser parser, DeserializationContext context) throws IOException {
			JsonNode node = parser.getCodec().readTree(parser);
			TransformOriginSpec origin = new TransformOriginSpec();
			if (node == null || node.isNull()) {
				return origin;
			}
			if (node.isTextual()) {
				applyKeyword(origin, node.asText());
				return origin;
			}
			JsonNode xNode = node.get("x");
			JsonNode yNode = node.get("y");
			if (xNode != null && !xNode.isNull()) {
				origin.x = xNode.asDouble();
			}
			if (yNode != null && !yNode.isNull()) {
				origin.y = yNode.asDouble();
			}
			return origin;
		}

		private void applyKeyword(TransformOriginSpec origin, String value) {
			String normalized = value == null ? "" : value.trim().toLowerCase();
			switch (normalized) {
				case "center" -> {
					origin.x = 0.5;
					origin.y = 0.5;
				}
				case "top-left", "left-top", "origin" -> {
					origin.x = 0.0;
					origin.y = 0.0;
				}
				case "top" -> {
					origin.x = 0.5;
					origin.y = 0.0;
				}
				case "top-right", "right-top" -> {
					origin.x = 1.0;
					origin.y = 0.0;
				}
				case "left" -> {
					origin.x = 0.0;
					origin.y = 0.5;
				}
				case "right" -> {
					origin.x = 1.0;
					origin.y = 0.5;
				}
				case "bottom-left", "left-bottom" -> {
					origin.x = 0.0;
					origin.y = 1.0;
				}
				case "bottom" -> {
					origin.x = 0.5;
					origin.y = 1.0;
				}
				case "bottom-right", "right-bottom" -> {
					origin.x = 1.0;
					origin.y = 1.0;
				}
				default -> {
					origin.x = 0.0;
					origin.y = 0.0;
				}
			}
		}
	}
}
