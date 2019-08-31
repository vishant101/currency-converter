package com.revolut.currencyconverter.viewmodel

import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView
import com.revolut.currencyconverter.R
import com.revolut.currencyconverter.interfaces.OnItemClickListener
import com.revolut.currencyconverter.model.ConversionRate
import com.revolut.currencyconverter.utils.DEFAULT_LONG_CURRENCY_PLACEHOLDER
import com.revolut.currencyconverter.utils.MIPMAP
import com.revolut.currencyconverter.utils.STRING


class CurrencyItemViewModel(itemView: View): RecyclerView.ViewHolder(itemView){
    private val currencyShortName: TextView = this.itemView.findViewById(R.id.currency_short)
    private val currencyLongName: TextView = this.itemView.findViewById(R.id.currency_long)
    private val currencyEditValue: EditText = this.itemView.findViewById(R.id.currency_value)
    private val currencyLayout: View = this.itemView.findViewById(R.id.currency_layout)
    private val currencyFlag: ImageView = this.itemView.findViewById(R.id.currency_flag)

    private lateinit var onClickListener: View.OnClickListener


    fun bind(conversionRate: ConversionRate, position: Int, onClickListener: OnItemClickListener, valueWatcher: TextWatcher) {
        this.onClickListener = View.OnClickListener { onClickListener.onItemClick(conversionRate) }
        updateOnClickListener()
        updateTextWatcher(valueWatcher, position)
        updateValue(conversionRate.value)
        updateCurrencyShortName(conversionRate.currency)
        updateCurrencyLongName(conversionRate.currency)
        updateCurrencyFlag(conversionRate.currency)
    }

    private fun updateCurrencyFlag(currencyShort: String){
        val flagDrawable = ResourcesCompat.getDrawable(currencyFlag.resources, getCurrencyFlag(currencyShort), null)
        currencyFlag.setImageDrawable(flagDrawable)
    }

    private fun updateValue(currencyValue: Float) {
        val value = "%.2f".format(currencyValue)
        if (currencyEditValue.text.toString() != value) currencyEditValue.setText(value)
    }

    private fun updateCurrencyShortName(shortName: String) {
        currencyShortName.text = shortName
    }

    private fun updateCurrencyLongName(shortName: String) {
        val resources = currencyLongName.context.resources
        val packageName = currencyLongName.context.packageName

        currencyLongName.text = try {
            resources.getString(resources.getIdentifier(shortName, STRING, packageName))
        } catch (e: Exception) {
            DEFAULT_LONG_CURRENCY_PLACEHOLDER
        }
    }

    private fun updateTextWatcher(valueWatcher: TextWatcher, position: Int) {
        currencyEditValue.isEnabled = position == 0
        currencyEditValue.removeTextChangedListener(valueWatcher)
        if (position == 0) currencyEditValue.addTextChangedListener(valueWatcher)
    }

    private fun updateOnClickListener(){
        currencyLayout.setOnClickListener(onClickListener)
    }

    private fun getCurrencyFlag(currencyName: String): Int {
        var currencyFlag: Int

        val resources = this.currencyFlag.context.resources
        val packageName = this.currencyFlag.context.packageName
        val drawableName = "ic_"+currencyName.substring(0,3).toLowerCase()

        currencyFlag = try {
            resources.getIdentifier(drawableName, MIPMAP, packageName)
        } catch (e: Exception) { 0 }

        if (currencyFlag==0) currencyFlag = R.mipmap.ic_eur

        return currencyFlag
    }
}