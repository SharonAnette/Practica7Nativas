package com.example.practica7nativas

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

data class ActivityEntry(
    val user: String = "",
    val action: String = "",
    val timestamp: Long = 0
)

@Composable
fun ActivityFeedScreen(onBack: () -> Unit) {
    val database = FirebaseDatabase.getInstance().getReference("activities")
    val activityList = remember { mutableStateListOf<ActivityEntry>() }

    // Listener para cambios en tiempo real
    LaunchedEffect(Unit) {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                activityList.clear()
                snapshot.children.mapNotNullTo(activityList) { it.getValue(ActivityEntry::class.java) }
            }

            override fun onCancelled(error: DatabaseError) {
                // Manejar errores
            }
        })
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Tablero en Tiempo Real", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(16.dp))

        LazyColumn {
            items(activityList.sortedByDescending { it.timestamp }) { item ->
                Card(modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text("${item.user} ${item.action}")
                        Text(
                            text = formatDate(item.timestamp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
            Text("Regresar")
        }
    }
}

fun formatDate(time: Long): String {
    val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return sdf.format(Date(time))
}
