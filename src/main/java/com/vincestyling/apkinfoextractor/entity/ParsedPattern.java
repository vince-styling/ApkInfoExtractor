/*
 * Copyright 2014 Vince Styling
 * https://github.com/vince-styling/ApkInfoExtractor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vincestyling.apkinfoextractor.entity;

import java.util.LinkedList;
import java.util.List;

public class ParsedPattern {
	private List<NamedField> namedFields;
	private String pattern;

	public ParsedPattern(String pattern) {
		this.namedFields = new LinkedList<>();
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
