package com.allybros.superego.fragment.result.complate

import com.allybros.superego.adapter.ScoresAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import com.allybros.superego.R
import android.annotation.SuppressLint
import android.content.*
import android.net.ConnectivityManager
import android.util.Log
import com.allybros.superego.api.LoadProfileTask
import com.allybros.superego.unit.ConstantValues
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.allybros.superego.activity.userpage.UserPageActivity
import com.allybros.superego.unit.ErrorCodes
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.view.View
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.allybros.superego.databinding.FragmentResultsCompleteBinding
import com.allybros.superego.unit.User
import com.allybros.superego.util.SessionManager

class ResultComplateFragment : Fragment() {

    private val currentUser: User = SessionManager.getInstance().user
    private var resultsRefreshReceiver: BroadcastReceiver? = null

    private val sessionManager = SessionManager.getInstance()

    lateinit var binding: FragmentResultsCompleteBinding
    val viewModel: ResultComplateVM by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_results_complete,
            container,
            false
        )
        return binding.root
    }

    @SuppressLint("StringFormatMatches")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupReceiver()
        setupView()
    }

    //Set up view objects
    @SuppressLint("StringFormatMatches")
    private fun setupView() {
        //Setup refresher
        binding.swipeLayout.setOnRefreshListener {
            // Check internet connection
            val cm = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
            if (isConnected) {
                //Start load task
                LoadProfileTask.loadProfileTask(
                    context,
                    SessionManager.getInstance().sessionToken,
                    ConstantValues.ACTION_REFRESH_RESULTS
                )
            } else {
                Snackbar.make(
                    binding.swipeLayout,
                    R.string.error_no_connection,
                    BaseTransientBottomBar.LENGTH_LONG
                ).show()
                Log.d("CONNECTION", isConnected.toString())
                binding.swipeLayout.isRefreshing = false
            }
        }

        binding.listViewTraits.adapter = ScoresAdapter(
            activity,
            currentUser.scores,
            true
        ) { shareResults() }
    }

    //Set up refresh receiver
    private fun setupReceiver() {
        resultsRefreshReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val status = intent.getIntExtra("status", 0)
                binding.swipeLayout.isRefreshing = false

                //Update fragments
                val pageActivity = activity as UserPageActivity?
                pageActivity?.refreshFragments(1)
                if (status == ErrorCodes.SUCCESS) {
                    Log.d("Profile refresh", "Success")
                    binding.swipeLayout.isRefreshing = false
                } else {
                    binding.swipeLayout.isRefreshing = false //Last
                    Toast.makeText(
                        getContext(),
                        requireContext().getString(R.string.error_no_connection),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        //TODO: Replace when new API package is developed
        LocalBroadcastManager.getInstance(requireContext()).registerReceiver(
            resultsRefreshReceiver!!,
            IntentFilter(ConstantValues.ACTION_REFRESH_RESULTS)
        )
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(resultsRefreshReceiver!!)
        super.onDestroy()
    }

    /**
     * Shows share result dialog
     */
    private fun shareResults() {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        val testResultId = SessionManager.getInstance().user.testResultId
        val testUrl = String.format("https://insightof.me/%s", sessionManager.user.testId)
        val shareBody = getString(
            R.string.body_share_results, """
     $testResultId
     
     """.trimIndent(), testUrl
        )
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.action_btn_share_results)
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody)
        startActivity(
            Intent.createChooser(
                sharingIntent,
                getString(R.string.action_btn_share_results)
            )
        )
    }
}