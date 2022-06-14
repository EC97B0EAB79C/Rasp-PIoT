package com.tkbaze.rasp_piot.temperature

import androidx.lifecycle.ViewModel

class TemperatureViewModel : ViewModel() {

    private var _minTemp: Int = 16
    val minTemp: Int get() = _minTemp

    private var _maxTemp: Int = 31
    val maxTemp: Int get() = _maxTemp

    private var _currentTemp: Int = 26
    val currentTemp: Int get() = _currentTemp

    private var _targetTemp: Int = 23
    val targetTemp: Int get() = _targetTemp

    private var _autoTemp:Int =23
    val autoTemp:Int get()= _autoTemp

}