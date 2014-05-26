package com.vincestyling.apkinfoextractor.core;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractTest {
	public static final String FIELD_PATTERN = "(.[^']*)";

	public static void test() throws Exception {
		String output;
		Pattern patn;
		Matcher match;

		output = "application: label='SS Galaxy 11' icon='res/drawable/app_icon.png'";
		patn = Pattern.compile("application: label='" + FIELD_PATTERN + "'");
		match = patn.matcher(output);

//		output = "versionCode='1107' versionName='9.9.107' versionCode='1107'";
//		patn = Pattern.compile("versionName='" + FIELD_PATTERN + "'");
//		match = patn.matcher(output);

//		output = "package: name='com.yy.lockfun' versionCode='2' versionName='1.0'";
//		patn = Pattern.compile("versionCode='(.[^'^ ]*)'");
//		match = patn.matcher(output);

		if (match.find()) {
			System.out.println("---" + match.group(1) + "---" + match.groupCount());
		} else {
			System.out.println("Not Match!");
		}
	}

	public static void main(String[] args) throws Exception {
//		test();
		run();
	}

	public static void run() throws Exception {
		File[] files = new File("/Users/vince/server/apks").listFiles();
		assert files != null;

		for (File file : files) {
			if (!file.getAbsolutePath().contains("Autonavi_V73.apk")) continue;
			System.out.println("\n\n>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
			System.out.println(file.getAbsolutePath());

			InputStream ins = Runtime.getRuntime().exec("/Users/vince/dev/android-sdk/build-tools/19.0.3/aapt dump badging " + file.getAbsolutePath()).getInputStream();
			String output = new String(toByteArray(ins));

			System.out.println(output + "\n-------------------------------");
			PackageInfo info = new PackageInfo();

			Pattern patn = Pattern.compile("application: label='" + FIELD_PATTERN + "'");
			Matcher match = patn.matcher(output);
			if (match.find()) {
				info.label = match.group(1).trim();
			}
			System.out.println("label : " + info.label);


			patn = Pattern.compile("package: name='" + FIELD_PATTERN + "'");
			match = patn.matcher(output);
			if (match.find()) {
				info.pkg = match.group(1).trim();
			}
			System.out.println("pkg : " + info.pkg);


			patn = Pattern.compile("versionCode='" + FIELD_PATTERN + "'");
			match = patn.matcher(output);
			if (match.find()) {
				info.versionCode = match.group(1).trim();
			}
			System.out.println("versionCode : " + info.versionCode);

			patn = Pattern.compile("versionName='" + FIELD_PATTERN + "'");
			match = patn.matcher(output);
			if (match.find()) {
				info.versionName = match.group(1).trim();
			}
			System.out.println("versionName : " + info.versionName);


			patn = Pattern.compile("launchable-activity: name='" + FIELD_PATTERN + "'");
			match = patn.matcher(output);
			if (match.find()) {
				info.launchActivity = match.group(1).trim();
			}
			System.out.println("launch : " + info.launchActivity);
		}
	}

	public static class PackageInfo {
		private String label;
		private String icon = "";
		private String pkg;
		private String versionCode;
		private String versionName;
		private String launchActivity;
	}

	public static byte[] toByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024 * 4];
		int readSize;
		while ((readSize = in.read(buffer)) >= 0) {
			result.write(buffer, 0, readSize);
		}
		result.flush();
		in.close();
		return result.toByteArray();
	}

}

