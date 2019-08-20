package com.allybros.superego.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;
import com.allybros.superego.R;
import com.allybros.superego.activity.LoginActivity;
import com.allybros.superego.activity.SplashActivity;
import com.allybros.superego.activity.UserPageActivity;
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

//TODO:api paketindeki sınıfların metotları arasında bir iletişim yapılmalı.
public class LoginTask extends Activity {
    final private static String LOGIN_URL ="https://api.allybros.com/superego/login.php";

    public static final int LOGIN_FAILED=0;
    public static final int LOGIN_SUCCESS=10;
    public static final int USERNAME_EMPTY=20;
    public static final int PASSWORD_EMPTY=26;

    public static void loginTask(final Context currentContext, final String uid, final String password) {

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
                Log.d("Response_Login",response);

                String session_token;
                int status;

                try {
                    JSONObject jsonObj=new JSONObject(response);
                    status = jsonObj.getInt("status");
                    Log.d("LoginTask-status", String.valueOf(status));
                    switch (status){
                        case LOGIN_FAILED:
                            Toast.makeText(currentContext, loginFailed, Toast.LENGTH_SHORT).show();
                            currentContext.startActivity(mainActivityIntent);
                            break;

                        case LOGIN_SUCCESS:

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

                        case USERNAME_EMPTY:

                            Toast.makeText(currentContext, usernameEmpty, Toast.LENGTH_SHORT).show();
                            currentContext.startActivity(mainActivityIntent);
                            break;

                        case PASSWORD_EMPTY:

                            Toast.makeText(currentContext, passwordEmpty, Toast.LENGTH_SHORT).show();
                            currentContext.startActivity(mainActivityIntent);
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
                params.put("login-submit", "True");
                params.put("uid", uid);
                params.put("password", password);
                return params;
            }
        };


        queue.add(jsonRequest);

    }
}