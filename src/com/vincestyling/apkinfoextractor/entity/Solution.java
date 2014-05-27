package com.vincestyling.apkinfoextractor.entity;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.vincestyling.apkinfoextractor.utils.GlobalUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Solution {
	private long id;
	private String name;
	private String apksDirectory;
	private String createTime;
	public String extractFields;

	public Solution(String name, String apksDirectory, String extractFields) {
		this.name = name;
		this.apksDirectory = apksDirectory;
		this.extractFields = extractFields;
		this.createTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public String getNameSafety() {
		if (name == null || name.trim().isEmpty()) return createTime.replaceAll(" ", "").replaceAll("-", "").replaceAll(":", "");
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getApksDirectory() {
//		return apksDirectory;
		return "/Users/vince/server/apks";
	}

	public List<File> fetchValidFiles() {
		List<File> fileList = new LinkedList<File>();
		getApkFiles(new File(getApksDirectory()), fileList);
		return fileList;
	}

	private void getApkFiles(File dir, List<File> fileList) {
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				getApkFiles(file, fileList);
			} else if (file.getName().toLowerCase().endsWith(".apk")) {
				fileList.add(file);
			}
		}
	}

	public File initWorkingFolder() throws Exception {
		File workingFolder = getWorkdingFolder();
		if (workingFolder.exists()) {
			workingFolder.renameTo(new File(workingFolder + "_" + System.currentTimeMillis()));
		} else {
			workingFolder.mkdirs();
		}
		return workingFolder;
	}

	public File getWorkdingFolder() throws Exception {
		return new File(GlobalUtils.getWorkingPath(), getNameSafety());
	}

	public ObjectContainer getDBInstance() throws Exception {
		return Db4oEmbedded.openFile(getWorkdingFolder() + String.format("/solution_%d.db4o", id));
	}

	public void setApksDirectory(String apksDirectory) {
		this.apksDirectory = apksDirectory;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getExtractFields() {
		return extractFields;
	}

	public void setExtractFields(String extractFields) {
		this.extractFields = extractFields;
	}

	@Override
	public String toString() {
		return "Solution{" +
				"id=" + id +
				", name='" + name + '\'' +
				", apksDirectory='" + apksDirectory + '\'' +
				", createTime='" + createTime + '\'' +
				", extractFields='" + extractFields + '\'' +
				'}';
	}
}
