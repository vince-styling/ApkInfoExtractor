package com.vincestyling.apkinfoextractor.launch;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.vincestyling.apkinfoextractor.Main;
import com.vincestyling.apkinfoextractor.core.AaptExtractor;
import com.vincestyling.apkinfoextractor.core.ApkHandleCallback;
import com.vincestyling.apkinfoextractor.core.BaseTableController;
import com.vincestyling.apkinfoextractor.core.ResultDataProvider;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.FileInputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class LaunchController extends BaseTableController implements ApkHandleCallback {
	public ProgressBar prgHandle;
	public Label txfTotal;
	public Label txfProcessed;
	public Label txfSuccess;
	public Label txfFailure;

	public Button btnOperation;
	public Button btnOpenOutputDir;

	public StackPane exportPane;
	private ExportDialog exportDialog;

	private Solution solution;
	private AaptExtractor extractorIns;

	public void init(Main apps, Solution slton) {
		this.application = apps;
		this.solution = slton;

		if (solution.getResultCount() == 0) {
			extractorIns = new AaptExtractor(slton, this);
			extractorIns.start();
		} else {
			txfProcessed.setText(String.valueOf(solution.getResultCount()));
			txfSuccess.setText(txfProcessed.getText());
			txfTotal.setText(txfProcessed.getText());
			txfFailure.setText("0");

			resultTable.setItems(FXCollections.observableArrayList(solution.getResultList()));

			prgHandle.setProgress(1);
			afterImport();
		}

		String[] fields = (Constancts.OP + ',' + slton.getExtractFields()).split(",");
		for (String field : fields) {
			TableColumn column = new TableColumn(field);

			if (field.equals(Constancts.ICON)) {
				column.setCellFactory(new Callback<TableColumn<String, String>, TableCell>() {
					@Override
					public TableCell call(TableColumn param) {
						return new TableCell<String, String>() {
							@Override
							public void updateItem(String iconPath, boolean empty) {
								super.updateItem(iconPath, empty);
								if (isEmpty()) return;
								try {
									ImageView iconView = new ImageView();
									iconView.setImage(new Image(new FileInputStream(iconPath)));
									iconView.setFitHeight(40);
									iconView.setFitWidth(40);
									setGraphic(iconView);
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
						};
					}
				});
			}
			else if (field.equals(Constancts.LABEL)) {
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

				column.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<ResultDataProvider, ObservableList<String>>>() {
					@Override
					public void handle(TableColumn.CellEditEvent<ResultDataProvider, ObservableList<String>> t) {
						ResultDataProvider provider = t.getTableView().getItems().get(t.getTablePosition().getRow());
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
			else if (field.equals(Constancts.OP)) {
				column.setCellFactory(new Callback<TableColumn<String, String>, TableCell>() {
					@Override
					public TableCell call(TableColumn param) {
						return new TableCell<ResultDataProvider, Object>() {
							@Override
							public void updateItem(Object o, boolean empty) {
								super.updateItem(o, empty);
								if (isEmpty()) return;

								Button btnDel = new Button("Delete");
								btnDel.setOnAction(new EventHandler<ActionEvent>() {
									@Override
									public void handle(ActionEvent actionEvent) {
										removeItem(getTableRow().getIndex());
									}
								});
								setGraphic(btnDel);
							}
						};
					}
				});
			}

			column.setCellValueFactory(new PropertyValueFactory(field));
			column.setPrefWidth(ApkInfo.getFieldColumnWidth(field));
			column.setMinWidth(ApkInfo.getFieldColumnWidth(field));
			resultTable.getColumns().add(column);
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		resultTable.setFocusTraversable(false);
		resultTable.setEditable(true);
		setTableMinimumSize(960, 450);
	}

	@Override
	public void callback(ApkInfo apkInfo, final int totalCount, final int successCount) {
		solution.addResult(new ResultDataProvider(apkInfo));

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				txfFailure.setText(String.valueOf(solution.getResultCount() - successCount));
				txfProcessed.setText(String.valueOf(solution.getResultCount()));
				txfSuccess.setText(String.valueOf(successCount));
				txfTotal.setText(String.valueOf(totalCount));

				resultTable.setItems(FXCollections.observableArrayList(solution.getResultList()));
				prgHandle.setProgress(solution.getResultCount() * 1.0f / totalCount);
				if (solution.getResultCount() == totalCount) afterImport();
			}
		});
	}

	private void removeItem(int index) {
		ResultDataProvider provider;
		try {
			provider = solution.getResultList().remove(index);
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

		resultTable.setItems(FXCollections.observableArrayList(solution.getResultList()));
	}

	public void cancelSolution(ActionEvent actionEvent) {
		if (extractorIns != null) extractorIns.cancel();
		if (solution.getResultCount() > 0) afterImport();
	}

	private void afterImport() {
		btnOperation.setText("Export");
		btnOperation.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				showExportDialog();
			}
		});
		btnOpenOutputDir.setVisible(true);
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
	}

	public Solution getSolution() {
		return solution;
	}

}
