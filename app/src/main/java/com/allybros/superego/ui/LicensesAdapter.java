package com.allybros.superego.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.allybros.superego.R;
import com.allybros.superego.unit.Score;

import java.util.ArrayList;

public class LicensesAdapter extends ArrayAdapter<String> {

    public LicensesAdapter(Context context, ArrayList<String> licenses) {
        super(context, R.layout.license_list_row, licenses);
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
        String[] licenseParts = licenseData.split(",");
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
