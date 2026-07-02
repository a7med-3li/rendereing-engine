package com.rendering.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AccessibilitySpec {
	public String ariaLabel;
	public String ariaRole;
	public Boolean ariaHidden;
	public Integer tabIndex;
}
