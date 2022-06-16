package com.tkbaze.rasp_piot.temperature

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.slider.Slider
import com.tkbaze.rasp_piot.R
import com.tkbaze.rasp_piot.databinding.ViewSliderWithValueBinding

class TemperatureFragment : Fragment() {

    companion object {
        const val TAG = "TemperatureFragment"
    }

    private var _binding: ViewSliderWithValueBinding? = null
    private val binding get() = _binding!!

    private val viewModel = TemperatureViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ViewSliderWithValueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val handler: Handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable{
            override fun run() {
                Log.d(TAG,"update call")
                viewModel.updateCurrentTemp()
                handler.postDelayed(this,60*1000)
            }
        })

        setListener()
        updateUI()
    }

    private fun setListener() {
        binding.sliderSetting.addOnChangeListener { _, value, _ ->
            viewModel.setTargetTemp(value.toInt())
            updateUI()
        }
        binding.sliderSetting.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            @SuppressLint("RestrictedApi")
            override fun onStartTrackingTouch(slider: Slider) {
                // Do nothing
            }

            @SuppressLint("RestrictedApi")
            override fun onStopTrackingTouch(slider: Slider) {
                viewModel.sendSettingRequest()
            }
        })

        binding.sliderAuto.addOnChangeListener { _, value, _ ->
            viewModel.setAutoTemp(value.toInt())
            updateUI()
        }

        binding.sliderAuto.addOnSliderTouchListener(object : Slider.OnSliderTouchListener {
            @SuppressLint("RestrictedApi")
            override fun onStartTrackingTouch(slider: Slider) {
                // Do nothing
            }

            @SuppressLint("RestrictedApi")
            override fun onStopTrackingTouch(slider: Slider) {
                viewModel.sendAutoRequest()
            }
        })

        binding.buttonPower.setOnClickListener {
            viewModel.toggleIsOn()
            updateUI()
        }

        binding.switchAuto.isChecked = viewModel.isAuto
        binding.switchAuto.setOnCheckedChangeListener { _, b ->
            viewModel.setIsAuto(b)
        }
    }

    private fun updateUI() {
        binding.apply {
            sliderSetting.valueFrom = viewModel.minTemp.toFloat()
            sliderSetting.valueTo = viewModel.maxTemp.toFloat()
            sliderSetting.value = viewModel.targetTemp.toFloat()

            sliderAuto.valueFrom = viewModel.minTemp.toFloat()
            sliderAuto.valueTo = viewModel.maxTemp.toFloat()
            sliderAuto.value = viewModel.autoTemp.toFloat()

            viewModel.currentTemp.observe(viewLifecycleOwner) {
                textCurrentValue.text =
                    if(it!=-273) it.toString()
                    else "Err"
            }
            textCurrentUnit.setText(R.string.celsius)
            textCurrent.setText(R.string.current)

            textTargetValue.text = viewModel.targetTemp.toString()
            textTargetUnit.setText(R.string.celsius)
            textTarget.setText(R.string.target)

            buttonPower.text =
                if (viewModel.isOn) getString(R.string.ac_on)
                else getString(R.string.ac_off)

            switchAuto.text = String.format(getString(R.string.autoAC), viewModel.autoTemp)
        }
    }
}