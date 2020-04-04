package com.allybros.superego.api;


import android.content.Context;
import android.util.Log;

import com.allybros.superego.activity.SplashActivity;
import com.allybros.superego.util.FileUploadHelper;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class ImageChangeTask {
    private static String URL="https://api.allybros.com/superego/upload.php";

    public static void imageChangeTask(final String image, Context context){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("sego-FileUpload",response.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("E-sego-FileUpload",error.toString());
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                params.put("session-token", SplashActivity.session_token);
                params.put("new-avatar-base64",image);
                return params;
            }
        };
        FileUploadHelper.getInstance(context).addToRequestQue(stringRequest);
    }

}
