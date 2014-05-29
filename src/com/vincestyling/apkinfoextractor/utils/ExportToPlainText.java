package com.vincestyling.apkinfoextractor.utils;

import com.vincestyling.apkinfoextractor.entity.ApkInfo;
import com.vincestyling.apkinfoextractor.entity.Solution;

public class ExportToPlainText {

	public static String substituteNamedFields(String pattern, ApkInfo apkInfo) {
		ParsedPattern parsedPattern = parseExportPattern(pattern);
		StringBuilder actualPattern = new StringBuilder();
		int lastIndex = 0;

		for (ParsedPattern.NamedField field : parsedPattern.getNamedFields()) {
			actualPattern.append(pattern.substring(lastIndex, field.getStartIndex()));
			try {
				actualPattern.append(ExportToExcel.getFieldValue(apkInfo, field.getName()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			lastIndex = field.getEndIndex();
		}

		actualPattern.append(pattern.substring(lastIndex, pattern.length()));

		return actualPattern.toString();
	}

	private static ParsedPattern parseExportPattern(String pattern) {
		ParsedPattern parsedPattern = new ParsedPattern(pattern);
		char[] statement = pattern.toCharArray();

		int i = 0;
		while (i < statement.length) {
			char c = statement[i];
			if (c == ':') {
				int j = i + 1;
				while (j < statement.length && !isFieldSeparator(statement[j])) {
					j++;
				}
				if (j - i > 1) {
					parsedPattern.addNamedField(new ParsedPattern.NamedField(i, j, pattern.substring(i + 1, j)));
				}
				i = j - 1;
			}
			i++;
		}

		return parsedPattern;
	}

	private static boolean isFieldSeparator(char c) {
		if (Character.isWhitespace(c)) {
			return true;
		}
		for (char separator : FIELD_SEPARATORS) {
			if (c == separator) {
				return true;
			}
		}
		return false;
	}

	private static final char[] FIELD_SEPARATORS =
			new char[] {'"', '\'', ':', '&', ',', ';', '(', ')', '|', '=', '+', '-', '*', '%', '/', '\\', '<', '>', '^'};

	public static String buildSqlOutputPattern(Solution solution) { // mysql style default
		StringBuilder output = new StringBuilder("insert into APKS_TABLE(");

		String[] fields = solution.getExtractFields().split(",");
		for (String field : fields) output.append(field).append(", ");
		output.deleteCharAt(output.length() - 1).deleteCharAt(output.length() - 1).append(") value(");

		for (String field : fields) output.append("':").append(field).append("', ");
		output.deleteCharAt(output.length() - 1).deleteCharAt(output.length() - 1).append(");");

		return output.toString();
	}

	public static String buildXmlOutputPattern(Solution solution) {
		StringBuilder output = new StringBuilder();
		output.append("    <apk id=\":").append(Constancts.ID).append("\" fileName=\":").append(Constancts.APKFILENAME).append("\">").append(System.lineSeparator());

		appendXmlField(output, solution, Constancts.LABEL);
		appendXmlField(output, solution, Constancts.PACKAGE);
		appendXmlField(output, solution, Constancts.VERSIONCODE);
		appendXmlField(output, solution, Constancts.VERSIONNAME);
		appendXmlField(output, solution, Constancts.LAUNCHACTIVITY);

		return output.append("    </apk>").toString();
	}

	private static void appendXmlField(StringBuilder output, Solution solution, String field) {
		if (solution.getExtractFields().contains(field)) {
			output.append("        <").append(field).append(" value=\":").append(field).append("\" />").append(System.lineSeparator());
		}
	}

}
