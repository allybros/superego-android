package com.allybros.superego.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.icu.util.Calendar;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.allybros.superego.R;
import com.allybros.superego.ui.CircledNetworkImageView;
import com.android.volley.toolbox.ImageLoader;

import java.io.ByteArrayOutputStream;
import java.util.Date;

public class HelperMethods {

    public static String imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte,Base64.DEFAULT);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void imageLoadFromUrl(Context context, String URL, CircledNetworkImageView imageView){
        URL = URL+"&"+HelperMethods.getDate();
        Log.d("imageLoadFromUrl","Image Loading from: "+URL);
        ImageLoader mImageLoader;
        mImageLoader = CustomVolleyRequestQueue.getInstance(context).getImageLoader();
        mImageLoader.get(URL, ImageLoader.getImageListener(imageView, R.drawable.simple_profile_photo, android.R.drawable.ic_dialog_alert));
        imageView.setImageUrl(URL, mImageLoader);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static String getDate(){
        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
        String formattedDate = df.format(c);
        System.out.println("Formatted => " + formattedDate);
        return formattedDate;
    }
}
