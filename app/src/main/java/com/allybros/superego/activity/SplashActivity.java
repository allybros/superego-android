package com.allybros.superego.activity;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.allybros.superego.R;
import com.allybros.superego.api.LoadProfileTask;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.Trait;
import com.allybros.superego.unit.User;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class SplashActivity extends AppCompatActivity {
    public static String session_token;
    private static User currentUser;
    private SharedPreferences pref;
    public static ArrayList<Trait> allTraits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        pref= getSharedPreferences(ConstantValues.getUserInformationPref(),MODE_PRIVATE);
        session_token=pref.getString("session_token","");
        Log.d("sessionTokenSplash",session_token);

        allTraits= SplashActivity.getAllTraits(getApplicationContext());
        Trait.setAllTraits(allTraits);
        if(!session_token.isEmpty()){
            LoadProfileTask.loadProfileTask(getApplicationContext(),session_token,"load");
        }else{
            Intent intent=new Intent(this,LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static void setCurrentUser(User currentUser) {
        SplashActivity.currentUser = currentUser;
    }

    public static ArrayList<Trait> getAllTraits(final Context currentContext){
        RequestQueue queue = Volley.newRequestQueue(currentContext);
        final ArrayList<Trait> traits=new ArrayList<>();

        final StringRequest jsonRequest = new StringRequest(Request.Method.GET, ConstantValues.getAllTraits(), new Response.Listener<String>() {
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

