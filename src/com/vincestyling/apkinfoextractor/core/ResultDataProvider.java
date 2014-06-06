package com.vincestyling.apkinfoextractor.core;

import com.vincestyling.apkinfoextractor.entity.ApkInfo;
import javafx.beans.property.*;
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
			this.label = new SimpleListProperty<String>(FXCollections.observableArrayList(apkInfo.getLabels()));
		} else if (apkInfo.getLabel() != null) {
			this.label = new SimpleListProperty<String>(FXCollections.observableArrayList(apkInfo.getLabel()));
		} else {
			this.label = new SimpleListProperty<String>(FXCollections.observableArrayList(""));
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
		this.label = new SimpleListProperty<String>(newLables);
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
