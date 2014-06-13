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
package com.vincestyling.apkinfoextractor.entity;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ResultDataProvider {
	private StringProperty op;
	private StringProperty id;
	private StringProperty apkFileName;
	private ListProperty label;
	private StringProperty icon;
	private StringProperty pkg;
	private StringProperty versionCode;
	private StringProperty versionName;
	private StringProperty launchActivity;
	private ApkInfo apkInfo;

	public ResultDataProvider(ApkInfo apkInfo) {
		this.apkInfo = apkInfo;
		this.apkFileName = new SimpleStringProperty(String.valueOf(apkInfo.getApkFileName()));
		this.id = new SimpleStringProperty(String.valueOf(apkInfo.getId()));
		this.op = new SimpleStringProperty("");

		if (apkInfo.getLabels() != null && apkInfo.getLabels().size() > 0) {
			this.label = new SimpleListProperty<>(FXCollections.observableArrayList(apkInfo.getLabels()));
		} else if (apkInfo.getLabel() != null) {
			this.label = new SimpleListProperty<>(FXCollections.observableArrayList(apkInfo.getLabel()));
		} else {
			this.label = new SimpleListProperty<>(FXCollections.observableArrayList(""));
		}

		this.icon = new SimpleStringProperty(apkInfo.getIcon());
		this.pkg = new SimpleStringProperty(apkInfo.getPackage());
		this.versionCode = new SimpleStringProperty(apkInfo.getVersionCode());
		this.versionName = new SimpleStringProperty(apkInfo.getVersionName());
		this.launchActivity = new SimpleStringProperty(apkInfo.getLaunchActivity());
	}

	public StringProperty apkFileNameProperty() {
		return apkFileName;
	}

	public StringProperty idProperty() {
		return id;
	}

	public ListProperty labelProperty() {
		return label;
	}

	public void setLabel(ObservableList<String> newLables) {
		this.label = new SimpleListProperty<>(newLables);
		apkInfo.setLabel(newLables.get(0));
		apkInfo.clearLabels();
	}

	public StringProperty iconProperty() {
		return icon;
	}

	public StringProperty packageProperty() {
		return pkg;
	}

	public StringProperty versionCodeProperty() {
		return versionCode;
	}

	public StringProperty versionNameProperty() {
		return versionName;
	}

	public StringProperty launchActivityProperty() {
		return launchActivity;
	}

	public StringProperty opProperty() {
		return op;
	}

	public ApkInfo getApkInfo() {
		return apkInfo;
	}
}
