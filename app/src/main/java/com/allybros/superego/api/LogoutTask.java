package com.allybros.superego.api;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.allybros.superego.R;
import com.allybros.superego.activity.LoginActivity;
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

public class LogoutTask {

    public static void logoutTask(final Context currentContext, final String session_token) {

        final String LOGIN_URL ="https://api.allybros.com/superego/logout.php";
        final String logoutSuccesful= (String) currentContext.getString(R.string.logoutSuccesful);
        final String sysFail=(String)currentContext.getString(R.string.connection_error);

        final Intent mainActivityIntent=new Intent(currentContext, LoginActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        RequestQueue queue = Volley.newRequestQueue(currentContext);

        StringRequest jsonRequest=new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int status;

                try {
                    JSONObject jsonObj=new JSONObject(response);
                    status = jsonObj.getInt("status");
                    switch (status){
                        case ErrorCodes.SYSFAIL:
                            Toast.makeText(currentContext, sysFail, Toast.LENGTH_SHORT).show();
                            break;

                        case ErrorCodes.SUCCESS:

                            Toast.makeText(currentContext, logoutSuccesful,Toast.LENGTH_SHORT).show();
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(currentContext,sysFail, Toast.LENGTH_SHORT);

            }
        }) {
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
