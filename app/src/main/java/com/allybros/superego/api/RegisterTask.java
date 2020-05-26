package com.allybros.superego.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.activity.LoginActivity;
import com.allybros.superego.unit.ConstantValues;
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
/**
 * Class includes the function of registering user
 * @author 0rcun
 */
public class RegisterTask {
    /**
     * The function sends a request to register with information and then receives response. After that broadcasts the response.
     * @param context   required to build request and send Broadcast
     * @param username  required to register user
     * @param email     required to register user
     * @param password  required to register user
     * @param agreement required to register user
     */
    public static void registerTask (final Context context, final String username, final String email, final String password, final boolean agreement){
        RequestQueue queue = Volley.newRequestQueue(context);
        final Intent intent = new Intent(ConstantValues.ACTION_REGISTER);

        final StringRequest jsonRequest=new StringRequest(Request.Method.POST, ConstantValues.REGISTER, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response",response.toString());
                int status;
                try {
                    JSONObject jsonObj = new JSONObject(response);
                    status = jsonObj.getInt("status");
                    intent.putExtra("status", status);
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
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
            protected Map<String,String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", username);
                params.put("email", email);
                params.put("password", password);
                params.put("conditions", String.valueOf(agreement));
                params.put("g-recaptcha-response", ConstantValues.RECAPTCHA_SKIP);
                return params;
            }
        };
        queue.add(jsonRequest);
    }
}


