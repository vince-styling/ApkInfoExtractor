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
import com.vincestyling.apkinfoextractor.utils.GlobalUtil;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.TimelineBuilder;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

public class LaunchController extends StackPane implements Initializable, ApkHandleCallback {
	public ProgressBar prgHandle;
	public Text txfTotal;
	public Text txfProcessed;
	public Text txfSuccess;
	public Text txfFailure;
	public TableView resultTable;
	public Button btnOperation;
	public Button btnOpenOutputDir;

	public StackPane exportPane;
	private ExportDialog exportDialog;

	private Solution solution;
	private Main application;
	private AaptExtractor extractorIns;

	public void init(Main apps, Solution slton) {
		this.application = apps;
		this.solution = slton;

		if (solution.getApkResultCount() == 0) {
			extractorIns = new AaptExtractor(slton, this);
			extractorIns.start();
		} else {
			txfFailure.setText("0");
			txfProcessed.setText(String.valueOf(solution.getApkResultCount()));
			txfSuccess.setText(txfProcessed.getText());
			txfTotal.setText(txfProcessed.getText());

			resultTable.setItems(FXCollections.observableArrayList(solution.getApkResultList()));

			btnOperation.setText("Export");
			btnOperation.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent actionEvent) {
					showExportDialog();
				}
			});
			prgHandle.setProgress(1);
		}

		String[] fields = (Constancts.OP + ',' + slton.getExtractFields()).split(",");
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
							ObjectSet<ApkInfo> dataset = db.queryByExample(new ApkInfo(provider.getApkInfo().getId()));
							if (dataset.size() > 0) {
								ApkInfo apkInfo = dataset.get(0);
								apkInfo.setLabel(t.getNewValue().get(0));
								apkInfo.clearLabels();
								db.store(apkInfo);
							}
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
	public void callback(ApkInfo apkInfo, final int totalCount, int successCount) {
		solution.addApkResult(new ApkResultDataProvider(apkInfo));

		txfFailure.setText(String.valueOf(solution.getApkResultCount() - successCount));
		txfProcessed.setText(String.valueOf(solution.getApkResultCount()));
		txfSuccess.setText(String.valueOf(successCount));
		txfTotal.setText(String.valueOf(totalCount));

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				resultTable.setItems(FXCollections.observableArrayList(solution.getApkResultList()));
				if (solution.getApkResultCount() == totalCount) {
					btnOperation.setText("Export");
					btnOperation.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent actionEvent) {
							showExportDialog();
						}
					});
				}
				prgHandle.setProgress(solution.getApkResultCount() * 1.0f / totalCount);
			}
		});
	}

	private void removeItem(int index) {
		ApkResultDataProvider provider;
		try {
			provider = solution.getApkResultList().remove(index);
			if (provider == null) return;
		} catch (Exception e) {
			return;
		}

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

		resultTable.setItems(FXCollections.observableArrayList(solution.getApkResultList()));
	}

	public void cancelSolution(ActionEvent actionEvent) {
		if (extractorIns != null) extractorIns.cancel();
		((Button) actionEvent.getSource()).setVisible(false);
	}

	private void showExportDialog() {
		if (exportDialog == null) {
			exportDialog = new ExportDialog(this);
			exportPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
				public void handle(MouseEvent t) {
					hideExportDialog();
					t.consume();
				}
			});
		}
		exportPane.getChildren().add(exportDialog);
		exportPane.setVisible(true);
		exportPane.setCache(true);
		exportPane.setOpacity(0);
		TimelineBuilder.create().keyFrames(
				new KeyFrame(Duration.millis(600), new EventHandler<ActionEvent>() {
					public void handle(ActionEvent t) {
						exportPane.setCache(false);
					}
				}, new KeyValue(exportPane.opacityProperty(), 1, Interpolator.EASE_BOTH)
				)).build().play();
	}

	public void hideExportDialog() {
		exportPane.setCache(true);
		TimelineBuilder.create().keyFrames(
				new KeyFrame(Duration.millis(600),
						new EventHandler<ActionEvent>() {
							public void handle(ActionEvent t) {
								exportPane.setCache(false);
								exportPane.setVisible(false);
								exportPane.getChildren().clear();
							}
						},
						new KeyValue(exportPane.opacityProperty(),0, Interpolator.EASE_BOTH)
				)).build().play();

		if (exportDialog != null) exportDialog.onClose();
	}

	public void openOutputDirectory(ActionEvent actionEvent) throws Exception {
		GlobalUtil.openOutputDirectory(solution.getDBFile());
	}

	public void onExportSuccess() {
		btnOpenOutputDir.setVisible(true);
	}

	public Solution getSolution() {
		return solution;
	}

}
