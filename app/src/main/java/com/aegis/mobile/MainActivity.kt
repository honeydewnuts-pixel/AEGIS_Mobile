package com.aegis.mobile

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.CombinedData

class MainActivity : AppCompatActivity() {

    private lateinit var chart: CombinedChart
    private lateinit var engine: IndicatorEngine
    private lateinit var batteryText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        chart = findViewById(R.id.mainChart)
        batteryText = findViewById(R.id.batteryText)
        
        setupChart()
        engine = IndicatorEngine(chart)
        
        val sampleData = DataFeed.getSampleCandles()
        val closePrices = sampleData.map { it.close }

        engine.addBollingerBands(closePrices, period = 20, deviation = 1.0, color = Color.parseColor("#C7C7CE"))
        engine.addBollingerBands(closePrices, period = 30, deviation = 1.0, color = Color.WHITE)
        engine.addMA(closePrices, period = 10, maMethod = "SMA", color = Color.BLUE)
        engine.addMA(closePrices, period = 100, maMethod = "SMA", color = Color.YELLOW)
        engine.addRSI(closePrices, period = 9, color = Color.parseColor("#C7C7CE"))
        engine.addCCI(sampleData, period = 6, color = Color.BLUE)
        engine.addWPR(sampleData, period = 60, color = Color.parseColor("#01325B"))
        engine.addEnvelopes(closePrices, period = 10, deviation = 0.6, color = Color.parseColor("#01325B"))

        val battery = BatteryMonitor.getStatus(this)
        batteryText.text = "Battery: ${battery.level}% | Temp: ${battery.temperature}°C"
    }

    private fun setupChart() {
        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.setBackgroundColor(Color.parseColor("#0D0D0D"))
        chart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        chart.xAxis.textColor = Color.WHITE
        chart.axisLeft.textColor = Color.WHITE
        chart.axisRight.textColor = Color.WHITE
        chart.legend.textColor = Color.WHITE
        chart.data = CombinedData()
    }
}
