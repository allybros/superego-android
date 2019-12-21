package com.allybros.superego.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.activity.SplashActivity;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.unit.User;
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

public class NameFindTask extends Activity {

    public static void nameFindTask(final Context currentContext) {

        final String FIND_NAME_URL ="https://api.allybros.com/superego/a.php";

        final String USER_INFORMATION_PREF="USER_INFORMATION_PREF";

        RequestQueue queue = Volley.newRequestQueue(currentContext);


        StringRequest jsonRequest=new StringRequest(Request.Method.POST, FIND_NAME_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject= null;
                try {
                    Log.d("LOG-->",response);
                    jsonObject = new JSONObject(response);
                    User.setImage(jsonObject.getString("message"));

                    Intent intent1 = new Intent("status-profilImageName");
                    LocalBroadcastManager.getInstance(currentContext).sendBroadcast(intent1);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(currentContext,"ERROR",Toast.LENGTH_SHORT).show();
                Log.d("ERRORR","ERRORR");

            }

        }) {
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("session-token", SplashActivity.session_token);
                params.put("username", User.getUsername());

                Log.d("sessionTokenAddTest",SplashActivity.session_token);
                Log.d("UsernameAddTest",User.getUsername());
                return params;
            }
        };


        queue.add(jsonRequest);

    }
}