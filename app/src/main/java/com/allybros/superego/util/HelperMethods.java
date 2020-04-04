package com.allybros.superego.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.allybros.superego.R;
import com.android.volley.toolbox.ImageLoader;

import java.io.ByteArrayOutputStream;

public class HelperMethods {

    public static String imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte,Base64.DEFAULT);
    }

    public static void imageLoadFromUrl(Context context, String URL, CircledNetworkImageView imageView){
        Log.d("imageLoadFromUrl","Image Loading from: "+URL);
        ImageLoader mImageLoader;
        mImageLoader = CustomVolleyRequestQueue.getInstance(context).getImageLoader();
        mImageLoader.get(URL, ImageLoader.getImageListener(imageView, R.drawable.simple_profile_photo, android.R.drawable.ic_dialog_alert));
        imageView.setImageUrl(URL, mImageLoader);
    }
}
