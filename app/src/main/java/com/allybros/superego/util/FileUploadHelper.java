package com.allybros.superego.util;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class FileUploadHelper{

    private RequestQueue mRequestQueue;
    private static FileUploadHelper mInstance;
    private static Context mContext;

    private FileUploadHelper(Context context){
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized FileUploadHelper getInstance(Context context) {
        if(mInstance == null){
            mInstance = new FileUploadHelper(context);
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
