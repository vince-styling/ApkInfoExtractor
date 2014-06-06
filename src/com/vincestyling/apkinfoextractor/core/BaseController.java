package com.vincestyling.apkinfoextractor.core;

import com.vincestyling.apkinfoextractor.Main;
import javafx.fxml.Initializable;
import javafx.scene.layout.Pane;

public abstract class BaseController extends Pane implements Initializable, OnResizeListener {
	protected Main application;

	protected double stableWidth;
	protected double stableHeight;

	@Override
	public void initSize(int width, int height) {
		stableHeight = height;
		stableWidth = width;
	}

	@Override
	public void onWidthResize(Number oldWidth, Number newWidth) {
//		System.out.println("oldWidth : " + oldWidth + " newWidth : " + newWidth);
		if (newWidth.doubleValue() > stableWidth) {
			widthResizeUp(newWidth.doubleValue());
		} else {
			widthResizeDown(newWidth.doubleValue());
		}
	}

	protected void widthResizeUp(double newWidth) {
	}

	protected void widthResizeDown(double newWidth) {
	}

	@Override
	public void onHeightResize(Number oldHeight, Number newHeight) {
//		System.out.println("oldHeight : " + oldHeight + " newHeight : " + newHeight);
		if (newHeight.doubleValue() > stableHeight) {
			heightResizeUp(newHeight.doubleValue());
		} else {
			heightResizeDown(newHeight.doubleValue());
		}
	}

	protected void heightResizeUp(double newHeight) {
	}

	protected void heightResizeDown(double newHeight) {
	}
}
