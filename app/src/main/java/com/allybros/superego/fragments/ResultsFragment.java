package com.allybros.superego.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.allybros.superego.R;
import com.allybros.superego.api.LoadProfileTask;
import com.allybros.superego.unit.Trait;
import com.allybros.superego.unit.User;
import com.allybros.superego.util.TraitListAdapter;

import java.util.ArrayList;

public class ResultsFragment extends Fragment {
    ListView listViewTraits;
    Activity activity;

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
        super.onActivityCreated(savedInstanceState);
        listViewTraits =(ListView) getView().findViewById(R.id.listViewTraits);

        ListAdapter adapter = new TraitListAdapter(this.activity, User.getScores());
        listViewTraits.setAdapter(adapter);
    }
}