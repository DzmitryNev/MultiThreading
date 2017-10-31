package com.parser;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.io.FileUtils;

public class Job implements Callable<Map<String, Long>> {

	private String fileName;
	private List<String> dictionary;

	public Job(String fileName, List<String> dictionary) {
		this.fileName = fileName;
		this.dictionary = dictionary;
	}

	@Override
	public Map<String, Long> call() throws Exception {
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

}
