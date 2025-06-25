package com.example.practica7nativas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class NotificationSenderActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotificationSenderScreen(onBack = { finish() })
        }
    }
}
