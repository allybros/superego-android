package com.allybros.superego.base

import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModel
import com.allybros.superego.R

abstract class SegoActivity<VM: SegoVM,DB: ViewDataBinding>: AppCompatActivity() {

    private lateinit var viewModel: VM
    protected lateinit var binding: DB

    abstract fun getSegoViewModel(): VM
    abstract fun getLayout(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = getSegoViewModel()
        setDataBinding()
        observe()
    }

    private fun setDataBinding() {
        binding = DataBindingUtil.setContentView(this, getLayout())
    }

    open fun observe(){
        viewModel.onApiError.observe(this) {
            Log.e("SegoError", it.toString())
        }
    }

    protected fun getViewModel() = viewModel
}
