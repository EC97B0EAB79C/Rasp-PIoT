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

    lateinit var handler: Handler
    private val updateTask = object : Runnable {
        override fun run() {
            Log.d(TAG, "update call")
            viewModel.updateCurrentTemp()
            viewModel.getSetting()
            handler.postDelayed(this, 60 * 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        handler = Handler(Looper.getMainLooper())
    }

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


        viewModel.isOn.observe(viewLifecycleOwner) {
            if (it) binding.buttonPower.text = getString(R.string.ac_on)
            else binding.buttonPower.text = getString(R.string.ac_off)
        }


        binding.buttonPower.setOnClickListener {
            viewModel.toggleIsOn()
            viewModel.sendSettingRequest()
            updateUI()
        }

        viewModel.isAuto.observe(viewLifecycleOwner) {
            binding.switchAuto.isChecked = it
        }
        binding.switchAuto.setOnCheckedChangeListener { _, b ->
            viewModel.setIsAuto(b)
            viewModel.sendAutoRequest()
        }
    }

    private fun updateUI() {
        binding.apply {
            sliderSetting.valueFrom = viewModel.minTemp.toFloat()
            sliderSetting.valueTo = viewModel.maxTemp.toFloat()
            viewModel.targetTemp.observe(viewLifecycleOwner) {
                sliderSetting.value = it.toFloat()
            }

            sliderAuto.valueFrom = viewModel.minTemp.toFloat()
            sliderAuto.valueTo = viewModel.maxTemp.toFloat()
            viewModel.autoTemp.observe(viewLifecycleOwner) {
                sliderAuto.value = it.toFloat()
            }

            viewModel.currentTemp.observe(viewLifecycleOwner) {
                textCurrentValue.text =
                    if (it != -273) it.toString()
                    else "Err"
            }
            textCurrentUnit.setText(R.string.celsius)
            textCurrent.setText(R.string.current)

            viewModel.targetTemp.observe(viewLifecycleOwner){
                textTargetValue.text = it.toString()
            }

            textTargetUnit.setText(R.string.celsius)
            textTarget.setText(R.string.target)

            viewModel.isOn.observe(viewLifecycleOwner) {
                if (it) buttonPower.text = getString(R.string.ac_on)
                else buttonPower.text = getString(R.string.ac_off)
            }

            viewModel.autoTemp.observe(viewLifecycleOwner){
                switchAuto.text = String.format(getString(R.string.autoAC), it)
            }

        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(updateTask)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateTask)
    }
}