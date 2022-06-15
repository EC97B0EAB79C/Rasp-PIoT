package com.tkbaze.rasp_piot.temperature

import android.util.Log
import androidx.lifecycle.ViewModel

class TemperatureViewModel : ViewModel() {
    private val TAG = "TemperatureViewModel"

    //
    private var _isOn: Boolean = false
    val isOn: Boolean get() = _isOn

    fun toggleIsOn() {
        _isOn = !isOn
    }

    //
    private var _minTemp: Int = 16
    val minTemp: Int get() = _minTemp

    //
    private var _maxTemp: Int = 31
    val maxTemp: Int get() = _maxTemp

    //
    private var _currentTemp: Int = 26
    val currentTemp: Int get() = _currentTemp

    //
    private var _targetTemp: Int = 23
    val targetTemp: Int get() = _targetTemp

    fun setTargetTemp(value: Int) {
        _targetTemp = value
    }

    //
    private var _isAuto: Boolean = false
    val isAuto: Boolean get() = _isAuto

    fun setIsAuto(value: Boolean) {
        _isAuto = value
    }

    //
    private var _autoTemp: Int = 23
    val autoTemp: Int get() = _autoTemp

    fun setAutoTemp(value: Int) {
        _autoTemp = value
    }

    //
    fun sendSettingRequest() {
        Log.d(TAG, "Sending Setting Request")
    }

    fun sendAutoRequest() {
        Log.d(TAG, "Sending Auto Request")
    }

}