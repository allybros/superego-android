package com.allybros.superego.api;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.unit.ConstantValues;
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

public class ChangeInfoTask extends Activity {

    public static void changeInfoTask(final Context context, final String new_uid, final String new_email, final String new_information, final String session_token){
        final Intent intent = new Intent(ConstantValues.getActionUpdateInformation());
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest jsonRequest=new StringRequest(Request.Method.POST, ConstantValues.getUpdateInformation(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("sego-Response",response.toString());
                try {
                    JSONObject jsonObj=new JSONObject(response);
                    int status = jsonObj.getInt("status");

                    switch (status){

                        case ErrorCodes.SUCCESS:
                            updateLocal(new_uid,new_email,new_information);
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
    private static void updateLocal(String new_uid, String new_email, String new_information){
        User.setUsername(new_uid);
        User.setUserBio(new_information);
        User.setEmail(new_email);
    }
}




