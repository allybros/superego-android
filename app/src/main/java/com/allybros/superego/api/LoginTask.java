package com.allybros.superego.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.util.SessionManager;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 *  Class includes the function of signing in function
 * @author 0rcun
 */
public class LoginTask {
    /**
     * Function requests to server and then receives response. After that broadcasts the response.
     * @param context   required to build request and send Broadcast
     * @param uid       required for verify user
     * @param password  required for verify user
     */
    public static void loginTask(final Context context, final String uid, final String password,
                                 final String gRecaptchaResponse) {
        final Intent intent = new Intent(ConstantValues.ACTION_LOGIN);
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest jsonRequest = new StringRequest(Request.Method.POST, ConstantValues.LOGIN_URL,
            response -> {
                int status;
                try {
                    JSONObject jsonObj=new JSONObject(response);
                    Log.d("Login Task Response", response);
                    status = jsonObj.getInt("status");
                    switch (status){
                        case ErrorCodes.SUCCESS:
                            String sessionToken = jsonObj.getString("session_token");
                            SessionManager.getInstance().setSessionToken(sessionToken);
                            SessionManager.getInstance().writeInfoLocalStorage(uid, password, sessionToken, context);
                            intent.putExtra("status", status);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            break;
                        default:
                            intent.putExtra("status", status);
                            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }, error -> {
                intent.putExtra("status", ErrorCodes.CONNECTION_ERROR);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }) {
            //Add parameters in request
            @Override
            protected Map<String,String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("uid", uid);
                params.put("password", password);
                params.put("g-recaptcha-response", gRecaptchaResponse);
                params.put("g-recaptcha-site-key", context.getString(R.string.recaptcha_client_key));
                JSONObject jsonParams = new JSONObject(params);
                Log.i("LoginTask", "Request: " + jsonParams);
                return params;
            }
        };

        queue.add(jsonRequest);

    }
}