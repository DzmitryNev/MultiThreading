package com.parser;

import java.awt.geom.CubicCurve2D;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.FileUtils;

public class MainApp {

	public static void main(String[] args) throws IOException {
		long startTime = System.nanoTime();
		String sourcePath = args[0];
		String dictonary = args[1];
		String result = args[2];

		List<String> javaPaths = getJavaPaths(sourcePath);
		System.out.println(javaPaths.size());

		List<String> dictionary = FileUtils.readLines(new File(dictonary), "utf-8");

		Map<String, Long> counter = new HashMap<>();
		for (String word : dictionary) {
			counter.put(word, 0L);
		}

		System.out.println("counter " + counter);

		for (String path : javaPaths) {
			counter.putAll(process(path, dictionary));
			System.out.println("path " + path);
			System.out.println("counter " + counter);
		}

		File resultFile = new File(result);
		counter.entrySet().stream().forEach(entry -> {
			try {
				FileUtils.write(resultFile, new StringBuilder(entry.getKey()).append(" - ").append(entry.getValue())
						.append(System.lineSeparator()).toString(), "utf-8", true);
			} catch (IOException e) {
				e.printStackTrace();
			}
		});

		long endTime = System.nanoTime();

		long duration = (endTime - startTime);
		System.out.println("duration " + String.format("%d min, %d sec", TimeUnit.NANOSECONDS.toSeconds(duration) / 60,
				TimeUnit.NANOSECONDS.toSeconds(duration) % 60));

	}

	private static Map<String, Long> process(String fileName, List<String> dictionary) throws IOException {
		Map<String, Long> counter = new HashMap<>();
		FileUtils.readLines(new File(fileName), "utf-8").stream().forEach(line -> {

			for (String word : dictionary) {
				String correctedWorld = new StringBuilder(".*\\b").append(word).append("\\b.*").toString();
				if (line.matches(correctedWorld)) {
					Long count = counter.getOrDefault(word, 0L);
					count++;
					counter.put(word, count);
				}
			}

		});
		return counter;

	}

	private static List<String> getJavaPaths(String sourcePath) throws IOException {
		List<String> javaFilesPaths = new ArrayList<>();
		File folder = new File(sourcePath);
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				javaFilesPaths.addAll(getJavaPaths(fileEntry.getAbsolutePath()));
			} else {
				if (fileEntry.getName().endsWith(".java")) {
					javaFilesPaths.add(fileEntry.getAbsolutePath());
				}
			}
		}
		return javaFilesPaths;
	}

}
