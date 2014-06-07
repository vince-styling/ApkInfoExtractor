package com.vincestyling.apkinfoextractor.core.ctrls;

public interface OnResizeListener {
	void initSize(int width, int height);
	void onWidthResize(Number oldWidth, Number newWidth);
	void onHeightResize(Number oldHeight, Number newHeight);
}
