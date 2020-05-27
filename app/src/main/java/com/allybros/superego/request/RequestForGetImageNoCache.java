package com.allybros.superego.request;


import android.content.Context;
import android.graphics.Bitmap;

import com.allybros.superego.util.NoCacheImageLoader;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;

/**
 * RequestForGetImage class defines the request to get image without cache from url
 * @author 0rcun
 */

public class RequestForGetImageNoCache {

    private static RequestForGetImageNoCache mInstance;
    private static Context mCtx;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;


    /**
     * Sets the variables needed
     */
    private RequestForGetImageNoCache(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();

        mImageLoader = new NoCacheImageLoader(mRequestQueue,    //No cache
                new ImageLoader.ImageCache() {
                    @Override
                    public Bitmap getBitmap(String url) {
                        return null;
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {}
                });
    }

    public static synchronized RequestForGetImageNoCache getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new RequestForGetImageNoCache(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            Cache cache = new DiskBasedCache(mCtx.getCacheDir(), 10 * 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache, network);
            // Don't forget to start the volley request queue
            mRequestQueue.start();
        }
        return mRequestQueue;
    }

    public ImageLoader getImageLoader() {
        return mImageLoader;
    }

}