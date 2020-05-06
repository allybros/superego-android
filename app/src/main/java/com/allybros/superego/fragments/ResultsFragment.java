package com.allybros.superego.fragments;

import android.annotation.SuppressLint;
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
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.allybros.superego.R;
import com.allybros.superego.activity.SplashActivity;
import com.allybros.superego.api.LoadProfileTask;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.Score;
import com.allybros.superego.unit.User;
import com.allybros.superego.util.ScoresAdapter;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class ResultsFragment extends Fragment {
    private ConstraintLayout constraintLayoutResult;
    private TextView tvRemainingRates;
    private ImageView imageLogo;
    private ListView listViewTraits;
    private Activity activity;
    private SwipeRefreshLayout swipeLayout;
    private User currentUser;

    //3 states of Result screen represented in an Enum
    private enum State {
        NONE, PARTIAL, COMPLETE
    }

    public ResultsFragment() {
        this.currentUser = SplashActivity.getCurrentUser();
    }

    public void setActivity(Activity activity){
        this.activity = activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Check state
        switch (this.getState()) {
            case PARTIAL:
                return inflater.inflate(R.layout.fragment_results_partial, container, false);
            case COMPLETE:
                return inflater.inflate(R.layout.fragment_results_complete, container, false);
            default:
                return inflater.inflate(R.layout.fragment_results_none, container, false);
        }

    }

    @SuppressLint("StringFormatMatches")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        swipeLayout = getView().findViewById(R.id.swipeLayout);
        //Setup refresher
        if (swipeLayout != null)
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                LoadProfileTask.loadProfileTask(getContext(), SplashActivity.session_token, ConstantValues.getActionRefreshProfile());
                YoYo.with(Techniques.SlideInDown)
                        .duration(700)
                        .repeat(0)
                        .playOn(swipeLayout);
                swipeLayout.setRefreshing(false);
                ResultsFragment newResultsFragment = new ResultsFragment();
                FragmentManager fragmentManager = getFragmentManager();
                if (getFragmentManager() != null){
                    //Replace fragment with new
                    fragmentManager
                            .beginTransaction()
                            .replace(ResultsFragment.super.getId(), newResultsFragment)
                            .commit();
                }
            }
        });

        //Populate views
        switch (this.getState()) {
            case PARTIAL: //One result
                listViewTraits = getView().findViewById(R.id.listViewPartialTraits);
                listViewTraits.setAdapter( new ScoresAdapter(this.activity, currentUser.getScores()) );
                break;
            case COMPLETE: //All results
                listViewTraits = getView().findViewById(R.id.listViewTraits);
                listViewTraits.setAdapter( new ScoresAdapter(this.activity, currentUser.getScores()) );
                break;
            default: //No results
                tvRemainingRates = getView().findViewById(R.id.tvRatedResultPage);
                int remainingCredits = 5 - SplashActivity.getCurrentUser().getRated();
                tvRemainingRates.setText( getString(R.string.remaining_credits, remainingCredits) );
                break;
        }

        super.onActivityCreated(savedInstanceState);
    }

    private State getState(){
        if (currentUser.getScores().size() >= 6)
            return State.COMPLETE;
        else if (currentUser.getScores().size() >= 1)
            return State.PARTIAL;
        else
            return State.NONE;
    }


}