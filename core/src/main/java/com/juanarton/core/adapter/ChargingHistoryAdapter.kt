package com.juanarton.core.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.juanarton.core.R
import com.juanarton.core.data.domain.batteryMonitoring.domain.ChargingHistory
import com.juanarton.core.databinding.ChargingHistoryItemViewBinding
import com.juanarton.core.utils.Utils.convertMillisToDateTime
import com.juanarton.core.utils.Utils.formatTime
import kotlin.math.abs

class ChargingHistoryAdapter (
    private val context: Context
) : PagingDataAdapter<ChargingHistory, ChargingHistoryAdapter.ViewHolder>(HistoryComparator) {

    object HistoryComparator: DiffUtil.ItemCallback<ChargingHistory>() {
        override fun areItemsTheSame(oldItem: ChargingHistory, newItem: ChargingHistory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ChargingHistory, newItem: ChargingHistory): Boolean {
            return oldItem == newItem
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChargingHistoryAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.charging_history_item_view, parent, false)
        return ViewHolder(view)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position)?.let {
            holder.bind(it)
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val binding = ChargingHistoryItemViewBinding.bind(itemView)
        fun bind(chargingHistory: ChargingHistory){
            binding.apply {
                if (chargingHistory.isCharging) {
                    val duration = formatTime(
                        (abs(chargingHistory.startTime - chargingHistory.endTime))/1000
                    )

                    piFirstLevel.progress = chargingHistory.endLevel
                    piSecondLevel.progress = chargingHistory.startLevel

                    setChargingHistoryView(
                        "${context.getString(R.string.charged_for)} $duration", chargingHistory,
                        binding, R.color.green, R.string.charging_started, R.string.charging_ended
                    )
                } else {
                    val duration = formatTime(
                    (abs(chargingHistory.startTime - chargingHistory.endTime))/1000
                    )

                    piFirstLevel.progress = chargingHistory.startLevel
                    piSecondLevel.progress = chargingHistory.endLevel

                    setChargingHistoryView(
                        "${context.getString(R.string.discharged_for)} $duration", chargingHistory,
                        binding, R.color.red, R.string.discharging_started, R.string.discharging_ended
                    )
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun setChargingHistoryView(
        title: String, chargingHistory: ChargingHistory, binding: ChargingHistoryItemViewBinding,
        color: Int, startStat: Int, endStat: Int
    ) {
        binding.apply {
            tvChargingHistoryTitle.text = title
            tvLevelDifference.text = "${chargingHistory.levelDifference}%"
            tvStartLevel.text = "${chargingHistory.startLevel}%"
            tvEndLevel.text = "${chargingHistory.endLevel}%"
            tvStartTime.text = convertMillisToDateTime(chargingHistory.startTime)
            tvEndTime.text = convertMillisToDateTime(chargingHistory.endTime)
            tvStartStat.text = context.getString(startStat)
            tvEndStat.text = context.getString(endStat)

            tvChargingHistoryTitle.setTextColor(
                ContextCompat.getColor(context, color)
            )
            tvLevelDifference.setTextColor(
                ContextCompat.getColor(context, color)
            )
            piSecondLevel.progressTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, color))
        }
    }


}