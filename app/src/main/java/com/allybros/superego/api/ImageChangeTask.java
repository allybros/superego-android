package com.allybros.superego.api;


import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.activity.SplashActivity;
import com.allybros.superego.unit.Api;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.util.FileUploadHelper;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ImageChangeTask {

    private static String URL="https://api.allybros.com/superego/upload.php";

    public static void imageChangeTask(final String image, final Context context){
        final Intent intent = new Intent(Api.getActionUpdateImage());
        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("sego-FileUpload",response.toString());

                try {
                    JSONObject jsonObject=new JSONObject(response);
                    int status=jsonObject.getInt("status");
                    switch (status){

                        case ErrorCodes.SYSFAIL:
                            intent.putExtra("status", status);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            break;

                        case ErrorCodes.SUCCESS:
                            intent.putExtra("status", status);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            break;

                        case ErrorCodes.INVALID_FILE_EXTENSION:
                            intent.putExtra("status", status);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            break;

                        case ErrorCodes.INVALID_FILE_TYPE:
                            intent.putExtra("status", status);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            break;

                        case ErrorCodes.INVALID_FILE_SIZE:
                            intent.putExtra("status", status);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            break;

                        case ErrorCodes.SESSION_EXPIRED:
                            intent.putExtra("status", status);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            break;

                        case ErrorCodes.FILE_WRITE_ERROR:
                            intent.putExtra("status", status);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            break;
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("E-sego-FileUpload",error.toString());
                intent.putExtra("status", ErrorCodes.SYSFAIL);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
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
