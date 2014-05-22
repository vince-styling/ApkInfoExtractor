package com.vincestyling.apkinfoextractor.core;

import com.vincestyling.apkinfoextractor.utils.Constancts;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class PackageInfo {
	public static final String ICON = "icon";
	public static final String LABEL = "label";
	public static final String PACKAGE = "package";
	public static final String VERSIONCODE = "versionCode";
	public static final String VERSIONNAME = "versionName";
	public static final String LAUNCHACTIVITY = "launchActivity";

	private StringProperty label;
	private StringProperty icon;
	private StringProperty pkg;
	private StringProperty versionCode;
	private StringProperty versionName;
	private StringProperty launchActivity;

	public PackageInfo(String label, String icon, String pkg, String versionCode, String versionName, String launchActivity) {
		this.label = new SimpleStringProperty(label);
		this.icon = new SimpleStringProperty(icon);
		this.pkg = new SimpleStringProperty(pkg);
		this.versionCode = new SimpleStringProperty(versionCode);
		this.versionName = new SimpleStringProperty(versionName);
		this.launchActivity = new SimpleStringProperty(launchActivity);
	}

	public static ObservableList<PackageInfo> buildSampleDatas() {
		return FXCollections.observableArrayList(
				new PackageInfo("AutoNavi", Constancts.DEFAULT_APK_LOGO, "com.autonavi.xmgd.navigator", "1028", "7.3.8803.1698", "com.autonavi.xmgd.navigator.Warn"),
				new PackageInfo("百度音乐", Constancts.DEFAULT_APK_LOGO, "com.ting.mp3.android", "45", "4.4.2", "com.baidu.music.ui.splash.SplashActivity"),
				new PackageInfo("Evernote", Constancts.DEFAULT_APK_LOGO, "com.evernote.world", "1057105", "5.7.1", "com.evernote.ui.HomeActivity"),
				new PackageInfo("嘀嘀打车", Constancts.DEFAULT_APK_LOGO, "com.sdu.didi.psnger", "40", "2.7", "com.sdu.didi.psnger.MainActivity"),
				new PackageInfo("FBReader", Constancts.DEFAULT_APK_LOGO, "org.geometerplus.zlibrary.ui.android", "109042", "1.9.4", "org.geometerplus.android.fbreader.FBReader"),
				new PackageInfo("植物大战僵尸2高清版", Constancts.DEFAULT_APK_LOGO, "com.popcap.pvz2cthdyyh", "110", "1.1.0", "com.popcap.SexyApp.SexyAppActivity"),
				new PackageInfo("愤怒的小鸟", Constancts.DEFAULT_APK_LOGO, "com.rovio.angrybirds", "2530", "2.5.3", "com.rovio.ka3d.App"),
				new PackageInfo("天天爱消除", Constancts.DEFAULT_APK_LOGO, "com.tencent.peng", "16", "1.0.9.0Build11", "com.tencent.peng.RedGame"),
				new PackageInfo("有道云笔记", Constancts.DEFAULT_APK_LOGO, "com.youdao.note", "30", "3.6.1", "com.youdao.note.activity2.SplashActivity"),
				new PackageInfo("YY语音斗地主", Constancts.DEFAULT_APK_LOGO, "com.yy.landlord", "1660", "1.0.1660", "com.yy.landlord.Landlord")
		);
	}

	public StringProperty labelProperty() {
		return label;
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
}
