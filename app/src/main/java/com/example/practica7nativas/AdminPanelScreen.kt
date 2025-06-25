package com.example.practica7nativas

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AdminPanelScreen() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()

    var adminName by remember { mutableStateOf("") }
    var adminEmail by remember { mutableStateOf("") }


    // Cargar datos del admin desde Firestore en tiempo real
    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect
        db.collection("users").document(uid)
            .addSnapshotListener { docSnapshot, error ->
                if (error != null || docSnapshot == null || !docSnapshot.exists()) return@addSnapshotListener

                adminName = docSnapshot.getString("name") ?: "Admin"
                adminEmail = docSnapshot.getString("email") ?: ""
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Bienvenido Administrador", style = MaterialTheme.typography.headlineSmall)

            Image(
                painter = painterResource(id = R.drawable.gato),
                contentDescription = "Foto de Admin",
                modifier = Modifier
                    .size(100.dp)
            )

            Text(adminName, style = MaterialTheme.typography.titleMedium)
            Text(adminEmail, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    context.startActivity(Intent(context, AdminCrudActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Gestión de usuarios")
            }

            Button(
                onClick = {
                    context.startActivity(Intent(context, NotificationSenderActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Enviar notificaciones")
            }

            OutlinedButton(
                onClick = {
                    auth.signOut()
                    context.startActivity(Intent(context, LoginActivity::class.java))
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}
