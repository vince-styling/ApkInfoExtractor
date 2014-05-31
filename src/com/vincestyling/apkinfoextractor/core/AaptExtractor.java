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
	public static final String CMD = "/Users/vince/dev/android-sdk/build-tools/19.0.3/aapt dump badging %s";
	public static final String FIELD_PATTERN = "(.[^']*)";

	private int successCount;
	private Solution solution;
	private boolean isCancelled;
	private ApkHandleCallback callback;

	public AaptExtractor(Solution solution, ApkHandleCallback callback) {
		super();
		this.callback = callback;
		this.solution = solution;
		setPriority(Thread.NORM_PRIORITY);
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
			InputStream ins = Runtime.getRuntime().exec(String.format(CMD, file.getPath())).getInputStream();
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

		for (String icon : apkInfo.getIcons()) {
			UnZip.extractRes(file, icon);
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

	public static void main(String[] args) throws Exception {
		Solution solution = new Solution("temp", "", "icon,label,package,versionCode,versionName,launchActivity");
		File file = new File("/Users/vince/server/apks/11renzuqiuxianfengTheMatch_StrikerSoccerG11_V1.0.1_mumayi_69988.apk");
		File solutionWorkingDir = new File("/Users/vince/ApkInfoExtractor");
		ApkInfo apkInfo = new ApkInfo(100, file.getName());
		new AaptExtractor(solution, null).extract(apkInfo, file, solutionWorkingDir);
		System.out.println(apkInfo);
	}

}
