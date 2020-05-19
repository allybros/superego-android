package com.allybros.superego.fragments;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.allybros.superego.R;
import com.allybros.superego.api.SearchTask;
import com.allybros.superego.ui.SearchAdapter;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.User;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Search Fragment Cass
 * @author umutalacam
 */

public class SearchFragment extends Fragment {

    private EditText etSearchUser;
    private ListView listViewSearchResults;
    private BroadcastReceiver searchResponseReceiver;

    public SearchFragment() {
        // Required empty public constructor
        searchResponseReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String resultResponse = intent.getStringExtra("result");
                try {
                    JSONArray results = new JSONArray(resultResponse);
                    ArrayList<User> usersFound = new ArrayList<>();
                    for (int i = 0; i < results.length(); i++) {
                        JSONObject result = results.getJSONObject(i);
                        String username = result.getString("username");
                        String userBio = result.getString("user_bio");
                        String testId = result.getString("test_id");
                        String avatar = result.getString("avatar");

                        User u = new User(testId, username, userBio, avatar);
                        usersFound.add(u);
                    }
                    populateResults(usersFound);
                } catch (JSONException e) {
                    Log.e("Exception on search", e.getMessage());
                    e.printStackTrace();
                }
            }
        };

        //TODO: Replace when the API is updated
        LocalBroadcastManager.getInstance(getContext())
                .registerReceiver(searchResponseReceiver, new IntentFilter(ConstantValues.getActionSearch()));

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        etSearchUser = getView().findViewById(R.id.etSearchUser);
        listViewSearchResults = getView().findViewById(R.id.listViewSearchResults);

        etSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                performSearch(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }

    private void performSearch(String query){
        SearchTask.searchTask(getContext(), query);
    }

    private void populateResults(ArrayList<User> users){
        //Prevent from null pointers
        Activity parent = getActivity();
        if (parent == null) return;
        SearchAdapter adapter = new SearchAdapter(parent.getApplicationContext(), users);
        YoYo.with(Techniques.FadeOut).duration(300).playOn(listViewSearchResults);
        listViewSearchResults.setAdapter(adapter);
        YoYo.with(Techniques.FadeIn).duration(300).playOn(listViewSearchResults);
    }
}
