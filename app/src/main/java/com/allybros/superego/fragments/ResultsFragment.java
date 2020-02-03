package com.allybros.superego.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.allybros.superego.R;
import com.allybros.superego.activity.SplashActivity;
import com.allybros.superego.api.LoadProfileTask;
import com.allybros.superego.unit.Api;
import com.allybros.superego.unit.User;
import com.allybros.superego.util.TraitListAdapter;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import jp.co.recruit_lifestyle.android.widget.WaveSwipeRefreshLayout;

public class ResultsFragment extends Fragment {
    ListView listViewTraits;
    Activity activity;
    WaveSwipeRefreshLayout mWaveSwipeRefreshLayout;

    public ResultsFragment() {
// Required empty public constructor
    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_results, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        listViewTraits =(ListView) getView().findViewById(R.id.listViewTraits);
        mWaveSwipeRefreshLayout = (WaveSwipeRefreshLayout) getView().findViewById(R.id.main_swipe);


        super.onActivityCreated(savedInstanceState);
        if(!User.getTestId().isEmpty() && User.getRated()>= Api.getRatedLimit()){
            ListAdapter adapter = new TraitListAdapter(this.activity, User.getScores());
            listViewTraits.setAdapter(adapter);

        }

        mWaveSwipeRefreshLayout.setOnRefreshListener(new WaveSwipeRefreshLayout.OnRefreshListener() {
            @Override public void onRefresh() {
                LoadProfileTask.loadProfileTask(getContext(), SplashActivity.session_token);

                YoYo.with(Techniques.SlideInDown)
                        .duration(700)
                        .repeat(0)
                        .playOn(getView().findViewById(R.id.listViewTraits));
                mWaveSwipeRefreshLayout.setRefreshing(false);


            }
        });
        //TODO:Boşken nasıl gözükeceği belirlenmeli
    }
}