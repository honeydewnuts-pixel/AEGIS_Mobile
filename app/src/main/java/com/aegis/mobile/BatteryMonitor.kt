package com.aegis.mobile

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager

object BatteryMonitor {
    data class BatteryStatus(
        val level: Int,
        val isCharging: Boolean,
        val temperature: Float,
        val health: Int
    )

    fun getStatus(context: Context): BatteryStatus {
        val bm = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val intent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        val level = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        val isCharging = intent?.getIntExtra(BatteryManager.EXTRA_STATUS, -1) == BatteryManager.BATTERY_STATUS_CHARGING
        val temp = intent?.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, 0)?.div(10f) ?: 0f
        val health = intent?.getIntExtra(BatteryManager.EXTRA_HEALTH, 0) ?: 0

        return BatteryStatus(level, isCharging, temp, health)
    }
}
