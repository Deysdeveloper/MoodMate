package com.example.moodmate.screens.Journals

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.example.moodmate.model.JournalEntry
import com.example.moodmate.model.repository.FirebaseRepository
import com.example.moodmate.screens.JournalScaffold
import com.google.firebase.auth.FirebaseAuth

@Composable
fun MyJournalsScreen(modifier: Modifier, navController: NavHostController) {
    val journals = remember { mutableStateListOf<JournalEntry>() }
    val userId = FirebaseAuth.getInstance().currentUser?.uid

    LaunchedEffect(userId) {
        userId?.let {
            FirebaseRepository.getJournalForUser(it) { myJournals ->
                journals.clear()
                journals.addAll(myJournals)
            }
        }
    }

    JournalScaffold(title = "My Journals", journals = journals, navController)
}
