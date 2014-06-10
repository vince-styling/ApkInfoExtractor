package com.vincestyling.apkinfoextractor.utils;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.vincestyling.apkinfoextractor.entity.Solution;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class GlobalUtil {

	public static String getWorkingPath() throws Exception {
		try {
			File workingParentDir = new File(System.getProperty("user.home"));
			return createWorkingDir(workingParentDir);
		} catch (Exception e) {
			try {
				File workingParentDir = new File(System.getProperty("user.dir"));
				return createWorkingDir(workingParentDir);
			} catch (Exception e1) {
				File workingParentDir = null;
				try {
					String codeSourcePath = GlobalUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
					workingParentDir = new File(codeSourcePath).getParentFile();
					return createWorkingDir(workingParentDir);
				} catch (Exception e2) {
					throw new IllegalStateException("Cannot create working parent directory in " + workingParentDir);
				}
			}
		}
	}

	private static String createWorkingDir(File workingParentDir) throws Exception {
		File workingDir = new File(workingParentDir, Constancts.APP_NAME);
		if (!workingDir.exists()) {
			boolean isCreated = workingDir.mkdir();
			if (!isCreated) {
				throw new IllegalStateException("Cannot create working directory in " + workingDir);
			}
			System.out.println("Working directory already create : " + workingDir);
		}
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
		if (isLinuxOS()) {
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
		else if (isWindowsOS()) {
			Runtime.getRuntime().exec(String.format("explorer /select, \"%s\"", outputFile));
		}
		else if (isUnixOS()) {
			Runtime.getRuntime().exec("open " + outputFile.getParentFile());
		}
	}

	public static boolean isLinuxOS() {
		String osName = System.getProperty("os.name").toLowerCase();
		return osName.contains("linux");
	}

	public static boolean isWindowsOS() {
		String osName = System.getProperty("os.name").toLowerCase();
		return osName.contains("windows");
	}

	public static boolean isUnixOS() {
		String osName = System.getProperty("os.name").toLowerCase();
		return osName.contains("mac") || osName.contains("unix");
	}

	public static void extractRes(File file, String resPath) {
		byte[] buffer = new byte[1024 * 6];
		ZipInputStream zis = null;
		ZipEntry ze;
		try {
			zis = new ZipInputStream(new FileInputStream(file));
			File tempDir = new File(GlobalUtil.getTempWorkingPath());

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

	public static String toString(InputStream in) throws IOException {
		return new String(toBytes(in), Constancts.DEFAULT_CHARSET);
	}

	public static byte[] toBytes(InputStream in) throws IOException {
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
				List<Solution> solutionList = new ArrayList<>(list.size());
				for (Solution solution : list) {
					solutionList.add(solution);
				}

				Collections.sort(solutionList, new Comparator<Solution>() {
					@Override
					public int compare(Solution left, Solution right) {
						return left.getId() > right.getId() ? -1 : 0;
					}
				});

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
