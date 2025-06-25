package com.example.practica7nativas
import com.example.practica7nativas.ui.theme.EditProfileWithImageScreen

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class EditProfileWithImageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EditProfileWithImageScreen(
                onBack = { finish() }
            )
        }
    }
}


