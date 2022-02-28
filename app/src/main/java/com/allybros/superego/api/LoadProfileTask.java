package com.allybros.superego.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.unit.Score;
import com.allybros.superego.unit.User;
import com.allybros.superego.util.SessionManager;
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

/**
 * Class includes the function of getting information from API
 * @author 0rcun
 */
public class LoadProfileTask{
    private static Intent intent;
    /**
     * Load and refresh process requests to API
     *
     * Function requests to server and then receives response. After that broadcasts the response.
     * @param context           required to build request and send Broadcast
     * @param session_token     required to verify the user
     * @param action            required to define type of broadcast
     *                          If call for loadProfile process define action parameter "load" else call ConstantValues.getActionRefreshProfile()
     */
    public static void loadProfileTask(final Context context, final String session_token , final String action){
        // Create intent
        intent = new Intent(action);
        RequestQueue queue = Volley.newRequestQueue(context);

        final StringRequest jsonRequest=new StringRequest(Request.Method.POST, ConstantValues.LOAD_PROFILE, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Load Profile Response", response);
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    int status = jsonObject.getInt("status");

                    if (status == ErrorCodes.SUCCESS) {
                        // Build user object
                        int user_type = jsonObject.getInt("user_type");
                        String username = jsonObject.getString("username");
                        String user_bio = jsonObject.getString("user_bio");
                        String test_id = jsonObject.getString("test_id");
                        String test_result_id = jsonObject.getString("test_result_id");
                        String email = jsonObject.getString("email");
                        String image = jsonObject.getString("avatar");
                        int rated = jsonObject.getInt("rated");
                        int credit = jsonObject.getInt("credit");

                        //Build scores list
                        ArrayList<Score> scoresList = new ArrayList<>();
                        if (!jsonObject.isNull("scores")) {
                            for (int i = 0; i < jsonObject.getJSONArray("scores").length(); i++) {
                                JSONObject scoresObject = (JSONObject) jsonObject.getJSONArray("scores").get(i);
                                int traitNo = scoresObject.getInt("traitNo");
                                float value = scoresObject.getInt("value");
                                scoresList.add(new Score(traitNo, value));
                            }
                        }

                        SessionManager.getInstance().setUser(
                                new User(user_type, rated, credit, image, test_id, test_result_id, username, user_bio, email, scoresList));
                    }
                    // Send broadcast
                    Log.d("Load Profile Task","Load profile completed.");
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
            protected Map<String,String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("session-token", session_token);
                return params;
            }
        };


        queue.add(jsonRequest);

    }
}