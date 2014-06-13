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
package com.vincestyling.apkinfoextractor.core.ctrls;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;

public abstract class BaseTableController extends BaseController {

	@FXML
	protected TableView resultTable;

	private double minimumTableWidth;
	private double minimumTableHeight;

	protected void setTableMinimumSize(double minWidth, double minHeight) {
		minimumTableHeight = minHeight;
		minimumTableWidth = minWidth;

		resultTable.setPrefSize(minimumTableWidth, minimumTableHeight);
		resultTable.setMinSize(minimumTableWidth, minimumTableHeight);
	}

	@Override
	protected void widthResizeUp(double newWidth) {
		double resizeWidth = minimumTableWidth + newWidth - stableWidth;
		resultTable.setPrefWidth(resizeWidth);
		resultTable.setMinWidth(resizeWidth);
	}

	@Override
	protected void widthResizeDown(double newWidth) {
		resultTable.setPrefWidth(minimumTableWidth);
		resultTable.setMinWidth(minimumTableWidth);
	}

	@Override
	protected void heightResizeUp(double newHeight) {
		double resizeHeight = minimumTableHeight + newHeight - stableHeight;
		resultTable.setPrefHeight(resizeHeight);
		resultTable.setMinHeight(resizeHeight);
	}

	@Override
	protected void heightResizeDown(double newHeight) {
		resultTable.setPrefHeight(minimumTableHeight);
		resultTable.setMinHeight(minimumTableHeight);
	}
}
