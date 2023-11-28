package com.juanarton.core.utils

import com.juanarton.core.data.repository.DataRepository
import com.topjohnwu.superuser.Shell

object Utils {
    fun getValue(config: String): Shell.Result {
        val command =
            "grep '$config' ${DataRepository.PATH}/3C.conf | awk -F '=' '{print \$2}' | tr -d ' '"

        return Shell.cmd(command).exec()
    }
}