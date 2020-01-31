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

public class ChangeInfoTask extends Activity {





    public static void changeInfoTask(final Context currentContext, final String new_uid,final String new_email,final String new_information, final String session_token) {

        final String CHANGE_URL ="https://api.allybros.com/superego/edit-profile.php";
        final String USER_INFORMATION_PREF="USER_INFORMATION_PREF";

        RequestQueue queue = Volley.newRequestQueue(currentContext);
        SharedPreferences pref = currentContext.getSharedPreferences(USER_INFORMATION_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        StringRequest jsonRequest=new StringRequest(Request.Method.POST, CHANGE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                int status;

                try {
                    Log.d("sego-Response",response.toString());
                    JSONObject jsonObj=new JSONObject(response);
                    status = jsonObj.getInt("status");
                    switch (status){
                        case ErrorCodes.SESSION_EXPIRED:
                            Log.d("changeInfoTaskFail",response.toString());
                            Toast.makeText(currentContext,"Lütfen tekrar giriş yapınız",Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(currentContext,LoginActivity.class);
                            currentContext.startActivity(intent);
                            break;

                        case ErrorCodes.USERNAME_NOT_LEGAL:
                            Log.d("changeInfoTaskFail",response.toString());
                            Toast.makeText(currentContext,R.string.usernameNotLegal,Toast.LENGTH_SHORT).show();
                            break;
                        case ErrorCodes.USERNAME_ALREADY_EXIST:
                            Log.d("changeInfoTaskFail",response.toString());
                            Toast.makeText(currentContext,R.string.usernameAlreadyExist,Toast.LENGTH_SHORT).show();
                            break;

                        case ErrorCodes.EMAIL_NOT_LEGAL:
                            Log.d("changeInfoTaskFail",response.toString());
                            Toast.makeText(currentContext,R.string.emailNotLegal,Toast.LENGTH_SHORT).show();
                            break;

                        case ErrorCodes.EMAIL_ALREADY_EXIST:
                            Log.d("changeInfoTaskFail",response.toString());
                            Toast.makeText(currentContext,R.string.emailAlreadyExist,Toast.LENGTH_SHORT).show();
                            break;

                        case ErrorCodes.SUCCESS:
                            Log.d("changeInfoTaskSucces",response.toString());
                            Intent intent1=new Intent(currentContext, SplashActivity.class);
                            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            currentContext.startActivity(intent1);
                            break;

                        case ErrorCodes.SYSFAIL:
                            Log.d("changeInfoTaskFail",response.toString());
                            Toast.makeText(currentContext, R.string.pleaseTryAgain,Toast.LENGTH_SHORT).show();
                            Intent intent2=new Intent(currentContext, UserPageActivity.class);
                            currentContext.startActivity(intent2);
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
                params.put("session-token", session_token);
                params.put("new-username",new_uid);
                params.put("new-email", new_email);
                params.put("new-user-bio",new_information );
                return params;
            }
        };


        queue.add(jsonRequest);

    }

}