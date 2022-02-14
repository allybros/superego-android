package com.allybros.superego.activity.changepassword

import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import android.widget.EditText
import android.content.BroadcastReceiver
import android.content.Context
import android.os.Bundle
import com.allybros.superego.R
import android.content.Intent
import com.allybros.superego.unit.ErrorCodes
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.snackbar.BaseTransientBottomBar
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.content.IntentFilter
import com.allybros.superego.unit.ConstantValues
import android.text.TextWatcher
import android.text.Editable
import android.text.method.PasswordTransformationMethod
import android.text.method.HideReturnsTransformationMethod
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.allybros.superego.activity.editprofile.EditProfileVM
import com.allybros.superego.api.PasswordChangeTask
import com.allybros.superego.databinding.ActivityChangePasswordBinding
import com.allybros.superego.databinding.ActivityEditProfileBinding
import com.allybros.superego.util.SessionManager

class ChangePasswordActivity : AppCompatActivity() {
    private var changePasswordReceiver: BroadcastReceiver? = null

    private val viewModel: ChangePasswordVM by viewModels()
    lateinit var binding: ActivityChangePasswordBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_change_password)

        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)

        changePasswordReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                val status = intent.getIntExtra("status", 0)
                setProgressVisibility(false)
                when (status) {
                    ErrorCodes.SUCCESS -> Snackbar.make(
                        binding.contentRootChangePassword,
                        R.string.message_process_succeed,
                        BaseTransientBottomBar.LENGTH_LONG
                    ).show()
                    ErrorCodes.PASSWORD_NOT_LEGAL -> {
                        setError(binding.etNewPassword, getString(R.string.error_password_not_legal))
                        clearError(binding.etNewPasswordAgain)
                    }
                    ErrorCodes.UNAUTHORIZED -> setError(
                        binding.etOldPassword,
                        getString(R.string.error_current_password_wrong)
                    )
                    else -> Snackbar.make(
                        binding.contentRootChangePassword,
                        R.string.error_no_connection,
                        BaseTransientBottomBar.LENGTH_LONG
                    )
                        .setAction(R.string.action_try_again) { attemptChangePassword() }.show()
                }
            }
        }
        LocalBroadcastManager.getInstance(applicationContext)
            .registerReceiver(
                changePasswordReceiver!!,
                IntentFilter(ConstantValues.ACTION_PASSWORD_CHANGE)
            )
        initListener()
    }

    private fun initListener() {
        binding.ivBack.setOnClickListener { onBackPressed() }
        binding.etOldPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //this method is empty
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //this method is empty
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isEmpty()) {
                    setError(binding.etOldPassword, getString(R.string.error_password_empty))
                } else {
                    clearError(binding.etOldPassword)
                }
            }
        })
        binding.etNewPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //this method is empty
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //this method is empty
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isEmpty()) {
                    setError(binding.etNewPassword, getString(R.string.input_error_enter_new_pass))
                } else {
                    clearError(binding.etNewPassword)
                }
            }
        })
        binding.etNewPasswordAgain.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //this method is empty
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //this method is empty
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isEmpty()) {
                    setError(binding.etNewPasswordAgain, getString(R.string.input_error_password_mismatch))
                } else {
                    clearError(binding.etNewPasswordAgain)
                }
            }
        })
        binding.btChangePassword.setOnClickListener { attemptChangePassword() }
        binding.ivOldPassToggle.setOnClickListener {
            changePasswordVisibility(
                binding.ivOldPassToggle,
                binding.etOldPassword
            )
        }
        binding.ivNewPassToggle.setOnClickListener {
            changePasswordVisibility(
                binding.ivNewPassToggle,
                binding.etNewPassword
            )
        }
        binding.ivNewPassAgainToggle.setOnClickListener {
            changePasswordVisibility(
                binding.ivNewPassAgainToggle,
                binding.etNewPasswordAgain
            )
        }
    }

    private fun changePasswordVisibility(imageView: ImageView?, editText: EditText?) {
        if (editText!!.transformationMethod == PasswordTransformationMethod.getInstance()) {
            imageView!!.setImageResource(R.drawable.ic_visibility_off)
            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
        } else {
            imageView!!.setImageResource(R.drawable.ic_visibility)
            editText.transformationMethod = PasswordTransformationMethod.getInstance()
        }
    }

    private fun attemptChangePassword() {
        // Show progress indicator.
        clearError(binding.etOldPassword)
        clearError(binding.etNewPassword)
        clearError(binding.etNewPasswordAgain)

        // Get user inputs.
        val oldPass = binding.etOldPassword.text.toString()
        val newPass = binding.etNewPassword.text.toString()
        val newPassAgain = binding.etNewPasswordAgain.text.toString()

        // Check required fields.
        if (oldPass.isEmpty()) {
            setError(binding.etOldPassword, getString(R.string.input_error_enter_current_password))
        }
        if (newPass.isEmpty()) {
            setError(binding.etNewPassword, getString(R.string.input_error_enter_new_pass))
        } else if (oldPass.isNotEmpty() && newPass != newPassAgain) {
            setError(binding.etNewPassword, getString(R.string.input_error_password_mismatch))
            setError(binding.etNewPasswordAgain, getString(R.string.input_error_password_mismatch))
        } else if (oldPass.isNotEmpty()) {
            // Check internet connection
            val cm =
                applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
            if (isConnected) {
                setProgressVisibility(true)
                PasswordChangeTask.passwordChangeTask(
                    applicationContext,
                    SessionManager.getInstance().sessionToken,
                    oldPass,
                    newPass
                )
            } else {
                Log.d("CONNECTION", isConnected.toString())
                Snackbar.make(
                    binding.contentRootChangePassword,
                    R.string.error_no_connection,
                    BaseTransientBottomBar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun setError(editText: EditText?, errorMessage: String) {
        editText!!.hint = errorMessage
        editText.background = ContextCompat.getDrawable(applicationContext, R.drawable.et_error_background)
    }

    private fun clearError(editText: EditText?) {
        editText!!.background = ContextCompat.getDrawable(applicationContext, R.drawable.et_background)
    }

    /**
     * Sets progress view visibility.
     * @param visible Set true when progress indicator needs to be shown.
     */
    private fun setProgressVisibility(visible: Boolean) {
        if (visible) {
            binding.progressChangePassword.visibility = View.VISIBLE
            binding.cardFormChangePassword.alpha = 0.5f
            binding.btChangePassword.isEnabled = false
        } else {
            binding.progressChangePassword.visibility = View.INVISIBLE
            binding.cardFormChangePassword.alpha = 1f
            binding.btChangePassword.isEnabled = true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    fun onBackPressed(view: View?) {
        onBackPressed()
    }
}