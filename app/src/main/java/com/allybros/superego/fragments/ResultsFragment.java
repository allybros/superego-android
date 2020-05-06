package com.allybros.superego.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.allybros.superego.R;
import com.allybros.superego.activity.SplashActivity;
import com.allybros.superego.api.LoadProfileTask;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.Score;
import com.allybros.superego.util.ScoresAdapter;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class ResultsFragment extends Fragment {
    ConstraintLayout constraintLayoutResult;
    TextView tvRatedResultPage;
    ImageView imageLogo;
    ListView listViewTraits;
    Activity activity;
    SwipeRefreshLayout swipeLayout;
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
        swipeLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeLayout);
        constraintLayoutResult= (ConstraintLayout) getView().findViewById(R.id.constraintLayoutResult);
        tvRatedResultPage= (TextView) getView().findViewById(R.id.tvRatedResultPage);
        imageLogo= (ImageView) getView().findViewById(R.id.imageLogo);


        super.onActivityCreated(savedInstanceState);

        if(SplashActivity.getCurrentUser().getScores().size() > 0){
            //User have results
            constraintLayoutResult.setVisibility(View.GONE);
            listViewTraits.setVisibility(View.VISIBLE);
            swipeLayout.setVisibility(View.VISIBLE);

            StringBuilder traitsBuilder = new StringBuilder();
            for (Score s : SplashActivity.getCurrentUser().getScores()) {
                traitsBuilder.append(s.getTraitName()).append("\n");
            }
            Log.d("Traits", traitsBuilder.toString());
            ListAdapter adapter = new ScoresAdapter(this.activity, SplashActivity.getCurrentUser().getScores());
            listViewTraits.setAdapter(adapter);

        }else{
            //User dont have any results
            constraintLayoutResult.setVisibility(View.VISIBLE);
            swipeLayout.setVisibility(View.GONE);
            listViewTraits.setVisibility(View.GONE);
            tvRatedResultPage.setText("Değerlendirme: "+ SplashActivity.getCurrentUser().getRated());

        }

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                LoadProfileTask.loadProfileTask(getContext(), SplashActivity.session_token, ConstantValues.getActionRefreshProfile());
                YoYo.with(Techniques.SlideInDown)
                        .duration(700)
                        .repeat(0)
                        .playOn(getView().findViewById(R.id.listViewTraits));
                swipeLayout.setRefreshing(false);
            }
        });

    }




}