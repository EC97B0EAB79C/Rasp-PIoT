package com.tkbaze.rasp_piot.temperature

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.tkbaze.rasp_piot.R
import com.tkbaze.rasp_piot.databinding.ViewSliderWithValueBinding

class TemperatureFragment : Fragment() {

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

        binding.sliderSetting.valueTo = viewModel.maxTemp.toFloat()
        binding.sliderSetting.valueFrom = viewModel.minTemp.toFloat()
        binding.sliderSetting.value = viewModel.targetTemp.toFloat()

        binding.textCurrentValue.text = viewModel.currentTemp.toString()
        binding.textTargetValue.text = viewModel.targetTemp.toString()

        binding.textCurrentUnit.setText(R.string.celsius)
        binding.textTargetUnit.setText(R.string.celsius)

        binding.textCurrent.setText(R.string.current)
        binding.textTarget.setText(R.string.target)
    }
}