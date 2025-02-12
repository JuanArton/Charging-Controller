package com.juanarton.core.adapter

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.juanarton.core.R
import com.juanarton.core.data.domain.batteryMonitoring.domain.ChargingHistory
import com.juanarton.core.databinding.ChargingHistoryItemViewBinding
import com.juanarton.core.utils.Utils.convertMillisToDateTime
import com.juanarton.core.utils.Utils.convertMillisToHourTime
import com.juanarton.core.utils.Utils.formatTime
import java.util.Locale
import kotlin.math.abs

class ChargingHistoryAdapter(
    private val onClick: (ChargingHistory) -> Unit,
    private val context: Context,
) : RecyclerView.Adapter<ChargingHistoryAdapter.ViewHolder>() {

    private var chargingHistoryList: ArrayList<ChargingHistory> = arrayListOf()

    fun setData(chargingHistory: List<ChargingHistory>) {
        chargingHistoryList.clear()
        chargingHistoryList.addAll(chargingHistory.asReversed())
        notifyDataSetChanged()
    }

    fun setChargingHistoryView(
        duration: String, chargingHistory: ChargingHistory, binding: ChargingHistoryItemViewBinding,
        color: Int
    ) {
        binding.apply {
            tvHistoryDate.text = convertMillisToDateTime(chargingHistory.endTime)
            tvLevelDifference.text = if (chargingHistory.levelDifference > 0) {
                "+${chargingHistory.levelDifference}%"
            } else {
                "${chargingHistory.levelDifference}%"
            }

            tvStartTime.text = buildString {
                append("${chargingHistory.startLevel}%")
                append(" ${context.getString(R.string.at)} ")
                append(convertMillisToHourTime(chargingHistory.startTime))
            }

            tvEndTime.text = buildString {
                append("${chargingHistory.endLevel}%")
                append(" ${context.getString(R.string.at)} ")
                append(convertMillisToHourTime(chargingHistory.endTime))
            }

            tvHistoryDuration.text = duration
            tvLevelDifference.setTextColor(
                ContextCompat.getColor(context, color)
            )
            piSecondLevel.progressTintList =
                ColorStateList.valueOf(ContextCompat.getColor(context, color))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.charging_history_item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(chargingHistoryList[position])
    }

    override fun getItemCount(): Int = chargingHistoryList.size

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ChargingHistoryItemViewBinding.bind(itemView)

        fun bind(chargingHistory: ChargingHistory) {
            binding.apply {
                if (chargingHistory.isCharging) {
                    val duration = formatTime(
                        (abs(chargingHistory.startTime - chargingHistory.endTime)) / 1000
                    )

                    piFirstLevel.progress = chargingHistory.endLevel
                    piSecondLevel.progress = chargingHistory.startLevel
                    piFirstLevel.setIndicatorColor(ContextCompat.getColor(context, R.color.green))

                    setChargingHistoryView(
                        "${context.getString(R.string.`in`)} $duration", chargingHistory,
                        binding, R.color.green
                    )

                    tvChargingSpeed.visibility = View.VISIBLE
                    tvDrainSpeed.visibility = View.GONE
                    chargingHistory.chargingSpeed?.let {
                        tvChargingSpeed.text = buildString {
                            append(context.getString(R.string.charging))
                            append(" ")
                            append(formatUsagePerHour(chargingHistory.chargingSpeed))
                        }

                    }
                } else {
                    val duration = formatTime(
                        (abs(chargingHistory.startTime - chargingHistory.endTime)) / 1000
                    )

                    piFirstLevel.progress = chargingHistory.startLevel
                    piSecondLevel.progress = chargingHistory.endLevel
                    piFirstLevel.setIndicatorColor(ContextCompat.getColor(context, R.color.red))

                    setChargingHistoryView(
                        "${context.getString(R.string.`in`)} $duration", chargingHistory,
                        binding, R.color.red
                    )

                    tvDrainSpeed.visibility = View.VISIBLE
                    tvChargingSpeed.visibility = View.GONE
                    chargingHistory.screenOnDrainPerHr?.let {
                        tvDrainSpeed.text = buildString {
                            append(context.getString(R.string.use))
                            append(" ")
                            append(formatUsagePerHour(chargingHistory.screenOnDrainPerHr))
                        }

                    }
                }

                itemClickMask.setOnClickListener {
                    onClick(chargingHistory)
                }
            }
        }

        private fun formatUsagePerHour(value1: Double): String {
            return buildString {
                append(
                    String.format(Locale.getDefault(), "%.1f", value1)
                )
                append("%/h")
            }
        }
    }
}