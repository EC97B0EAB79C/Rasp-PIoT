package com.tkbaze.rasp_piot.temperature

import android.os.SystemClock
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL

class TemperatureViewModel : ViewModel() {
    private val TAG = "TemperatureViewModel"
    private val BASE_URI = "http://192.168.144.59/~pi/"

    // Is AC ON/OFF
    private var _isOn: Boolean = false
    val isOn: Boolean get() = _isOn

    fun toggleIsOn() {
        _isOn = !isOn
    }

    // Min Settable Temperature
    private var _minTemp: Int = 16
    val minTemp: Int get() = _minTemp

    // Max Settable Temperature
    private var _maxTemp: Int = 31
    val maxTemp: Int get() = _maxTemp

    // Current Temperature From Raspberry Pi
    private var _currentTemp = MutableLiveData<Int>(0)
    val currentTemp: LiveData<Int> get() = _currentTemp

    fun updateCurrentTemp() {
        viewModelScope.launch {
            _currentTemp.value = httpGetCurrentTemp()
        }
    }

    private fun httpGetCurrentTemp(): Int {
        var http: HttpURLConnection? = null
        var src = "-273"
        //SystemClock.sleep(5000)
        try {
            val url = URL(BASE_URI + "currentTemp.php?")
            http = url.openConnection() as HttpURLConnection
            http.requestMethod = "GET"
            http.connect()

            src = http.inputStream.bufferedReader().readText()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            http?.disconnect()
        }

        return src.toInt()
    }

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

    fun getCurrentSetting(){
        viewModelScope.launch {
            //TODO get json
            val setting=httpGetCurrentSetting()
        }
    }

    private fun httpGetCurrentSetting(){
        var http: HttpURLConnection? = null
        var src = "-273"
        //SystemClock.sleep(5000)
        try {
            val url = URL(BASE_URI + "currentSetting.php?")
            http = url.openConnection() as HttpURLConnection
            http.requestMethod = "GET"
            http.connect()

            src = http.inputStream.bufferedReader().readText()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            http?.disconnect()
        }

//        return src
    }

}