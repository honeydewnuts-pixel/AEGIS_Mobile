package com.aegis.mobile.ui

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.aegis.mobile.R
import com.aegis.mobile.automation.Mt5AccessibilityService
import com.aegis.mobile.capture.ScreenCaptureService

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: StatusViewModel
    private lateinit var statusText: TextView
    private lateinit var startBtn: Button
    private lateinit var settingsBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        statusText = findViewById(R.id.statusText)
        startBtn = findViewById(R.id.startBtn)
        settingsBtn = findViewById(R.id.settingsBtn)
        
        viewModel = ViewModelProvider(this)[StatusViewModel::class.java]

        // Observe signal from ViewModel
        viewModel.signal.observe(this) { signal ->
            statusText.text = "Signal: $signal"
            
            // PHASE 2: AUTO-CLICK MT5
            if (signal == "BUY" || signal == "SELL") {
                if (isAccessibilityEnabled()) {
                    Mt5AccessibilityService.executeTrade(signal)
                    Toast.makeText(this, "Executing $signal on MT5", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Enable Accessibility Service first!", Toast.LENGTH_LONG).show()
                    openAccessibilitySettings()
                }
            }
        }

        startBtn.setOnClickListener {
            startForegroundService(Intent(this, ScreenCaptureService::class.java))
            Toast.makeText(this, "AEGIS Started", Toast.LENGTH_SHORT).show()
        }

        settingsBtn.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }
    }

    private fun isAccessibilityEnabled(): Boolean {
        val service = "$packageName/.automation.Mt5AccessibilityService"
        val enabledServices = Settings.Secure.getString(
            contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        return enabledServices?.contains(service) == true
    }

    private fun openAccessibilitySettings() {
        startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
    }
}
