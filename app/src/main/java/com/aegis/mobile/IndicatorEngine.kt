package com.aegis.mobile

import android.graphics.Color
import com.github.mikephil.charting.charts.CombinedChart
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.*

class IndicatorEngine(private val chart: CombinedChart) {

    private fun toEntries(data: List<Double>): List<Entry> {
        return data.mapIndexed { i, v -> Entry(i.toFloat(), v.toFloat()) }
    }

    fun addMA(data: List<Double>, period: Int, maMethod: String = "SMA", color: Int) {
        val calcData = if(maMethod == "SMMA") CalculationEngine.smma(data, period) else CalculationEngine.sma(data, period)
        val ma = LineDataSet(toEntries(calcData), "MA${period}_${maMethod}")
        ma.color = color; ma.lineWidth = 1f; ma.setDrawCircles(false)

        val combinedData = chart.data ?: CombinedData()
        combinedData.addDataSet(ma)
        chart.data = combinedData
        chart.invalidate()
    }

    fun addBollingerBands(data: List<Double>, period: Int, deviation: Double, color: Int) {
        val (upper, middle, lower) = CalculationEngine.bollingerBands(data, period, deviation)
        
        val u = LineDataSet(toEntries(upper), "BB${period}_U"); u.color = color; u.lineWidth = 1f; u.setDrawCircles(false)
        val m = LineDataSet(toEntries(middle), "BB${period}_M"); m.color = color; m.lineWidth = 1f; m.setDrawCircles(false)
        val l = LineDataSet(toEntries(lower), "BB${period}_L"); l.color = color; l.lineWidth = 1f; l.setDrawCircles(false)
        
        val combinedData = chart.data ?: CombinedData()
        combinedData.addDataSet(u); combinedData.addDataSet(m); combinedData.addDataSet(l)
        chart.data = combinedData
        chart.invalidate()
    }

    fun addRSI(data: List<Double>, period: Int, color: Int) {
        val calcData = CalculationEngine.rsi(data, period)
        val rsi = LineDataSet(toEntries(calcData), "RSI${period}")
        rsi.color = color; rsi.lineWidth = 1f; rsi.axisDependency = YAxis.AxisDependency.RIGHT

        val combinedData = chart.data ?: CombinedData()
        combinedData.addDataSet(rsi)
        chart.data = combinedData
        chart.invalidate()
    }
    
    // 1. CCI - NOW FILLED
    fun addCCI(candles: List<CalculationEngine.Candle>, period: Int, color: Int) {
        val calcData = CalculationEngine.cci(candles, period)
        val cci = LineDataSet(toEntries(calcData), "CCI${period}")
        cci.color = color; cci.lineWidth = 1f; cci.axisDependency = YAxis.AxisDependency.RIGHT
        val combinedData = chart.data?: CombinedData()
        combinedData.addDataSet(cci); chart.data = combinedData; chart.invalidate()
    }

    // 2. WPR - NOW FILLED
    fun addWPR(candles: List<CalculationEngine.Candle>, period: Int, color: Int) {
        val calcData = CalculationEngine.wpr(candles, period)
        val wpr = LineDataSet(toEntries(calcData), "WPR${period}")
        wpr.color = color; wpr.lineWidth = 1f; wpr.axisDependency = YAxis.AxisDependency.RIGHT
        val combinedData = chart.data?: CombinedData()
        combinedData.addDataSet(wpr); chart.data = combinedData; chart.invalidate()
    }

    // 3. ENVELOPES - NOW FILLED
    fun addEnvelopes(data: List<Double>, period: Int, deviation: Double, color: Int) {
        val (upper, lower) = CalculationEngine.envelopes(data, period, deviation)
        val u = LineDataSet(toEntries(upper), "ENV${period}_U"); u.color = color; u.lineWidth = 1f; u.setDrawCircles(false)
        val l = LineDataSet(toEntries(lower), "ENV${period}_L"); l.color = color; l.lineWidth = 1f; l.setDrawCircles(false)
        val combinedData = chart.data?: CombinedData()
        combinedData.addDataSet(u); combinedData.addDataSet(l); chart.data = combinedData; chart.invalidate()
    }
}
