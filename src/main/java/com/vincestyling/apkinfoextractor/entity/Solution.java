package com.vincestyling.apkinfoextractor.entity;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.vincestyling.apkinfoextractor.utils.Constancts;
import com.vincestyling.apkinfoextractor.utils.GlobalUtil;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Solution implements Serializable {
	private long id;
	private String name;
	private String apksDirectory;
	private String createTime;
	public String extractFields;

	private List<ResultDataProvider> resultList = new LinkedList<ResultDataProvider>();

	public Solution() {}

	public Solution(String name, String apksDirectory, String extractFields) {
		setApksDirectory(apksDirectory);
		setExtractFields(extractFields);
		setCreateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		setName(name);
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

	public String generateOutputFileName() {
		StringBuilder fileName = new StringBuilder();
		fileName.append(name == null || name.isEmpty() ? Constancts.APP_NAME : name).append('_');
		fileName.append(new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(new Date()));
		return fileName.toString();
	}

	public String getNameSafety() {
		if (name == null || name.isEmpty()) return createTime.replaceAll(" ", "").replaceAll("-", "").replaceAll(":", "");
		return name;
	}

	public void setName(String name) {
		this.name = name;
		this.name = getNameSafety();
	}

	public String getApksDirectory() {
		return apksDirectory;
	}

	public List<File> fetchValidFiles() {
		List<File> fileList = new LinkedList<>();
		getApkFiles(new File(getApksDirectory()), fileList);
		return fileList;
	}

	private void getApkFiles(File dir, List<File> fileList) {
		File[] files = dir.listFiles();
		for (File file : files) {
			if (file.isHidden()) continue;
			if (file.isDirectory()) {
				getApkFiles(file, fileList);
			} else if (file.getName().toLowerCase().endsWith(".apk")) {
				fileList.add(file);
			}
		}
	}

	public File initWorkingFolder() throws Exception {
		File workingFolder = getWorkingFolder();
		if (workingFolder.exists()) {
			workingFolder.renameTo(new File(workingFolder + "_old"));
		} else {
			workingFolder.mkdirs();
		}
		return workingFolder;
	}

	public File getWorkingFolder() throws Exception {
		return new File(GlobalUtil.getWorkingPath(), getNameSafety());
	}

	public ObjectContainer getDBInstance() throws Exception {
		return Db4oEmbedded.openFile(getDBFileName());
	}

	public String getDBFileName() throws Exception {
		return getWorkingFolder() + String.format("/solution_%d.db4o", id);
	}

	public File getDBFile() throws Exception {
		return new File(getDBFileName());
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

	public int getResultCount() {
		return resultList.size();
	}

	public void addResult(ResultDataProvider provider) {
		resultList.add(provider);
	}

	public List<ResultDataProvider> getResultList() {
		return resultList;
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
