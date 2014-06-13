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

import com.db4o.ObjectContainer;
import com.db4o.ObjectSet;
import com.vincestyling.apkinfoextractor.Main;
import com.vincestyling.apkinfoextractor.core.PrepareRequirement;
import com.vincestyling.apkinfoextractor.entity.ApkInfo;
import com.vincestyling.apkinfoextractor.entity.ResultDataProvider;
import com.vincestyling.apkinfoextractor.entity.Solution;
import com.vincestyling.apkinfoextractor.utils.Constancts;
import com.vincestyling.apkinfoextractor.utils.GlobalUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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

public class SplashController extends BaseController implements Runnable {
	private List<Solution> solutionList;

	private final double MINIMUM_LIST_WIDTH = 600;
	private final double MINIMUM_LIST_HEIGHT = 180;

	@FXML
	private ListView lsvRecentSolution;

	public void setApp(Main application) {
		this.application = application;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		lsvRecentSolution.setPrefSize(MINIMUM_LIST_WIDTH, MINIMUM_LIST_HEIGHT);
		lsvRecentSolution.setMinSize(MINIMUM_LIST_WIDTH, MINIMUM_LIST_HEIGHT);

		// Prepare all requirement, now just release the aapt tool.
		new PrepareRequirement().start();

		new Thread(new Runnable() {
			@Override
			public void run() {
				solutionList = GlobalUtil.getRecentSolutions();
				Platform.runLater(SplashController.this);
			}
		}).start();
	}

	public void doCreateSolution() throws Exception {
		application.showCreateSolution();
	}

	@Override
	public void run() {
		if (solutionList == null || solutionList.isEmpty()) return;

		lsvRecentSolution.setItems(FXCollections.observableArrayList(solutionList));

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
															getItem().getResultList().add(new ResultDataProvider(apkInfo));
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

	@Override
	protected void widthResizeUp(double newWidth) {
		double resizeWidth = MINIMUM_LIST_WIDTH + newWidth - stableWidth;
		lsvRecentSolution.setPrefWidth(resizeWidth);
		lsvRecentSolution.setMinWidth(resizeWidth);
	}

	@Override
	protected void widthResizeDown(double newWidth) {
		lsvRecentSolution.setPrefWidth(MINIMUM_LIST_WIDTH);
		lsvRecentSolution.setMinWidth(MINIMUM_LIST_WIDTH);
	}

	@Override
	protected void heightResizeUp(double newHeight) {
		double resizeHeight = MINIMUM_LIST_HEIGHT + newHeight - stableHeight;
		lsvRecentSolution.setPrefHeight(resizeHeight);
		lsvRecentSolution.setMinHeight(resizeHeight);
	}

	@Override
	protected void heightResizeDown(double newHeight) {
		lsvRecentSolution.setPrefHeight(MINIMUM_LIST_HEIGHT);
		lsvRecentSolution.setMinHeight(MINIMUM_LIST_HEIGHT);
	}
}
