package com.allybros.superego.api;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.allybros.superego.R;
import com.allybros.superego.activity.SplashActivity;
import com.allybros.superego.activity.UserPageActivity;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.unit.Score;
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

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class LoadProfileTask{
    public static final String USER_INFORMATION_PREF="USER_INFORMATION_PREF";


    final private static String LOAD_PROFILE_URL ="https://api.allybros.com/superego/load-profile.php";
    final private static String ALL_TRAITS_URL="https://api.allybros.com/superego/traits.php";

    public static void loadProfileTask(final Context currentContext, final String session_token){

        RequestQueue queue = Volley.newRequestQueue(currentContext);

        final StringRequest jsonRequest=new StringRequest(Request.Method.POST, LOAD_PROFILE_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Response_Load_Profile",response.toString());
                Intent intent;
                try {
                    JSONObject jsonObject=new JSONObject(response);
                    int status=jsonObject.getInt("status");
                    switch (status){
                        case ErrorCodes.SYSFAIL:

                            Toast.makeText(currentContext, currentContext.getString(R.string.please_login_again), Toast.LENGTH_SHORT).show();
                            intent=new Intent(currentContext, SplashActivity.class);
                            currentContext.startActivity(intent);
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
                            //TODO: Fix user class data fields
                            User.setRated(rated);
                            User.setUserType(user_type);
                            User.setUsername(username);
                            User.setUserBio(user_bio);
                            User.setEmail(email);
                            User.setTestId(test_id);
                            User.setScores(scoresList);
                            User.setCredit(credit);
                            User.setImage(image);

                            intent=new Intent(currentContext, UserPageActivity.class);
                            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                            currentContext.startActivity(intent);
                            break;

                        case ErrorCodes.SESSION_EXPIRED:
                            SharedPreferences pref = currentContext.getSharedPreferences(USER_INFORMATION_PREF, currentContext.MODE_PRIVATE);
                            String uid= pref.getString("uid", "");
                            String password=pref.getString("password","");
                            LoginTask.loginTask(currentContext,uid,password);
                            break;
                    }
                        
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(currentContext,currentContext.getString(R.string.connection_error), Toast.LENGTH_SHORT).show();

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

    public static ArrayList<Trait> getAllTraits(final Context currentContext){
        RequestQueue queue = Volley.newRequestQueue(currentContext);
        final ArrayList<Trait> traits=new ArrayList<>();

        final StringRequest jsonRequest = new StringRequest(Request.Method.GET, ALL_TRAITS_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("getAllTraits",response.toString());

                    try {
                        JSONObject jsonObject=new JSONObject(response);

                        for (int i = 0; i < jsonObject.getJSONArray("traits").length(); i++) {
                            JSONObject iter= (JSONObject) jsonObject.getJSONArray("traits").get(i);
                            int traitNo;
                            String positiveName,negativeName,positiveIcon,negativeIcon;

                            traitNo=iter.getInt("traitNo");
                            positiveName=iter.getString("positive");
                            negativeName=iter.getString("negative");
                            positiveIcon=iter.getString("positive_icon");
                            negativeIcon=iter.getString("negative_icon");
                            traits.add(new Trait(traitNo,positiveName,negativeName,positiveIcon,negativeIcon));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(currentContext,currentContext.getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(jsonRequest);
        return traits;
    }
}