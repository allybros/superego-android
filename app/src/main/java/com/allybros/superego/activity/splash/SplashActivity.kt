package com.allybros.superego.activity.splash

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.allybros.superego.R
import android.net.ConnectivityManager
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.allybros.superego.api.LoadProfileTask
import com.allybros.superego.unit.ConstantValues
import com.allybros.superego.unit.ErrorCodes
import com.allybros.superego.api.LoginTask
import com.google.android.material.snackbar.Snackbar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlin.jvm.Synchronized
import com.allybros.superego.activity.userpage.UserPageActivity
import com.allybros.superego.activity.login.LoginActivity
import com.allybros.superego.databinding.ActivitySplashBinding
import com.android.volley.toolbox.Volley
import com.allybros.superego.unit.Trait
import com.allybros.superego.util.SessionManager
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import org.json.JSONObject
import org.json.JSONException
import java.util.ArrayList
import kotlin.system.exitProcess

class SplashActivity : AppCompatActivity() {
    private var loadProfileRegister: BroadcastReceiver? = null
    private var loginReceiver: BroadcastReceiver? = null
    var loadTaskLock = false
    var getTraitsLock = false

    lateinit var binding: ActivitySplashBinding
    val viewModel: SplashVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash)

        // Check internet connection
        val cm = applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
        if (isConnected) {
            getAllTraits(applicationContext)
            setupReceivers()
            SessionManager.getInstance().readInfo(applicationContext)
            if (SessionManager.getInstance().sessionToken.isNotEmpty()) {
                // User signed in before
                LoadProfileTask.loadProfileTask(
                    applicationContext,
                    SessionManager.getInstance().sessionToken, ConstantValues.ACTION_LOAD_PROFILE
                )
            } else {
                returnLoginActivity()
            }
        } else {
            AlertDialog.Builder(this@SplashActivity, R.style.SegoAlertDialog)
                .setTitle("insightof.me")
                .setMessage(R.string.error_no_connection)
                .setPositiveButton(
                    getString(R.string.action_ok)) { _, _ ->
                    finish()
                    exitProcess(0)
                }.show()
        }
    }

    private fun setupReceivers() {
        loadProfileRegister = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.getIntExtra("status", ErrorCodes.SYSFAIL)) {
                    ErrorCodes.SUCCESS -> {
                        // Profile loaded successfully, current user must be set on SessionManager
                        loadTaskLock = true
                        notifyTaskComplete()
                    }
                    ErrorCodes.SESSION_EXPIRED -> {
                        // Session is not valid, or expired. Try to login again.
                        val uid = SessionManager.getInstance().userId
                        val password = SessionManager.getInstance().password
                        // Check if user login data present
                        if (uid.isEmpty() || password.isEmpty()) {
                            returnLoginActivity()
                        } else {
                            LoginTask.loginTask(this@SplashActivity, uid, password)
                        }
                    }
                    ErrorCodes.SYSFAIL -> AlertDialog.Builder(
                        this@SplashActivity,
                        R.style.SegoAlertDialog
                    )
                        .setTitle("insightof.me")
                        .setMessage(R.string.error_login_failed)
                        .setPositiveButton(
                            getString(R.string.action_ok)) { _, _ ->
                            SessionManager.getInstance().clearSession(
                                applicationContext
                            )
                            returnLoginActivity()
                        }.show()
                }
            }
        }
        loginReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.getIntExtra("status", 0)) {
                    ErrorCodes.SUCCESS ->                         // Login succeeded, start load profile task
                        LoadProfileTask.loadProfileTask(
                            applicationContext,
                            SessionManager.getInstance().sessionToken,
                            ConstantValues.ACTION_LOAD_PROFILE
                        )
                    ErrorCodes.CONNECTION_ERROR -> AlertDialog.Builder(
                        this@SplashActivity,
                        R.style.SegoAlertDialog
                    )
                        .setTitle("insightof.me")
                        .setMessage(R.string.error_no_connection)
                        .setPositiveButton(
                            getString(R.string.action_ok)) { _, _ ->
                            finish()
                            exitProcess(0)
                        }.show()
                    else -> {
                        Snackbar.make(
                            window.decorView,
                            R.string.error_login_again,
                            2000
                        ).show()
                        returnLoginActivity()
                    }
                }
            }
        }
        LocalBroadcastManager.getInstance(applicationContext)
            .registerReceiver(loginReceiver!!, IntentFilter(ConstantValues.ACTION_LOGIN))
        LocalBroadcastManager.getInstance(applicationContext)
            .registerReceiver(loadProfileRegister!!, IntentFilter(ConstantValues.ACTION_LOAD_PROFILE))
    }

    /**
     * Checks if both task are completed. If both of them completed, starts UserPageActivity
     */
    @Synchronized
    private fun notifyTaskComplete() {
        if (loadTaskLock && getTraitsLock) {
            // Both task are completed
            LocalBroadcastManager.getInstance(applicationContext)
                .unregisterReceiver((loadProfileRegister)!!)
            LocalBroadcastManager.getInstance(applicationContext)
                .unregisterReceiver((loginReceiver)!!)
            val i = Intent(this@SplashActivity, UserPageActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(i)
            finish()
        }
    }

    /**
     * Starts login activity and finished this activity
     */
    private fun returnLoginActivity() {
        LocalBroadcastManager.getInstance(applicationContext)
            .unregisterReceiver((loadProfileRegister)!!)
        LocalBroadcastManager.getInstance(applicationContext).unregisterReceiver((loginReceiver)!!)
        val i = Intent(this@SplashActivity, LoginActivity::class.java)
        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(i)
        finish()
    }

    /**
     * Sets all trait list
     * @param context    require for sending request
     */
    private fun getAllTraits(context: Context?) {
        val queue = Volley.newRequestQueue(context)
        val traits = ArrayList<Trait>()
        val jsonRequest = StringRequest(
            Request.Method.GET,
            ConstantValues.ALL_TRAITS, { response ->
                Log.d("getAllTraits",response.toString());
                try {
                    val jsonObject = JSONObject(response)
                    for (i in 0 until jsonObject.getJSONArray("o").length()) {
                        val iter = jsonObject.getJSONArray("o")[i] as JSONObject
                        val traitNo: Int = iter.getInt("traitNo")
                        val positiveName: String? = iter.getString("positive")
                        val negativeName: String? = iter.getString("negative")
                        val positiveIcon: String? = iter.getString("positive_icon")
                        val negativeIcon: String? = iter.getString("negative_icon")
                        traits.add(
                            Trait(
                                traitNo,
                                positiveName,
                                negativeName,
                                positiveIcon,
                                negativeIcon
                            )
                        )
                    }
                    for (i in 0 until jsonObject.getJSONArray("c").length()) {
                        val iter = jsonObject.getJSONArray("c")[i] as JSONObject
                        val traitNo: Int = iter.getInt("traitNo")
                        val positiveName: String? = iter.getString("positive")
                        val negativeName: String? = iter.getString("negative")
                        val positiveIcon: String? = iter.getString("positive_icon")
                        val negativeIcon: String? = iter.getString("negative_icon")
                        traits.add(
                            Trait(
                                traitNo,
                                positiveName,
                                negativeName,
                                positiveIcon,
                                negativeIcon
                            )
                        )
                    }
                    for (i in 0 until jsonObject.getJSONArray("e").length()) {
                        val iter = jsonObject.getJSONArray("e")[i] as JSONObject
                        val traitNo: Int = iter.getInt("traitNo")
                        val positiveName: String? = iter.getString("positive")
                        val negativeName: String? = iter.getString("negative")
                        val positiveIcon: String? = iter.getString("positive_icon")
                        val negativeIcon: String? = iter.getString("negative_icon")
                        traits.add(
                            Trait(
                                traitNo,
                                positiveName,
                                negativeName,
                                positiveIcon,
                                negativeIcon
                            )
                        )
                    }
                    for (i in 0 until jsonObject.getJSONArray("a").length()) {
                        val iter = jsonObject.getJSONArray("a")[i] as JSONObject
                        val traitNo: Int = iter.getInt("traitNo")
                        val positiveName: String? = iter.getString("positive")
                        val negativeName: String? = iter.getString("negative")
                        val positiveIcon: String? = iter.getString("positive_icon")
                        val negativeIcon: String? = iter.getString("negative_icon")
                        traits.add(
                            Trait(
                                traitNo,
                                positiveName,
                                negativeName,
                                positiveIcon,
                                negativeIcon
                            )
                        )
                    }
                    for (i in 0 until jsonObject.getJSONArray("n").length()) {
                        val iter = jsonObject.getJSONArray("n")[i] as JSONObject
                        val traitNo: Int = iter.getInt("traitNo")
                        val positiveName: String? = iter.getString("positive")
                        val negativeName: String? = iter.getString("negative")
                        val positiveIcon: String? = iter.getString("positive_icon")
                        val negativeIcon: String? = iter.getString("negative_icon")
                        traits.add(
                            Trait(
                                traitNo,
                                positiveName,
                                negativeName,
                                positiveIcon,
                                negativeIcon
                            )
                        )
                    }
                    getTraitsLock = true
                    notifyTaskComplete()
                    Trait.setAllTraits(traits)
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        ) {
            getTraitsLock = true
            notifyTaskComplete()
        }
        queue.add(jsonRequest)
    }
}