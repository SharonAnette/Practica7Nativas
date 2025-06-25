package com.example.practica7nativas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment



data class Usuario(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val role: String = ""
)

@Composable
fun AdminCrudScreen(onBack: () -> Unit) {

    /* ---------- Estado y helpers ---------- */
    val db = FirebaseFirestore.getInstance()
    val scope = rememberCoroutineScope()

    var usuarios            by remember { mutableStateOf(listOf<Usuario>()) }
    var nombre              by remember { mutableStateOf("") }
    var email               by remember { mutableStateOf("") }
    var role                by remember { mutableStateOf("user") }
    var usuarioEditandoId   by remember { mutableStateOf<String?>(null) }

    suspend fun recargar() {
        usuarios = db.collection("users").get().await().mapNotNull { d ->
            Usuario(
                uid   = d.id,
                name  = d.getString("name")  ?: return@mapNotNull null,
                email = d.getString("email") ?: return@mapNotNull null,
                role  = d.getString("role")  ?: "user"
            )
        }
    }

    LaunchedEffect(Unit) { scope.launch { recargar() } }

    /* ---------- UI ---------- */
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        shape  = RoundedCornerShape(24.dp),
        color  = MaterialTheme.colorScheme.surfaceVariant
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            /* --- Barra superior  --- */
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                }
                Text("Gestión de usuarios", style = MaterialTheme.typography.headlineSmall)
            }

            /* --- Formulario Crear/Actualizar --- */
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = role,
                onValueChange = { role = it },
                label = { Text("Rol (user/admin)") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    scope.launch {
                        val uid = usuarioEditandoId ?: db.collection("users").document().id
                        db.collection("users").document(uid).set(
                            mapOf("name" to nombre, "email" to email, "role" to role)
                        ).await()
                        usuarioEditandoId = null
                        nombre = ""; email = ""; role = "user"
                        recargar()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(if (usuarioEditandoId == null) "Crear usuario" else "Guardar cambios")
            }

            Divider()

            Text("Lista de usuarios", style = MaterialTheme.typography.titleMedium)

            /* --- Lista desplazable --- */
            LazyColumn(
                modifier = Modifier.weight(1f),           // <- ocupa todo el alto sobrante
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(usuarios) { usuario ->

                    // Estados locales para edición en línea
                    var nombreEdit by remember { mutableStateOf(usuario.name) }
                    var emailEdit  by remember { mutableStateOf(usuario.email) }
                    var roleEdit   by remember { mutableStateOf(usuario.role) }
                    val editing = usuario.uid == usuarioEditandoId

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape    = RoundedCornerShape(12.dp)
                    ) {
                        Column(Modifier.padding(12.dp)) {

                            if (editing) {
                                OutlinedTextField(
                                    value = nombreEdit,
                                    onValueChange = { nombreEdit = it },
                                    label = { Text("Nombre") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = emailEdit,
                                    onValueChange = { emailEdit = it },
                                    label = { Text("Email") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                                OutlinedTextField(
                                    value = roleEdit,
                                    onValueChange = { roleEdit = it },
                                    label = { Text("Rol") },
                                    modifier = Modifier.fillMaxWidth()
                                )
                            } else {
                                Text("Nombre: ${usuario.name}")
                                Text("Email: ${usuario.email}")
                                Text("Rol: ${usuario.role}")
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedButton(
                                    onClick = {
                                        scope.launch {
                                            db.collection("users")
                                                .document(usuario.uid)
                                                .delete()
                                                .await()
                                            recargar()
                                        }
                                    }
                                ) { Text("Eliminar") }

                                if (editing) {
                                    OutlinedButton(
                                        onClick = {
                                            scope.launch {
                                                db.collection("users")
                                                    .document(usuario.uid)
                                                    .update(
                                                        mapOf(
                                                            "name"  to nombreEdit,
                                                            "email" to emailEdit,
                                                            "role"  to roleEdit
                                                        )
                                                    ).await()
                                                usuarioEditandoId = null
                                                recargar()
                                            }
                                        }
                                    ) { Text("Guardar") }
                                } else {
                                    OutlinedButton(onClick = { usuarioEditandoId = usuario.uid }) {
                                        Text("Editar")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
