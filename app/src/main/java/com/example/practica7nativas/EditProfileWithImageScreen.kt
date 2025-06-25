package com.example.practica7nativas.ui.theme

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import androidx.compose.ui.layout.ContentScale
import java.util.*

@Composable
fun EditProfileWithImageScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val storage = FirebaseStorage.getInstance().reference

    var name by remember { mutableStateOf("") }
    var imageUrl by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }

    val uid = auth.currentUser?.uid ?: return

    // Cargar datos del usuario
    LaunchedEffect(Unit) {
        db.collection("users").document(uid).get()
            .addOnSuccessListener { doc ->
                name = doc.getString("name") ?: ""
                imageUrl = doc.getString("photoUrl") ?: ""
            }
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) imageUri = uri
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        color = Color(0xFFF5F0FF)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Editar perfil",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            val painter = rememberAsyncImagePainter(
                model = imageUri ?: imageUrl,
                contentScale = ContentScale.Crop
            )

            Image(
                painter = painter,
                contentDescription = "Profile Image",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .clickable { imagePickerLauncher.launch("image/*") },
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (imageUri != null) {
                        val ref = storage.child("profile_images/$uid.jpg")
                        ref.putFile(imageUri!!)
                            .addOnSuccessListener {
                                ref.downloadUrl.addOnSuccessListener { uri ->
                                    val userMap = mapOf(
                                        "name" to name,
                                        "photoUrl" to uri.toString()
                                    )
                                    db.collection("users").document(uid).update(userMap)
                                        .addOnSuccessListener {
                                            Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                                            imageUrl = uri.toString()
                                            imageUri = null
                                        }
                                }
                            }
                    } else {
                        db.collection("users").document(uid)
                            .update("name", name)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Nombre actualizado", Toast.LENGTH_SHORT).show()
                            }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Guardar cambios")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { imagePickerLauncher.launch("image/*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Seleccionar nueva imagen")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val ref = storage.child("profile_images/$uid.jpg")
                    ref.delete().addOnSuccessListener {
                        db.collection("users").document(uid)
                            .update("photoUrl", "")
                            .addOnSuccessListener {
                                Toast.makeText(context, "Imagen eliminada", Toast.LENGTH_SHORT).show()
                                imageUrl = ""
                                imageUri = null
                            }
                    }.addOnFailureListener {
                        Toast.makeText(context, "No se pudo eliminar", Toast.LENGTH_SHORT).show()
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Eliminar imagen de perfil")
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp)
            ) {
                Text("Regresar")
            }
        }
    }
}