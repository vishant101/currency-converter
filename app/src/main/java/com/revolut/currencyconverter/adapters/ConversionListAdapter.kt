package com.revolut.currencyconverter.adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.revolut.currencyconverter.R
import com.revolut.currencyconverter.databinding.CurrencyItemBinding
import com.revolut.currencyconverter.interfaces.OnItemClickListener
import com.revolut.currencyconverter.model.ConversionRate
import com.revolut.currencyconverter.viewmodel.CurrencyItemViewModel


class ConversionListAdapter: RecyclerView.Adapter<ConversionListAdapter.ViewHolder>() {
    private lateinit var conversionList: List<ConversionRate>
    private lateinit var onClickListener: OnItemClickListener
    private lateinit var  valueWatcher: TextWatcher

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.currency_item, parent, false)
        return ViewHolder(view)
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
                    if (i == 0){
                        if ( conversionList[i].currency != oldConversionList[i].currency) {
                            notifyItemChanged(i)
                        }
                    } else {
                        notifyItemChanged(i)
                    }
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

    inner class ViewHolder(private var view: View):RecyclerView.ViewHolder(view){
        private val viewModel = CurrencyItemViewModel(view)

        fun bind(conversionRate:ConversionRate, position: Int, onItemClickListener: OnItemClickListener, valueWatcher: TextWatcher){
            viewModel.bind(conversionRate, position, onItemClickListener,valueWatcher)
        }
    }
}