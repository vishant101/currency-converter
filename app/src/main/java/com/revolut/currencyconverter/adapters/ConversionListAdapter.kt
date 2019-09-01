package com.revolut.currencyconverter.adapters

import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.ObservableInt
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.revolut.currencyconverter.R
import com.revolut.currencyconverter.interfaces.OnItemClickListener
import com.revolut.currencyconverter.model.ConversionRate
import com.revolut.currencyconverter.utils.VALUE_CHANGE
import com.revolut.currencyconverter.viewmodel.CurrencyItemViewModel


class ConversionListAdapter: RecyclerView.Adapter<ConversionListAdapter.ViewHolder>() {
    private lateinit var conversionList: List<ConversionRate>
    private lateinit var onClickListener: OnItemClickListener
    private lateinit var  valueWatcher: TextWatcher
    var scrollTo = ObservableInt()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(parent.context).inflate(R.layout.currency_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(conversionList[position], position, onItemClickListener = onClickListener, valueWatcher = valueWatcher)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        val set = payloads.firstOrNull() as Set<String>?
        when {
            set == null || set.isEmpty() -> return super.onBindViewHolder(holder, position, payloads)
            set.contains(VALUE_CHANGE) -> holder.update(conversionList[position], textWatcher = valueWatcher, position = position)
        }
    }

    override fun getItemCount(): Int {
        return if(::conversionList.isInitialized) conversionList.size else 0
    }

    fun updateCurrencyList(conversionList:List<ConversionRate>){
        if (itemCount == 0) {
            this.conversionList = conversionList
            notifyDataSetChanged()
        } else {
            updateCurrencyListData(conversionList)
        }
    }

    private fun updateCurrencyListData(conversionList: List<ConversionRate>){
        val diffResult = DiffUtil.calculateDiff(ConversionListDiff(this.conversionList, conversionList))
        this.conversionList = conversionList
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateOnClickListener(listener: OnItemClickListener){
        this.onClickListener = listener
    }

    fun updateValueWatcher(valueWatcher: TextWatcher){
        this.valueWatcher = valueWatcher
    }

    fun updateBaseValue(rate: Float) {
        conversionList[0].value = rate
    }

    inner class ViewHolder(view: View):RecyclerView.ViewHolder(view){
        private val viewModel = CurrencyItemViewModel(view)

        fun bind(conversionRate:ConversionRate, position: Int, onItemClickListener: OnItemClickListener, valueWatcher: TextWatcher){
            viewModel.bind(conversionRate, position, onItemClickListener,valueWatcher)
        }

        fun update(conversionRate: ConversionRate, textWatcher: TextWatcher, position: Int){
            viewModel.updateOnlyValue(conversionRate.value, textWatcher, position)
        }
    }

    inner class ConversionListDiff(private val oldList: List<ConversionRate>, private val newList: List<ConversionRate>) : DiffUtil.Callback() {
        override fun getOldListSize() = oldList.size

        override fun getNewListSize() = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].currency == newList[newItemPosition].currency
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }

        override fun getChangePayload(oldPosition: Int, newPosition: Int): Any? {
            val payloadSet = mutableSetOf<String>()
            val oldRate = oldList[oldPosition]
            val newRate = newList[newPosition]

            if (oldRate.value!=newRate.value)
                payloadSet.add(VALUE_CHANGE)

            return payloadSet
        }
    }
}