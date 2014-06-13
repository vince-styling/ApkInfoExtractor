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
import com.vincestyling.apkinfoextractor.entity.Solution;
import com.vincestyling.apkinfoextractor.utils.Constancts;
import com.vincestyling.apkinfoextractor.utils.GlobalUtil;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.FileOutputStream;

public class ExportToSql extends ExportToXml {

	public ExportToSql(
			Solution solution, ExportProcessCallback callback,
			TextArea txaPattern, ProgressBar prgBar, Button btnExport) {
		super(solution, callback, txaPattern, prgBar, btnExport);
	}

	@Override
	protected void export() throws Exception {
		StringBuilder output = new StringBuilder();
		output.append(GlobalUtil.toString(Main.class.getResourceAsStream(Constancts.EXPORT_LICENSE_FILE)));
		output.append(System.lineSeparator()).append(System.lineSeparator());
		buildOutput(txaPattern.getText(), output);

		File outputFile = new File(
				solution.getWorkingFolder(),
				solution.generateOutputFileName() + ".sql");
		FileOutputStream fos = new FileOutputStream(outputFile);
		fos.write(output.toString().getBytes(Constancts.DEFAULT_CHARSET));
		fos.close();

		callback.onProcessSuccess(outputFile);
	}

	public static String buildSqlOutputPattern(Solution solution) { // mysql style default
		StringBuilder output = new StringBuilder("insert into APKS_TABLE(");

		String[] fields = solution.getExtractFields().split(",");
		for (String field : fields) {
			if (field.equals(Constancts.ICON)) continue;
			output.append(field).append(", ");
		}
		output.deleteCharAt(output.length() - 1).deleteCharAt(output.length() - 1).append(") value(");

		for (String field : fields) {
			if (field.equals(Constancts.ICON)) continue;
			output.append("':").append(field).append("', ");
		}
		output.deleteCharAt(output.length() - 1).deleteCharAt(output.length() - 1).append(");");

		return output.toString();
	}

}
