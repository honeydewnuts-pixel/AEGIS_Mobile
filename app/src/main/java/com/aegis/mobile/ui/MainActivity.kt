package com.aegis.mobile.ui

import android.app.Activity
import android.content.Intent
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.aegis.mobile.R
import com.aegis.mobile.capture.ScreenCaptureService

class MainActivity : AppCompatActivity() {

    private lateinit var viewModel: StatusViewModel
    private lateinit var btnStart: Button
    private lateinit var tvSignal: TextView
    private lateinit var tvDetails: TextView
    private var isRunning = false

    private val projectionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val serviceIntent = Intent(this, ScreenCaptureService::class.java).apply {
                putExtra(ScreenCaptureService.EXTRA_RESULT_CODE, result.resultCode)
                putExtra(ScreenCaptureService.EXTRA_RESULT_DATA, result.data)
            }
            startForegroundService(serviceIntent)
            isRunning = true
            btnStart.text = "STOP AEGIS"
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this)[StatusViewModel::class.java]
        btnStart = findViewById(R.id.btnStart)
        tvSignal = findViewById(R.id.tvSignal)
        tvDetails = findViewById(R.id.tvDetails)

        btnStart.setOnClickListener {
            if (!isRunning) {
                val projectionManager = getSystemService(MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
                projectionLauncher.launch(projectionManager.createScreenCaptureIntent())
            } else {
                stopService(Intent(this, ScreenCaptureService::class.java))
                isRunning = false
                btnStart.text = "START AEGIS"
                viewModel.updateSignal(null)
            }
        }

        viewModel.currentSignal.observe(this) { signal ->
            tvSignal.text = signal?.signal ?: "HOLD"
            tvDetails.text = "Confidence: ${(signal?.confidence ?: 0f) * 100}% \n${signal?.details ?: "Waiting for Brain..."}"
            
            // Change color based on signal
            tvSignal.setBackgroundColor(
                when(signal?.signal) {
                    "BUY" -> 0xFF4CAF50.toInt() // Green
                    "SELL" -> 0xFFF44336.toInt() // Red
                    else -> 0xFF9E9E9E.toInt() // Gray
                }
            )
        }
    }
}
