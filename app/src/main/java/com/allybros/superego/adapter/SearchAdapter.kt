package com.allybros.superego.adapter

import android.content.Context
import android.widget.ArrayAdapter
import com.allybros.superego.R
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.squareup.picasso.Picasso
import com.allybros.superego.unit.ConstantValues
import com.allybros.superego.unit.User
import de.hdodenhof.circleimageview.CircleImageView
import java.util.ArrayList

class SearchAdapter(context: Context, users: List<User?>?) :
    ArrayAdapter<User?>(context, R.layout.search_user_row, users!!) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (view == null) {
            val inflater = LayoutInflater.from(context)
            view = inflater.inflate(R.layout.search_user_row, parent, false)
        }
        val u = getItem(position) ?: return view!!
        val ivSearchUserAvatar: CircleImageView =
            view!!.findViewById(R.id.nivSearchUserAvatar)
        val tvSearchUsername = view.findViewById<TextView>(R.id.tvSearchUsername)
        val tvSearchUserbio = view.findViewById<TextView>(R.id.tvSearchUserBio)
        var bioSum = u.userBio
        if (u.userBio == null) {
            bioSum = view.context.getString(R.string.default_bio_search)
        } else if (u.userBio.length >= 30) {
            bioSum = u.userBio.substring(0, 30)
            bioSum += "..."
        }
        tvSearchUsername.text = u.username
        tvSearchUserbio.text = bioSum
        Picasso.get().load(u.avatarName).into(ivSearchUserAvatar)
        return view
    }
}