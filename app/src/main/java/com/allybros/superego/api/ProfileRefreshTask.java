package com.allybros.superego.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.unit.Trait;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileRefreshTask{
    public static final String USER_INFORMATION_PREF="USER_INFORMATION_PREF";


    final private static String PROFILE_REFRESH_TASK ="https://api.allybros.com/superego/load-profile.php";

    public static void profileRefreshTask(final Context currentContext, final String session_token){

        RequestQueue queue = Volley.newRequestQueue(currentContext);

        final StringRequest jsonRequest=new StringRequest(Request.Method.POST, PROFILE_REFRESH_TASK, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response_RefreshProfile",response.toString());
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    int status=jsonObject.getInt("status");
                    switch (status){
                        case ErrorCodes.SYSFAIL:
                            Intent intent = new Intent("profile-refresh-status-share");
                            intent.putExtra("status", ErrorCodes.SYSFAIL);
                            LocalBroadcastManager.getInstance(currentContext).sendBroadcast(intent);
                            break;

                        case ErrorCodes.SUCCESS:
                            int user_type = jsonObject.getInt("user_type");
                            String username = jsonObject.getString("username");
                            String user_bio = jsonObject.getString("user_bio");
                            String test_id = jsonObject.getString("test_id");
                            String email = jsonObject.getString("email");
                            String image = jsonObject.getString("avatar");
                            int rated = jsonObject.getInt("rated");
                            int credit = jsonObject.getInt("credit");

                            ArrayList<Trait> traits=new ArrayList<>();
                            if(!jsonObject.isNull("scores")){
                                for (int i = 0; i < jsonObject.getJSONArray("scores").length(); i++) {
                                    JSONObject iter= (JSONObject) jsonObject.getJSONArray("scores").get(i);
                                    int traitNo;
                                    float value;

                                    traitNo=iter.getInt("traitNo");
                                    value=iter.getInt("value");

                                    traits.add(new Trait(traitNo,value));
                                }
                            }
                            User.setRated(rated);
                            User.setUserType(user_type);
                            User.setUsername(username);
                            User.setUserBio(user_bio);
                            User.setEmail(email);
                            User.setTestId(test_id);
                            User.setScores(traits);
                            User.setCredit(credit);
                            User.setImage(image);

                            Intent intent1 = new Intent("profile-refresh-status-share");
                            intent1.putExtra("status", ErrorCodes.SUCCESS);
                            LocalBroadcastManager.getInstance(currentContext).sendBroadcast(intent1);
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
                return params;
            }
        };
        queue.add(jsonRequest);
    }
}