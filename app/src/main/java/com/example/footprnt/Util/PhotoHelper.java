package com.example.footprnt.Util;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;

public class PhotoHelper {

    public final String APP_TAG = "footprnt";

    public File getPhotoFileUri(Context context, String fileName) {
        File mediaStorageDir = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), APP_TAG);

        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
            Log.d(APP_TAG, "failed to create directory");
        }

        File file = new File(mediaStorageDir.getPath() + File.separator + fileName);
        return file;
    }
}
