package com.allybros.superego.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.allybros.superego.R;
import com.allybros.superego.unit.Api;
import com.allybros.superego.unit.User;
import com.allybros.superego.util.TraitListAdapter;

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

        if(!User.getTestId().isEmpty() ) Log.d("result_info","isEmpty->"+User.getTestId().isEmpty());
        if((User.getTestId().equals("null"))) Log.d("result_info","null->"+User.getTestId().equals("null"));
        if(User.getRated()>= Api.getRatedLimit()) Log.d("result_info","Limit"+User.getTestId()+"  " + (User.getRated()>= Api.getRatedLimit()));

        if(!User.getTestId().isEmpty() && User.getRated()>= Api.getRatedLimit()){
            listViewTraits =(ListView) getView().findViewById(R.id.listViewTraits);
            ListAdapter adapter = new TraitListAdapter(this.activity, User.getScores());
            listViewTraits.setAdapter(adapter);

        }//TODO:Boşken nasıl gözükeceği belirlenmeli
    }
}