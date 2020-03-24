package com.allybros.superego.fragments;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.allybros.superego.R;
import com.allybros.superego.activity.AddTestActivity;
import com.allybros.superego.api.LoginTask;
import com.allybros.superego.unit.Api;
import com.allybros.superego.unit.User;
import com.allybros.superego.util.CircledNetworkImageView;
import com.allybros.superego.util.CustomVolleyRequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.vlad1m1r.lemniscate.funny.HeartProgressView;

public class ProfilFragment extends Fragment {

    TextView tvUsernameProfilPage,tvRatedProfilPage,tvUserInfoProfilPage,tvAppInformationProfilePage;
    ProgressBar progressBarProfilPage;
    Button addTest,copyTestLink, shareTest;
    HeartProgressView heart1;
    CircledNetworkImageView profileImage;
    public static final String USER_INFORMATION_PREF="USER_INFORMATION_PREF";
    private String session_token,uid,password;
    private ImageLoader mImageLoader;

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


        profileImage=(CircledNetworkImageView) getView().findViewById(R.id.profile_image);
        addTest =(Button) getView().findViewById(R.id.addTest);
        copyTestLink=(Button) getView().findViewById(R.id.copyTestLink);
        shareTest =(Button) getView().findViewById(R.id.shareTest);
        tvUserInfoProfilPage =(TextView) getView().findViewById(R.id.tvUserInfoProfilPage);
        tvUsernameProfilPage =(TextView) getView().findViewById(R.id.tvUsernameProfilPage);
        progressBarProfilPage = (ProgressBar) getView().findViewById(R.id.progressBarProfilPage);
        tvRatedProfilPage=(TextView) getView().findViewById(R.id.tvRatedProfilPage);
        tvAppInformationProfilePage=(TextView) getView().findViewById(R.id.tvAppInformationProfilePage);


        tvUserInfoProfilPage.setText(User.getUserBio());
        tvUsernameProfilPage.setText(User.getUsername());
        if(User.getRated()>= Api.getRatedLimit()){
            progressBarProfilPage.setVisibility(View.GONE);
        }else{
            progressBarProfilPage.setProgress(User.getRated());
        }
        tvRatedProfilPage.setText(String.valueOf(User.getRated()+" değerlendirme"));
        tvAppInformationProfilePage.setText(R.string.profile_page_information_text);

        //Image Load Process
        mImageLoader = CustomVolleyRequestQueue.getInstance(getContext()).getImageLoader();
        final String url = "https://api.allybros.com/superego/images.php?get="+User.getImage();
        mImageLoader.get(url, ImageLoader.getImageListener(profileImage,R.drawable.simple_profile_photo, android.R.drawable.ic_dialog_alert));
        profileImage.setImageUrl(url, mImageLoader);


        addTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences pref = getContext().getSharedPreferences(USER_INFORMATION_PREF, Context.MODE_PRIVATE);
                final String session_token=pref.getString("session-token","");
                Log.d("sessionTokenProfilFragm",session_token);
                Intent addTestIntent= new Intent(getContext(),AddTestActivity.class);
                startActivity(addTestIntent);
            }
        });

        copyTestLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("testUrl", User.getTestId());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(),getString(R.string.link_copied),Toast.LENGTH_SHORT).show();
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