package com.allybros.superego.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;

import com.allybros.superego.R;
import com.allybros.superego.request.RequestForGetImageNoCache;
import com.allybros.superego.request.RequestForGetImageWithCache;
import com.allybros.superego.ui.CircledNetworkImageView;
import com.android.volley.toolbox.ImageLoader;

import java.io.ByteArrayOutputStream;

/**
 * HelperMethods contains some methods for projects
 * @author 0rcun
 */

public class HelperMethods {

    /**
     * Class creating blocked
     */
    private HelperMethods(){}

    /**
     * Converts from bitmap to string
     * @param bitmap    to be converted bitmap
     * @return          string form bitmap
     */
    public static String imageToString(Bitmap bitmap){
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgByte = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgByte,Base64.DEFAULT);
    }
    /**
     * Load image from url without cache
     * @param context       require to use requst method
     * @param imageView     to be filled imageView
     * @param URL           which contains the image
     */
    public static void imageLoadFromUrlNoCache(Context context, String URL, CircledNetworkImageView imageView){
        Log.d("imageLoadFromUrl","Image Loading from: "+URL);
        ImageLoader mImageLoader;
        mImageLoader = RequestForGetImageNoCache.getInstance(context).getImageLoader();
        mImageLoader.get(URL, ImageLoader.getImageListener(imageView, R.drawable.default_avatar, android.R.drawable.ic_dialog_alert));
        imageView.setImageUrl(URL, mImageLoader);
    }

    /**
     * Load image from url with cache
     * @param context
     * @param URL
     * @param imageView
     */
    public static void imageLoadFromUrlCache(Context context, String URL, CircledNetworkImageView imageView){
        Log.d("imageLoadFromUrl","Image Loading from: "+URL);
        ImageLoader mImageLoader;
        mImageLoader = RequestForGetImageWithCache.getInstance(context).getImageLoader();
        mImageLoader.get(URL, ImageLoader.getImageListener(imageView, R.drawable.default_avatar, android.R.drawable.ic_dialog_alert));
        imageView.setImageUrl(URL, mImageLoader);
    }
}
