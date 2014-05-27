package com.vincestyling.apkinfoextractor.launch;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.vincestyling.apkinfoextractor.Main;
import com.vincestyling.apkinfoextractor.core.AaptExtractor;
import com.vincestyling.apkinfoextractor.core.ApkHandleCallback;
import com.vincestyling.apkinfoextractor.core.ApkResultDataProvider;
import com.vincestyling.apkinfoextractor.entity.ApkInfo;
import com.vincestyling.apkinfoextractor.entity.Solution;
import com.vincestyling.apkinfoextractor.utils.Constancts;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class LaunchController implements Initializable, ApkHandleCallback {
	public ProgressBar prgHandle;
	public Text txfTotal;
	public Text txfProcessed;
	public Text txfSuccess;
	public Text txfFailure;
	public TableView resultTable;
	public Button btnOperation;

	private Solution solution;
	private Main application;
	private AaptExtractor extractorIns;

	private List<ApkResultDataProvider> apkInfoList = new LinkedList<ApkResultDataProvider>();
	private int totalCount;
	private int successCount;

	public void init(Main apps, Solution slton) {
		this.application = apps;
		this.solution = slton;
		extractorIns = new AaptExtractor(slton, this);
		extractorIns.start();

		totalCount = slton.getTotalFiles();
		txfTotal.setText(String.valueOf(totalCount));

		String[] fields = slton.getExtractFields().split(",");
		for (String field : fields) {
			TableColumn column = new TableColumn(field);

			if (field.equals(Constancts.ICON)) {
				column.setPrefWidth(50);
				column.setCellFactory(new Callback<TableColumn<String, String>, TableCell>() {
					@Override
					public TableCell call(TableColumn param) {
						return new TableCell<String, String>() {
							@Override
							public void updateItem(String iconPath, boolean empty) {
								super.updateItem(iconPath, empty);
								if (iconPath == null || iconPath.isEmpty()) return;

								File iconFile = new File(iconPath);
								if (iconFile.exists() && iconFile.isFile()) {
									ImageView iconView = new ImageView();
									try {
										iconView.setImage(new Image(new FileInputStream(iconFile)));
									} catch (FileNotFoundException e) {
										System.out.println(e.getMessage());
									}
									iconView.setFitHeight(40);
									iconView.setFitWidth(40);
									setGraphic(iconView);
								}
							}
						};
					}
				});
			}
			else if (field.equals(Constancts.LABEL)) {
				column.setPrefWidth(100);
				column.setCellFactory(new Callback<TableColumn, TableCell>() {
					public TableCell call(TableColumn p) {
						final TableCell cell = new TableCell<String, ObservableList<String>>() {
							private TextField textField;
							private ComboBox<String> cmBox;

							@Override
							public void startEdit() {
								if (isEmpty()) return;
								super.startEdit();

								if (getItem().size() > 1) {
									createComboBox();
									setText(null);
									setGraphic(cmBox);
								} else {
									createTextField();
									setText(null);
									setGraphic(textField);
									textField.selectAll();
								}
							}

							@Override
							public void cancelEdit() {
								super.cancelEdit();
								setText(getString());
								setGraphic(null);
							}

							@Override
							public void updateItem(ObservableList<String> labelList, boolean empty) {
								super.updateItem(labelList, empty);

								if (empty || labelList == null || labelList.isEmpty()) {
									setText(null);
									setGraphic(null);
								} else {
									if (isEditing()) {
										if (labelList.size() > 1) {
											if (cmBox != null) {
												cmBox.setValue(getString());
											}
											setText(null);
											setGraphic(cmBox);
										} else {
											if (textField != null) {
												textField.setText(getString());
											}
											setText(null);
											setGraphic(textField);
										}
									} else {
										setText(getString());
										setGraphic(null);
									}
								}
							}

							private void createComboBox() {
								cmBox = new ComboBox<String>(getItem());
								cmBox.setMinWidth(getWidth() - getGraphicTextGap() * 2);
								cmBox.setVisibleRowCount(5);
								cmBox.valueProperty().addListener(new ChangeListener<String>() {
									@Override
									public void changed(ObservableValue ov, String oldValue, String newValue) {
										if (newValue != null && newValue.length() > 0) commitEdit();
									}
								});
							}

							private void createTextField() {
								textField = new TextField(getString());
								textField.setMinWidth(getWidth() - getGraphicTextGap() * 2);
								textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
									@Override
									public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
										if (!newValue) commitEdit(null);
									}
								});
							}

							public void commitEdit() {
								super.commitEdit(FXCollections.observableArrayList(cmBox.getValue()));
							}

							@Override
							public void commitEdit(ObservableList<String> strings) {
								super.commitEdit(FXCollections.observableArrayList(textField.getText()));
							}

							private String getString() {
								return getItem() == null || getItem().isEmpty() ? "" : getItem().get(0);
							}
						};

						cell.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
							@Override
							public void handle(KeyEvent event) {
								if (event.getCode().equals(KeyCode.ENTER)) {
									cell.commitEdit(null);
									event.consume();
								}
							}
						});

						return cell;
					}
				});

				column.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<ApkResultDataProvider, ObservableList<String>>>() {
					@Override
					public void handle(TableColumn.CellEditEvent<ApkResultDataProvider, ObservableList<String>> t) {
						ApkResultDataProvider provider = t.getTableView().getItems().get(t.getTablePosition().getRow());
						provider.setLabel(t.getNewValue());

						ObjectContainer db = null;
						try {
							db = solution.getDBInstance();
							db.store(provider.getApkInfo());
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (db != null) db.close();
						}
					}
				});
			}
			else if (field.equals(Constancts.LAUNCHACTIVITY)) {
				column.setPrefWidth(250);
			}
			else if (field.equals(Constancts.VERSIONCODE)) {
				column.setPrefWidth(120);
			}
			else if (field.equals(Constancts.VERSIONNAME)) {
				column.setPrefWidth(120);
			}
			else if (field.equals(Constancts.APKFILENAME)) {
				column.setPrefWidth(120);
			}
			else if (field.equals(Constancts.PACKAGE)) {
				column.setPrefWidth(180);
			}
			else if (field.equals(Constancts.ID)) {
				column.setPrefWidth(40);
			}
			else if (field.equals(Constancts.OP)) {
				column.setCellFactory(new Callback<TableColumn<String, String>, TableCell>() {
					@Override
					public TableCell call(TableColumn param) {
						return new TableCell<ApkResultDataProvider, Object>() {
							@Override
							public void updateItem(Object o, boolean empty) {
								super.updateItem(o, empty);

								HBox hbox = new HBox();
								hbox.setMinWidth(80);
								hbox.setAlignment(Pos.CENTER);
								Button btnDel = new Button("Delete");
								btnDel.setOnAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent actionEvent) {
										removeItem(getTableRow().getIndex());
									}
								});
								hbox.getChildren().add(btnDel);
								setGraphic(hbox);
							}
						};
					}
				});
				column.setPrefWidth(80);
				column.setText("");
			}

			column.setCellValueFactory(new PropertyValueFactory(field));

			resultTable.getColumns().add(column);
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		resultTable.setFocusTraversable(false);
		resultTable.setEditable(true);
	}

	@Override
	public void callback(ApkInfo apkInfo) {
		apkInfoList.add(new ApkResultDataProvider(apkInfo));
		if (apkInfo.isSuccess()) txfSuccess.setText(String.valueOf(++successCount));
		txfFailure.setText(String.valueOf(apkInfoList.size() - successCount));
		txfProcessed.setText(String.valueOf(apkInfoList.size()));

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (apkInfoList.size() == totalCount) btnOperation.setText("Export");
				resultTable.setItems(FXCollections.observableArrayList(apkInfoList));
				prgHandle.setProgress(apkInfoList.size() * 1.0f / totalCount);
			}
		});
	}

	private void removeItem(int index) {
		ApkResultDataProvider provider;
		try {
			provider = apkInfoList.remove(index);
			if (provider == null) return;
		} catch (Exception e) {
			return;
		}

		resultTable.setItems(FXCollections.observableArrayList(apkInfoList));

		if (provider.getApkInfo().isSuccess()) txfSuccess.setText(String.valueOf(--successCount));
		txfFailure.setText(String.valueOf(apkInfoList.size() - successCount));
		txfProcessed.setText(String.valueOf(apkInfoList.size()));

		ObjectContainer db = null;
		try {
			db = solution.getDBInstance();
			ObjectSet<ApkInfo> querySet = db.queryByExample(new ApkInfo(provider.getApkInfo().getId()));
			for (ApkInfo item : querySet) {
				db.delete(item);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) db.close();
		}
	}

	public void cancelSolution(ActionEvent actionEvent) {
		if (extractorIns != null) extractorIns.cancel();
		((Button) actionEvent.getSource()).setVisible(false);
	}
}
