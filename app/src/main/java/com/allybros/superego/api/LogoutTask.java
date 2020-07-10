package com.allybros.superego.api;


import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

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

/**
 * Class includes the function of signing out function
 * @author 0rcun
 */
public class LogoutTask {
    /**
     * Function requests to server and then receives response. After that broadcasts the response.
     * @param context       required to build request and send Broadcast
     * @param session_token required for verify user
     */
    public static void logoutTask(final Context context, final String session_token) {
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest jsonRequest=new StringRequest(Request.Method.POST, ConstantValues.LOGOUT_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int status;
                Log.d("sego-Response-logout",response.toString());

                try {
                    JSONObject jsonObj=new JSONObject(response);
                    Log.d("sego-Response-logout",response.toString());
                    status = jsonObj.getInt("status");

                    Intent intent = new Intent(ConstantValues.ACTION_LOGOUT);
                    intent.putExtra("status", status);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, ErrorCodes.CONNECTION_ERROR, Toast.LENGTH_SHORT);

            }
        }) {
            //Add parameters in request
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("session-token", session_token);
                return params;
            }
        };


        queue.add(jsonRequest);

    }


}
