package com.juanarton.core.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.juanarton.core.R
import com.juanarton.core.data.domain.batteryMonitoring.domain.BatteryHistory
import com.juanarton.core.databinding.HistoryItemViewBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HistoryAdapter (
    private val context: Context
) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    private val batteryHistories: ArrayList<BatteryHistory> = arrayListOf()
    fun setData(items: List<BatteryHistory>?) {
        batteryHistories.apply {
            clear()
            items?.let { addAll(it) }
        }
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistoryAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.history_item_view, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(batteryHistories[position])

    override fun getItemCount(): Int = batteryHistories.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val binding = HistoryItemViewBinding.bind(itemView)
        fun bind(batteryHistory: BatteryHistory){
            binding.apply {
                val position = layoutPosition + 1
                tvNumber.text = position.toString()
                tvTimeStamp.text = convertMillisToDateTime(batteryHistory.timestamp)
                tvCurrent.text = buildString {
                    append(batteryHistory.current)
                    append(context.getString(R.string.ma))
                }
                tvLevel.text = buildString {
                    append(batteryHistory.level)
                    append("%")
                }
                tvTemperature.text = buildString {
                    append(batteryHistory.temperature)
                    append(context.getString(R.string.degree_symbol))
                }
                tvPower.text = buildString {
                    append(
                        "${String.format("%.2f", batteryHistory.power)}${context.getString(R.string.wattage)}"
                    )
                }
                tvVoltage.text = buildString {
                    append(
                        "${String.format("%.2f", batteryHistory.voltage)}${context.getString(R.string.volt_unit)}"
                    )
                }
            }
        }
    }

    fun convertMillisToDateTime(millis: Long): String {
        val sdf = SimpleDateFormat("dd-MM HH:mm:ss", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        return sdf.format(calendar.time)
    }
}