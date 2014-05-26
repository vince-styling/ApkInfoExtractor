package com.vincestyling.apkinfoextractor.core;

import com.vincestyling.apkinfoextractor.utils.GlobalUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class UnZip {

	public static void main(String[] args) {
		String rootPath = "/Users/vince/server/apks/";
//		extractRes(new File(rootPath + ""kugouyinle_V6.3.1_mumayi_9037f.apk"), "res/drawable-xxhdpi/icon.png");
		extractRes(new File(rootPath + "com.evernote.world.yinxiang.AM_571.all.451.apk"), "res/drawable-xxhdpi-v4/ic_launcher.png");
	}

	public static void extractRes(File file, String resPath) {
		byte[] buffer = new byte[1024 * 6];
		ZipInputStream zis = null;
		ZipEntry ze;
		try {
			zis = new ZipInputStream(new FileInputStream(file));
			File tempDir = new File(GlobalUtils.getTempWorkingPath());

			while ((ze = zis.getNextEntry()) != null) {
				String entryName = ze.getName();

				if (entryName.equals(resPath)) {
					File newFile = new File(tempDir, System.currentTimeMillis() + ".png");
//					System.out.println("file unzip : " + newFile.getAbsoluteFile());

					FileOutputStream fos = new FileOutputStream(newFile);

					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}

					fos.close();
					return;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (zis != null) try {
				zis.closeEntry();
				zis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
