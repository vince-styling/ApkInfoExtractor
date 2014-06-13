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

import com.vincestyling.apkinfoextractor.core.ctrls.LaunchController;
import com.vincestyling.apkinfoextractor.utils.GlobalUtil;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.io.File;

public class ExportDialog extends StackPane implements ExportProcessCallback {
	private final ExportToExcelPanel exportToExcelPanel;
	private final ExportToXmlPanel exportToXmlPanel;
	private final ExportToSqlPanel exportToSqlPanel;

	private final Tab exportToExcelTab;
	private final Tab exportToXmlTab;
	private final Tab exportToSqlTab;

	private final TabPane options;
	private final VBox processBox;
	private final Button btnCheckResult;
	private final Text txtProcessResultMessage;

	private final LaunchController launchController;

	private File outputFile;

	public ExportDialog(LaunchController controller) {
		this.launchController = controller;

		setId("ExportDialog");
		setMaxSize(430, USE_PREF_SIZE);

		// block mouse clicks
		setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent t) {
				t.consume();
			}
		});

		exportToExcelTab = new Tab("Export To excel");
		exportToExcelPanel = new ExportToExcelPanel();
		exportToExcelTab.setContent(exportToExcelPanel);

		exportToXmlTab = new Tab("Export To xml");
		exportToXmlPanel = new ExportToXmlPanel();
		exportToXmlTab.setContent(exportToXmlPanel);

		exportToSqlTab = new Tab("Export To sql");
		exportToSqlPanel = new ExportToSqlPanel();
		exportToSqlTab.setContent(exportToSqlPanel);

		options = new TabPane();
		options.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
		options.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
		options.getTabs().addAll(exportToExcelTab, exportToXmlTab, exportToSqlTab);


		processBox = new VBox(10);
		processBox.setVisible(false);

		txtProcessResultMessage = new Text();
		txtProcessResultMessage.setId("ProcessMessage");

		HBox hBox = new HBox(10);

		Button btnBack = new Button("Back");
		btnBack.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				onClose();
			}
		});

		Button btnClose = new Button("Close");
		btnClose.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				launchController.hideExportDialog();
			}
		});

		btnCheckResult = new Button("Check The Output Result");
		btnCheckResult.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				try {
					GlobalUtil.openOutputDirectory(outputFile);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		hBox.getChildren().addAll(btnCheckResult, btnBack, btnClose);

		processBox.getChildren().addAll(txtProcessResultMessage, hBox);


		getChildren().addAll(options, processBox);
	}

	@Override
	public void onProcessSuccess(File outputFile) {
		this.outputFile = outputFile;
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtProcessResultMessage.setText("Process has been successfully!");
				btnCheckResult.setVisible(true);
				btnCheckResult.setManaged(true);
				processBox.setVisible(true);
				options.setVisible(false);
			}
		});
		launchController.onExportSuccess();
	}

	@Override
	public void onProcessFailure(final Exception e) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txtProcessResultMessage.setText(String.format("Process failure reason : %s", e.getMessage()));
				btnCheckResult.setVisible(false);
				btnCheckResult.setManaged(false);
				processBox.setVisible(true);
				options.setVisible(false);
			}
		});
	}

	public void onClose() {
		processBox.setVisible(false);
		options.setVisible(true);
	}

	private class ExportToExcelPanel extends VBox {
		private TextArea txaPattern;
		private ProgressBar prgBar;
		private Button btnExport;

		public ExportToExcelPanel() {
			setPadding(new Insets(8));
			setSpacing(10);

			Label label = new Label("Not Output Item Pattern Here!");

			txaPattern = new TextArea();

			prgBar = new ProgressBar();
			prgBar.setVisible(false);
			prgBar.setProgress(0);

			btnExport = new Button("Export");
			btnExport.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					try {
						new ExportToExcel(launchController.getSolution(), ExportDialog.this, txaPattern, prgBar, btnExport).start();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});

			HBox hBox = new HBox(10);
			hBox.setAlignment(Pos.CENTER_LEFT);
			hBox.getChildren().addAll(btnExport, prgBar);

			getChildren().addAll(label, hBox);
		}
	}

	private class ExportToXmlPanel extends VBox implements EventHandler<ActionEvent> {
		protected TextArea txaPattern;
		protected ProgressBar prgBar;
		protected Button btnExport;

		public ExportToXmlPanel() {
			setPadding(new Insets(10));
			setSpacing(10);

			Label label = new Label("Output Item Pattern :");

			txaPattern = new TextArea(buildOutputPattern());
			txaPattern.setPrefColumnCount(200);
			txaPattern.setPrefRowCount(5);
			txaPattern.setWrapText(true);

			btnExport = new Button("Export");
			btnExport.setOnAction(this);

			prgBar = new ProgressBar();
			prgBar.setVisible(false);
			prgBar.setProgress(0);

			HBox hBox = new HBox(10);
			hBox.setAlignment(Pos.CENTER_LEFT);
			hBox.getChildren().addAll(btnExport, prgBar);

			getChildren().addAll(label, txaPattern, hBox);
		}

		protected String buildOutputPattern() {
			return ExportToXml.buildXmlOutputPattern(launchController.getSolution());
		}

		@Override
		public void handle(ActionEvent actionEvent) {
			new ExportToXml(launchController.getSolution(), ExportDialog.this, txaPattern, prgBar, btnExport).start();
		}
	}

	private class ExportToSqlPanel extends ExportToXmlPanel {
		@Override
		protected String buildOutputPattern() {
			return ExportToSql.buildSqlOutputPattern(launchController.getSolution());
		}

		@Override
		public void handle(ActionEvent actionEvent) {
			new ExportToSql(launchController.getSolution(), ExportDialog.this, txaPattern, prgBar, btnExport).start();
		}
	}

}
