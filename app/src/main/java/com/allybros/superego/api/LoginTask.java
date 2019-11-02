package com.allybros.superego.api;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.activity.LoginActivity;
import com.allybros.superego.activity.SplashActivity;
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

public class LoginTask extends Activity {





    public static void loginTask(final Context currentContext, final String uid, final String password) {

        final String LOGIN_URL ="https://api.allybros.com/superego/login.php";
        final String loginFailed= (String) currentContext.getString(R.string.loginFailed);
        final String loginSuccess= (String) currentContext.getString(R.string.loginSuccess);
        final String usernameEmpty= (String) currentContext.getString(R.string.usernameEmpty);
        final String passwordEmpty= (String) currentContext.getString(R.string.passwordEmpty);
        final String USER_INFORMATION_PREF="USER_INFORMATION_PREF";

        final Intent mainActivityIntent=new Intent(currentContext, LoginActivity.class);
        mainActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        RequestQueue queue = Volley.newRequestQueue(currentContext);

        StringRequest jsonRequest=new StringRequest(Request.Method.POST, LOGIN_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                String session_token;
                int status;

                try {
                    JSONObject jsonObj=new JSONObject(response);
                    status = jsonObj.getInt("status");
                    switch (status){
                        case ErrorCodes.SYSFAIL:
                            Log.d("sender", "Status Message");
                            Intent intent1 = new Intent("status-share");
                            intent1.putExtra("status", ErrorCodes.SYSFAIL);
                            LocalBroadcastManager.getInstance(currentContext).sendBroadcast(intent1);

                            break;

                        case ErrorCodes.SUCCESS:

                            SharedPreferences pref = currentContext.getSharedPreferences(USER_INFORMATION_PREF, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = pref.edit();

                            session_token=jsonObj.getString("session_token");
                            editor.putString("uid",uid);
                            editor.putString("password",password);
                            editor.putString("session_token", session_token);
                            editor.commit();

                            Intent intent=new Intent(currentContext, SplashActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            currentContext.startActivity(intent);
                            break;

                        case ErrorCodes.USERNAME_EMPTY:
                            Log.d("sender", "Status Message");
                            Intent intent2 = new Intent("status-share");
                            intent2.putExtra("status", ErrorCodes.USERNAME_EMPTY);
                            LocalBroadcastManager.getInstance(currentContext).sendBroadcast(intent2);
                            break;

                        case ErrorCodes.PASSWORD_EMPTY:
                            Log.d("sender", "Status Message");
                            Intent intent3 = new Intent("status-share");
                            intent3.putExtra("status", ErrorCodes.PASSWORD_EMPTY);
                            LocalBroadcastManager.getInstance(currentContext).sendBroadcast(intent3);

                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(currentContext,currentContext.getString(R.string.connection_error), Toast.LENGTH_SHORT);

            }
        }) {
            @Override
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("uid", uid);
                params.put("password", password);
                return params;
            }
        };


        queue.add(jsonRequest);

    }
}