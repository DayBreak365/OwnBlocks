package me.breakofday.ownblocks.utils;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUtil {

	private static final Logger logger = Logger.getLogger(FileUtil.class.getName());
	private static final File mainDirectory = new File("plugins/OwnBlocks");

	static {
		if (!mainDirectory.exists()) {
			mainDirectory.mkdir();
		}
	}

	public static File createFile(String name) {
		File file = new File(mainDirectory.getPath() + "/" + name);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				logger.log(Level.SEVERE, file.getPath() + " 파일을 만드는 도중 오류가 발생하였습니다.");
			}
		}
		return file;
	}

}
