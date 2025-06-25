package com.example.practica7nativas

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationHistoryScreen(onBack: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val currentToken = remember { mutableStateOf("") }
    var notifications by remember { mutableStateOf<List<NotificationItem>>(emptyList()) }

    // Obtener token actual del usuario para filtrar
    LaunchedEffect(Unit) {
        val auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        if (uid != null) {
            val userDoc = db.collection("users").document(uid).get().await()
            currentToken.value = userDoc.getString("fcmToken") ?: ""
        }

        // Obtener historial de notificaciones
        try {
            val snapshot = db.collection("notifications")
                .whereEqualTo("toToken", currentToken.value)
                .orderBy("timestamp")
                .get().await()

            notifications = snapshot.documents.mapNotNull { doc ->
                val title = doc.getString("title") ?: return@mapNotNull null
                val body = doc.getString("body") ?: return@mapNotNull null
                NotificationItem(title, body)
            }
        } catch (e: Exception) {
            Log.e("NotifHist", "Error al obtener historial", e)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Historial de Notificaciones") })
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (notifications.isEmpty()) {
                Text("No hay notificaciones recibidas.")
            } else {
                notifications.forEach {
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text("🔔 ${it.title}", style = MaterialTheme.typography.titleMedium)
                            Text(it.body, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Regresar")
            }
        }
    }
}

data class NotificationItem(
    val title: String,
    val body: String
)
