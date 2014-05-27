package com.vincestyling.apkinfoextractor.core;

import com.vincestyling.apkinfoextractor.entity.ApkInfo;

public interface ApkHandleCallback {
	void callback(ApkInfo apkInfo, int totalCount, int successCount);
}
