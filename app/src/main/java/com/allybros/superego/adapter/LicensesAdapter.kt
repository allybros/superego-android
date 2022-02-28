package com.allybros.superego.adapter

import android.content.Context
import android.widget.ArrayAdapter
import com.allybros.superego.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.AdapterView.OnItemClickListener
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ListView
import java.util.ArrayList

class LicensesAdapter(
    context: Context?,
    licenses: List<String>,
    private val parentListView: ListView
) : ArrayAdapter<String?>(
    context!!, R.layout.license_list_row, licenses
) {
    private val licenses: ArrayList<String>? = null
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        if (convertView == null) {
            val inflater = LayoutInflater.from(context)
            convertView = inflater.inflate(R.layout.license_list_row, parent, false)
        }
        val licenseData = getItem(position) ?: return convertView!!
        val licenseParts = licenseData.split(",").toTypedArray()
        if (licenseParts.size < 4) return convertView!!
        val tvLicenseLibName = convertView!!.findViewById<TextView>(R.id.licenseLibName)
        val tvLicenseName = convertView.findViewById<TextView>(R.id.licenseName)
        val tvLicenseURL = convertView.findViewById<TextView>(R.id.licenseLibURL)
        tvLicenseLibName.text = licenseParts[0]
        tvLicenseName.text = licenseParts[2]
        tvLicenseURL.text = licenseParts[3]
        return convertView
    }

    init {
        parentListView.onItemClickListener =
            OnItemClickListener { parent, view, position, id -> // URL
                val licenseLine = licenses[position]
                val licenseParts = licenseLine.split(",").toTypedArray()
                if (licenseParts.size != 4) return@OnItemClickListener
                val licenseUrl = licenseParts[3]
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(licenseUrl))
                browserIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                view.context.startActivity(browserIntent)
            }
    }
}