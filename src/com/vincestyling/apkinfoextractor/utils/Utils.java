package com.vincestyling.apkinfoextractor.utils;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.vincestyling.apkinfoextractor.entity.Solution;

import java.io.File;

public class Utils {

	public static String getWorkingPath() throws Exception {
		File workingDir = new File(System.getProperty("user.home"), Constancts.APP_NAME);
		if (!workingDir.exists() && !workingDir.mkdir())
			throw new IllegalStateException("Cannot create working directory in " + workingDir);
		return workingDir.getPath();
	}

	public static ObjectContainer getDatabase() throws Exception {
		String DB4OFILENAME = Utils.getWorkingPath() + "/data.db4o";
//		System.out.println("Database : " + DB4OFILENAME);
		return Db4oEmbedded.openFile(Db4oEmbedded.newConfiguration(), DB4OFILENAME);
	}

	public static boolean createSolution(Solution solution) {
		ObjectContainer db = null;
		try {
			db = Utils.getDatabase();
			db.store(solution);
			return true;
		} catch (Exception e) {
			return false;
		} finally {
			if (db != null) {
				db.close();
			}
		}
	}

}
