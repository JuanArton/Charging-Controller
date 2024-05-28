package com.juanarton.batterysense.ui.fragments.onboarding

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.juanarton.batterysense.R
import com.juanarton.batterysense.batterymonitorservice.Action
import com.juanarton.batterysense.batterymonitorservice.BatteryMonitorService
import com.juanarton.batterysense.batterymonitorservice.ServiceState
import com.juanarton.batterysense.batterymonitorservice.getServiceState

class WelcomePageFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_welcome_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        actionOnService(Action.START)
    }

    private fun actionOnService(action: Action) {
        if (getServiceState(requireContext()) == ServiceState.STOPPED && action == Action.STOP) return
        Intent(requireContext(), BatteryMonitorService::class.java).also {
            it.action = action.name
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d("BatteryMonitorService", "Starting the service in >=26 Mode")
                requireContext().startForegroundService(it)
                return
            }
            Log.d("BatteryMonitorService", "Starting the service in < 26 Mode")
            requireContext().startService(it)
        }
    }
}