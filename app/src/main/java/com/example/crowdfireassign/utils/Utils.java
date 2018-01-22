package com.example.crowdfireassign.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.util.Random;

/**
 * Created by chitra on 18/1/18.
 */

public class Utils {

    public Bitmap getBitmap(Context context, Uri fileUri){
        Bitmap bitmap = null;
        try {
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;
            bitmap = BitmapFactory.decodeFile(fileUri.getPath(),
                    options);

            if(bitmap == null){
                bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), fileUri);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            }

            return bitmap;
        } catch (Exception ae){
            ae.printStackTrace();
        }
        return bitmap;
    }

    public int randInt(int max){
        return (int) (Math.random() * max);
    }
}
