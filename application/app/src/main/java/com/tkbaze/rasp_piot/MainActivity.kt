package com.tkbaze.rasp_piot

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.tkbaze.rasp_piot.databinding.ActivityMainBinding
import com.tkbaze.rasp_piot.temperature.TemperatureFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.container, TemperatureFragment())
                .commitNow()
        }
    }
}