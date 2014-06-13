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
package com.vincestyling.apkinfoextractor.core;

import com.vincestyling.apkinfoextractor.Main;
import com.vincestyling.apkinfoextractor.utils.Constancts;
import com.vincestyling.apkinfoextractor.utils.GlobalUtil;

import java.io.File;
import java.io.FileOutputStream;

public class PrepareRequirement extends Thread {
	@Override
	public void run() {
		try {
			releaseAapt();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void releaseAapt() throws Exception {
		String aaptSuffix;

		if (GlobalUtil.isWindowsOS()) {
			aaptSuffix = "4win";
		} else if (GlobalUtil.isUnixOS()) {
			aaptSuffix = "4unix";
		} else if (GlobalUtil.isLinuxOS()) {
			aaptSuffix = "4linux";
		} else return;

		String aaptPath = "/aapts/" + Constancts.CORE_AAPT_NAME + aaptSuffix;
		byte[] aaptBytes = GlobalUtil.toBytes(Main.class.getResourceAsStream(aaptPath));

		System.out.println("Releasing aapt " + aaptPath + " length : " + aaptBytes.length);

		File aaptCmdFile = new File(GlobalUtil.getWorkingPath(), Constancts.CORE_AAPT_NAME);
		FileOutputStream fos = new FileOutputStream(aaptCmdFile);
		fos.write(aaptBytes);
		fos.close();

		if (!GlobalUtil.isWindowsOS()) {
			try {
				new ProcessBuilder("chmod", "+x", aaptCmdFile.getPath()).start();
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}

		System.out.println("Released aapt at " + aaptCmdFile);
	}

}
