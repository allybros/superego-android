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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.allybros.superego.R;
import com.allybros.superego.activity.AddTestActivity;
import com.allybros.superego.activity.SplashActivity;
import com.allybros.superego.api.LoginTask;
import com.allybros.superego.api.ProfileRefreshTask;
import com.allybros.superego.unit.Api;
import com.allybros.superego.unit.User;
import com.allybros.superego.util.CircledNetworkImageView;
import com.allybros.superego.util.HelperMethods;

public class ProfilFragment extends Fragment {

    private TextView tvUsernameProfilPage,tvRatedProfilPage,tvUserInfoProfilPage,tvAppInformationProfilePage;
    private ProgressBar progressBarProfilPage;
    private Button addTest,copyTestLink, shareTest;
    private CircledNetworkImageView avatar;
    private String session_token,uid,password;
    private SwipeRefreshLayout profileSwipeLayout;
    public static final String USER_INFORMATION_PREF="USER_INFORMATION_PREF";

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

        chechkLogin();
        initializeViewComponents();
        loadProfile();
        setButtons();


    }

    private void loadProfile(){
        tvUserInfoProfilPage.setText(User.getUserBio());
        tvUsernameProfilPage.setText(User.getUsername());

        if(User.getRated()>= Api.getRatedLimit()){
            progressBarProfilPage.setVisibility(View.GONE);
        }else{
            progressBarProfilPage.setProgress(User.getRated());
        }
        tvRatedProfilPage.setText(String.valueOf(User.getRated()+" değerlendirme"));
        tvAppInformationProfilePage.setText(R.string.profile_page_information_text);

        HelperMethods.imageLoadFromUrl(getContext(),Api.getAvatarUrl()+User.getImage(),avatar);
    }
    private void initializeViewComponents(){
        addTest =(Button) getView().findViewById(R.id.addTest);
        copyTestLink=(Button) getView().findViewById(R.id.copyTestLink);
        shareTest =(Button) getView().findViewById(R.id.shareTest);
        tvUserInfoProfilPage =(TextView) getView().findViewById(R.id.tvUserInfoProfilPage);
        tvUsernameProfilPage =(TextView) getView().findViewById(R.id.tvUsernameProfilPage);
        progressBarProfilPage = (ProgressBar) getView().findViewById(R.id.progressBarProfilPage);
        tvRatedProfilPage=(TextView) getView().findViewById(R.id.tvRatedProfilPage);
        tvAppInformationProfilePage=(TextView) getView().findViewById(R.id.tvAppInformationProfilePage);
        avatar= (CircledNetworkImageView) getView().findViewById(R.id.profile_image);
        profileSwipeLayout = (SwipeRefreshLayout) getView().findViewById(R.id.profileSwipeLayout);
    }
    private void chechkLogin(){
        //TODO: Bu sistem splash screen static değişkenine dönüştürülecek
        SharedPreferences pref = getContext().getSharedPreferences(USER_INFORMATION_PREF, getContext().MODE_PRIVATE);

        session_token = pref.getString("session_token", "");
        uid=pref.getString("uid","");
        password=pref.getString("password","");
        if(session_token.isEmpty()) {
            LoginTask.loginTask(getContext(),uid,password);
        }
        session_token=pref.getString("session_token","");
    }
    private void setButtons(){
        addTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedPreferences pref = getContext().getSharedPreferences(USER_INFORMATION_PREF, Context.MODE_PRIVATE);
                final String session_token=pref.getString("session-token","");
                Log.d("sessionTokenProfilFragm",session_token);
                Intent addTestIntent= new Intent(getContext(), AddTestActivity.class);
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

        profileSwipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ProfileRefreshTask.profileRefreshTask(getContext(), SplashActivity.session_token);
                profileSwipeLayout.setRefreshing(false);
                loadProfile();
            }
        });
    }
}