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
 * Class includes the function of registering user
 * @author 0rcun
 */
public class RegisterTask {
    private static final String REGISTER_TASK = "RegisterTask";
    private static final String FIELD_STATUS = "status";
    private static final String FIELD_SESSION_TOKEN = "session_token";

    private RegisterTask() {}

    /**
     * The function sends a request to register with information and then receives response. After that broadcasts the response.
     * @param context   required to build request and send Broadcast
     * @param username  required to register user
     * @param email     required to register user
     * @param password  required to register user
     * @param agreement required to register user
     */
    public static void registerTask (final Context context, final String username,
                                     final String email, final String password,
                                     final boolean agreement, final String recaptchaResponse){
        RequestQueue queue = Volley.newRequestQueue(context);
        final Intent intent = new Intent(ConstantValues.ACTION_REGISTER);
        final StringRequest jsonRequest = new StringRequest(Request.Method.POST, ConstantValues.REGISTER, response -> {
            // Handle Response
            Log.d("Response", response);
            int status = 0;
            try {
                // Decode response
                JSONObject jsonObj = new JSONObject(response);
                status = jsonObj.getInt(FIELD_STATUS);
                String sessionToken = jsonObj.getString(FIELD_SESSION_TOKEN);
                // Save session information
                Log.i(REGISTER_TASK, "Creating session from register api response");
                SessionManager.getInstance().setSessionToken(sessionToken);
                SessionManager.getInstance().writeInfoLocalStorage(username, password, sessionToken, context);

            } catch (JSONException e) {
                Log.e(REGISTER_TASK, "Unable to decode API response");
            }
            // Send broadcast
            intent.putExtra(FIELD_STATUS, status);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
        }, error -> {
            intent.putExtra(FIELD_STATUS, ErrorCodes.CONNECTION_ERROR);
            LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            Log.e(REGISTER_TASK, "An error occurred while sending the request");
        }) {
            //Add parameters in request
            @Override
            protected Map<String,String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);
                params.put("conditions", String.valueOf(agreement));
                params.put("g-recaptcha-response", recaptchaResponse);
                params.put("g-recaptcha-site-key", context.getString(R.string.recaptcha_client_key));
                return params;
            }
        };
        queue.add(jsonRequest);
    }
}


