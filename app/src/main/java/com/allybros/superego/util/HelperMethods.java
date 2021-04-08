package com.allybros.superego.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.allybros.superego.R;
import com.allybros.superego.request.RequestForGetImageNoCache;
import com.allybros.superego.request.RequestForGetImageWithCache;
import com.android.volley.toolbox.ImageLoader;

import java.io.ByteArrayOutputStream;

/**
 * HelperMethods contains some methods for projects
 * @author 0rcun
 */

public class HelperMethods {

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
     * Sets views margin
     * @param v
     * @param l
     * @param t
     * @param r
     * @param b
     */
    public static void setMargins (View v, int l, int t, int r, int b) {
        if (v.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            p.setMargins(l, t, r, b);
            v.requestLayout();
        }
    }
}
