package com.aegis.mobile

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.CombinedData

class MainActivity : AppCompatActivity() {

    private lateinit var chart: CombinedChart
    private lateinit var engine: IndicatorEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupChart()
        engine = IndicatorEngine(chart)
        
        // THIS IS THE MAGIC: Auto-load the full AEGIS template
        TemplateLoader.loadAEGISTemplate(engine)
    }

    private fun setupChart() {
        chart = findViewById(R.id.mainChart)
        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.setPinchZoom(true)
        chart.setDrawGridBackground(false)
        chart.setBackgroundColor(Color.parseColor("#0D0D0D"))

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = Color.WHITE
        xAxis.setDrawGridLines(false)

        val leftAxis = chart.axisLeft
        leftAxis.textColor = Color.WHITE
        leftAxis.setDrawGridLines(true)
        leftAxis.gridColor = Color.parseColor("#333333")

        val rightAxis = chart.axisRight
        rightAxis.isEnabled = true
        rightAxis.textColor = Color.WHITE

        chart.legend.isEnabled = true
        chart.legend.textColor = Color.WHITE
        
        chart.data = CombinedData()
    }
}
