package com.allybros.superego.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.allybros.superego.R;
import com.allybros.superego.activity.WebViewActivity;
import com.allybros.superego.api.SearchTask;
import com.allybros.superego.adapter.SearchAdapter;
import com.allybros.superego.api.response.SearchResponse;
import com.allybros.superego.unit.ConstantValues;
import com.allybros.superego.unit.User;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import java.util.Arrays;

/**
 * Search Fragment Class
 *
 * @author umutalacam
 */
public class SearchFragment extends Fragment {

    private EditText etSearchUser;
    private ListView listViewSearchResults;
    private ImageView ivIconSearchInfo;
    private TextView tvSearchInfo, tvSearchHeader;
    private ConstraintLayout constraintSearchFragment;
    private final Activity parentActivity;

    public SearchFragment(Activity parentActivity) {
        this.parentActivity = parentActivity;
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
        constraintSearchFragment = getView().findViewById(R.id.constraintSearchFragment);
        tvSearchHeader = getView().findViewById(R.id.tvSearchHeader);


        listViewSearchResults.setOnItemClickListener((adapterView, view, i, l) -> {
            SearchResponse u = (SearchResponse) adapterView.getItemAtPosition(i);
            // Set web activity title
            String belirtmeHali = turkceBelirtmeHalEkiBulucu(u.getUsername());
            @SuppressLint("StringFormatMatches") final String webActivityTitle = getContext().getString(R.string.activity_label_rate_user, u.getUsername(), belirtmeHali);
            ConstantValues constantValues = new ConstantValues();
            String rateUrl = constantValues.getWebUrlWithLocale(ConstantValues.RATE_URL);

            String testUrl = rateUrl + u.getTestId();
            Intent intent = new Intent(getContext(), WebViewActivity.class);
            intent.putExtra("url", testUrl);
            intent.putExtra("title", webActivityTitle);
            intent.putExtra("slidr", false);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getContext().startActivity(intent);
        });

        etSearchUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // It's not necessary
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Check internet connection
                ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
                if (isConnected) {
                    performSearch(charSequence.toString());
                } else {
                    Log.d("CONNECTION", String.valueOf(isConnected));
                    Snackbar.make(constraintSearchFragment, R.string.error_no_connection, BaseTransientBottomBar.LENGTH_LONG).show();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // It's not necessary
            }
        });

        tvSearchHeader.setOnClickListener(v ->
                YoYo.with(Techniques.Hinge).duration(2000).onEnd(animator ->
                        Toast.makeText(
                                getContext(),
                                "Tebrikler :) ",
                                Toast.LENGTH_LONG
                        ).show()
                ).playOn(tvSearchHeader));
    }

    private void performSearch(String query) {
        SearchTask searchTask = new SearchTask(query);
        searchTask.setOnResponseListener(response -> handleSearchResponse(response, query));
        searchTask.execute(parentActivity.getApplicationContext());
    }

    private void handleSearchResponse(SearchResponse[] response, String query) {
        if (etSearchUser != null && !query.equals(etSearchUser.getText().toString())) {
            Log.d("Search Receiver", "Late response, skipping results");
            return;
        }
        populateResults(response);
    }

    /**
     * Populate list view with search results.
     *
     * @param searchResponse Array of users that intended to be shown in ListView
     */
    private void populateResults(SearchResponse[] searchResponse) {
        //Prevent from null pointers
        Activity parent = getActivity();
        if (parent == null) return;

        if (tvSearchInfo.getVisibility() == View.INVISIBLE) {
            if (searchResponse.length == 0) {
                //Show info if not visible
                YoYo.with(Techniques.FadeIn).duration(300).playOn(ivIconSearchInfo);
                YoYo.with(Techniques.FadeIn).duration(300).playOn(tvSearchInfo);
                ivIconSearchInfo.setVisibility(View.VISIBLE);
                tvSearchInfo.setVisibility(View.VISIBLE);
                // Hide list
                YoYo.with(Techniques.FadeOut).duration(300).playOn(listViewSearchResults);
                listViewSearchResults.setVisibility(View.INVISIBLE);
            }
        } else {
            if (searchResponse.length > 0) {
                // Result set is not empty, hide search info
                YoYo.with(Techniques.FadeOut).duration(200).playOn(ivIconSearchInfo);
                YoYo.with(Techniques.FadeOut).duration(200).playOn(tvSearchInfo);
                ivIconSearchInfo.setVisibility(View.INVISIBLE);
                tvSearchInfo.setVisibility(View.INVISIBLE);
                //Show list
                YoYo.with(Techniques.FadeIn).duration(300).playOn(listViewSearchResults);
                listViewSearchResults.setVisibility(View.VISIBLE);
            }
        }

        SearchAdapter adapter = new SearchAdapter(parent.getApplicationContext(), searchResponse);
        listViewSearchResults.setAdapter(adapter);
    }

    /**
     * Builds title for specifically Turkish language structure
     * İsmin belirtme haline uygun bir başlık oluşturur. (Orçun'u, Umut'u)
     * Türkçe de pek yakıştı değil mi?
     */
    private String turkceBelirtmeHalEkiBulucu(String isim) {
        char[] unluler = "aeıioöuü".toCharArray();
        char sonEk = 'i';

        for (int i = isim.length() - 1; i >= 0; i--) {
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

        if (Arrays.binarySearch(unluler, isim.charAt(isim.length() - 1)) >= 0) {
            return "y" + sonEk;
        }

        return String.valueOf(sonEk);
    }
}
