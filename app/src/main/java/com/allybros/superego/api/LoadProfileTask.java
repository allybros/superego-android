package com.allybros.superego.api;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.activity.SplashActivity;
import com.allybros.superego.activity.UserPageActivity;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.unit.Ocean;
import com.allybros.superego.unit.Personality;
import com.allybros.superego.unit.Score;
import com.allybros.superego.unit.User;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
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
                    JSONObject result=new JSONObject(response);
                    int status = result.getInt("status");

                    if (status == ErrorCodes.SUCCESS) {
                        // Build user object
                        int user_type = result.getInt("user_type");
                        String username = result.getString("username");
                        String user_bio = result.getString("user_bio");
                        String test_id = result.getString("test_id");
                        String test_result_id = result.getString("test_result_id");
                        String email = result.getString("email");
                        String image = result.getString("avatar");
                        int rated = result.getInt("rated");
                        int credit = result.getInt("credit");

                        //Build scores list
                        ArrayList<Score> scoresList = new ArrayList<>();
                        if (!result.isNull("scores")) {
                            for (int i = 0; i < result.getJSONArray("scores").length(); i++) {
                                JSONObject scoresObject = (JSONObject) result.getJSONArray("scores").get(i);
                                int traitNo = scoresObject.getInt("traitNo");
                                float value = scoresObject.getInt("value");
                                scoresList.add(new Score(traitNo, value));
                            }
                        }

                        Ocean ocean = null;
                        if (!result.isNull("ocean")) {
                            JSONObject oceanObject = result.getJSONObject("ocean");
                            double o = oceanObject.getDouble("o");
                            double c = oceanObject.getDouble("c");
                            double e = oceanObject.getDouble("e");
                            double a = oceanObject.getDouble("a");
                            double n = oceanObject.getDouble("n");
                            ocean = new Ocean(o,c,e,a,n);
                        }

                        Personality personality = null;
                        if (!result.isNull("personality")) {
                            JSONObject oceanObject = result.getJSONObject("personality");
                            String  type = oceanObject.getString("type");
                            String  title = oceanObject.getString("title");
                            String  description = oceanObject.getString("description");
                            String  detail_url = oceanObject.getString("detail_url");
                            String  primary_color = oceanObject.getString("primary_color");
                            String  secondary_color = oceanObject.getString("secondary_color");
                            String  img_url = oceanObject.getString("img_url");
                            personality = new Personality(type, title, description, detail_url, primary_color, secondary_color, img_url);
                        }


                        SessionManager.getInstance().setUser(
                                new User(
                                        user_type,
                                        rated,
                                        credit,
                                        image,
                                        test_id,
                                        test_result_id,
                                        username,
                                        user_bio,
                                        email,
                                        scoresList,
                                        ocean,
                                        personality
                                )
                        );
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