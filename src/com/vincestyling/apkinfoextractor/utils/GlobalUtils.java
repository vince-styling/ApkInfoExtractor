package com.vincestyling.apkinfoextractor.utils;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.vincestyling.apkinfoextractor.entity.Solution;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class GlobalUtils {

	public static String getWorkingPath() throws Exception {
		File workingDir = new File(System.getProperty("user.home"), Constancts.APP_NAME);
		if (!workingDir.exists() && !workingDir.mkdir())
			throw new IllegalStateException("Cannot create working directory in " + workingDir);
		return workingDir.getPath();
	}

	public static String getTempWorkingPath() throws Exception {
		File tempDir = new File(getWorkingPath(), "temp");
		if (!tempDir.exists() && !tempDir.mkdir())
			throw new IllegalStateException("Cannot create temp directory in " + tempDir);
		return tempDir.getPath();
	}

	public static boolean deleteDirectory(File dir) {
		// why should I rename before delete : http://stackoverflow.com/a/11776458/1294681
		File newDir = new File(dir.getAbsolutePath() + System.currentTimeMillis());
		dir.renameTo(newDir);

		File[] bookFiles = newDir.listFiles();
		for (File bookFile : bookFiles) {
			if (bookFile.isDirectory()) deleteDirectory(bookFile);
			bookFile.delete();
		}
		return newDir.delete();
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

	public static ObjectContainer getGlobalDatabase() throws Exception {
		String DB4OFILENAME = GlobalUtils.getWorkingPath() + "/data.db4o";
		return Db4oEmbedded.openFile(DB4OFILENAME);
	}

	public static long createSolution(Solution solution) {
		ObjectContainer db = null;
		try {
			db = GlobalUtils.getGlobalDatabase();
			db.store(solution);
			return db.ext().getObjectInfo(solution).getInternalID();
		} catch (Exception e) {
			return 0;
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

}
