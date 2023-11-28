package com.juanarton.chargingcurrentcontroller.ui.fragments.quicksetting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.juanarton.core.data.domain.model.Config
import com.juanarton.core.data.domain.usecase.DataRepositoryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuickSettingViewModel @Inject constructor(private val dataRepositoryUseCase: DataRepositoryUseCase): ViewModel(){
    private var targetCurrent: MutableLiveData<String> = MutableLiveData()
    private var chargingSwitch: MutableLiveData<Boolean> = MutableLiveData()
    private var limitChargingSwitch: MutableLiveData<Boolean> = MutableLiveData()
    private var maximumLimitValue: MutableLiveData<String> = MutableLiveData()

    private val _config : MutableLiveData<Config> = MutableLiveData()
    val config: LiveData<Config> = _config

    fun getConfig() {
        viewModelScope.launch {
            dataRepositoryUseCase.getConfig().collect {
                _config.value = it
            }
        }
    }

    fun setCurrent(targetCurrent: String) {
        this.targetCurrent.value = targetCurrent
    }

    fun setTargetCurrent() = targetCurrent.switchMap {
        dataRepositoryUseCase.setTargetCurrent(it).asLiveData()
    }

    fun setChargingSwitch(chargingSwitch: Boolean) {
        this.chargingSwitch.value = chargingSwitch
    }

    fun setChargingSwitchStatus() = chargingSwitch.switchMap {
        dataRepositoryUseCase.setChargingSwitchStatus(it).asLiveData()
    }

    fun setChargingLimitSwitch(limitChargingSwitch: Boolean) {
        this.limitChargingSwitch.value = limitChargingSwitch
    }

    fun setChargingLimitStatus() = limitChargingSwitch.switchMap {
        dataRepositoryUseCase.setChargingLimitStatus(it).asLiveData()
    }

    fun setMaximumCapacityValue(maximumLimitValue: String) {
        this.maximumLimitValue.value = maximumLimitValue
    }

    fun setMaximumCapacity() = maximumLimitValue.switchMap {
        dataRepositoryUseCase.setMaximumCapacity(it).asLiveData()
    }
}