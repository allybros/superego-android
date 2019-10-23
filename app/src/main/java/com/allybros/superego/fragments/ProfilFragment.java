package com.allybros.superego.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
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
import com.allybros.superego.activity.AddTestActivity;
import com.allybros.superego.api.LoginTask;
import com.allybros.superego.unit.User;
import com.beardedhen.androidbootstrap.AwesomeTextView;
import com.beardedhen.androidbootstrap.BootstrapProgressBar;

public class ProfilFragment extends Fragment {

    TextView tvUsernameProfilPage,tvRatedProfilPage,tvUserInfoProfilPage,tvAppInformationProfilePage;
    BootstrapProgressBar progressBarProfilPage;
    ImageView profileImage,addTest,copyTestLink, shareTest;
    public static final String USER_INFORMATION_PREF="USER_INFORMATION_PREF";
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


        profileImage=(ImageView)getView().findViewById(R.id.profile_image);
        addTest =(ImageView) getView().findViewById(R.id.addTest);
        copyTestLink=(ImageView) getView().findViewById(R.id.copyTestLink);
        shareTest =(ImageView) getView().findViewById(R.id.shareTest);
        tvUserInfoProfilPage =(TextView) getView().findViewById(R.id.tvUserInfoProfilPage);
        tvUsernameProfilPage =(TextView) getView().findViewById(R.id.tvUsernameProfilPage);
        progressBarProfilPage = (BootstrapProgressBar) getView().findViewById(R.id.progressBarProfilPage);
        tvRatedProfilPage=(TextView) getView().findViewById(R.id.tvRatedProfilPage);
        tvAppInformationProfilePage=(AwesomeTextView) getView().findViewById(R.id.tvAppInformationProfilePage);



        tvUserInfoProfilPage.setText(User.getUserBio());
        tvUsernameProfilPage.setText(User.getUsername());
        if(User.getRated()>=rateLimit){
            progressBarProfilPage.setProgress(rateLimit);
        }else{
            progressBarProfilPage.setProgress(User.getRated());
        }
        tvRatedProfilPage.setText(String.valueOf(User.getRated()+" değerlendirme"));
        tvAppInformationProfilePage.setText(R.string.profile_page_information_text);

        addTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getContext(), AddTestActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        copyTestLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("testUrl", User.getTestId());
                clipboard.setPrimaryClip(clip);

            }
        });

        shareTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "Beni nasıl görüyorsun? -->"+User.getTestId();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, R.string.shareTest);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

    }

}