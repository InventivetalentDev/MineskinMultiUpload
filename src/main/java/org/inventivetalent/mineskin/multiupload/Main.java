package org.inventivetalent.mineskin.multiupload;

import java.io.File;
import java.io.IOException;

public class Main {

	public static void main(String[] args) throws IOException {
		File imageDir = new File(args[0]);

		if (!imageDir.isDirectory()) {
			System.out.println(imageDir + " is not a directory");
			System.exit(-1);
			return;
		}

		Uploader uploader = new Uploader(imageDir);

		for (File file : imageDir.listFiles()) {
			if (file.isFile()) {
				if (file.getName().endsWith(".png")) {
					String strippedExtension = file.getName().replace(".png", "");
					File jsonOutputFile = new File(imageDir, strippedExtension + ".json");
					if (jsonOutputFile.exists()) {// No need to generate again
						continue;
					}

					uploader.uploadQueue.add(file);
				}
			}
		}

		uploader.generateNext();
	}

}
