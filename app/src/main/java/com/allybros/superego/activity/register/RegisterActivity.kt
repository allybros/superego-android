package com.allybros.superego.activity.register

import android.content.*
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.facebook.CallbackManager
import android.os.Bundle
import com.allybros.superego.R
import com.allybros.superego.unit.ErrorCodes
import com.allybros.superego.api.LoginTask
import com.allybros.superego.activity.splash.SplashActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.allybros.superego.unit.ConstantValues
import com.allybros.superego.api.RegisterTask
import android.os.Build
import android.text.Html
import android.text.TextWatcher
import android.text.Editable
import android.util.Log
import android.view.View
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.facebook.login.LoginManager
import com.facebook.FacebookCallback
import com.facebook.login.LoginResult
import com.facebook.AccessToken
import com.allybros.superego.api.SocialMediaSignInTask
import com.facebook.FacebookException
import com.allybros.superego.activity.login.LoginActivity
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.allybros.superego.databinding.ActivityRegisterBinding
import com.google.android.gms.tasks.Task
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private var registerReceiver: BroadcastReceiver? = null
    private var autoLoginReceiver: BroadcastReceiver? = null
    private var usernameInput: String? = null
    private var emailInput: String? = null
    private var passwordInput: String? = null
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private var callbackManager: CallbackManager? = null

    private val viewModel: RegisterVM by viewModels()
    lateinit var binding: ActivityRegisterBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)
        setupReceivers()
        setupUi()
    }

    private fun setupReceivers() {
        /* Catches broadcasts of api/RegisterTask class */
        registerReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val status = intent.getIntExtra("status", 0)
                Log.d("Register receiver", "Got message: $status")
                setProgress(false)
                when (status) {
                    ErrorCodes.SUCCESS -> {
                        //Auto Login User
                        LoginTask.loginTask(applicationContext, usernameInput, passwordInput)
                        setProgress(true)
                    }
                    ErrorCodes.SYSFAIL -> AlertDialog.Builder(
                        applicationContext, R.style.SegoAlertDialog
                    )
                        .setTitle(R.string.alert_sign_up)
                        .setMessage(R.string.error_no_connection)
                        .setPositiveButton(R.string.action_ok, null)
                        .show()
                    ErrorCodes.USERNAME_NOT_LEGAL -> binding.etRegisterUsername.setError(getString(R.string.error_username_not_legal))
                    ErrorCodes.USERNAME_ALREADY_EXIST -> binding.etRegisterUsername.setError(getString(R.string.error_username_taken))
                    ErrorCodes.EMAIL_ALREADY_EXIST -> binding.etRegisterMail.setError(getString(R.string.error_email_already_exist))
                    ErrorCodes.EMAIL_NOT_LEGAL -> binding.etRegisterMail.setError(getString(R.string.error_email_not_legal))
                    ErrorCodes.PASSWORD_NOT_LEGAL -> binding.etRegisterPassword.setError(getString(R.string.error_password_not_legal))
                }
            }
        }

        /* Automatically called login task receiver which is called when register operation succeed. */autoLoginReceiver =
            object : BroadcastReceiver() {
                override fun onReceive(context: Context, intent: Intent) {
                    val status = intent.getIntExtra("status", ErrorCodes.SYSFAIL)
                    //User logged in successfully
                    if (status == ErrorCodes.SUCCESS) {
                        Log.d("Register Activity", "Automated login succeeded")
                        //Redirect to splash
                        val i = Intent(this@RegisterActivity, SplashActivity::class.java)
                        startActivity(i)
                        finish()
                        // Finish the parent for performance
                        if (parent != null) parent.finish()
                    }
                }
            }
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(registerReceiver!!, IntentFilter(ConstantValues.ACTION_REGISTER))
        LocalBroadcastManager.getInstance(this)
            .registerReceiver(autoLoginReceiver!!, IntentFilter(ConstantValues.ACTION_LOGIN))
    }

    private fun setupUi() {
        binding.btSignUp.setOnClickListener {
            binding.etRegisterUsername.clearError()
            binding.etRegisterMail.clearError()
            binding.etRegisterPassword.clearError()
            binding.checkboxAgreement.background = ContextCompat.getDrawable(
                applicationContext,
                R.drawable.selector_check_box
            )
            usernameInput = binding.etRegisterUsername.text
            emailInput = binding.etRegisterMail.text
            passwordInput = binding.etRegisterPassword.text
            val conditions = binding.checkboxAgreement.isChecked


            //Validate fields
            if (usernameInput?.isEmpty() == true) {
                binding.etRegisterUsername.setError(resources.getString(R.string.error_username_empty))
            }
            if (emailInput?.isEmpty() == true) {
                binding.etRegisterMail.setError(resources.getString(R.string.error_email_empty))
            }
            if (passwordInput?.isEmpty() == true) {
                binding.etRegisterPassword.setError(resources.getString(R.string.error_password_empty))
            }
            if (!conditions) {
                binding.checkboxAgreement.background = ContextCompat.getDrawable(
                    applicationContext,
                    R.drawable.checkbox_error
                )
            }
            if (usernameInput?.isNotEmpty() == true
                && emailInput?.isNotEmpty() == true
                && passwordInput?.isNotEmpty() == true
                && conditions
            ) {
                Log.d("Register request send", "Register request send")
                setProgress(true)
                RegisterTask.registerTask(
                    applicationContext,
                    binding.etRegisterUsername.text.toString(),
                    binding.etRegisterMail.text.toString(),
                    binding.etRegisterPassword.text.toString(),
                    true
                )
            }
        }
        binding.tvAgreementRegister.setOnClickListener {
            val url = resources.getString(R.string.conditions_link)
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            binding.tvSignIn.setText(
                Html.fromHtml(resources.getString(R.string.action_login)),
                TextView.BufferType.SPANNABLE
            )
        }
        binding.etRegisterPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //this method is empty
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //this method is empty
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isEmpty()) {
                    binding.etRegisterPassword.setError(getString(R.string.error_password_empty))
                } else {
                    binding.etRegisterPassword.clearError()
                }
            }
        })
        binding.checkboxAgreement.setOnClickListener {
            binding.checkboxAgreement.background = ContextCompat.getDrawable(
                applicationContext,
                R.drawable.selector_check_box
            )
        }

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

        //Set Facebook Sign In
        binding.btSignInFacebook.setOnClickListener { binding.btHiddenFacebook.callOnClick() }
        callbackManager = CallbackManager.Factory.create()
        binding.btHiddenFacebook.setPermissions(mutableListOf("email", "public_profile"))
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
     * Shows progress view upon login form and disables form items,
     * Indicates a process is going on
     * @param visible Set true when progress view needs to be shown.
     */
    private fun setProgress(visible: Boolean) {
        if (visible) {
            // Disable form elements
            binding.etRegisterUsername.isEnabled = false
            binding.etRegisterMail.isEnabled = false
            binding.etRegisterPassword.isEnabled = false
            binding.checkboxAgreement.isEnabled = false
            binding.btSignUp.isEnabled = false
            binding.cardFormRegister.alpha = 0.8f
            binding.progressViewRegister.visibility = View.VISIBLE
        } else {
            //Enable form elements
            binding.etRegisterUsername.isEnabled = true
            binding.etRegisterMail.isEnabled = true
            binding.etRegisterPassword.isEnabled = true
            binding.checkboxAgreement.isEnabled = true
            binding.btSignUp.isEnabled = true
            binding.cardFormRegister.alpha = 1f
            binding.progressViewRegister.visibility = View.GONE
        }
    }

    override fun onBackPressed() {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    fun onSignInButtonClicked() {
        val intent = Intent(applicationContext, LoginActivity::class.java)
        startActivity(intent)
        finish()
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
            AlertDialog.Builder(this@RegisterActivity, R.style.SegoAlertDialog)
                .setTitle("insightof.me")
                .setMessage(R.string.error_google_signin)
                .setPositiveButton(getString(R.string.action_ok)) { dialog, which -> dialog.dismiss() }
                .show()
        }
    }

    companion object {
        private const val RC_SIGN_IN = 0 // It require to come back from Google Sign in intent
    }
}