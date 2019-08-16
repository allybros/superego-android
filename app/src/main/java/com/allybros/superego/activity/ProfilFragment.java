package com.allybros.superego.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.allybros.superego.R;
import com.allybros.superego.api.LoadProfileTask;
import com.allybros.superego.api.LoginTask;
import com.allybros.superego.util.Trait;
import com.allybros.superego.util.User;
import com.beardedhen.androidbootstrap.BootstrapProgressBar;

import java.util.ArrayList;

public class ProfilFragment extends Fragment {

    TextView tvUsernameProfilPage,tvUserInfoProfilPage;
    BootstrapProgressBar progressBarProfilPage;
    public static final String USER_INFORMATION_PREF="USER_INFORMATION_PREF";
    private String username,userBio,email;
    private int userType;
    private ArrayList<Trait> scores;
    private String session_token,uid,password;

    public ProfilFragment() {
// Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        SharedPreferences pref = getContext().getSharedPreferences(USER_INFORMATION_PREF, getContext().MODE_PRIVATE);

        session_token = pref.getString("session_token", "");
        uid=pref.getString("uid","");
        password=pref.getString("password","");

        if(session_token.equals("")) {
            LoginTask.loginTask(getContext(),uid,password);
        }
        session_token=pref.getString("session_token","");


        tvUserInfoProfilPage =(TextView) getView().findViewById(R.id.tvUserInfoProfilPage);
        tvUsernameProfilPage =(TextView) getView().findViewById(R.id.tvUsernameProfilPage);
        progressBarProfilPage = (BootstrapProgressBar) getView().findViewById(R.id.progressBarProfilPage);

        tvUserInfoProfilPage.setText(User.getUserBio());
        tvUsernameProfilPage.setText(User.getUsername());
        progressBarProfilPage.setProgress(User.getRated());
    }
}