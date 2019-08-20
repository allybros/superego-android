package com.allybros.superego.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.allybros.superego.R;
import com.allybros.superego.api.LoginTask;
import com.allybros.superego.unit.Trait;
import com.allybros.superego.unit.User;
import com.beardedhen.androidbootstrap.BootstrapProgressBar;

import java.util.ArrayList;

public class ProfilFragment extends Fragment {

    TextView tvUsernameProfilPage,tvRatedProfilPage,tvUserInfoProfilPage;
    BootstrapProgressBar progressBarProfilPage;
    ImageView deneme;
    public static final String USER_INFORMATION_PREF="USER_INFORMATION_PREF";
    private String username,userBio,email;
    private int userType;
    private ArrayList<Trait> scores;
    private String session_token,uid,password;
    private int rateLimit=15;   //This variable defined from progressBarProfilPage that is in fragment_profile

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


        if(session_token.isEmpty()) {
            LoginTask.loginTask(getContext(),uid,password);
        }
        session_token=pref.getString("session_token","");


        deneme=(ImageView)getView().findViewById(R.id.deneme);
        tvUserInfoProfilPage =(TextView) getView().findViewById(R.id.tvUserInfoProfilPage);
        tvUsernameProfilPage =(TextView) getView().findViewById(R.id.tvUsernameProfilPage);
        progressBarProfilPage = (BootstrapProgressBar) getView().findViewById(R.id.progressBarProfilPage);
        tvRatedProfilPage=(TextView) getView().findViewById(R.id.tvRatedProfilPage);

        tvUserInfoProfilPage.setText(User.getUserBio());
        tvUsernameProfilPage.setText(User.getUsername());
        if(User.getRated()>=rateLimit){
            progressBarProfilPage.setProgress(rateLimit);
        }else{
            progressBarProfilPage.setProgress(User.getRated());
        }
        tvRatedProfilPage.setText(String.valueOf(User.getRated()+" değerlendirme"));

    }
}