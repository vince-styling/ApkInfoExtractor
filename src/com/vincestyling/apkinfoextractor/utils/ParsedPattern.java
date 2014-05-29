package com.vincestyling.apkinfoextractor.utils;

import java.util.LinkedList;
import java.util.List;

public class ParsedPattern {
	private List<NamedField> namedFields;
	private String pattern;

	ParsedPattern(String pattern) {
		this.namedFields = new LinkedList<NamedField>();
		this.pattern = pattern;
	}

	public String getPattern() {
		return pattern;
	}

	public void addNamedField(NamedField field) {
		namedFields.add(field);
	}

	public List<NamedField> getNamedFields() {
		return namedFields;
	}

	public static class NamedField {
		private int startIndex;
		private int endIndex;
		private String name;

		public NamedField(int startIndex, int endIndex, String name) {
			this.startIndex = startIndex;
			this.endIndex = endIndex;
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public int getStartIndex() {
			return startIndex;
		}

		public int getEndIndex() {
			return endIndex;
		}
	}
}
