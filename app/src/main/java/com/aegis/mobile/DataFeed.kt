package com.aegis.mobile

object DataFeed {
    // Sample candles so we can see the indicators work before connecting real API
    fun getSampleCandles(): List<CalculationEngine.Candle> {
        val list = mutableListOf<CalculationEngine.Candle>()
        var price = 50000.0
        for (i in 0..200) {
            price += (Math.random() - 0.5) * 500
            list.add(CalculationEngine.Candle(
                open = price,
                high = price + 200,
                low = price - 200,
                close = price
            ))
        }
        return list
    }
}
