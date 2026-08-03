package com.aegis.mobile

import android.graphics.Color
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*

class IndicatorEngine(private val chart: CombinedChart) {

    fun addBollingerBands(period: Int, deviation: Double, color: Int, applyTo: String = "CLOSE", showData: Boolean = true) {
        // Logic: Calculate BB and add 3 lines: Upper, Middle, Lower
        // This is a placeholder - real calc needs candle data
        val upper = LineDataSet(mutableListOf(), "BB${period}_Upper")
        val middle = LineDataSet(mutableListOf(), "BB${period}_Middle") 
        val lower = LineDataSet(mutableListOf(), "BB${period}_Lower")
        
        upper.color = color; upper.lineWidth = 1f; upper.setDrawCircles(false)
        middle.color = color; middle.lineWidth = 1f; middle.setDrawCircles(false)
        lower.color = color; lower.lineWidth = 1f; lower.setDrawCircles(false)
        
        val data = chart.data ?: CombinedData()
        data.addDataSet(upper); data.addDataSet(middle); data.addDataSet(lower)
        chart.data = data
        chart.invalidate()
    }

    fun addMA(period: Int, maMethod: String = "SMA", color: Int, applyTo: String = "CLOSE", showData: Boolean = true) {
        val ma = LineDataSet(mutableListOf(), "MA${period}_${maMethod}")
        ma.color = color; ma.lineWidth = 1f; ma.setDrawCircles(false)
        
        val data = chart.data ?: CombinedData()
        data.addDataSet(ma)
        chart.data = data
        chart.invalidate()
    }

    fun addRSI(period: Int, color: Int, showData: Boolean = true) {
        val rsi = LineDataSet(mutableListOf(), "RSI${period}")
        rsi.color = color; rsi.lineWidth = 1f; rsi.axisDependency = YAxis.AxisDependency.RIGHT
        
        val data = chart.data ?: CombinedData()
        data.addDataSet(rsi)
        chart.data = data
        chart.invalidate()
    }

    fun addCCI(period: Int, applyTo: String = "MEDIAN_PRICE", color: Int, showData: Boolean = true) {
        val cci = LineDataSet(mutableListOf(), "CCI${period}")
        cci.color = color; cci.lineWidth = 1f; cci.axisDependency = YAxis.AxisDependency.RIGHT
        
        val data = chart.data ?: CombinedData()
        data.addDataSet(cci)
        chart.data = data
        chart.invalidate()
    }

    fun addWPR(period: Int, color: Int, showData: Boolean = true) {
        val wpr = LineDataSet(mutableListOf(), "WPR${period}")
        wpr.color = color; wpr.lineWidth = 1f; wpr.axisDependency = YAxis.AxisDependency.RIGHT
        
        val data = chart.data ?: CombinedData()
        data.addDataSet(wpr)
        chart.data = data
        chart.invalidate()
    }

    fun addEnvelopes(period: Int, deviation: Double, maMethod: String = "SMA", color: Int, applyTo: String = "CLOSE", showData: Boolean = true) {
        val upper = LineDataSet(mutableListOf(), "ENV${period}_Upper")
        val lower = LineDataSet(mutableListOf(), "ENV${period}_Lower")
        
        upper.color = color; upper.lineWidth = 1f; upper.setDrawCircles(false)
        lower.color = color; lower.lineWidth = 1f; lower.setDrawCircles(false)
        
        val data = chart.data ?: CombinedData()
        data.addDataSet(upper); data.addDataSet(lower)
        chart.data = data
        chart.invalidate()
    }
}
