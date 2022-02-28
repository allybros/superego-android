package com.allybros.superego.fragment.search

import android.content.BroadcastReceiver
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.allybros.superego.R
import android.widget.AdapterView.OnItemClickListener
import android.annotation.SuppressLint
import com.allybros.superego.unit.ConstantValues
import android.content.Intent
import com.allybros.superego.activity.webview.WebViewActivity
import android.text.TextWatcher
import android.net.ConnectivityManager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.BaseTransientBottomBar
import android.text.Editable
import com.daimajia.androidanimations.library.YoYo
import com.daimajia.androidanimations.library.Techniques
import com.allybros.superego.api.SearchTask
import android.content.Context
import com.allybros.superego.adapter.SearchAdapter
import org.json.JSONArray
import org.json.JSONException
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.content.IntentFilter
import android.util.Log
import android.view.View
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.allybros.superego.databinding.FragmentSearchBinding
import com.allybros.superego.unit.User
import java.util.*

/**
 * Search Fragment Class
 * @author umutalacam
 */
class SearchFragment : Fragment() {
    private val searchResponseReceiver: BroadcastReceiver
    
    lateinit var binding: FragmentSearchBinding
    val viewModel: SearchVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search,
            container,
            false
        )
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        binding.listViewSearchResults.onItemClickListener = OnItemClickListener { adapterView, _, i, _ ->
            val u = adapterView.getItemAtPosition(i) as User
            // Set web activity title
            val belirtmeHali = "" + turkceBelirtmeHalEkiBulucu(u.username)
            @SuppressLint("StringFormatMatches") val webActivityTitle =
                requireContext().getString(R.string.activity_label_rate_user, u.username, belirtmeHali)
            val testUrl = ConstantValues.RATE_URL + u.testId
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra("url", testUrl)
            intent.putExtra("title", webActivityTitle)
            intent.putExtra("slidr", false)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            requireContext().startActivity(intent)
        }
        binding.etSearchUser.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                // Check internet connection
                val cm =
                    context!!.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
                val activeNetwork = cm.activeNetworkInfo
                val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
                if (isConnected) {
                    performSearch(charSequence.toString())
                } else {
                    Log.d("CONNECTION", isConnected.toString())
                    Snackbar.make(
                        binding.constraintSearchFragment,
                        R.string.error_no_connection,
                        BaseTransientBottomBar.LENGTH_LONG
                    ).show()
                }
            }

            override fun afterTextChanged(editable: Editable) {}
        })
        binding.tvSearchHeader.setOnClickListener(View.OnClickListener {
            YoYo.with(Techniques.Hinge).duration(2000)
                .onEnd { Toast.makeText(context, "Tebrikler :) ", Toast.LENGTH_LONG).show() }
                .playOn(binding.tvSearchHeader)
        })

        //TODO: Replace when the API is updated
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(searchResponseReceiver, IntentFilter(ConstantValues.ACTION_SEARCH))
    }

    private fun performSearch(query: String) {
        SearchTask.searchTask(context, query)
    }

    /**
     * Populate list view with search results.
     * @param users List of users that intended to be shown in ListView
     */
    private fun populateResults(users: ArrayList<User>) {
        //Prevent from null pointers
        val parent = activity ?: return
        if (binding.tvSearchInfo.visibility == View.INVISIBLE) {
            if (users.size == 0) {
                //Show info if not visible
                YoYo.with(Techniques.FadeIn).duration(300).playOn(binding.ivIconSearchInfo)
                YoYo.with(Techniques.FadeIn).duration(300).playOn(binding.tvSearchInfo)
                binding.ivIconSearchInfo.visibility = View.VISIBLE
                binding.tvSearchInfo.visibility = View.VISIBLE
                // Hide list
                YoYo.with(Techniques.FadeOut).duration(300).playOn(binding.listViewSearchResults)
                binding.listViewSearchResults.visibility = View.INVISIBLE
            }
        } else {
            if (users.size > 0) {
                // Result set is not empty, hide search info
                YoYo.with(Techniques.FadeOut).duration(200).playOn(binding.ivIconSearchInfo)
                YoYo.with(Techniques.FadeOut).duration(200).playOn(binding.tvSearchInfo)
                binding.ivIconSearchInfo.visibility = View.INVISIBLE
                binding.tvSearchInfo.visibility = View.INVISIBLE
                //Show list
                YoYo.with(Techniques.FadeIn).duration(300).playOn(binding.listViewSearchResults)
                binding.listViewSearchResults.visibility = View.VISIBLE
            }
        }
        val adapter = SearchAdapter(parent.applicationContext, users)
        binding.listViewSearchResults.adapter = adapter
    }

    /**
     * Builds title for specifically Turkish language structure
     * İsmin belirtme haline uygun bir başlık oluşturur. (Orçun'u, Umut'u)
     * Türkçe de pek yakıştı değil mi?
     */
    private fun turkceBelirtmeHalEkiBulucu(isim: String): String {
        val unluler = "aeıioöuü".toCharArray()
        var sonEk = 'i'
        var i = isim.length - 1
        while (i >= 0) {
            val c = isim[i]
            when (c) {
                'a', 'ı' -> {
                    sonEk = 'ı'
                    i = -1
                }
                'e', 'i' -> {
                    sonEk = 'i'
                    i = -1
                }
                'o', 'u' -> {
                    sonEk = 'u'
                    i = -1
                }
                'ö', 'ü' -> {
                    sonEk = 'ü'
                    i = -1
                }
            }
            i--
        }
        return if (Arrays.binarySearch(unluler, isim[isim.length - 1]) >= 0) {
            "y$sonEk"
        } else "" + sonEk
    }

    init {
        // Required empty public constructor
        searchResponseReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val query = intent.getStringExtra("query")
                val resultResponse = intent.getStringExtra("result")
                if (query != binding.etSearchUser.text.toString()) {
                    Log.d("Search Receiver", "Late response, skipping results")
                    return
                }
                try {
                    val results = JSONArray(resultResponse)
                    val usersFound = ArrayList<User>()
                    for (i in 0 until results.length()) {
                        //Decode search response
                        val result = results.getJSONObject(i)
                        val username = result.getString("username")
                        val userBio = result.getString("user_bio")
                        val testId = result.getString("test_id")
                        val avatar = result.getString("avatar")
                        //Collect users
                        val u = User(testId, username, userBio, avatar)
                        usersFound.add(u)
                    }
                    populateResults(usersFound)
                } catch (e: JSONException) {
                    Log.e("Exception on search", e.message)
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.unbind()
    }
}