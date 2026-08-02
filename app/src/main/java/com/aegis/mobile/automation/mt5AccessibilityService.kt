package com.aegis.mobile.automation

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class Mt5AccessibilityService : AccessibilityService() {

    companion object {
        @Volatile
        var instance: Mt5AccessibilityService? = null
        
        fun executeTrade(signal: String) {
            instance?.performMt5Click(signal)
        }
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        val info = AccessibilityServiceInfo().apply {
            eventTypes = AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED
            packageNames = arrayOf("net.metaquotes.metatrader5") // MT5 package
            feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC
            flags = AccessibilityServiceInfo.FLAG_RETRIEVE_INTERACTIVE_WINDOWS
        }
        this.serviceInfo = info
        Log.d("AEGIS-Auto", "Accessibility Service Connected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    private fun performMt5Click(signal: String) {
        val root = rootInActiveWindow ?: return
        // WARNING: You must manually find the "New Order" button text/ID in MT5
        // This is a template. We will need to inspect MT5 UI to get exact button text
        val buttonText = if (signal == "BUY") "Buy" else "Sell"
        
        findAndClick(root, buttonText)
        Log.d("AEGIS-Auto", "Attempted to click: $buttonText")
    }

    private fun findAndClick(node: AccessibilityNodeInfo, text: String): Boolean {
        if (node.text?.toString()?.contains(text, ignoreCase = true) == true && node.isClickable) {
            return node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let {
                if (findAndClick(it, text)) return true
            }
        }
        return false
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
    }
}
