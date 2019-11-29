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
        //TODO:Sanırım burada Oylama yoksa liste oluşturmayı deniyor bu düzeltilmeli!
        super.onActivityCreated(savedInstanceState);
        listViewTraits =(ListView) getView().findViewById(R.id.listViewTraits);

        ListAdapter adapter = new TraitListAdapter(this.activity, User.getScores());
        listViewTraits.setAdapter(adapter);
    }
}