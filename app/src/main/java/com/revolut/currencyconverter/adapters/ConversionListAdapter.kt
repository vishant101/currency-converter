package com.revolut.currencyconverter.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.revolut.currencyconverter.R
import com.revolut.currencyconverter.databinding.CurrencyItemBinding
import com.revolut.currencyconverter.interfaces.CurrencyChangeListener
import com.revolut.currencyconverter.interfaces.OnItemClickListener
import com.revolut.currencyconverter.model.ConversionRate
import com.revolut.currencyconverter.viewmodel.CurrencyItemViewModel


class ConversionListAdapter: RecyclerView.Adapter<ConversionListAdapter.ViewHolder>() {
    private lateinit var conversionList:List<ConversionRate>
    private lateinit var onClickListener: OnItemClickListener
    private lateinit var  onTextChangeListener: CurrencyChangeListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: CurrencyItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.context), R.layout.currency_item, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(conversionList[position], listener = onClickListener)
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

    fun updateTextListener(textListener: CurrencyChangeListener){
        this.onTextChangeListener = textListener
    }

    inner class ViewHolder(private val binding: CurrencyItemBinding):RecyclerView.ViewHolder(binding.root){
        private val viewModel = CurrencyItemViewModel()

        fun bind(conversionRate:ConversionRate, listener: OnItemClickListener){
            viewModel.bind(conversionRate, listener, onTextChangeListener)
            binding.viewModel = viewModel
        }
    }
}