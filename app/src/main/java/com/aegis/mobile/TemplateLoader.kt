package com.aegis.mobile

import android.graphics.Color

object TemplateLoader {
    
    fun loadAEGISTemplate(chart: ChartView) {
        // === MAIN CHART WINDOW ===
        chart.addBollingerBands(period = 20, deviation = 1.0, color = Color.parseColor("#C7C7CE"), showData = true)
        chart.addBollingerBands(period = 30, deviation = 1.0, color = Color.WHITE, showData = true)
        chart.addMA(period = 10, maMethod = "SMA", color = Color.BLUE, showData = false)
        chart.addMA(period = 100, maMethod = "SMA", color = Color.YELLOW, showData = true)
        chart.addMA(period = 60, maMethod = "SMMA", color = Color.CYAN, applyTo = "PREVIOUS", showData = true)
        chart.addMA(period = 80, maMethod = "SMMA", color = Color.parseColor("#01325B"), applyTo = "PREVIOUS", showData = true)
        chart.addMA(period = 2, maMethod = "SMMA", color = Color.RED, applyTo = "TYPICAL_PRICE", showData = true)
        chart.addEnvelopes(period = 10, deviation = 0.06, maMethod = "SMMA", color = Color.parseColor("#01325B"), applyTo = "TYPICAL_PRICE", showData = false)

        // === INDICATOR WINDOW ===
        chart.addRSI(period = 3, color = Color.BLACK, showData = false)
        chart.addRSI(period = 9, color = Color.parseColor("#C7C7CE"), showData = true)
        chart.addWPR(period = 10, color = Color.CYAN, showData = false)
        chart.addWPR(period = 60, color = Color.parseColor("#01325B"), showData = true)
        chart.addCCI(period = 6, applyTo = "MEDIAN_PRICE", color = Color.BLUE, showData = true)
        chart.addBollingerBands(period = 34, deviation = 1.618, applyTo = "PREVIOUS", color = Color.WHITE, showData = true)
        chart.addBollingerBands(period = 17, deviation = 2.618, applyTo = "PREVIOUS", color = Color.GREEN, showData = true)
        chart.addMA(period = 7, maMethod = "SMA", applyTo = "PREVIOUS", color = Color.MAGENTA, showData = true)
        chart.addMA(period = 17, maMethod = "SMMA", applyTo = "PREVIOUS", color = Color.RED, showData = false)
    }
}
