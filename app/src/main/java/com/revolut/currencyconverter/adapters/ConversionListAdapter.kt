package com.revolut.currencyconverter.adapters

import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.revolut.currencyconverter.R
import com.revolut.currencyconverter.databinding.CurrencyItemBinding
import com.revolut.currencyconverter.interfaces.OnItemClickListener
import com.revolut.currencyconverter.model.ConversionRate
import com.revolut.currencyconverter.viewmodel.CurrencyItemViewModel


class ConversionListAdapter: RecyclerView.Adapter<ConversionListAdapter.ViewHolder>() {
    private lateinit var conversionList:List<ConversionRate>
    private lateinit var onClickListener: OnItemClickListener
    private lateinit var  valueWatcher: TextWatcher

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: CurrencyItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.currency_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(conversionList[position], position, onItemClickListener = onClickListener, valueWatcher = valueWatcher)
    }

    override fun getItemCount(): Int {
        return if(::conversionList.isInitialized) conversionList.size else 0
    }

    fun updateCurrencyList(conversionList:List<ConversionRate>){
        if (itemCount == 0) {
            this.conversionList = conversionList
            notifyDataSetChanged()
        } else {
            val oldConversionList = this.conversionList
            this.conversionList = conversionList
            for ( i in 0 .. conversionList.size){
                if (conversionList[i] != oldConversionList[i]){
                    notifyItemChanged(i)
                }
            }
        }
    }

    fun updateOnClickListener(listener: OnItemClickListener){
        this.onClickListener = listener
    }

    fun updateValueWatcher(valueWatcher: TextWatcher){
        this.valueWatcher = valueWatcher
    }

    inner class ViewHolder(private val binding: CurrencyItemBinding):RecyclerView.ViewHolder(binding.root){
        private val viewModel = CurrencyItemViewModel()

        fun bind(conversionRate:ConversionRate, position: Int, onItemClickListener: OnItemClickListener, valueWatcher: TextWatcher){
            viewModel.bind(conversionRate, position, onItemClickListener,valueWatcher)
            binding.viewModel = viewModel
        }
    }
}