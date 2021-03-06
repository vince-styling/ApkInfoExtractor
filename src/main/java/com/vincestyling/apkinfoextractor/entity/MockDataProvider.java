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

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MockDataProvider {
	private StringProperty label;
	private StringProperty pkg;
	private StringProperty versionCode;
	private StringProperty versionName;
	private StringProperty launchActivity;

	private MockDataProvider(String label, String pkg, String versionCode, String versionName, String launchActivity) {
		this.label = new SimpleStringProperty(label);
		this.pkg = new SimpleStringProperty(pkg);
		this.versionCode = new SimpleStringProperty(versionCode);
		this.versionName = new SimpleStringProperty(versionName);
		this.launchActivity = new SimpleStringProperty(launchActivity);
	}

	public static ObservableList<MockDataProvider> buildSampleDatas() {
		return FXCollections.observableArrayList(
				new MockDataProvider("AutoNavi", "com.autonavi.xmgd.navigator", "1028", "7.3.8803.1698", "com.autonavi.xmgd.navigator.Warn"),
				new MockDataProvider("百度音乐", "com.ting.mp3.android", "45", "4.4.2", "com.baidu.music.ui.splash.SplashActivity"),
				new MockDataProvider("嘀嘀打车", "com.sdu.didi.psnger", "40", "2.7", "com.sdu.didi.psnger.MainActivity"),
				new MockDataProvider("FBReader", "org.geometerplus.zlibrary.ui.android", "109042", "1.9.4", "org.geometerplus.android.fbreader.FBReader"),
				new MockDataProvider("植物大战僵尸2高清版", "com.popcap.pvz2cthdyyh", "110", "1.1.0", "com.popcap.SexyApp.SexyAppActivity"),
				new MockDataProvider("愤怒的小鸟", "com.rovio.angrybirds", "2530", "2.5.3", "com.rovio.ka3d.App"),
				new MockDataProvider("天天爱消除", "com.tencent.peng", "16", "1.0.9.0Build11", "com.tencent.peng.RedGame"),
				new MockDataProvider("Evernote", "com.evernote.world", "1057105", "5.7.1", "com.evernote.ui.HomeActivity"),
				new MockDataProvider("有道云笔记", "com.youdao.note", "30", "3.6.1", "com.youdao.note.activity2.SplashActivity")
		);
	}

	public StringProperty labelProperty() {
		return label;
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
