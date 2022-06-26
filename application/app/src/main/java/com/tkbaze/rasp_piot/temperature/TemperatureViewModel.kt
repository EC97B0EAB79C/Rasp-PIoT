package com.tkbaze.rasp_piot.temperature

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class TemperatureViewModel : ViewModel() {
    private val TAG = "TemperatureViewModel"
    private val BASE_URI = "http://192.168.144.143/~pi/"

    // Is AC ON/OFF
    private var _isOn = MutableLiveData<Boolean>(false)
    val isOn: LiveData<Boolean> get() = _isOn

    fun toggleIsOn() {
        _isOn.value = !(isOn.value)!!
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

    private suspend fun httpGetCurrentTemp(): Int {
        var src = "-273"
        withContext(Dispatchers.IO) {
            var http: HttpURLConnection? = null
            try {
                val url = URL(BASE_URI + "current_temperature.php")
                http = url.openConnection() as HttpURLConnection
                http.requestMethod = "GET"
                http.connect()

                src = http.inputStream.bufferedReader().readText()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                http?.disconnect()
            }
        }
        return src.toFloat().toInt()
    }


    //
    private var _targetTemp = MutableLiveData<Int>(23)
    val targetTemp: LiveData<Int> get() = _targetTemp

    fun setTargetTemp(value: Int) {
        _targetTemp.value = value
    }

    //
    private var _isAuto = MutableLiveData<Boolean>(false)
    val isAuto: LiveData<Boolean> get() = _isAuto

    fun setIsAuto(value: Boolean) {
        _isAuto.value = value
    }

    //
    private var _autoTemp = MutableLiveData<Int>(23)
    val autoTemp: LiveData<Int> get() = _autoTemp

    fun setAutoTemp(value: Int) {
        _autoTemp.value = value
    }

    fun getSetting() {
        viewModelScope.launch {
            var src = "failed"
            src = httpGetSetting()
            if (src == "failed") {
                //TODO
            } else {
                Log.d(TAG, src)
                val temp = src.split(" ").toTypedArray()
                _isOn.value = temp[0].toInt() > 0
                _targetTemp.value = temp[1].toInt()
                _isAuto.value = temp[2].toInt() > 0
                _autoTemp.value = temp[3].toInt()
            }
        }
    }

    private suspend fun httpGetSetting(): String {
        var src = "failed"
        withContext(Dispatchers.IO) {
            var http: HttpURLConnection? = null
            try {
                val url = URL(BASE_URI + "ac_setting.php?")
                http = url.openConnection() as HttpURLConnection
                http.requestMethod = "GET"
                http.connect()

                src = http.inputStream.bufferedReader().readText()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                http?.disconnect()
            }
        }
        return src

    }

    //
    fun sendSettingRequest() {
        Log.d(TAG, "Sending Setting Request")
        viewModelScope.launch {
            val result = httpSendSetting()
            if(isAuto.value!!)
                httpSendAuto()
            Log.d(TAG, "Setting Request $result")
        }
    }

    private suspend fun httpSendSetting(): String {
        var src = "failed"

        withContext(Dispatchers.IO) {
            var http: HttpURLConnection? = null

            try {
                val urlString = BASE_URI + "ac_control.php?" + String.format(
                    "isOn=%d&targetTemp=%d",
                    if (isOn.value!!) 1 else 0,
                    targetTemp.value
                )
                Log.d(TAG, urlString)
                val url = URL(urlString)
                http = url.openConnection() as HttpURLConnection
                http.requestMethod = "GET"
                http.connect()

                src = http.inputStream.bufferedReader().readText()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                http?.disconnect()
            }
        }
        return src
    }

    fun sendAutoRequest() {
        Log.d(TAG, "Sending Auto Request")
        viewModelScope.launch {
            val result = httpSendAuto()
            Log.d(TAG, "Auto Request $result")
        }
    }

    private suspend fun httpSendAuto(): String {
        var src = "failed"
        withContext(Dispatchers.IO) {
            var http: HttpURLConnection? = null
            try {
                val url = URL(
                    BASE_URI + "ac_auto.php?" + String.format(
                        "isEnabled=%d&onTemp=%d&targetTemp=%d",
                        if (isAuto.value!!) 1 else 0,
                        autoTemp.value,
                        targetTemp.value
                    )
                )
                http = url.openConnection() as HttpURLConnection
                http.requestMethod = "GET"
                http.connect()

                src = http.inputStream.bufferedReader().readText()
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                http?.disconnect()
            }
        }
        return src
    }
/*
    fun getCurrentSetting() {
        viewModelScope.launch {
            //TODO get json
            val setting = httpGetCurrentSetting()
        }
    }

    private suspend fun httpGetCurrentSetting() {

        var src = "-273"
        withContext(Dispatchers.IO) {
            var http: HttpURLConnection? = null
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
        }
//        return src
    }

 */

}