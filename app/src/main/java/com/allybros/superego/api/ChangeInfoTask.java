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
 * Class includes the function of changing users profile information
 * @author 0rcun
 */
public class ChangeInfoTask extends Activity {
    /**
     * User informations change request to API
     * @param context           required to build request and send Broadcast
     * @param new_uid           required to change old username
     * @param new_email         required to change old mail
     * @param new_information   required to change old information
     * @param session_token     required to verify the user
     */
    public static void changeInfoTask(final Context context, final String new_uid, final String new_email, final String new_information, final String session_token){
        final Intent intent = new Intent(ConstantValues.ACTION_UPDATE_INFORMATION);
        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest jsonRequest=new StringRequest(Request.Method.POST, ConstantValues.UPDATE_INFORMATION, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("sego-Response", response);
                try {
                    JSONObject jsonObj=new JSONObject(response);
                    int status = jsonObj.getInt("status");

                    if (status == ErrorCodes.SUCCESS) {
                        updateLocal(new_uid, new_email, new_information);
                    }
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
                params.put("session-token", session_token);
                params.put("new-username",new_uid);
                params.put("new-email", new_email);
                params.put("new-user-bio",new_information );
                return params;
            }
        };
        queue.add(jsonRequest);
    }

    /**
     * Change variables in local storage
     *
     * @param new_uid           required to verify the user
     * @param new_email         required to verify the user
     * @param new_information   required to verify the user
     */
    private static void updateLocal(String new_uid, String new_email, String new_information){
        SessionManager.getInstance().getUser().setUsername(new_uid);
        SessionManager.getInstance().getUser().setUserBio(new_information);
        SessionManager.getInstance().getUser().setEmail(new_email);
    }
}




