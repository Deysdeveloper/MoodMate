package com.example.moodmate.screens

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun AddJournalScreen(modifier: Modifier, navController: NavHostController) {
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }

    val moods = listOf("😊", "😐", "😢", "😠", "😴", "😵", "🥳", "🤩", "🤯", "🥰")

    val currentDate = remember {
        val formatter = SimpleDateFormat("EEEE, MMMM d • yyyy", Locale.getDefault())
        formatter.format(Date())
    }

    // Fetch name from Firestore
    LaunchedEffect(Unit) {
        Firebase.firestore.collection("users")
            .document(FirebaseAuth.getInstance().currentUser!!.uid)
            .get().addOnCompleteListener {
                name = it.result.get("name").toString().split(" ")[0]
            }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("New Journal Entry", style = MaterialTheme.typography.bodyMedium)
        Text("Today is $currentDate")

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Text("Select Mood:")
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            moods.forEach {
                Text(
                    text = it,
                    fontSize = MaterialTheme.typography.bodyMedium.fontSize,
                    modifier = Modifier
                        .clickable { mood = it }
                        .padding(4.dp)
                )
            }
        }

        OutlinedTextField(
            value = note,
            onValueChange = { note = it },
            label = { Text("What are you thinking today?") },
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            maxLines = 6
        )

        Button(
            onClick = {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null && title.isNotBlank() && note.isNotBlank() && mood.isNotBlank()) {
                    val database = FirebaseDatabase.getInstance().reference
                    val journalId = database.child("journals").push().key!!

                    val entry = mapOf(
                        "userId" to user.uid,
                        "username" to name,
                        "title" to title,
                        "mood" to mood,
                        "note" to note,
                        "timestamp" to System.currentTimeMillis()
                    )

                    database.child("journals").child(journalId).setValue(entry)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Journal saved", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed to save", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Toast.makeText(context, "Fill all fields", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Save Entry")
        }
    }
}
