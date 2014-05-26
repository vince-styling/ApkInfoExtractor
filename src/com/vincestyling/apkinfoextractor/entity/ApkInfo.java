package com.vincestyling.apkinfoextractor.entity;

import java.util.HashSet;
import java.util.Set;

public class ApkInfo {
	private int id;
	private String apkFileName;
	private boolean isSuccess;

	private Set<String> labels = new HashSet<String>(3);
	private String label;

	private Set<String> icons = new HashSet<String>(3);
	private String icon;

	private String pkg;
	private String versionCode;
	private String versionName;
	private String launchActivity;

	public ApkInfo(int id, String apkFileName) {
		this.apkFileName = apkFileName;
		this.id = id;
	}

	public ApkInfo(String label, String icon, String pkg, String versionCode, String versionName, String launchActivity) {
		this.label = label;
		this.icon = icon;
		this.pkg = pkg;
		this.versionCode = versionCode;
		this.versionName = versionName;
		this.launchActivity = launchActivity;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Set<String> getLabels() {
		return labels;
	}

	public void addLabel(String label) {
		this.labels.add(label);
		this.label = label;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Set<String> getIcons() {
		return icons;
	}

	public void addIcon(String icon) {
		this.icons.add(icon);
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getPkg() {
		return pkg;
	}

	public void setPkg(String pkg) {
		this.pkg = pkg;
	}

	public String getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}

	public String getVersionName() {
		return versionName;
	}

	public void setVersionName(String versionName) {
		this.versionName = versionName;
	}

	public String getLaunchActivity() {
		return launchActivity;
	}

	public void setLaunchActivity(String launchActivity) {
		this.launchActivity = launchActivity;
	}

	public String getApkFileName() {
		return apkFileName;
	}

	public void setApkFileName(String apkFileName) {
		this.apkFileName = apkFileName;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean success) {
		isSuccess = success;
	}

	@Override
	public String toString() {
		return "ApkInfo{" +
				"id=" + id +
				", apkFileName='" + apkFileName + '\'' +
				", isSuccess=" + isSuccess +
				", labels=" + labels +
				", label='" + label + '\'' +
				", icons=" + icons +
				", icon='" + icon + '\'' +
				", pkg='" + pkg + '\'' +
				", versionCode='" + versionCode + '\'' +
				", versionName='" + versionName + '\'' +
				", launchActivity='" + launchActivity + '\'' +
				'}';
	}
}
