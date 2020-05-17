package com.allybros.superego.fragments;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.allybros.superego.R;
import com.allybros.superego.activity.UserPageActivity;
import com.allybros.superego.api.LoadProfileTask;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.ErrorCodes;
import com.allybros.superego.unit.User;
import com.allybros.superego.util.ScoresAdapter;
import com.allybros.superego.util.SessionManager;

public class ResultsFragment extends Fragment {
    private TextView tvRemainingRates;
    private ListView listViewTraits;
    private SwipeRefreshLayout swipeLayout;
    private User currentUser;
    private BroadcastReceiver resultsRefreshReceiver;

    //3 states of Result screen represented in an Enum
    private enum State {
        NONE, PARTIAL, COMPLETE
    }

    public ResultsFragment() {
        this.currentUser = SessionManager.getInstance().getUser();
        //Set up refresh receiver
        resultsRefreshReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int status = intent.getIntExtra("status",0);
                swipeLayout.setRefreshing(false);

                //Update fragments
                UserPageActivity pageActivity = (UserPageActivity) getActivity();
                if (pageActivity != null) pageActivity.refreshFragments(1);

                if (status == ErrorCodes.SUCCESS) {
                    Log.d("Profile refresh", "Success");
                    swipeLayout.setRefreshing(false);
                } else {
                    swipeLayout.setRefreshing(false); //Last
                    Toast.makeText(getContext(), getContext().getString(R.string.connection_error), Toast.LENGTH_SHORT).show();
                }
            }
        };
        //TODO: Replace when new API package is developed
        LocalBroadcastManager.getInstance(getContext()).registerReceiver(resultsRefreshReceiver,
                new IntentFilter(ConstantValues.getActionRefreshResults()));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Check state then inflate required layout
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
        //Setup refresher
        swipeLayout = getView().findViewById(R.id.swipeLayout);
        if (swipeLayout != null)
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
            //Start load task
            LoadProfileTask.loadProfileTask(getContext(),
                SessionManager.getInstance().getSessionToken(),
                ConstantValues.getActionRefreshResults());
            }
        });

        //Populate views depending on current state
        switch (this.getState()) {
            //One result
            case PARTIAL:
                //Get views
                listViewTraits = getView().findViewById(R.id.listViewPartialTraits);
                tvRemainingRates = getView().findViewById(R.id.tvRemainingRatesPartial);
                //Populate views
                int remainingRates = 10 - (currentUser.getRated() + currentUser.getCredit());
                tvRemainingRates.setText(getString(R.string.remaining_credits, remainingRates));
                listViewTraits.setAdapter( new ScoresAdapter(getActivity(), currentUser.getScores()) );
                break;

            //All results
            case COMPLETE:
                listViewTraits = getView().findViewById(R.id.listViewTraits);
                listViewTraits.setAdapter( new ScoresAdapter(getActivity(), currentUser.getScores()) );
                break;

            //No results
            default:
                //Get views
                tvRemainingRates = getView().findViewById(R.id.tvRatedResultPage);
                //Populate views
                remainingRates = 5 - currentUser.getRated();
                tvRemainingRates.setText( getString(R.string.remaining_credits, remainingRates) );
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

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(resultsRefreshReceiver);
        super.onDestroy();
    }
}
