package com.allybros.superego.oauth;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

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
import java.util.Objects;

public class TwitterCallbackTask {

    private TwitterCallbackTask() {}

    private static final String STATUS = "status";

    /**
     * Twitter callback task, sends twitter callback data to Sego API, and creates the user session
     * @param context context
     * @param code twitter callback code
     * @param challenge twitter challenge id
     */
    public static void handleTwitterCallback(Context context, String code, String challenge) {
        final RequestQueue queue = Volley.newRequestQueue(context);
        final Intent intent = new Intent(ConstantValues.ACTION_SOCIAL_MEDIA_LOGIN);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ConstantValues.URL_SOCIAL_ACCOUNTS_LOGIN,
            response -> {
                Log.d("TwitterOAuthResponse", response);
                int status = ErrorCodes.SYSFAIL;
                JSONObject jsonObj;
                try {
                    jsonObj = new JSONObject(response);
                    status = jsonObj.getInt(STATUS);
                    if (status == ErrorCodes.SUCCESS) {
                        String sessionToken = jsonObj.getString("session_token");
                        SessionManager sessionManager = SessionManager.getInstance();
                        sessionManager.writeInfoLocalStorage(sessionManager.getUserId(),
                                sessionManager.getPassword(), sessionToken, context);
                    }
                } catch (JSONException e) {
                    Log.e("TwitterCallbackTask", "Failed to extract session token from oauth response");
                }

                // Send broadcast
                intent.putExtra(STATUS, status);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            },
            error -> {
                Log.e("TwitterOAuthError", Objects.requireNonNull(error.getMessage()));
                // Send broadcast
                intent.putExtra(STATUS, ErrorCodes.CONNECTION_ERROR);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("authenticator", "twitter");
                params.put("code", code);
                params.put("challenge", challenge);
                return params;
            }
        };

        queue.add(stringRequest);

    }

}
