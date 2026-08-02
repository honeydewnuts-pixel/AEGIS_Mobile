package com.aegis.mobile.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.lifecycleScope
import com.aegis.mobile.R
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private val SERVER_IP_KEY = stringPreferencesKey("server_ip")
    private val dataStore by lazy { applicationContext.dataStore }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val etIp = findViewById<EditText>(R.id.etServerIp)
        val btnSave = findViewById<Button>(R.id.btnSave)

        // Load saved IP
        lifecycleScope.launch {
            etIp.setText(dataStore.data.first()[SERVER_IP_KEY] ?: "192.168.1.100")
        }

        btnSave.setOnClickListener {
            lifecycleScope.launch {
                dataStore.edit { settings ->
                    settings[SERVER_IP_KEY] = etIp.text.toString()
                }
                finish() // go back
            }
        }
    }
}
