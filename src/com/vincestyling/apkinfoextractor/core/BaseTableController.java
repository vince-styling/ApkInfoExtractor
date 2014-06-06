package com.vincestyling.apkinfoextractor.core;

import javafx.scene.control.TableView;

public abstract class BaseTableController extends BaseController {
	public TableView resultTable;

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
