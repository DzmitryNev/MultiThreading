package com.parser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;

public class MainApp2 {

	public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {
		long startTime = System.nanoTime();
		String sourcePath = args[0];
		String dictonary = args[1];
		String result = args[2];

		ExecutorService executor = Executors.newFixedThreadPool(10);

		// get java file paths
		List<String> javaPaths = getJavaPaths(sourcePath);
		System.out.println(javaPaths.size());

		// get dictionary
		List<String> dictionary = FileUtils.readLines(new File(dictonary), "utf-8");

		// init global map
		Map<String, Long> counter = new HashMap<>();
		for (String word : dictionary) {
			counter.put(word, 0L);
		}

		System.out.println("counter " + counter);
		// submit tasks
		List<Future<Map<String, Long>>> futures = javaPaths.stream().map(path -> new Job(path, dictionary))
				.map(task -> executor.submit(task)).collect(Collectors.toList());

		// waiting result
		for (Future<Map<String, Long>> future : futures) {
			Map<String, Long> futureMap = future.get();
			futureMap.entrySet().stream().forEach(entry -> {
				Long valueOld = counter.get(entry.getKey());
				Long correctedValue = valueOld + entry.getValue();
				counter.put(entry.getKey(), correctedValue);
			});
		}

		// write result
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
