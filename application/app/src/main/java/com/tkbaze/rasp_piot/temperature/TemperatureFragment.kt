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

    // ViewBinding Variable
    private var _binding: ViewSliderWithValueBinding? = null
    private val binding get() = _binding!!

    // ViewModel Variable
    private val viewModel = TemperatureViewModel()

    // Run Runnable every minute
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

        // Start Handler
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

    /* Set Listener for Views
        slider  - onChangeListener, onSliderTouchListener,
        button  - onClickListener
        switch  - onCheckedChangeListener
     */
    private fun setListener() {

        // sliderSetting
        // set value in ViewModel when slider value changes
        binding.sliderSetting.addOnChangeListener { _, value, _ ->
            viewModel.setTargetTemp(value.toInt())
            updateUI()
        }
        // make HTTP request when user stop using slider
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

        // sliderAuto
        // set value in ViewModel when slider value changes
        binding.sliderAuto.addOnChangeListener { _, value, _ ->
            viewModel.setAutoTemp(value.toInt())
            updateUI()
        }
        // make HTTP request when user stop using slider
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

        // buttonPower
        // set button text according to status
        viewModel.isOn.observe(viewLifecycleOwner) {
            if (it) binding.buttonPower.text = getString(R.string.ac_on)
            else binding.buttonPower.text = getString(R.string.ac_off)
        }
        // set button onClickListener
        binding.buttonPower.setOnClickListener {
            viewModel.toggleIsOn()
            viewModel.sendSettingRequest()
            updateUI()
        }

        // switchAuto
        // set switch checked according to status
        viewModel.isAuto.observe(viewLifecycleOwner) {
            binding.switchAuto.isChecked = it
        }
        // set switch onCheckedChangeListener
        binding.switchAuto.setOnCheckedChangeListener { _, b ->
            viewModel.setIsAuto(b)
            viewModel.sendAutoRequest()
        }
    }

    // Update UI
    private fun updateUI() {
        binding.apply {
            // sliderSetting
            sliderSetting.valueFrom = viewModel.minTemp.toFloat()
            sliderSetting.valueTo = viewModel.maxTemp.toFloat()
            viewModel.targetTemp.observe(viewLifecycleOwner) {
                sliderSetting.value = it.toFloat()
            }

            // sliderAuto
            sliderAuto.valueFrom = viewModel.minTemp.toFloat()
            sliderAuto.valueTo = viewModel.maxTemp.toFloat()
            viewModel.autoTemp.observe(viewLifecycleOwner) {
                sliderAuto.value = it.toFloat()
            }

            // currentTemp
            viewModel.currentTemp.observe(viewLifecycleOwner) {
                textCurrentValue.text =
                    if (it != -273) it.toString()
                    else "Err"
            }
            textCurrentUnit.setText(R.string.celsius)
            textCurrent.setText(R.string.current)

            // targetTemp
            viewModel.targetTemp.observe(viewLifecycleOwner) {
                textTargetValue.text = it.toString()
            }
            textTargetUnit.setText(R.string.celsius)
            textTarget.setText(R.string.target)

            // buttonPower text
            viewModel.isOn.observe(viewLifecycleOwner) {
                if (it) buttonPower.text = getString(R.string.ac_on)
                else buttonPower.text = getString(R.string.ac_off)
            }

            // switchTemp  text
            viewModel.autoTemp.observe(viewLifecycleOwner) {
                switchAuto.text = String.format(getString(R.string.autoAC), it)
            }

        }
    }

    // Start and Resume handler when onResume and OnPause
    override fun onResume() {
        super.onResume()
        handler.post(updateTask)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateTask)
    }
}