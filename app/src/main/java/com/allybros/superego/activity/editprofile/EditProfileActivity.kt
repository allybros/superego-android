package com.allybros.superego.activity.editprofile

import android.widget.EditText
import com.allybros.superego.R
import android.content.Intent
import com.allybros.superego.unit.ErrorCodes
import com.google.android.material.snackbar.Snackbar
import com.allybros.superego.unit.ConstantValues
import android.net.ConnectivityManager
import com.squareup.picasso.Picasso
import com.google.android.material.snackbar.BaseTransientBottomBar
import android.text.TextWatcher
import android.text.Editable
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.allybros.superego.base.SegoActivity
import com.allybros.superego.util.HelperMethods
import com.allybros.superego.util.SessionManager
import java.io.IOException
import com.allybros.superego.databinding.ActivityEditProfileBinding
import com.allybros.superego.unit.EditProfileRequest
import com.allybros.superego.unit.UploadImageRequest

class EditProfileActivity : SegoActivity<EditProfileVM, ActivityEditProfileBinding>() {
    private val IMG_REQUEST = 1 //Needs for image selection from local storage
    private var newImagePath: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        observeEvents()
        setupUi()
        setListeners()
        setupTextWatchers()
    }

    override fun getSegoViewModel(): EditProfileVM {
        val viewModel by viewModels<EditProfileVM>()
        return viewModel
    }

    override fun getLayout(): Int = R.layout.activity_edit_profile

    private fun observeEvents(){
        getViewModel().editProfileResponseLiveData.observe(this, {
            setProgressVisibility(false)
            when (it?.status) {
                ErrorCodes.SESSION_EXPIRED -> Snackbar.make(
                    binding.editProfileLayout,
                    applicationContext.getString(R.string.error_session_expired),
                    Snackbar.LENGTH_LONG
                ).show()
                ErrorCodes.USERNAME_NOT_LEGAL -> Snackbar.make(
                    binding.editProfileLayout,
                    applicationContext.getString(R.string.error_username_not_legal),
                    Snackbar.LENGTH_LONG
                ).show()
                ErrorCodes.USERNAME_ALREADY_EXIST -> Snackbar.make(
                    binding.editProfileLayout,
                    applicationContext.getString(R.string.error_username_taken),
                    Snackbar.LENGTH_LONG
                ).show()
                ErrorCodes.EMAIL_NOT_LEGAL -> Snackbar.make(
                    binding.editProfileLayout,
                    applicationContext.getString(R.string.error_email_not_legal),
                    Snackbar.LENGTH_LONG
                ).show()
                ErrorCodes.EMAIL_ALREADY_EXIST -> Snackbar.make(
                    binding.editProfileLayout,
                    applicationContext.getString(R.string.error_email_already_exist),
                    Snackbar.LENGTH_LONG
                ).show()
                ErrorCodes.SUCCESS -> Snackbar.make(
                    binding.editProfileLayout,
                    applicationContext.getString(R.string.message_process_succeed),
                    Snackbar.LENGTH_LONG
                ).show()
                ErrorCodes.SYSFAIL -> Snackbar.make(
                    binding.editProfileLayout,
                    applicationContext.getString(R.string.error_no_connection),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })

        getViewModel().editProfileResponseLiveData.observe(this, {
            setProgressVisibility(false)
            binding.ivUserAvatarEditProfile.visibility = View.VISIBLE
            when (it.status) {
                ErrorCodes.SUCCESS -> Snackbar.make(
                    binding.editProfileLayout,
                    applicationContext.getString(R.string.message_process_succeed),
                    Snackbar.LENGTH_LONG
                ).show()
                ErrorCodes.SYSFAIL -> Snackbar.make(
                    binding.editProfileLayout,
                    applicationContext.getString(R.string.error_no_connection),
                    Snackbar.LENGTH_LONG
                ).show()
                ErrorCodes.INVALID_FILE_EXTENSION -> Snackbar.make(
                    binding.editProfileLayout,
                    applicationContext.getString(R.string.error_invalid_file_type),
                    Snackbar.LENGTH_LONG
                ).show()
                ErrorCodes.INVALID_FILE_TYPE -> Snackbar.make(
                    binding.editProfileLayout,
                    applicationContext.getString(R.string.error_invalid_file_type),
                    Snackbar.LENGTH_LONG
                ).show()
                ErrorCodes.INVALID_FILE_SIZE -> Snackbar.make(
                    binding.editProfileLayout,
                    applicationContext.getString(R.string.error_invalid_file_size),
                    Snackbar.LENGTH_LONG
                ).show()
                ErrorCodes.FILE_WRITE_ERROR -> Snackbar.make(
                    binding.editProfileLayout,
                    applicationContext.getString(R.string.error_no_connection),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun setupUi() {

        //Set view components
        binding.etEmail.setText(SessionManager.getInstance().user.email)
        binding.etUsername.setText(SessionManager.getInstance().user.username)
        binding.etBio.setText(SessionManager.getInstance().user.userBio)


        // Check internet connection
        val cm = applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
        //Load image
        val URL = SessionManager.getInstance().user.image
        Picasso.get().load(URL).into(binding.ivUserAvatarEditProfile)
        if (!isConnected) Snackbar.make(
            binding.editProfileLayout,
            R.string.error_no_connection,
            BaseTransientBottomBar.LENGTH_LONG
        ).show()
    }

    private fun setListeners(){
        binding.ivChangeAvatar.setOnClickListener { // Check internet connection
            val cm = applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
            if (isConnected) {
                selectImage()
            } else {
                Log.d("CONNECTION", isConnected.toString())
                Snackbar.make(
                    binding.editProfileLayout,
                    R.string.error_no_connection,
                    BaseTransientBottomBar.LENGTH_LONG
                ).show()
            }
        }


        binding.btnSaveProfile.setOnClickListener { // Check internet connection
            val cm = applicationContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetwork = cm.activeNetworkInfo
            val isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting
            if (isConnected) {
                saveProfile()
            } else {
                Log.d("CONNECTION", isConnected.toString())
                Snackbar.make(
                    binding.editProfileLayout,
                    R.string.error_no_connection,
                    BaseTransientBottomBar.LENGTH_LONG
                ).show()
            }
        }

        binding.ivBack.setOnClickListener { onBackPressed() }
        binding.tvOptionsTitle.setOnClickListener { onBackPressed() }
    }
    private fun setupTextWatchers() {
        binding.etUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //this method is empty
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //this method is empty
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isEmpty()) {
                    setError(binding.etUsername, getString(R.string.error_username_empty))
                } else {
                    clearError(binding.etUsername)
                }
            }
        })
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                //this method is empty
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                //this method is empty
            }

            override fun afterTextChanged(s: Editable) {
                if (s.toString().isEmpty()) {
                    setError(binding.etEmail, getString(R.string.error_email_empty))
                } else {
                    clearError(binding.etEmail)
                }
            }
        })
    }

    //Opens intent that provide selecting image from local storage
    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, IMG_REQUEST)
    }

    //Provides that cacth the results that come back from selectImage() function
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null) {
            newImagePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, newImagePath)
                val fileSize = bitmap.byteCount
                Log.d("SIZE:  ", "" + bitmap.byteCount)
                if (fileSize < ConstantValues.MAX_FILE_SIZE) {
                    SessionManager.getInstance().user.avatar = bitmap
                    binding.ivUserAvatarEditProfile.setImageBitmap(SessionManager.getInstance().user.avatar)
                    binding.ivUserAvatarEditProfile.visibility = View.INVISIBLE
                    setProgressVisibility(true)
                    getViewModel().uploadImage(UploadImageRequest(
                        SessionManager.getInstance().sessionToken,
                        HelperMethods.imageToString(SessionManager.getInstance().user.avatar)
                    ))
                } else {
                    Snackbar.make(
                        binding.editProfileLayout,
                        "Seçilen dosya boyutu çok büyük",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * Shows material progress bar and disables form.
     * @param visible set true when progress view needs to be shown.
     */
    private fun setProgressVisibility(visible: Boolean) {
        if (visible) {
            binding.progressEditProfile.visibility = View.VISIBLE
            binding.cardFormEditProfile.alpha = 0.5f
            binding.btnSaveProfile.isEnabled = false
        } else {
            binding.progressEditProfile.visibility = View.INVISIBLE
            binding.cardFormEditProfile.alpha = 1f
            binding.btnSaveProfile.isEnabled = true
        }
    }

    /**
     * Validate user information and send request to the API
     */
    private fun saveProfile() {
        clearError(binding.etUsername)
        clearError(binding.etEmail)
        if (binding.etUsername.text.toString().isEmpty()) {
            setError(binding.etUsername, getString(R.string.error_username_empty))
        }
        if (binding.etEmail.text.toString().isEmpty()) {
            setError(binding.etEmail, getString(R.string.error_email_empty))
        }
        if (binding.etUsername.text.toString().isNotEmpty() && binding.etEmail.text.toString().isNotEmpty()) {
            setProgressVisibility(true)

            getViewModel().editProfile(EditProfileRequest(
                sessionToken =SessionManager.getInstance().sessionToken,
                newUsername = binding.etUsername.text.toString(),
                newEmail = binding.etEmail.text.toString(),
                newUserBio = binding.etBio.text.toString()
            ))
        }
    }

    private fun setError(editText: EditText?, errorMessage: String) {
        editText!!.hint = errorMessage
        editText.background = ContextCompat.getDrawable(
            applicationContext,
            R.drawable.et_error_background
        )
    }

    private fun clearError(editText: EditText?) {
        editText!!.background = ContextCompat.getDrawable(
            applicationContext,
            R.drawable.et_background
        )
    }
}