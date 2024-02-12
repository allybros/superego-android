package com.allybros.superego.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.util.SessionManager;
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

/**
 * Class includes the function of changing password
 *
 * @author orcunkamiloglu
 */
public class PasswordChangeTask extends Activity {
    /**
     * Password change request to API
     *
     * @param context       required to build request and send Broadcast
     * @param session_token required to verify the user
     * @param old_password  required to verify the user
     * @param new_password  required to change old password
     */
    public static void passwordChangeTask(final Context context, final String session_token, final String old_password, final String new_password) {
        final Intent intent = new Intent(ConstantValues.ACTION_PASSWORD_CHANGE);
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest jsonRequest = new StringRequest(Request.Method.POST, ConstantValues.PASSWORD_CHANGE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("sego-Response", response.toString());
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    int status = jsonObj.getInt("status");

                    switch (status) {

                        case ErrorCodes.SUCCESS:
                            SessionManager.getInstance().updateCredentials(new_password, context);
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
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                intent.putExtra("status", ErrorCodes.CONNECTION_ERROR);
                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
            }
        }) {
            //Add parameters in request
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("session-token", session_token);
                params.put("old-password", old_password);
                params.put("new-password", new_password);
                params.put("new-password-again", new_password);
                return params;
            }
        };
        queue.add(jsonRequest);
    }
}
