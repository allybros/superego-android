package com.allybros.superego.api;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import com.allybros.superego.activity.AddTestActivity;
import com.allybros.superego.activity.LoginActivity;
import com.allybros.superego.activity.SplashActivity;
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

    public static void addTestTask(final Context currentContext) {

        final String ADD_TEST_URL ="https://demo.allybros.com/superego/create.php";

        final String USER_INFORMATION_PREF="USER_INFORMATION_PREF";


        final Intent mainActivityIntent=new Intent(currentContext, LoginActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        RequestQueue queue = Volley.newRequestQueue(currentContext);

        StringRequest jsonRequest=new StringRequest(Request.Method.POST, ADD_TEST_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                AddTestActivity.addPageHTML=response;
                Log.d("site",response.toString());
                Intent addTestIntent= new Intent(currentContext,AddTestActivity.class);
                currentContext.startActivity(addTestIntent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(currentContext,"ERROR",Toast.LENGTH_SHORT).show();

            }

        }) {
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("session-token", SplashActivity.session_token);
                Log.d("sessionTokenAddTest",SplashActivity.session_token);
                return params;
            }
        };


        queue.add(jsonRequest);

    }
}