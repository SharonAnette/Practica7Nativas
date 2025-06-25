package com.example.practica7nativas

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class ActivityFeedActivity : ComponentActivity() {
    companion object {
        fun newIntent(context: Context): Intent =
            Intent(context, ActivityFeedActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ActivityFeedScreen(onBack = { finish() })
        }
    }
}
