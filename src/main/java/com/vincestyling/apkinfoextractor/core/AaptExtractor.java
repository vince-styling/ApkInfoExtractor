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
package com.vincestyling.apkinfoextractor.core;

import com.db4o.ObjectContainer;
import com.vincestyling.apkinfoextractor.entity.ApkInfo;
import com.vincestyling.apkinfoextractor.entity.Solution;
import com.vincestyling.apkinfoextractor.utils.Constancts;
import com.vincestyling.apkinfoextractor.utils.GlobalUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AaptExtractor extends Thread {
	private static final String FIELD_PATTERN = "(.[^']*)";
	private String aaptCommand;

	private int successCount;
	private Solution solution;
	private boolean isCancelled;
	private ApkHandleCallback callback;

	public AaptExtractor(Solution solution, ApkHandleCallback callback) {
		super();
		this.callback = callback;
		this.solution = solution;
	}

	@Override
	public void run() {
		try {
			extractAll();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void cancel() {
		isCancelled = true;
	}

	private void extractAll() throws Exception {
		File aaptCmdFile = new File(GlobalUtil.getWorkingPath(), Constancts.CORE_AAPT_NAME);
		if (aaptCmdFile.length() == 0 || !aaptCmdFile.canExecute()) {
			throw new IllegalStateException(aaptCmdFile + " was invalid!");
		} else {
			aaptCommand = aaptCmdFile.getPath();
		}

		AtomicInteger idGenerator = new AtomicInteger(new Random(System.currentTimeMillis()).nextInt(2222));
		File workingFolder = solution.initWorkingFolder();
		ObjectContainer db = solution.getDBInstance();
		try {
			List<File> fileList = solution.fetchValidFiles();
			for (File file : fileList) {
				if (isCancelled) return;
				ApkInfo apkInfo = new ApkInfo(idGenerator.incrementAndGet(), file.getName());
				boolean result = extract(apkInfo, file, workingFolder);
				db.store(apkInfo);

				callback.callback(apkInfo, fileList.size(), result ? ++successCount : successCount);
			}
		} finally {
			db.close();
		}
	}

	private boolean extract(ApkInfo apkInfo, File file, File workingFolder) throws Exception {
		try {
			ProcessBuilder pb = new ProcessBuilder(aaptCommand, "dump", "badging", file.getPath());
			InputStream ins = pb.start().getInputStream();
			String output = GlobalUtil.toString(ins);

			if (solution.getExtractFields().contains(Constancts.ICON)) {
				extractIcon(apkInfo, file, workingFolder, output);
			}

			if (solution.getExtractFields().contains(Constancts.LABEL)) {
				extractLabel(apkInfo, output);
			}

			if (solution.getExtractFields().contains(Constancts.PACKAGE)) {
				extractPackage(apkInfo, output);
			}

			if (solution.getExtractFields().contains(Constancts.VERSIONNAME)) {
				extractVersionName(apkInfo, output);
			}

			if (solution.getExtractFields().contains(Constancts.VERSIONCODE)) {
				extractVersionCode(apkInfo, output);
			}

			if (solution.getExtractFields().contains(Constancts.LAUNCHACTIVITY)) {
				extractLaunchActivity(apkInfo, output);
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	private void extractVersionCode(ApkInfo apkInfo, String output) {
		Pattern patn = Pattern.compile("versionCode='" + FIELD_PATTERN + "'");
		Matcher match = patn.matcher(output);
		if (match.find()) {
			apkInfo.setVersionCode(match.group(1).trim());
		}
	}

	private void extractVersionName(ApkInfo apkInfo, String output) {
		Pattern patn = Pattern.compile("versionName='" + FIELD_PATTERN + "'");
		Matcher match = patn.matcher(output);
		if (match.find()) {
			apkInfo.setVersionName(match.group(1).trim());
		}
	}

	private void extractLaunchActivity(ApkInfo apkInfo, String output) {
		Pattern patn = Pattern.compile("launchable-activity: name='" + FIELD_PATTERN + "'");
		Matcher match = patn.matcher(output);
		if (match.find()) {
			apkInfo.setLaunchActivity(match.group(1).trim());
		}
	}

	private void extractPackage(ApkInfo apkInfo, String output) {
		Pattern patn = Pattern.compile("package: name='" + FIELD_PATTERN + "'");
		Matcher match = patn.matcher(output);
		if (match.find()) {
			apkInfo.setPackage(match.group(1).trim());
		}
	}

	private void extractLabel(ApkInfo apkInfo, String output) {
		Pattern patn = Pattern.compile("application: label='" + FIELD_PATTERN + "'");
		Matcher match = patn.matcher(output);
		if (match.find()) {
			apkInfo.addLabel(match.group(1).trim());
		}

		patn = Pattern.compile("application-label" + FIELD_PATTERN + ":'" + FIELD_PATTERN + "'");
		match = patn.matcher(output);
		while (match.find()) {
			apkInfo.addLabel(match.group(2).trim());
		}

		if (apkInfo.getLabels() != null && apkInfo.getLabels().size() == 1) {
			apkInfo.clearLabels();
		}
	}

	private void extractIcon(ApkInfo apkInfo, File file, File workingFolder, String output) throws Exception {
		Pattern patn = Pattern.compile("icon='" + FIELD_PATTERN + "'");
		Matcher match = patn.matcher(output);
		if (match.find()) {
			apkInfo.addIcon(match.group(1).trim());
		}

		patn = Pattern.compile("application-icon" + FIELD_PATTERN + ":'" + FIELD_PATTERN + "'");
		match = patn.matcher(output);
		while (match.find()) {
			apkInfo.addIcon(match.group(2).trim());
		}

		if (apkInfo.getIcons() != null) {
			for (String icon : apkInfo.getIcons()) {
				GlobalUtil.extractRes(file, icon);
			}
		}

		int maxWidth = 0;
		File tempDir = new File(GlobalUtil.getTempWorkingPath());
		for (File iconFile : tempDir.listFiles()) {
			BufferedImage bimg = ImageIO.read(iconFile);
			int width = bimg.getWidth();

			if (width == Constancts.PREFER_ICON_DIMS) {
				apkInfo.setIcon(iconFile.getPath());
				maxWidth = width;
				break;
			}

			if (width > maxWidth) {
				apkInfo.setIcon(iconFile.getPath());
				maxWidth = width;
			}
		}

		File iconFile = new File(apkInfo.getIcon());
		File targetFile = new File(workingFolder, String.format("ic_%d.png", apkInfo.getId()));
		if (maxWidth == Constancts.PREFER_ICON_DIMS) {
			iconFile.renameTo(targetFile);
		} else {
			Image newImage = ImageIO.read(iconFile).getScaledInstance(Constancts.PREFER_ICON_DIMS, Constancts.PREFER_ICON_DIMS, Image.SCALE_SMOOTH);
			BufferedImage newIconBufImg = new BufferedImage(Constancts.PREFER_ICON_DIMS, Constancts.PREFER_ICON_DIMS, BufferedImage.TYPE_INT_ARGB);
			newIconBufImg.getGraphics().drawImage(newImage, 0, 0 , null);
			ImageIO.write(newIconBufImg, "png", new FileOutputStream(targetFile));
		}

		apkInfo.setIcon(targetFile.getPath());
		GlobalUtil.deleteDirectory(tempDir);
		apkInfo.clearIcons();
	}
}
