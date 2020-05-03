package com.allybros.superego.api;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class GoogleRegisterTask {
    public static void loginTask(final Context context, final String access_token) {

        final Intent intent = new Intent(ConstantValues.getActionGoogleLogin());

        RequestQueue queue = Volley.newRequestQueue(context);
        StringRequest jsonRequest=new StringRequest(Request.Method.POST, ConstantValues.getUrlSocialAccountsLogin(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObj=new JSONObject(response);
                    Log.d("sego-Response-Go",response.toString());
                    int status = jsonObj.getInt("status");

                    switch (status){

                        case ErrorCodes.SUCCESS:
                            String session_token = jsonObj.getString("session_token");
                            SharedPreferences pref = context.getSharedPreferences(ConstantValues.getUserInformationPref(), Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString("session_token", session_token);
                            editor.commit();

                            Log.d("sessionTokenLogin",session_token);

                            intent.putExtra("status", status);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            break;

                        default:
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
                intent.putExtra("status", ErrorCodes.CONNECTION_ERROR);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        }) {
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("authenticator", "google");
                params.put("access_token", access_token);
                return params;
            }
        };


        queue.add(jsonRequest);

    }
}
