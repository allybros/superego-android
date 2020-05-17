package com.allybros.superego.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.allybros.superego.activity.WebViewActivity;
import com.allybros.superego.activity.LoginActivity;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.util.SessionManager;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class AddTestTask extends Activity {

    public static void addTestTask(final Context context) {
        final Intent mainActivityIntent=new Intent(context, LoginActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest jsonRequest=new StringRequest(Request.Method.POST, ConstantValues.getAddTestUrl(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Intent addTestIntent= new Intent(context, WebViewActivity.class);
                context.startActivity(addTestIntent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,"ERROR",Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("session-token", SessionManager.getInstance().getSessionToken());
                //Log.d("sessionTokenAddTest",SplashActivity.session_token);
                return params;
            }
        };
        queue.add(jsonRequest);
    }
}