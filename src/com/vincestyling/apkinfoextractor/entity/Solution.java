package com.vincestyling.apkinfoextractor.entity;

import java.io.File;
import java.io.FilenameFilter;
import java.text.SimpleDateFormat;
import java.util.Date;

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

	public void setName(String name) {
		this.name = name;
	}

	public String getApksDirectory() {
//		return apksDirectory;
		return "/Users/vince/server/apks";
	}

	public int getTotalFiles() {
		return getApkFiles().length;
	}

	public File[] getApkFiles() {
		return new File(getApksDirectory()).listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().trim().endsWith(".apk");
			}
		});
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
