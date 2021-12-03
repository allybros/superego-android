package com.allybros.superego.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.allybros.superego.R;

import java.util.ArrayList;

public class LicensesAdapter extends ArrayAdapter<String> {

    private ListView parentListView;
    private ArrayList<String> licenses;

    public LicensesAdapter(Context context, final ArrayList<String> licenses, ListView parentListView) {
        super(context, R.layout.license_list_row, licenses);
        this.parentListView = parentListView;
        parentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // URL
                String licenseLine = licenses.get(position);
                String[] licenseParts = licenseLine.split(",");
                if (licenseParts.length != 4) return;
                String licenseUrl = licenseParts[3];

                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(licenseUrl));
                browserIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                view.getContext().startActivity(browserIntent);
            }
        });
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.license_list_row, parent,false);
        }

        String licenseData = getItem(position);
        if (licenseData == null) return convertView;
        final String[] licenseParts = licenseData.split(",");
        if (licenseParts.length < 4) return convertView;

        TextView tvLicenseLibName = convertView.findViewById(R.id.licenseLibName);
        TextView tvLicenseName = convertView.findViewById(R.id.licenseName);
        TextView tvLicenseURL = convertView.findViewById(R.id.licenseLibURL);

        tvLicenseLibName.setText(licenseParts[0]);
        tvLicenseName.setText(licenseParts[2]);
        tvLicenseURL.setText(licenseParts[3]);

        return convertView;
    }


}
