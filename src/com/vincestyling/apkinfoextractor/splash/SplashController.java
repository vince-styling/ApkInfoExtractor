package com.vincestyling.apkinfoextractor.splash;

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.vincestyling.apkinfoextractor.Main;
import com.vincestyling.apkinfoextractor.core.ApkResultDataProvider;
import com.vincestyling.apkinfoextractor.entity.ApkInfo;
import com.vincestyling.apkinfoextractor.entity.Solution;
import com.vincestyling.apkinfoextractor.utils.Constancts;
import com.vincestyling.apkinfoextractor.utils.GlobalUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Callback;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class SplashController implements Initializable, Runnable {
	private List<Solution> solutionList;

	public ListView lsvRecentSolution;
	private Main application;

	public void setApp(Main application) {
		this.application = application;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				solutionList = GlobalUtil.getRecentSolutions();
				Platform.runLater(SplashController.this);
			}
		}).run();
	}

	public void doCreateSolution(ActionEvent actionEvent) throws Exception {
		application.showCreateSolution();
	}

	@Override
	public void run() {
		if (solutionList == null || solutionList.isEmpty()) return;

		lsvRecentSolution.setItems(FXCollections.observableArrayList(solutionList));
		lsvRecentSolution.setVisible(true);
		lsvRecentSolution.setManaged(true);

		lsvRecentSolution.setCellFactory(new Callback<ListView, ListCell>() {
			@Override
			public ListCell call(ListView listView) {
				return new ListCell<Solution>() {
					@Override
					protected void updateItem(Solution item, boolean empty) {
						super.updateItem(item, empty);
						if (isEmpty()) return;

						FXMLLoader loader = new FXMLLoader();
						InputStream ins = Main.class.getResourceAsStream(Constancts.SOLUTION_ITEM_PAGE);
						loader.setBuilderFactory(new JavaFXBuilderFactory());
						loader.setLocation(Main.class.getResource(Constancts.SOLUTION_ITEM_PAGE));
						try {
							Parent solutionPane = (Parent) loader.load(ins);

							Text txtName = (Text) solutionPane.lookup("#txtName");
							txtName.setText(item.getName());

							Text txtCreateTime = (Text) solutionPane.lookup("#txtCreateTime");
							txtCreateTime.setText('(' + item.getCreateTime() + ')');

							Text txtApkDirectory = (Text) solutionPane.lookup("#txtApkDirectory");
							txtApkDirectory.setText(item.getApksDirectory());

							Text txtOutputDirectory = (Text) solutionPane.lookup("#txtOutputDirectory");
							txtOutputDirectory.setText(item.getWorkingFolder().getPath());

							solutionPane.setOnMouseClicked(new EventHandler<MouseEvent>() {
								@Override
								public void handle(MouseEvent mouseEvent) {
									if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
										if (mouseEvent.getClickCount() == 2) {
											new Thread(new Runnable() {
												@Override
												public void run() {
													ObjectContainer db = null;
													try {
														db = getItem().getDBInstance();
														ObjectSet<ApkInfo> dataSet = db.queryByExample(ApkInfo.class);
														for (ApkInfo apkInfo : dataSet) {
															getItem().getApkResultList().add(new ApkResultDataProvider(apkInfo));
														}

														Platform.runLater(new Runnable() {
															@Override
															public void run() {
																try {
																	application.launchSolution(getItem());
																} catch (Exception e) {
																	e.printStackTrace();
																}
															}
														});
													} catch (Exception e) {
														e.printStackTrace();
													} finally {
														if (db != null) db.close();
													}
												}
											}).start();
										}
									}
								}
							});

							setGraphic(solutionPane);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							try {
								ins.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				};
			}
		});
	}
}
