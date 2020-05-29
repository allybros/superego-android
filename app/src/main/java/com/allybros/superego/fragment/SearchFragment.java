package com.allybros.superego.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.allybros.superego.R;
import com.allybros.superego.activity.WebViewActivity;
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
import java.util.Arrays;

/**
 * Search Fragment Class
 * @author umutalacam
 */
public class SearchFragment extends Fragment {

    private EditText etSearchUser;
    private ListView listViewSearchResults;
    private ImageView ivIconSearchInfo;
    private TextView tvSearchInfo;

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
                        //Decode search response
                        JSONObject result = results.getJSONObject(i);
                        String username = result.getString("username");
                        String userBio = result.getString("user_bio");
                        String testId = result.getString("test_id");
                        String avatar = result.getString("avatar");
                        //Collect users
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
                .registerReceiver(searchResponseReceiver, new IntentFilter(ConstantValues.ACTION_SEARCH));

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
        ivIconSearchInfo = getView().findViewById(R.id.ivIconSearchInfo);
        tvSearchInfo = getView().findViewById(R.id.tvSearchInfo);

        listViewSearchResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                User u = (User) adapterView.getItemAtPosition(i);
                // Set web activity title
                String belirtmeHali = "" + turkceBelirtmeHalEkiBulucu(u.getUsername());
                final String webActivityTitle = getContext().getString(R.string.activity_label_rate_user, u.getUsername(), belirtmeHali);

                String testUrl = ConstantValues.RATE_URL + u.getTestId();
                Intent intent = new Intent(getContext(), WebViewActivity.class);
                intent.putExtra("url", testUrl);
                intent.putExtra("title", webActivityTitle);
                intent.putExtra("slidr", false);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getContext().startActivity(intent);
            }
        });

        etSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { performSearch(charSequence.toString()); }

            @Override
            public void afterTextChanged(Editable editable) { }
        });


    }

    private void performSearch(String query){
        SearchTask.searchTask(getContext(), query);
    }

    /**
     * Populate list view with search results.
     * @param users List of users that intended to be shown in ListView
     */
    private void populateResults(ArrayList<User> users){
        //Prevent from null pointers
        Activity parent = getActivity();
        if (parent == null) return;

        if (users.size() != 0){
            // Result set is not empty, hide search info
            YoYo.with(Techniques.FadeOut).duration(200).playOn(ivIconSearchInfo);
            YoYo.with(Techniques.FadeOut).duration(200).playOn(tvSearchInfo);
            ivIconSearchInfo.setVisibility(View.INVISIBLE);
            tvSearchInfo.setVisibility(View.INVISIBLE);
            // Show results
            YoYo.with(Techniques.FadeOut).duration(300).playOn(listViewSearchResults);
            SearchAdapter adapter = new SearchAdapter(parent.getApplicationContext(), users);
            listViewSearchResults.setAdapter(adapter);
            YoYo.with(Techniques.FadeIn).duration(300).playOn(listViewSearchResults);
        } else if (tvSearchInfo.getVisibility() == View.INVISIBLE) {
            //Show info if not visible
            YoYo.with(Techniques.FadeIn).duration(300).playOn(ivIconSearchInfo);
            YoYo.with(Techniques.FadeIn).duration(300).playOn(tvSearchInfo);
            ivIconSearchInfo.setVisibility(View.VISIBLE);
            tvSearchInfo.setVisibility(View.VISIBLE);
            // Clear results
            YoYo.with(Techniques.FadeOut).duration(300).playOn(listViewSearchResults);
            SearchAdapter adapter = new SearchAdapter(parent.getApplicationContext(), users);
            listViewSearchResults.setAdapter(adapter);
        }
    }

    /**
     * Builds title for specifically Turkish language structure
     * İsmin belirtme haline uygun bir başlık oluşturur. (Orçun'u, Umut'u)
     * Türkçe de pek yakıştı değil mi?
     */
    private String turkceBelirtmeHalEkiBulucu(String isim){
        char[] unluler = "aeıioöuü".toCharArray();
        char sonEk = 'i';

        for (int i = isim.length()-1; i >= 0; i--) {
            char c = isim.charAt(i);
            switch (c) {
                case 'a':
                case 'ı':
                    sonEk = 'ı';
                    i = -1;
                    break;
                case 'e':
                case 'i':
                    sonEk = 'i';
                    i = -1;
                    break;
                case 'o':
                case 'u':
                    sonEk = 'u';
                    i = -1;
                    break;
                case 'ö':
                case 'ü':
                    sonEk = 'ü';
                    i = -1;
                    break;
            }
        }

        if (Arrays.binarySearch(unluler, isim.charAt(isim.length()-1)) != -1) {
            return "y"+sonEk;
        }

        return ""+sonEk;
    }
}
