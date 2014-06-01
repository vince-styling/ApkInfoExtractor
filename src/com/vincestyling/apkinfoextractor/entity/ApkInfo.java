package com.vincestyling.apkinfoextractor.entity;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

public class ApkInfo {
	private int id;
	private String apkFileName;

	private Set<String> labels;
	private String label;

	private Set<String> icons;
	private String icon;

	private String pkg;
	private String versionCode;
	private String versionName;
	private String launchActivity;

	public ApkInfo(int id) {
		this.id = id;
	}

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
		if (this.labels == null) this.labels = new LinkedHashSet<String>(3);
		if (this.label == null) this.label = label;
		this.labels.add(label);
	}

	public void clearLabels() {
		if (labels != null) {
			labels.clear();
			labels = null;
		}
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
		if (icons == null) icons = new HashSet<String>(3);
		icons.add(icon);
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public void clearIcons() {
		this.icons.clear();
		this.icons = null;
	}

	public String getPackage() {
		return pkg;
	}

	public void setPackage(String pkg) {
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


	public static int getIdCharacterCount() {
		return 8;
	}

	public static int getLabelCharacterCount() {
		return 20;
	}

	public static int getPackageCharacterCount() {
		return 42;
	}

	public static int getVersionCodeCharacterCount() {
		return 18;
	}

	public static int getVersionNameCharacterCount() {
		return 18;
	}

	public static int getApkFileNameCharacterCount() {
		return 35;
	}

	public static int getLaunchActivityCharacterCount() {
		return 55;
	}

	@Override
	public String toString() {
		return "ApkInfo{" +
				"id=" + id +
				", apkFileName='" + apkFileName + '\'' +
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
