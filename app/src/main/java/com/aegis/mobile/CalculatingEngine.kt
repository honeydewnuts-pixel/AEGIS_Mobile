package com.aegis.mobile

import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.math.pow

object CalculationEngine {

    data class Candle(val open: Double, val high: Double, val low: Double, val close: Double)

    fun sma(data: List<Double>, period: Int): List<Double> {
        return data.windowed(period) { it.average() }
    }

    fun smma(data: List<Double>, period: Int): List<Double> {
        val result = MutableList(data.size) { 0.0 }
        if(data.size < period) return result
        result[period-1] = data.take(period).average()
        for (i in period until data.size) {
            result[i] = (result[i-1] * (period - 1) + data[i]) / period
        }
        return result
    }

    fun bollingerBands(data: List<Double>, period: Int, deviation: Double): Triple<List<Double>, List<Double>, List<Double>> {
        val ma = sma(data, period)
        val stdDev = data.windowed(period) { it.stdDev() }
        val upper = ma.zip(stdDev) { m, s -> m + s * deviation }
        val lower = ma.zip(stdDev) { m, s -> m - s * deviation }
        return Triple(upper, ma, lower)
    }

    fun rsi(data: List<Double>, period: Int): List<Double> {
        val gains = mutableListOf<Double>()
        val losses = mutableListOf<Double>()
        for (i in 1 until data.size) {
            val change = data[i] - data[i-1]
            gains.add(if (change > 0) change else 0.0)
            losses.add(if (change < 0) -change else 0.0)
        }
        val avgGain = sma(gains, period)
        val avgLoss = sma(losses, period)
        return avgGain.zip(avgLoss) { g, l -> if (l == 0.0) 100.0 else 100 - (100 / (1 + g / l)) }
    }

    // 1. CCI - Commodity Channel Index
    fun cci(candles: List<Candle>, period: Int): List<Double> {
        val tp = candles.map { (it.high + it.low + it.close) / 3.0 } // Typical Price
        val smaTP = sma(tp, period)
        val meanDev = tp.windowed(period).mapIndexed { i, window ->
            window.map { abs(it - smaTP[i]) }.average()
        }
        return smaTP.zip(meanDev) { s, m -> if(m == 0.0) 0.0 else (tp[tp.indexOf(s)] - s) / (0.015 * m) }
    }

    // 2. WPR - Williams %R
    fun wpr(candles: List<Candle>, period: Int): List<Double> {
        return candles.windowed(period).map { window ->
            val highestHigh = window.maxOf { it.high }
            val lowestLow = window.minOf { it.low }
            val close = window.last().close
            if(highestHigh == lowestLow) 0.0 else -100 * ((highestHigh - close) / (highestHigh - lowestLow))
        }
    }

    // 3. ENVELOPES
    fun envelopes(data: List<Double>, period: Int, deviationPercent: Double): Pair<List<Double>, List<Double>> {
        val ma = sma(data, period)
        val upper = ma.map { it * (1 + deviationPercent / 100.0) }
        val lower = ma.map { it * (1 - deviationPercent / 100.0) }
        return Pair(upper, lower)
    }

    private fun List<Double>.stdDev(): Double {
        val mean = average()
        return sqrt(map { (it - mean).pow(2) }.average())
    }
}
