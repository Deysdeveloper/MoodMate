package com.example.moodmate.screens


import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.moodmate.model.JournalEntry
import com.example.moodmate.model.repository.FirebaseRepository
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun JournalCard(journal: JournalEntry, onUpdateClick: () -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "${journal.username} ${journal.mood}",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))

            if (journal.title.isNotBlank()) {
                Text(
                    text = journal.title,
                    style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(2.dp))
            }

            Text(
                text = journal.note,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())
                    .format(Date(journal.timestamp)),
                style = MaterialTheme.typography.labelSmall
            )

            Spacer(modifier = Modifier.height(8.dp))
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = "Delete Journal Entry",
                modifier = Modifier.clickable {
                    FirebaseRepository.deleteJournal(journal.journalId){ success->
                        if(success) {
                            Toast.makeText(context, "Journal deleted", Toast.LENGTH_SHORT).show()
                        } else {
                        Toast.makeText(context, "Not authorized to delete", Toast.LENGTH_SHORT).show()
                    }

                }},
                tint = Color.Red
            )
        }
    }
}
