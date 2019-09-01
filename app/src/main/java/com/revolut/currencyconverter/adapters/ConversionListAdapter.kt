package com.revolut.currencyconverter.adapters

import android.text.Editable
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
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject


class ConversionListAdapter: RecyclerView.Adapter<ConversionListAdapter.ViewHolder>() {
    private lateinit var conversionList: List<ConversionRate>
    private var onClickListener: OnItemClickListener
    private var  valueWatcher: TextWatcher

    private val clickSubject = PublishSubject.create<ConversionRate>()
    private val valueChangeSubject = PublishSubject.create<Float>()

    val clickEvent: Observable<ConversionRate> = clickSubject
    val valueChangeEvent: Observable<Float> = valueChangeSubject

    var scrollTo = ObservableInt()

    init {
        this.onClickListener = OnClickListener()
        this.valueWatcher = ValueWatcher()
    }

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
            when { oldList[oldPosition].value != newList[newPosition].value -> payloadSet.add(VALUE_CHANGE) }
            return payloadSet
        }
    }

    inner class OnClickListener : OnItemClickListener {
        override fun onItemClick(conversionRate: ConversionRate) {
            clickSubject.onNext(conversionRate)
            scrollTo.set(0)
        }
    }

    inner class ValueWatcher : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        override fun onTextChanged(newValue: CharSequence, p1: Int, p2: Int, p3: Int) {}

        override fun afterTextChanged(newValue: Editable) {
            val rate =  if (newValue.isEmpty()) 0.0F else newValue.toString().toFloat()
            conversionList[0].value = rate
            valueChangeSubject.onNext(rate)
        }
    }
}