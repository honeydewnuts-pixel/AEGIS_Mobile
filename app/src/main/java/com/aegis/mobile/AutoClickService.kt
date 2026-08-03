package com.aegis.mobile

import android.accessibilityservice.AccessibilityService
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import org.json.JSONObject

class AutoClickService : AccessibilityService() {

    private val tradeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val signalString = intent?.getStringExtra("signal")?: return
            try {
                val signal = JSONObject(signalString)
                if (signal.getString("action")!= "HOLD") {
                    executeTrade(signal)
                }
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        registerReceiver(tradeReceiver, IntentFilter("EXECUTE_TRADE"), RECEIVER_NOT_EXPORTED)
    }

    private fun executeTrade(signal: JSONObject) {
        val action = signal.getString("action") // BUY or SELL
        val rootNode = rootInActiveWindow?: return

        // Step 1: Open New Order window
        findAndClick(rootNode, "New order")
        Thread.sleep(500)

        // Step 2: Click Buy or Sell button
        if (action == "BUY") {
            findAndClick(rootNode, "Buy")
        } else if (action == "SELL") {
            findAndClick(rootNode, "Sell")
        }
    }

    private fun findAndClick(node: AccessibilityNodeInfo, text: String) {
        val nodes = node.findAccessibilityNodeInfosByText(text)
        if (nodes.isNotEmpty()) {
            nodes[0].performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(tradeReceiver)
    }
}
