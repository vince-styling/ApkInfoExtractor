/*
 * Copyright 2014 Vince Styling
 * https://github.com/vince-styling/ApkInfoExtractor
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.vincestyling.apkinfoextractor.core.ctrls;

import com.vincestyling.apkinfoextractor.Main;
import com.vincestyling.apkinfoextractor.core.ctrls.OnResizeListener;
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
