package net.hydrotekz.BlowableObsidians.util;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Random;

public class Util {

	public static void copyUrlToFile(URL url, File dest) throws Exception {
		try (InputStream in = url.openStream()) {
			Files.copy(in, dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
	}

	public static int getRandomNumber(int start, int stop){
		Random r = new Random();
		int Low = start;
		int High = stop;
		int R = r.nextInt(High-Low) + Low;
		return R;
	}
}