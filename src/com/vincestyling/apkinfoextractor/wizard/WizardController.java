package com.vincestyling.apkinfoextractor.wizard;

import com.vincestyling.apkinfoextractor.Main;
import com.vincestyling.apkinfoextractor.core.ApkInfoDataProvider;
import com.vincestyling.apkinfoextractor.entity.Solution;
import com.vincestyling.apkinfoextractor.utils.Constancts;
import com.vincestyling.apkinfoextractor.utils.GlobalUtils;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.DirectoryChooser;
import javafx.util.Callback;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

public class WizardController implements Initializable {
	public Button btnChoose;
	public TextField txfPath;
	public TableView<ApkInfoDataProvider> resultTable;
	public TextField txfName;
	private Main application;

	public void setApp(Main application) {
		this.application = application;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		resultTable.setFocusTraversable(false);
	}

	public void showFileChooseer(ActionEvent actionEvent) {
		DirectoryChooser dirChooser = new DirectoryChooser();
		dirChooser.setInitialDirectory(FileSystemView.getFileSystemView().getDefaultDirectory());
		dirChooser.setTitle("Choosing apk Directory");
		File result = dirChooser.showDialog(btnChoose.getScene().getWindow());
		txfPath.setText(result.getAbsolutePath());
	}

	private Map<String, TableColumn> resultTableColumns = new LinkedHashMap<String, TableColumn>(6);
	private ObservableList<ApkInfoDataProvider> data = ApkInfoDataProvider.buildSampleDatas();

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
								ImageView imageview = new ImageView();
								@Override
								public void updateItem(Object o, boolean empty) {
									HBox box = new HBox();
									imageview.setFitWidth(40);
									imageview.setFitHeight(40);
//									imageview.setImage(new Image(Main.class.getResourceAsStream(o.toString())));
									imageview.setImage(new Image(Main.class.getResourceAsStream(Constancts.DEFAULT_APK_LOGO)));
//									imageview.setImage(new Image(new FileInputStream("/Users/vince/Downloads/1400327021_android.png")));
									box.getChildren().add(imageview);
									box.setAlignment(Pos.CENTER);
									setGraphic(box);
								}
							};
						}
					});
				}
				column.setCellValueFactory(new PropertyValueFactory(key));
				column.setSortable(false);

				resultTableColumns.put(key, column);
				resultTable.getColumns().add(column);
				resultTable.setItems(data);
			}
		} else {
			resultTableColumns.get(key).setVisible(false);
			resultTableColumns.remove(key);
		}
	}

	public void createSolution(ActionEvent actionEvent) {
		String name = txfName.getText();
		String path = txfPath.getText();

		Set<String> fieldSets = resultTableColumns.keySet();
		StringBuilder extractFields = new StringBuilder();
		extractFields.append(Constancts.OP).append(',');
		extractFields.append(Constancts.ID).append(',');
		for (String field : fieldSets) {
			extractFields.append(field).append(',');
		}
		extractFields.append(Constancts.APKFILENAME).append(',');

		Solution solution = new Solution(name, path, extractFields.toString());
		solution.setId(GlobalUtils.createSolution(solution));
		System.out.println(solution);
		try {
			application.launchSolution(solution);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
