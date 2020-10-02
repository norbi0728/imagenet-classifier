package com.example.imageclassifierclient.utility;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import static android.app.Activity.RESULT_OK;

public class PhotoUtility {

	private PackageManager pm;
	private File externalFilesDir;
	private AppCompatActivity activity;
	private Uri photoURI;
	private final static int REQUEST_TAKE_PHOTO = 1;

	public PhotoUtility(PackageManager pm, File externalFilesDir, AppCompatActivity activity) {
		this.pm = pm;
		this.externalFilesDir = externalFilesDir;
		this.activity = activity;
	}

	public void startImageCapture() {
		/*https://developer.android.com/training/camera/photobasics#java*/
		Intent captureImageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (captureImageIntent.resolveActivity(pm) != null) {
			File photoFile = null;
			try {
				photoFile = createImageFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			if (photoFile != null) {
				photoURI = FileProvider.getUriForFile(activity,
						"com.example.android.fileprovider",
						photoFile);
				captureImageIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
				activity.startActivityForResult(captureImageIntent, REQUEST_TAKE_PHOTO);
			}
		}
	}

	public File getResultImage(int requestCode, int resultCode) {
		File imageToPredict = null;
		if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {

			String fileName = getFileNameFromUri(photoURI);
			imageToPredict = new File(externalFilesDir, fileName);
		}

		return imageToPredict;
	}

	private String getFileNameFromUri(Uri path) {
		int cutIdx = Objects.requireNonNull(path.getPath()).lastIndexOf('/');

		return path.getPath().substring(cutIdx + 1);
	}

	/*https://developer.android.com/training/camera/photobasics#java*/
	private File createImageFile() throws IOException {
		String imageFileName = "JPEG__";
		File storageDir = externalFilesDir;

		return File.createTempFile(
				imageFileName,  /* prefix */
				".jpg",         /* suffix */
				storageDir      /* directory */
		);
	}
}
