package com.example.practica7nativas

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import kotlinx.coroutines.tasks.await

@Composable
fun NotificationSenderScreen(onBack: () -> Unit) {
    val db = FirebaseFirestore.getInstance()
    val functions = FirebaseFunctions.getInstance()

    var title by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var users by remember { mutableStateOf<List<UserOption>>(emptyList()) }
    val selectedUids = remember { mutableStateMapOf<String, Boolean>() }

    // Cargar usuarios desde Firestore
    LaunchedEffect(Unit) {
        try {
            val snapshot = db.collection("users").get().await()
            val userList = snapshot.documents.mapNotNull { doc ->
                val uid = doc.id
                val name = doc.getString("name") ?: return@mapNotNull null
                val email = doc.getString("email") ?: return@mapNotNull null
                val token = doc.getString("fcmToken")
                UserOption(uid, "$name <$email>", token)
            }
            users = userList
        } catch (e: Exception) {
            Log.e("NotificationScreen", "Error al cargar usuarios", e)
        }
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Enviar notificaciones", style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Título") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                label = { Text("Mensaje") },
                modifier = Modifier.fillMaxWidth()
            )

            Text("Selecciona destinatarios:", style = MaterialTheme.typography.bodyMedium)

            if (users.isEmpty()) {
                Text("No hay usuarios disponibles", style = MaterialTheme.typography.bodyMedium)
            } else {
                Column {
                    users.forEach { user ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Checkbox(
                                checked = selectedUids[user.uid] ?: false,
                                onCheckedChange = { selectedUids[user.uid] = it }
                            )
                            Text(user.label)
                        }
                    }
                }
            }

            Button(
                onClick = {
                    val selectedUsers = users.filter { selectedUids[it.uid] == true }

                    selectedUsers.forEach { user ->
                        val data = hashMapOf(
                            "body" to message,
                            "title" to title,
                            "token" to user.fcmToken

                        )

                        // 🔍 Log para depurar qué datos se están enviando
                        Log.d("NotifDebug", "Enviando notificación con data: $data")

                        functions
                            .getHttpsCallable("sendNotification")
                            .call(data)
                            .addOnSuccessListener {
                                Log.d("Notif", "✅ Notificación enviada a ${user.label}")
                            }
                            .addOnFailureListener { err ->
                                Log.e("Notif", "❌ Error al enviar notificación", err)
                            }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Enviar notificación")
            }


            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Regresar")
            }
        }
    }
}

// Clase de modelo de usuario con token incluido
data class UserOption(
    val uid: String,
    val label: String,
    val fcmToken: String?
)
