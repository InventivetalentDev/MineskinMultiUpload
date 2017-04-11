package org.inventivetalent.mineskin.multiupload;

import com.google.gson.Gson;
import org.mineskin.MineskinClient;
import org.mineskin.SkinOptions;
import org.mineskin.data.Skin;
import org.mineskin.data.SkinCallback;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;

public class Uploader {

	final File imageDir;

	ArrayList<File> uploadQueue = new ArrayList<>();

	MineskinClient mineskinClient = new MineskinClient(Executors.newSingleThreadExecutor(), "MineskinMultiUpload");

	Uploader(File imageDir) {
		this.imageDir = imageDir;
	}

	void generateNext() {
		if (uploadQueue.isEmpty()) {
			System.out.println("Done!");
			System.exit(0);
			return;
		}
		File file = uploadQueue.remove(0);
		String strippedExtension = file.getName().replace(".png", "");
		File jsonOutputFile = new File(imageDir, strippedExtension + ".json");

		System.out.println("Uploading " + strippedExtension + "...");
		mineskinClient.generateUpload(file, SkinOptions.name(strippedExtension), new SkinCallback() {
			public void done(Skin skin) {
				String jsonSkin = new Gson().toJson(skin);
				try {
					jsonOutputFile.createNewFile();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				try (BufferedWriter writer = new BufferedWriter(new FileWriter(jsonOutputFile))) {
					writer.write(jsonSkin);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				generateNext();
			}

			@Override
			public void error(String errorMessage) {
				System.out.println("Error: " + errorMessage);

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				generateNext();
			}

			@Override
			public void exception(Exception exception) {
				System.out.println("Exception: ");
				exception.printStackTrace();

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				generateNext();
			}

			@Override
			public void parseException(Exception exception, String body) {
				System.out.println("Parse Exception: ");
				exception.printStackTrace();

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				generateNext();
			}
		});
	}

}
