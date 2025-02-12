package com.juanarton.batterysense.ui.fragments.fullhistory.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.juanarton.batterysense.R
import com.juanarton.batterysense.databinding.BatteryHistoryItemViewBinding
import com.juanarton.core.data.domain.batteryMonitoring.domain.BatteryHistory
import com.juanarton.core.utils.Utils.convertMillisToDateTimeSecond
import java.text.DecimalFormat

class FullHistoryAdapter (
    private val context: Context,
) : RecyclerView.Adapter<FullHistoryAdapter.ViewHolder>() {

    private var batteryHistoryList: ArrayList<BatteryHistory> = arrayListOf()

    fun setData(batteryHistory: List<BatteryHistory>) {
        batteryHistoryList.clear()
        batteryHistoryList.addAll(batteryHistory)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.battery_history_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(batteryHistoryList[position])
    }

    override fun getItemCount(): Int = batteryHistoryList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = BatteryHistoryItemViewBinding.bind(itemView)

        fun bind(batteryHistory: BatteryHistory) {
            binding.apply {
                tvHistoryDate.text = convertMillisToDateTimeSecond(batteryHistory.timestamp)

                tvBatteryLevel.text = buildString {
                    append(batteryHistory.level)
                    append("%")
                }

                tvBatteryCurrent.text = buildString {
                    append(batteryHistory.current)
                    append(context.getString(R.string.ma))
                }

                tvBatteryTemperature.text = buildString {
                    append(batteryHistory.temperature)
                    append(context.getString(R.string.degree_symbol))
                }

                val decimalFormat = DecimalFormat("#.##")
                val power = decimalFormat.format(batteryHistory.power)
                tvBatteryPower.text = buildString {
                    append(power)
                    append(context.getString(R.string.wattage))
                }

                tvBatteryVolt.text = buildString {
                    append(batteryHistory.voltage)
                    append(context.getString(R.string.volt_unit))
                }
            }
        }
    }
}