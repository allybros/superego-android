package com.allybros.superego.api;


import android.content.Context;
import android.util.Log;

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

public class google {
    public static void loginTask(final Context currentContext, final String authenticator, final String access_token) {

        final String LOGIN_URL ="https://api.allybros.com/superego/oauth-client.php";
        RequestQueue queue = Volley.newRequestQueue(currentContext);
        StringRequest jsonRequest=new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObj=new JSONObject(response);
                    Log.d("sego-Response-Go",response.toString());
                    String session_token = jsonObj.getString("session_token");
                    int status = jsonObj.getInt("status");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("EROOR","ELOROEO");
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
