package com.allybros.superego.api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.activity.SplashActivity;
import com.allybros.superego.activity.UserPageActivity;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.unit.Score;
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

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class LoadProfileTask{

    static Intent intent;

    public static void loadProfileTask(final Context context, final String session_token , final String action){

        /*
         *       If call for loadProfile process define action parameter "load" else call ConstantValues.getActionRefreshProfile()
         *
         *
         *
         * */

        RequestQueue queue = Volley.newRequestQueue(context);
        if(action.equals(ConstantValues.getActionRefreshProfile())) intent=new Intent(ConstantValues.getActionRefreshProfile());

        final StringRequest jsonRequest=new StringRequest(Request.Method.POST, ConstantValues.getLoadProfile(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response_Load_Profile",response.toString());
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    int status=jsonObject.getInt("status");
                    switch (status){

                        case ErrorCodes.SYSFAIL:
                            if(action.equals(ConstantValues.getActionRefreshProfile())){
                                intent.putExtra("status", ErrorCodes.SUCCESS);
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            }else {
                                Toast.makeText(context, context.getString(R.string.please_login_again), Toast.LENGTH_SHORT).show();
                                intent=new Intent(context, SplashActivity.class);
                                context.startActivity(intent);
                            }

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

                            //Build scores list
                            ArrayList<Score> scoresList = new ArrayList<>();
                            if(!jsonObject.isNull("scores")){
                                for (int i = 0; i < jsonObject.getJSONArray("scores").length(); i++) {
                                    JSONObject scoresObject = (JSONObject) jsonObject.getJSONArray("scores").get(i);
                                    int traitNo =  scoresObject.getInt("traitNo");
                                    float value = scoresObject.getInt("value");;
                                    scoresList.add(new Score(traitNo,value));
                                }
                            }
                            SplashActivity.setCurrentUser(new User(user_type,rated,credit,image,test_id,username,user_bio,email,scoresList));

                            if(action.equals(ConstantValues.getActionRefreshProfile())){
                                intent.putExtra("status", ErrorCodes.SUCCESS);
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                            }else{
                                intent=new Intent(context, UserPageActivity.class);
                                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                            break;

                        case ErrorCodes.SESSION_EXPIRED:
                            SharedPreferences pref = context.getSharedPreferences(ConstantValues.getUserInformationPref(), context.MODE_PRIVATE);
                            String uid= pref.getString("uid", "");
                            String password=pref.getString("password","");
                            LoginTask.loginTask(context,uid,password);
                            break;
                    }
                        
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context,context.getString(R.string.connection_error), Toast.LENGTH_SHORT);

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