package com.example.practica7nativas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.practica7nativas.NotificationHistoryScreen


class NotificationHistoryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotificationHistoryScreen(onBack = { finish() })
        }
    }
}

