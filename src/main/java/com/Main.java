package com;

import com.rendering.RenderingEngine;

import java.nio.file.Path;

public class Main {
	public static void main(String[] args) throws Exception {
		Path input = args.length > 0 ? Path.of(args[0]) : Path.of("shapes.json");
		Path output = args.length > 1 ? Path.of(args[1]) : Path.of("shapes1.png");
		new RenderingEngine().render(input, output);
	}
}
