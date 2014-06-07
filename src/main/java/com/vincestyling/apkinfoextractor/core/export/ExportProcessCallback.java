package com.vincestyling.apkinfoextractor.core.export;

import java.io.File;

public interface ExportProcessCallback {
	void onProcessSuccess(File outputFile);
	void onProcessFailure(Exception e);
}
