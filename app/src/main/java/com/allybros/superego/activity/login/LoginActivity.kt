package com.allybros.superego.activity.login

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.facebook.CallbackManager
import android.os.Bundle
import com.allybros.superego.R
import com.allybros.superego.unit.ErrorCodes
import com.allybros.superego.activity.splash.SplashActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.allybros.superego.unit.ConstantValues
import android.os.Build
import android.text.Html
import com.allybros.superego.api.LoginTask
import android.text.TextWatcher
import android.text.Editable
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.allybros.superego.activity.playground.PlayGroundActivity
import com.facebook.login.LoginManager
import com.facebook.FacebookCallback
import com.facebook.login.LoginResult
import com.facebook.AccessToken
import com.allybros.superego.api.SocialMediaSignInTask
import com.facebook.FacebookException
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.allybros.superego.activity.register.RegisterActivity
import com.allybros.superego.databinding.ActivityLoginBinding
import com.google.android.gms.tasks.Task
import java.util.*

class LoginActivity : AppCompatActivity() {
    var mGoogleSignInClient: GoogleSignInClient? = null
    var callbackManager: CallbackManager? = null
    private var loginSocialMediaReceiver: BroadcastReceiver? = null
    private var loginReceiver: BroadcastReceiver? = null

    private val viewModel: LoginVM by viewModels()
    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        setupReceivers()
        setupUi()
    }

    private fun setupReceivers() {
        /* Catches broadcasts of api/LoginTask class */
        loginReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val status = intent.getIntExtra("status", 0)
                Log.d("Login receiver", "Status: $status")
                setProgress(false)
                when (status) {
                    ErrorCodes.SYSFAIL, ErrorCodes.CAPTCHA_REQUIRED -> {
                        binding.etLoginUid.setError(" ")
                        binding.etPassword.setError(getString(R.string.error_login_failed))
                    }
                    ErrorCodes.SUSPEND_SESSION -> {
                        val builder =
                            AlertDialog.Builder(this@LoginActivity, R.style.SegoAlertDialog)
                        builder.setTitle("insightof.me")
                        builder.setMessage(getString(R.string.error_desc_session_suspended))
                        builder.setPositiveButton(getString(R.string.action_ok)) { dialog, id -> dialog.dismiss() }
                        builder.show()
                    }
                    ErrorCodes.SUCCESS -> {
                        //Login User
                        val i = Intent(applicationContext, SplashActivity::class.java)
                        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(i)
                    }
                    ErrorCodes.CONNECTION_ERROR -> {
                        val builder1 =
                            AlertDialog.Builder(this@LoginActivity, R.style.SegoAlertDialog)
                        builder1.setTitle("insightof.me")
                        builder1.setMessage(getString(R.string.error_check_connection))
                        builder1.setPositiveButton(getString(R.string.action_ok)) { dialog, id -> dialog.dismiss() }
                        builder1.show()
                    }
                }
            }
        }

        /* Listens broadcasts of api/SocialMediaSignInTask class */loginSocialMediaReceiver =
            object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val status = intent.getIntExtra("status", 0)
                    Log.d("OAuth Receiver", "Status: $status")
                    setProgress(false)
                    when (status) {
                        ErrorCodes.EMAIL_EMPTY -> AlertDialog.Builder(
                            this@LoginActivity,
                            R.style.SegoAlertDialog
                        )
                            .setTitle("insightof.me")
                            .setMessage(R.string.error_email_empty)
                            .setPositiveButton(getString(R.string.action_ok)) { dialog, id -> }
                            .show()
                        ErrorCodes.EMAIL_NOT_LEGAL -> AlertDialog.Builder(
                            this@LoginActivity,
                            R.style.SegoAlertDialog
                        )
                            .setTitle("insightof.me")
                            .setMessage(R.string.error_email_not_legal)
                            .setPositiveButton(getString(R.string.action_ok)) { dialog, id -> }
                            .show()
                        ErrorCodes.USERNAME_NOT_LEGAL -> AlertDialog.Builder(
                            this@LoginActivity,
                            R.style.SegoAlertDialog
                        )
                            .setTitle("insightof.me")
                            .setMessage(R.string.error_username_not_legal)
                            .setPositiveButton(getString(R.string.action_ok)) { dialog, id -> }
                            .show()
                        ErrorCodes.SUCCESS -> {
                            val i = Intent(applicationContext, SplashActivity::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(i)
                        }
                        ErrorCodes.SYSFAIL, ErrorCodes.CONNECTION_ERROR -> AlertDialog.Builder(
                            this@LoginActivity,
                            R.style.SegoAlertDialog
                        )
                            .setTitle("insightof.me")
                            .setMessage(R.string.error_no_connection)
                            .setPositiveButton(getString(R.string.action_ok)) { dialog, id -> }
                            .show()
                    }
                }
            }

        //Registers Receivers
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(loginReceiver!!, IntentFilter(ConstantValues.ACTION_LOGIN))
        LocalBroadcastManager.getInstance(this).registerReceiver(
            loginSocialMediaReceiver!!,
            IntentFilter(ConstantValues.ACTION_SOCIAL_MEDIA_LOGIN)
        )
    }

    private fun setupUi() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            binding.tvRegister.setText(
                Html.fromHtml(resources.getString(R.string.desc_call_register)),
                TextView.BufferType.SPANNABLE
            )
        }
        binding.btLogin.setOnClickListener {
            binding.etPassword.clearError()
            binding.etLoginUid.clearError()
            if (binding.etLoginUid.text.isEmpty()) {
                binding.etLoginUid.setError(getString(R.string.error_username_empty))
            }
            if (binding.etPassword.text.isEmpty()) {
                binding.etPassword.setError(getString(R.string.error_password_empty))
            }
            if (binding.etPassword.text.isNotEmpty() && binding.etLoginUid.text.toString().isNotEmpty()) {
                setProgress(true)
                LoginTask.loginTask(
                    applicationContext,
                    binding.etLoginUid.text.toString(),
                    binding.etPassword.text.toString()
                )
            }
        }
        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //this method is empty
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //this method is empty
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isEmpty()) {
                    binding.etPassword.setError(getString(R.string.error_password_empty))
                } else {
                    binding.etPassword.clearError()
                }
            }
        })

        //Set Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.GOOGLE_CLIENT_ID))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(applicationContext, gso)
        binding.btSignInGoogle.setOnClickListener {
            val signInIntent = mGoogleSignInClient!!.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }
        binding.btHi.setOnClickListener {
            startActivity(
                Intent(
                    applicationContext,
                    PlayGroundActivity::class.java
                )
            )
        }

        //Set Facebook Sign In
        binding.btHiddenFacebook.setOnClickListener { binding.btHiddenFacebook.callOnClick() }
        callbackManager = CallbackManager.Factory.create()
        binding.btHiddenFacebook.setPermissions(Arrays.asList("email", "public_profile"))
        callbackManager = CallbackManager.Factory.create()
        LoginManager.getInstance().registerCallback(callbackManager,
            object : FacebookCallback<LoginResult?> {
                override fun onSuccess(loginResult: LoginResult?) {
                    // App code
                    val accessToken = AccessToken.getCurrentAccessToken()
                    Log.d("Facebook", "B" + accessToken.token)
                    SocialMediaSignInTask.loginTask(
                        applicationContext,
                        accessToken.token,
                        "facebook"
                    )
                }

                override fun onCancel() {
                    // App code
                }

                override fun onError(exception: FacebookException) {
                    // App code
                }
            })
    }

    /**
     * Provides that cacth the results that come back from Google an Facebook sign in
     * @param data is a variable that comes back from the intent. It includes data that is needed.
     * @param requestCode is a variable that defines what intent come back.
     * @param resultCode  is a variable that defines intent result.
     */
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager!!.onActivityResult(requestCode, resultCode, data)
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            setProgress(true)
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        }
    }

    /**
     * Sends request to Ally Bros Api for signing in with Google
     * @param completedTask that has provided from GoogleSignInApi. It has result of google sign in task.
     */
    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(
                ApiException::class.java
            )
            // Signed in successfully, show authenticated UI.
            Log.w(
                "GoogleSignInSuccess",
                account!!.displayName + account.email + account.photoUrl + account.idToken
            )
            SocialMediaSignInTask.loginTask(applicationContext, account.idToken, "google")
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("GoogleSignInError", "signInResult:failed code=" + e.statusCode)
            setProgress(false)
            // Show error dialog
            AlertDialog.Builder(this@LoginActivity, R.style.SegoAlertDialog)
                .setTitle("insightof.me")
                .setMessage(R.string.error_google_signin)
                .setPositiveButton(getString(R.string.action_ok)) { dialog, _ -> dialog.dismiss() }
                .show()
        }
    }

    /**
     * Shows progress view upon login form and disables form items,
     * Indicates a process is going on
     * @param visible Set true when progress view needs to be shown.
     */
    private fun setProgress(visible: Boolean) {
        if (visible) {
            binding.etPassword.isEnabled = false
            binding.etLoginUid.isEnabled = false
            binding.btLogin.isEnabled = false
            binding.progressViewLogin.visibility = View.VISIBLE
        } else {
            binding.etPassword.isEnabled = true
            binding.etLoginUid.isEnabled = true
            binding.btLogin.isEnabled = true
            binding.progressViewLogin.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        //Delete receivers
        LocalBroadcastManager.getInstance(this).unregisterReceiver(loginReceiver!!)
        LocalBroadcastManager.getInstance(this).unregisterReceiver(loginSocialMediaReceiver!!)
        super.onDestroy()
    }

    fun onRegisterButtonClicked(view: View?) {
        val intent = Intent(applicationContext, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    companion object {
        private const val RC_SIGN_IN = 0 // It require to come back from Google Sign in intent
    }
}