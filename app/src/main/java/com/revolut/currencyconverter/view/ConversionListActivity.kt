package com.revolut.currencyconverter.view

import android.os.Bundle
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.revolut.currencyconverter.R
import com.revolut.currencyconverter.databinding.ActivityConversionListBinding
import com.revolut.currencyconverter.injection.ViewModelFactory
import com.revolut.currencyconverter.viewmodel.ConversionListViewModel
import io.reactivex.disposables.Disposable

class ConversionListActivity: AppCompatActivity() {
    private lateinit var binding: ActivityConversionListBinding
    private lateinit var viewModel: ConversionListViewModel
    private var errorSnackbar: Snackbar? = null
    private lateinit var itemClickSubscription: Disposable

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_conversion_list)
        binding.conversionList.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        viewModel = ViewModelProviders.of(this, ViewModelFactory()).get(ConversionListViewModel::class.java)
        viewModel.errorMessage.observe(this, Observer {
                errorMessage -> if(errorMessage != null) showError(errorMessage) else hideError()
        })
        binding.viewModel = viewModel

        setupItemClick()
    }

    private fun showError(@StringRes errorMessage:Int){
        errorSnackbar = Snackbar.make(binding.root, errorMessage, Snackbar.LENGTH_INDEFINITE)
        errorSnackbar?.setAction(R.string.retry, viewModel.errorClickListener)
        errorSnackbar?.show()
    }

    private fun hideError(){
        errorSnackbar?.dismiss()
    }

    private fun scrollToTop(){
        binding.conversionList.scrollToPosition(0)
    }

    private fun setupItemClick() {
        itemClickSubscription = viewModel.conversionListAdapter.clickEvent
            .subscribe { scrollToTop() }
    }
}