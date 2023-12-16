package com.juanarton.chargingcurrentcontroller.ui.fragments.alarm

import androidx.lifecycle.ViewModel
import com.juanarton.core.data.domain.usecase.DataRepositoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlarmViewModel @Inject constructor(private val dataRepositoryUseCase: DataRepositoryUseCase) : ViewModel() {
    fun setBatteryLevelThreshold (min: Int, max: Int, callback: (Boolean) -> Unit) =
        dataRepositoryUseCase.setBatteryLevelTeshold(min, max, callback)
}