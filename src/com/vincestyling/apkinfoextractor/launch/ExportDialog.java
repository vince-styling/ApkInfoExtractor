package com.vincestyling.apkinfoextractor.launch;

import com.vincestyling.apkinfoextractor.Main;
import com.vincestyling.apkinfoextractor.core.ApkResultDataProvider;
import com.vincestyling.apkinfoextractor.utils.Constancts;
import com.vincestyling.apkinfoextractor.utils.ExportToExcel;
import com.vincestyling.apkinfoextractor.utils.ExportToPlainText;
import com.vincestyling.apkinfoextractor.utils.GlobalUtil;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class ExportDialog extends VBox {
	private Button exportBtn;

	private final ExportToExcelPanel exportToExcelPanel;
	private final ExportToXmlPanel exportToXmlPanel;
	private final ExportToSqlPanel exportToSqlPanel;

	private final Tab exportToExcelTab;
	private final Tab exportToXmlTab;
	private final Tab exportToSqlTab;

	private final TabPane options;
	private final LaunchController launchController;

	public ExportDialog(LaunchController controller) {
		this.launchController = controller;

		setSpacing(10);
		setMaxSize(430, USE_PREF_SIZE);
		setStyle("-fx-border-color: #545454; -fx-background-color: #9acd32;");

		// block mouse clicks
		setOnMouseClicked(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent t) {
				t.consume();
			}
		});

		BorderPane explPane = new BorderPane();
		VBox.setMargin(explPane, new Insets(5, 5, 5, 5));

		Label title = new Label("Export Wizard");
		title.setMaxWidth(Double.MAX_VALUE);
		title.setAlignment(Pos.CENTER);
		getChildren().add(title);

		exportToExcelTab = new Tab("To Excel");
		exportToExcelPanel = new ExportToExcelPanel();
		exportToExcelTab.setContent(exportToExcelPanel);

		exportToXmlTab = new Tab("To xml");
		exportToXmlPanel = new ExportToXmlPanel();
		exportToXmlTab.setContent(exportToXmlPanel);

		exportToSqlTab = new Tab("To sql");
		exportToSqlPanel = new ExportToSqlPanel();
		exportToSqlTab.setContent(exportToSqlPanel);

		options = new TabPane();
		options.getStyleClass().add(TabPane.STYLE_CLASS_FLOATING);
		options.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
		options.getTabs().addAll(exportToExcelTab, exportToXmlTab, exportToSqlTab);

		getChildren().addAll(explPane, options);
	}

	private class ExportToXmlPanel extends VBox {
		private TextArea txaPattern;

		public ExportToXmlPanel() {
			setPadding(new Insets(8));
			setSpacing(10);

			Label label = new Label("Output Item Pattern :");

			txaPattern = new TextArea(ExportToPlainText.buildXmlOutputPattern(launchController.getSolution()));
			txaPattern.setPrefColumnCount(200);
			txaPattern.setPrefRowCount(5);

			exportBtn = new Button("Export");
			exportBtn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					try {
						String content = GlobalUtil.toString(Main.class.getResourceAsStream(Constancts.XML_EXPORT_PATTERN_FILE));
						StringBuilder exportOutput = new StringBuilder();
						buildOutput(txaPattern.getText(), exportOutput);
						content = content.replace(Constancts.XML_EXPORT_CONTENT_REPLACEMENT, exportOutput.toString());
						plainTextOutput(content, launchController.getSolution().generateOutputFileName() + ".xml");
						launchController.hideModalMessage();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			exportBtn.setMinWidth(74);
			exportBtn.setPrefWidth(74);

			getChildren().addAll(label, txaPattern, exportBtn);
		}
	}

	private class ExportToExcelPanel extends VBox {
		public ExportToExcelPanel() {
			setPadding(new Insets(8));
			setSpacing(10);

			Label label = new Label("Not Output Item Pattern Here!");

			exportBtn = new Button("Export");
			exportBtn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					exportToExcel();
				}
			});
			exportBtn.setMinWidth(74);
			exportBtn.setPrefWidth(74);

			getChildren().addAll(label, exportBtn);
		}
	}

	private void exportToExcel() {
		try {
			ExportToExcel.exportToExcel(launchController);
			launchController.hideModalMessage();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class ExportToSqlPanel extends VBox {
		private TextArea txaPattern;
		private ProgressBar prgBar;

		public ExportToSqlPanel() {
			setPadding(new Insets(8));
			setSpacing(10);

			Label label = new Label("Output Item Pattern :");

			txaPattern = new TextArea(ExportToPlainText.buildSqlOutputPattern(launchController.getSolution()));
			txaPattern.setPrefColumnCount(200);
			txaPattern.setPrefRowCount(5);
			txaPattern.setWrapText(true);

			HBox hbox = new HBox(5);
			hbox.setAlignment(Pos.CENTER_LEFT);

			prgBar = new ProgressBar();
			prgBar.setVisible(false);

			exportBtn = new Button("Export");
			exportBtn.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					try {
						StringBuilder exportOutput = new StringBuilder();
						exportOutput.append(GlobalUtil.toString(Main.class.getResourceAsStream(Constancts.EXPORT_LICENSE_FILE)));
						exportOutput.append(System.lineSeparator()).append(System.lineSeparator());

						buildOutput(txaPattern.getText(), exportOutput);
						plainTextOutput(exportOutput.toString(), launchController.getSolution().generateOutputFileName() + ".sql");
						launchController.hideModalMessage();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			exportBtn.setMinWidth(74);
			exportBtn.setPrefWidth(74);
			hbox.getChildren().addAll(exportBtn, prgBar);

			getChildren().addAll(label, txaPattern, hbox);
		}
	}

	private void buildOutput(String pattern, StringBuilder exportOutput) {
		for (ApkResultDataProvider provider : launchController.getApkInfoList()) {
			String itemOutput = ExportToPlainText.substituteNamedFields(pattern, provider.getApkInfo());
			exportOutput.append(itemOutput).append(System.lineSeparator()).append(System.lineSeparator());
		}
	}

	private void plainTextOutput(String output, String outputFileName) throws Exception {
		FileOutputStream fos = new FileOutputStream(new File(launchController.getSolution().getWorkdingFolder(), outputFileName));
		fos.write(output.getBytes(Charset.defaultCharset()));
		fos.close();
	}
}
