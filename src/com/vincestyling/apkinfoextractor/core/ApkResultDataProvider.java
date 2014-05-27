package com.vincestyling.apkinfoextractor.core;

import com.vincestyling.apkinfoextractor.entity.ApkInfo;
import com.vincestyling.apkinfoextractor.utils.Constancts;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ApkResultDataProvider {
	private StringProperty id;
	private StringProperty apkFileName;
	private ListProperty label;
	private StringProperty icon;
	private StringProperty pkg;
	private StringProperty versionCode;
	private StringProperty versionName;
	private StringProperty launchActivity;
	private ApkInfo apkInfo;

	public ApkResultDataProvider(ApkInfo apkInfo) {
		this.apkInfo = apkInfo;
		this.apkFileName = new SimpleStringProperty(String.valueOf(apkInfo.getApkFileName()));
		this.id = new SimpleStringProperty(String.valueOf(apkInfo.getId()));
		this.label = new SimpleListProperty<String>(FXCollections.observableArrayList(apkInfo.getLabels()));
		this.icon = new SimpleStringProperty(apkInfo.getIcon());
		this.pkg = new SimpleStringProperty(apkInfo.getPkg());
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
		this.label = new SimpleListProperty<String>(FXCollections.observableArrayList(newLables));
		apkInfo.setLabel(newLables.get(0));
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

	public ApkInfo getApkInfo() {
		return apkInfo;
	}
}
