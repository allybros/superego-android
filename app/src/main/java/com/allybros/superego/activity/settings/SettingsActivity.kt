package com.allybros.superego.activity.settings

import android.content.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import android.os.Bundle
import android.util.Log
import com.allybros.superego.R
import com.allybros.superego.activity.editprofile.EditProfileActivity
import com.allybros.superego.activity.changepassword.ChangePasswordActivity
import com.squareup.picasso.Picasso
import com.allybros.superego.unit.ConstantValues
import com.allybros.superego.unit.ErrorCodes
import com.allybros.superego.activity.splash.SplashActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.facebook.CallbackManager
import com.facebook.login.LoginManager
import android.view.MenuItem
import android.view.View
import android.widget.ListView
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.allybros.superego.adapter.LicensesAdapter
import com.allybros.superego.databinding.ActivitySettingsBinding
import com.allybros.superego.util.SessionManager
import java.util.*

class SettingsActivity : AppCompatActivity() {
    private var logoutReceiver: BroadcastReceiver? = null

    lateinit var binding: ActivitySettingsBinding
    val viewModel: SettingsVM by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_settings)

        val appBar = supportActionBar
        appBar?.setDisplayHomeAsUpEnabled(true)
        setViews()
    }

    private fun setViews() {
        setupReceivers()
        binding.cardBtnEditProfile.setOnClickListener {
            val i = Intent(applicationContext, EditProfileActivity::class.java)
            startActivity(i)
        }
        binding.cardBtnSingOut.setOnClickListener { logout() }
        binding.cardBtnPassword.setOnClickListener {
            val i = Intent(applicationContext, ChangePasswordActivity::class.java)
            startActivity(i)
        }
        binding.cardBtnAbout.setOnClickListener { showAboutDialog() }
        binding.cardBtnLicenses.setOnClickListener { showLicensesDialog() }
        binding.ivBack.setOnClickListener { onBackPressed() }
        val username = "@" + SessionManager.getInstance().user.username
        binding.tvUsername.text = username
        Picasso.get().load(SessionManager.getInstance().user.image)
            .into(binding.ivUserAvatar)
    }

    private fun setupReceivers() {
        logoutReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val status = intent.getIntExtra("status", 0)
                Log.d("receiver", "Got message: $status")
                when (status) {
                    ErrorCodes.SYSFAIL -> Log.d("Logout-status", "" + status)
                    ErrorCodes.SUCCESS -> {
                        Log.d("Logout-status", "" + status)
                        SessionManager.getInstance().clearSession(
                            applicationContext
                        ) //Clear local variables that use login
                        val intent1 = Intent(applicationContext, SplashActivity::class.java)
                        intent1.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        applicationContext.startActivity(intent1)
                        finish()
                    }
                }
            }
        }
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(logoutReceiver!!, IntentFilter(ConstantValues.ACTION_LOGOUT))
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun logout() {
        val builder = AlertDialog.Builder(this@SettingsActivity, R.style.SegoAlertDialog)
        builder.setTitle(R.string.alert_title_end_session)
            .setMessage(R.string.alert_context_end_session)
            .setPositiveButton(R.string.action_yes) { dialogInterface, i ->
                //Sign Out Google
                if (SessionManager.getInstance().user.userType == 1) { //Google Sign out
                    val mGoogleSignInClient: GoogleSignInClient
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(getString(R.string.GOOGLE_CLIENT_ID))
                        .requestEmail()
                        .build()
                    mGoogleSignInClient = GoogleSignIn.getClient(applicationContext, gso)
                    mGoogleSignInClient.signOut()
                        .addOnCompleteListener {
                            Log.d("Google-Logout", "Google-Logout")
                            /*Ally Bros Logout
                                                                   String session_token = SessionManager.getInstance().getSessionToken();
                                                                    LogoutTask.logoutTask(getApplicationContext(), session_token);*/

                            //TODO: Bizim logout çalışınca silinmeli
                            SessionManager.getInstance().clearSession(
                                applicationContext
                            ) //Clear local variables that use login
                            val intent1 = Intent(applicationContext, SplashActivity::class.java)
                            intent1.flags =
                                Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            applicationContext.startActivity(intent1)
                            finish()
                        }
                } else if (SessionManager.getInstance().user.userType == 2) {  //Sign Out Facebook
                    //Facebook logout requirements
                    var callbackManager = CallbackManager.Factory.create()
                    binding.logoutFacebook!!.setPermissions(Arrays.asList("email", "public_profile"))
                    callbackManager = CallbackManager.Factory.create()
                    LoginManager.getInstance().logOut() //Trigger logout process


                    /*Ally Bros Logout
                                   String session_token = SessionManager.getInstance().getSessionToken();
                                            LogoutTask.logoutTask(getApplicationContext(), session_token);*/
                    //TODO: Bizim logout çalışınca silinmeli
                    SessionManager.getInstance().clearSession(
                        applicationContext
                    ) //Clear local variables that use login
                    val intent1 = Intent(applicationContext, SplashActivity::class.java)
                    intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    applicationContext.startActivity(intent1)
                    finish()
                } else { //Sign out Ally Bros

                    /*Ally Bros Logout
                                   String session_token = SessionManager.getInstance().getSessionToken();
                                    LogoutTask.logoutTask(getApplicationContext(), session_token);*/

                    //TODO: Bizim logout çalışınca silinmeli
                    SessionManager.getInstance().clearSession(
                        applicationContext
                    ) //Clear local variables that use login
                    val intent1 = Intent(applicationContext, SplashActivity::class.java)
                    intent1.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    applicationContext.startActivity(intent1)
                    finish()
                }
            }
            .setNegativeButton(R.string.action_no, null)
            .setCancelable(true).show()
    }

    private fun showAboutDialog() {
        //Inflate dialog layout
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_about, null)
        val tvWebSite = dialogView.findViewById<TextView>(R.id.tvWebSite)
        tvWebSite.setOnClickListener {
            val url = "http://" + getString(R.string.web_root)
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(url)
            startActivity(i)
        }
        //Show dialog
        val builder = AlertDialog.Builder(this@SettingsActivity, R.style.SegoAlertDialog)
        builder.setView(dialogView).show()
    }

    private fun showLicensesDialog() {

        //Infalate dialog layout
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.dialog_licenses, null)
        val listViewLicenses = dialogView.findViewById<ListView>(R.id.listviewLicenses)

        //Get licenses
        val licenses = resources.getStringArray(R.array.licenses)
        val licenseList = ArrayList(Arrays.asList(*licenses))
        val adapter = LicensesAdapter(applicationContext, licenseList, listViewLicenses)

        //Show
        listViewLicenses.adapter = adapter

        //Show dialog
        val builder = AlertDialog.Builder(this@SettingsActivity, R.style.SegoAlertDialog)
        builder.setTitle(R.string.option_licenses)
        builder.setView(dialogView).show()
    }

    fun onBackPressed(view: View?) {
        onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(logoutReceiver!!)
    }
}