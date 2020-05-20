package com.allybros.superego.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * RequestForUploadImage class defines the request to upload image to url
 *
 * @author 0rcun
 */

public class RequestForUploadImage {

    private RequestQueue mRequestQueue;
    private static RequestForUploadImage mInstance;
    private static Context mContext;

    /**
     * Sets the variables needed
     */
    private RequestForUploadImage(Context context){
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized RequestForUploadImage getInstance(Context context) {
        if(mInstance == null){
            mInstance = new RequestForUploadImage(context);
        }

        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }

        return mRequestQueue;
    }

    public<T> void addToRequestQue(Request<T> request){
        getRequestQueue().add(request);
    }
}
