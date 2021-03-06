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

import com.vincestyling.apkinfoextractor.Main;
import com.vincestyling.apkinfoextractor.entity.MockDataProvider;
import com.vincestyling.apkinfoextractor.entity.ApkInfo;
import com.vincestyling.apkinfoextractor.entity.Solution;
import com.vincestyling.apkinfoextractor.utils.Constancts;
import com.vincestyling.apkinfoextractor.utils.GlobalUtil;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class WizardController extends BaseTableController {
	@FXML
	private Button btnChoose;
	@FXML
	private TextField txfPath;
	@FXML
	private Text txtDirWraning;

	@FXML
	private TextField txfName;
	@FXML
	private Text txtFieldWraning;

	public void setApp(Main application) {
		this.application = application;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		resultTable.setFocusTraversable(false);
		setTableMinimumSize(960, 230);

		txfPath.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				showFileChooser();
			}
		});
	}

	public void showFileChooser() {
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setInitialDirectory(FileSystemView.getFileSystemView().getDefaultDirectory());
		dirChooser.setTitle("Choosing apk Directory");
		File result = dirChooser.showDialog(btnChoose.getScene().getWindow());
		try {
			txfPath.setText(result.getAbsolutePath());
			txtDirWraning.setVisible(false);
		} catch (Exception e) {}
	}

	private Map<String, TableColumn> resultTableColumns = new LinkedHashMap<String, TableColumn>(6);
	private ObservableList<MockDataProvider> data = MockDataProvider.buildSampleDatas();

	public void pkgFieldChange(ActionEvent actionEvent) {
		handleColumnSelected(actionEvent.getSource(), Constancts.PACKAGE);
	}

	public void launchActivityFieldChange(ActionEvent actionEvent) {
		handleColumnSelected(actionEvent.getSource(), Constancts.LAUNCHACTIVITY);
	}

	public void versionNameFieldChange(ActionEvent actionEvent) {
		handleColumnSelected(actionEvent.getSource(), Constancts.VERSIONNAME);
	}

	public void versionCodeFieldChange(ActionEvent actionEvent) {
		handleColumnSelected(actionEvent.getSource(), Constancts.VERSIONCODE);
	}

	public void iconFieldChange(ActionEvent actionEvent) {
		handleColumnSelected(actionEvent.getSource(), Constancts.ICON);
	}

	public void labelFieldChange(ActionEvent actionEvent) {
		handleColumnSelected(actionEvent.getSource(), Constancts.LABEL);
	}

	private void handleColumnSelected(Object chkField, String key) {
		if (((CheckBox) chkField).isSelected()) {
			if (!resultTableColumns.containsKey(key)) {
				TableColumn column = new TableColumn(key);

				if (key.equals(Constancts.ICON)) {
					column.setCellFactory(new Callback<TableColumn, TableCell>() {
						@Override
						public TableCell call(TableColumn param) {
							return new TableCell() {
								@Override
								public void updateItem(Object o, boolean empty) {
									super.updateItem(o, empty);
									ImageView iconView = new ImageView();
									iconView.setImage(new Image(Main.class.getResourceAsStream(Constancts.DEFAULT_APK_LOGO)));
									iconView.setFitHeight(40);
									iconView.setFitWidth(40);
									setGraphic(iconView);
								}
							};
						}
					});
				} else {
					column.setCellValueFactory(new PropertyValueFactory(key));
				}
				column.setPrefWidth(ApkInfo.getFieldColumnWidth(key));

				resultTableColumns.put(key, column);
				resultTable.getColumns().add(column);
				resultTable.setItems(data);
			}
			txtFieldWraning.setVisible(false);
		} else {
			resultTableColumns.get(key).setVisible(false);
			resultTableColumns.remove(key);
		}
	}

	public void createSolution() {
		String name = txfName.getText();
		String path = txfPath.getText();

		if (path == null || path.isEmpty()) {
			txtDirWraning.setVisible(true);
			return;
		}

		Set<String> fieldSets = resultTableColumns.keySet();
		if (fieldSets == null || fieldSets.isEmpty()) {
			txtFieldWraning.setVisible(true);
			return;
		}

		StringBuilder extractFields = new StringBuilder();
		extractFields.append(Constancts.ID).append(',');
		for (String field : fieldSets) {
			extractFields.append(field).append(',');
		}
		extractFields.append(Constancts.APKFILENAME).append(',');

		Solution solution = new Solution(name, path, extractFields.toString());
		solution.setId(GlobalUtil.createSolution(solution));
		System.out.println(solution);
		try {
			application.launchSolution(solution);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
