package com.allybros.superego.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.activity.LoginActivity;
import com.allybros.superego.unit.Api;
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

public class RegisterTask {
    public static void registerTask (final Context currentContext, final String username, final String email, final String password, final boolean agreement){

        final String loginFailed= (String) currentContext.getString(R.string.loginFailed);
        final String loginSuccess= (String) currentContext.getString(R.string.loginSuccess);
        final String usernameEmpty= (String) currentContext.getString(R.string.usernameEmpty);
        final String passwordEmpty= (String) currentContext.getString(R.string.passwordEmpty);
        final String emailEmpty= (String) currentContext.getString(R.string.emailEmpty);
        final String usernameNotLegal=(String) currentContext.getString(R.string.usernameNotLegal);
        final String usernameAlreadyExist=(String) currentContext.getString(R.string.usernameAlreadyExist);
        final String emailAlreadyExist=(String) currentContext.getString(R.string.emailAlreadyExist);
        final String emailNotLegal=(String) currentContext.getString(R.string.emailNotLegal);
        final String passwordNotLegal=(String) currentContext.getString(R.string.passwordNotLegal);
        final String successRegister=(String) currentContext.getString(R.string.successRegister);
        final String REGISTER_URL ="https://api.allybros.com/superego/register.php";


        RequestQueue queue = Volley.newRequestQueue(currentContext);
        Log.d("LOGG","LOGG");

        final StringRequest jsonRequest=new StringRequest(Request.Method.POST, REGISTER_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.d("Response",response.toString());
                int status;
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    status = jsonObj.getInt("status");

                    switch (status) {
                        case ErrorCodes.SYSFAIL:
//                            Toast.makeText(currentContext, loginFailed, Toast.LENGTH_SHORT).show();
                            Intent intent1 = new Intent("register-status-share");
                            intent1.putExtra("status", ErrorCodes.SYSFAIL);
                            LocalBroadcastManager.getInstance(currentContext).sendBroadcast(intent1);
                            break;

                        case ErrorCodes.USERNAME_NOT_LEGAL:
//                            Toast.makeText(currentContext, usernameNotLegal, Toast.LENGTH_SHORT).show();
                            Intent intent2 = new Intent("register-status-share");
                            intent2.putExtra("status", ErrorCodes.USERNAME_NOT_LEGAL);
                            LocalBroadcastManager.getInstance(currentContext).sendBroadcast(intent2);
                            break;

                        case ErrorCodes.USERNAME_ALREADY_EXIST:
//                            Toast.makeText(currentContext, usernameAlreadyExist, Toast.LENGTH_SHORT).show();
                            Intent intent3 = new Intent("register-status-share");
                            intent3.putExtra("status", ErrorCodes.USERNAME_ALREADY_EXIST);
                            LocalBroadcastManager.getInstance(currentContext).sendBroadcast(intent3);
                            break;

                        case ErrorCodes.EMAIL_ALREADY_EXIST:
//                            Toast.makeText(currentContext, emailAlreadyExist, Toast.LENGTH_SHORT).show();
                            Intent intent4 = new Intent("register-status-share");
                            intent4.putExtra("status", ErrorCodes.EMAIL_ALREADY_EXIST);
                            LocalBroadcastManager.getInstance(currentContext).sendBroadcast(intent4);
                            break;

                        case ErrorCodes.EMAIL_NOT_LEGAL:
//                            Toast.makeText(currentContext, emailNotLegal, Toast.LENGTH_SHORT).show();
                            Intent intent5 = new Intent("register-status-share");
                            intent5.putExtra("status", ErrorCodes.EMAIL_NOT_LEGAL);
                            LocalBroadcastManager.getInstance(currentContext).sendBroadcast(intent5);
                            break;

                        case ErrorCodes.PASSWORD_NOT_LEGAL:
//                            Toast.makeText(currentContext, passwordNotLegal, Toast.LENGTH_SHORT).show();
                            Intent intent6 = new Intent("register-status-share");
                            intent6.putExtra("status", ErrorCodes.PASSWORD_NOT_LEGAL);
                            LocalBroadcastManager.getInstance(currentContext).sendBroadcast(intent6);
                            break;

                        case ErrorCodes.SUCCESS:
//                            Toast.makeText(currentContext, successRegister, Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(currentContext, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            currentContext.startActivity(intent);
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
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);
                params.put("conditions", String.valueOf(agreement));
                params.put("g-recaptcha-response", Api.getRECAPTCHA_SKIP());
                return params;
            }
        };
        queue.add(jsonRequest);
    }
}


