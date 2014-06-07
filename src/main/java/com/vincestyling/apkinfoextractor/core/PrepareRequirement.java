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
		byte[] aaptBytes = GlobalUtil.toBytes(Main.class.getResourceAsStream('/' + Constancts.CORE_AAPT_NAME));
		System.out.println("Releasing aapt length : " + aaptBytes.length);

		File aaptCmdFile = new File(GlobalUtil.getWorkingPath(), Constancts.CORE_AAPT_NAME);
		FileOutputStream fos = new FileOutputStream(aaptCmdFile);
		fos.write(aaptBytes);
		fos.close();

		// When run in windows, it will be trouble, ignore this exception.
		try {
			Runtime.getRuntime().exec("chmod +x " + aaptCmdFile);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

}
