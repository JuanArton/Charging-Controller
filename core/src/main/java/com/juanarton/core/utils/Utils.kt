package com.juanarton.core.utils

import android.content.Context
import com.juanarton.core.R
import com.juanarton.core.data.repository.AppConfigRepository
import com.topjohnwu.superuser.Shell

object Utils {
    fun getValue(config: String): Shell.Result {
        val command =
            "grep '$config' ${AppConfigRepository.PATH}/3C.conf | awk -F '=' '{print \$2}' | tr -d ' '"

        return Shell.cmd(command).exec()
    }

    fun mapBatteryStatus(status: Int, context: Context): String {
        return when (status){
            1 -> context.getString(R.string.unknown)
            2 -> context.getString(R.string.charging)
            3 -> context.getString(R.string.discharging)
            4 -> context.getString(R.string.not_charging)
            else -> context.getString(R.string.full)
        }
    }
}