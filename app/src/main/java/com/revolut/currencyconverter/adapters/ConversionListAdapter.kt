package com.revolut.currencyconverter.adapters

import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.revolut.currencyconverter.R
import com.revolut.currencyconverter.databinding.CurrencyItemBinding
import com.revolut.currencyconverter.interfaces.CurrencyChangeListener
import com.revolut.currencyconverter.interfaces.OnItemClickListener
import com.revolut.currencyconverter.interfaces.TextChangedListener

import com.revolut.currencyconverter.model.ConversionRate
import com.revolut.currencyconverter.viewmodel.CurrencyItemViewModel


class ConversionListAdapter: RecyclerView.Adapter<ConversionListAdapter.ViewHolder>() {
    private lateinit var conversionList:List<ConversionRate>
    private lateinit var listener: OnItemClickListener
    private lateinit var  textListener: CurrencyChangeListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: CurrencyItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.currency_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(conversionList[position], listener = listener)
    }

    override fun getItemCount(): Int {
        return if(::conversionList.isInitialized) conversionList.size else 0
    }


    fun updatePostList(conversionLIst:List<ConversionRate>){
        this.conversionList = conversionLIst
        notifyDataSetChanged()
    }

    fun updatePostListener(listener: OnItemClickListener){
        this.listener = listener
    }

    fun updateTextListener(textListener: CurrencyChangeListener){
        this.textListener = textListener
    }

    inner class ViewHolder(private val binding: CurrencyItemBinding):RecyclerView.ViewHolder(binding.root){
        private val viewModel = CurrencyItemViewModel()

        fun bind(conversionRate:ConversionRate, listener: OnItemClickListener){
            viewModel.bind(conversionRate, listener, textListener)
            binding.viewModel = viewModel

        }
    }
}