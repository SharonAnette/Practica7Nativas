package com.example.practica7nativas

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check


@Composable
fun LoginScreen() {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db   = FirebaseFirestore.getInstance()

    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error    by remember { mutableStateOf<String?>(null) }
    var isAdmin  by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        shape  = RoundedCornerShape(24.dp),
        color  = MaterialTheme.colorScheme.surfaceVariant      // 💜
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Inicio de sesión", style = MaterialTheme.typography.headlineSmall)
            Spacer(Modifier.height(16.dp))

            // Selector usuario / admin
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = !isAdmin,
                    onClick = { isAdmin = false },
                    label = { Text("Usuario") },
                    enabled = true,
                    leadingIcon = if (!isAdmin) {
                        { Icon(Icons.Default.Check, contentDescription = null) }
                    } else null
                )

                Spacer(modifier = Modifier.width(8.dp))

                FilterChip(
                    selected = isAdmin,
                    onClick = { isAdmin = true },
                    label = { Text("Admin") },
                    enabled = true,
                    leadingIcon = if (isAdmin) {
                        { Icon(Icons.Default.Check, contentDescription = null) }
                    } else null
                )
            }


            Spacer(Modifier.height(24.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    auth.signInWithEmailAndPassword(email, password)
                        .addOnSuccessListener { result ->
                            val uid = result.user?.uid ?: return@addOnSuccessListener
                            db.collection("users").document(uid).get()
                                .addOnSuccessListener { doc ->
                                    val role = doc.getString("role") ?: "user"
                                    if (isAdmin && role != "admin") {
                                        error = "No tienes permiso de administrador"
                                        auth.signOut()
                                    } else {
                                        // Obtener y guardar FCM token
                                        FirebaseMessaging.getInstance().token
                                            .addOnSuccessListener { token ->
                                                db.collection("users").document(uid)
                                                    .update("fcmToken", token)
                                                    .addOnSuccessListener {
                                                        context.startActivity(
                                                            Intent(
                                                                context,
                                                                if (role == "admin")
                                                                    AdminPanelActivity::class.java
                                                                else
                                                                    MainActivity::class.java
                                                            )
                                                        )
                                                    }
                                                    .addOnFailureListener {
                                                        error = "No se pudo guardar el token FCM"
                                                    }
                                            }
                                            .addOnFailureListener {
                                                error = "No se pudo obtener el token FCM"
                                            }
                                    }
                                }
                        }
                        .addOnFailureListener {
                            error = "Error al iniciar sesión: ${it.localizedMessage}"
                        }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Entrar")
            }

            if (!isAdmin) {
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = {
                        context.startActivity(Intent(context, RegisterActivity::class.java))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Registrarse")
                }
            }

            error?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}
