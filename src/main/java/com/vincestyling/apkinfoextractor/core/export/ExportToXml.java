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
package com.vincestyling.apkinfoextractor.core.export;

import com.vincestyling.apkinfoextractor.Main;
import com.vincestyling.apkinfoextractor.entity.ApkInfo;
import com.vincestyling.apkinfoextractor.entity.ParsedPattern;
import com.vincestyling.apkinfoextractor.entity.ResultDataProvider;
import com.vincestyling.apkinfoextractor.entity.Solution;
import com.vincestyling.apkinfoextractor.utils.Constancts;
import com.vincestyling.apkinfoextractor.utils.GlobalUtil;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;

public class ExportToXml extends Thread {
	protected Solution solution;
	protected ExportProcessCallback callback;
	protected TextArea txaPattern;
	protected ProgressBar prgBar;
	protected Button btnExport;

	public ExportToXml(
			Solution solution, ExportProcessCallback callback,
			TextArea txaPattern, ProgressBar prgBar, Button btnExport) {
		super();

		this.solution = solution;
		this.callback = callback;

		this.txaPattern = txaPattern;
		this.btnExport = btnExport;
		this.prgBar = prgBar;
	}

	@Override
	public synchronized void start() {
		txaPattern.setDisable(true);
		btnExport.setDisable(true);
		prgBar.setVisible(true);
		prgBar.setProgress(0);
		super.start();
	}

	@Override
	public void run() {
		try {
			export();
		} catch (Exception e) {
			callback.onProcessFailure(e);
			e.printStackTrace();
		}

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txaPattern.setDisable(false);
				btnExport.setDisable(false);
				prgBar.setVisible(false);
			}
		});
	}

	protected void export() throws Exception {
		String content = GlobalUtil.toString(Main.class.getResourceAsStream(Constancts.XML_EXPORT_PATTERN_FILE));
		StringBuilder output = new StringBuilder();
		buildOutput(txaPattern.getText(), output);
		output.deleteCharAt(output.length() - 1).deleteCharAt(output.length() - 1);
		content = content.replace(Constancts.XML_EXPORT_CONTENT_REPLACEMENT, output.toString());

		File outputFile = new File(
				solution.getWorkingFolder(),
				solution.generateOutputFileName() + ".xml");
		FileOutputStream fos = new FileOutputStream(outputFile);
		fos.write(content.getBytes(Constancts.DEFAULT_CHARSET));
		fos.close();

		callback.onProcessSuccess(outputFile);
	}

	protected void buildOutput(String pattern, StringBuilder exportOutput) {
		for (int i = 0; i < solution.getResultCount(); i++) {
			postProgress(i + 1);
			ResultDataProvider provider = solution.getResultList().get(i);
			String itemOutput = substituteNamedFields(pattern, provider.getApkInfo());
			exportOutput.append(itemOutput).append(System.lineSeparator()).append(System.lineSeparator());
		}
	}

	protected void postProgress(final int processedCount) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				prgBar.setProgress(processedCount * 1.0f / solution.getResultCount());
			}
		});
	}

	protected String getFieldValue(ApkInfo apkInfo, String fieldName) throws Exception {
		for (Method method : apkInfo.getClass().getMethods()) {
			if (method.getName().equalsIgnoreCase("get" + fieldName)) {
				return method.invoke(apkInfo).toString();
			}
		}
		return "";
	}

	private String substituteNamedFields(String pattern, ApkInfo apkInfo) {
		ParsedPattern parsedPattern = parseExportPattern(pattern);
		StringBuilder actualPattern = new StringBuilder();
		int lastIndex = 0;

		for (ParsedPattern.NamedField field : parsedPattern.getNamedFields()) {
			actualPattern.append(pattern.substring(lastIndex, field.getStartIndex()));
			try {
				actualPattern.append(getFieldValue(apkInfo, field.getName()));
			} catch (Exception e) {
				e.printStackTrace();
			}
			lastIndex = field.getEndIndex();
		}

		actualPattern.append(pattern.substring(lastIndex, pattern.length()));

		return actualPattern.toString();
	}

	private ParsedPattern parseExportPattern(String pattern) {
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

	private boolean isFieldSeparator(char c) {
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

	private char[] FIELD_SEPARATORS = new char[] {'"', '\'', ':', '&', ',', ';', '(', ')', '|', '=', '+', '-', '*', '%', '/', '\\', '<', '>', '^'};

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
