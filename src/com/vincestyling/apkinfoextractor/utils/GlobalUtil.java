package com.vincestyling.apkinfoextractor.utils;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.vincestyling.apkinfoextractor.entity.Solution;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GlobalUtil {

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

	public static void openOutputDirectory(File outputFile) throws Exception {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("mac")) {
			Runtime.getRuntime().exec("open " + outputFile.getParentFile());
		}
		else if (osName.contains("ubuntu") || osName.contains("linux")) {
			try {
				Runtime.getRuntime().exec("nautilus " + outputFile);
			} catch (IOException e) {
				try {
					Runtime.getRuntime().exec("xdg-open " + outputFile.getParentFile());
				} catch (IOException e1) {
					try {
						Runtime.getRuntime().exec("gnome-open " + outputFile.getParentFile());
					} catch (IOException e2) {
						throw  e2;
					}
				}
			}
		}
		else if (osName.contains("windows")) {
			Runtime.getRuntime().exec(String.format("explorer /select, \"%s\"", outputFile));
		}
	}

	public static String toString(InputStream in) throws IOException {
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024 * 4];
		int readSize;
		while ((readSize = in.read(buffer)) >= 0) {
			result.write(buffer, 0, readSize);
		}
		result.flush();
		in.close();
		return new String(result.toByteArray());
	}

	public static ObjectContainer getGlobalDatabase() throws Exception {
		String DB4OFILENAME = getWorkingPath() + "/data.db4o";
		return Db4oEmbedded.openFile(DB4OFILENAME);
	}

	public static long createSolution(Solution solution) {
		ObjectContainer db = null;
		try {
			db = getGlobalDatabase();
			db.store(solution);
			long id = db.ext().getObjectInfo(solution).getInternalID();
			solution.setId(id);
			db.store(solution);
			return id;
		} catch (Exception e) {
			return 0;
		} finally {
			if (db != null) db.close();
		}
	}

	public static List<Solution> getRecentSolutions() {
		ObjectContainer db = null;
		try {
			db = getGlobalDatabase();
			List<Solution> list = db.queryByExample(Solution.class);
			if (list != null && list.size() > 0) {
				List<Solution> solutionList = new ArrayList<Solution>(list.size());
				for (Solution solution : list) {
					solutionList.add(solution);
				}
				return solutionList;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (db != null) db.close();
		}
		return Collections.EMPTY_LIST;
	}

}
