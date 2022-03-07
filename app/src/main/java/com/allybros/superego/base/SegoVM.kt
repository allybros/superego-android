package com.allybros.superego.base

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.allybros.superego.unit.SegoResponse

open class SegoVM(application: Application): AndroidViewModel(application) {

    var onApiError: MutableLiveData<SegoResponse> = MutableLiveData<SegoResponse>()

}