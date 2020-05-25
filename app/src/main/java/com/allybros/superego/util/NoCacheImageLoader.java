package com.allybros.superego.util;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
/**
 * Provide to be imageloder that is no cache
 * @author 0rcun
 */
public class NoCacheImageLoader extends ImageLoader {
    public NoCacheImageLoader(RequestQueue queue, ImageCache imageCache) {
        super(queue, imageCache);
    }

    @Override
    protected Request<Bitmap> makeImageRequest(String requestUrl, int maxWidth, int maxHeight, ImageView.ScaleType scaleType, String cacheKey) {
        Request<Bitmap> request =  super.makeImageRequest(requestUrl, maxWidth, maxHeight, scaleType, cacheKey);
        // key method
        request.setShouldCache(false);
        return request;
    }
}
