package com.example.practica7nativas

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun HomeScreen(email: String) {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    val context = LocalContext.current

    var name by remember { mutableStateOf("Cargando...") }
    var role by remember { mutableStateOf("") }
    var photoUrl by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val uid = auth.currentUser?.uid ?: return@LaunchedEffect
        db.collection("users").document(uid)
            .addSnapshotListener { docSnapshot, error ->
                if (error != null || docSnapshot == null || !docSnapshot.exists()) return@addSnapshotListener

                name = docSnapshot.getString("name") ?: ""
                role = docSnapshot.getString("role") ?: "user"
                photoUrl = docSnapshot.getString("photoUrl") ?: ""
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
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Bienvenida/o, $email", style = MaterialTheme.typography.headlineSmall)

            // Imagen de perfil o predeterminada
            if (photoUrl.isNotBlank()) {
                Image(
                    painter = rememberAsyncImagePainter(photoUrl),
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.default_profile),
                    contentDescription = "Imagen predeterminada",
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                )
            }

            Text("Nombre: $name", style = MaterialTheme.typography.bodyLarge)
            Text("Rol: $role", style = MaterialTheme.typography.bodyLarge)

            if (role == "admin") {
                Button(
                    onClick = {
                        context.startActivity(Intent(context, AdminPanelActivity::class.java))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Operaciones CRUD y notificaciones")
                }
            }

            if (role == "user") {
                Button(
                    onClick = {
                        context.startActivity(Intent(context, EditProfileWithImageActivity::class.java))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Editar mi perfil")
                }

                Button(
                    onClick = {
                        context.startActivity(Intent(context, NotificationHistoryActivity::class.java))
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text("Historial de notificaciones")
                }

            }

            OutlinedButton(
                onClick = {
                    auth.signOut()
                    val intent = Intent(context, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    context.startActivity(intent)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {
                Text("Cerrar sesión")
            }
        }
    }
}
