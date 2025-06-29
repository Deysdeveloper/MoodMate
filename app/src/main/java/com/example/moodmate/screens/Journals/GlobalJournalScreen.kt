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

@Composable
fun GlobalJournalsScreen(modifier: Modifier, navController: NavHostController) {
    val journals = remember { mutableStateListOf<JournalEntry>() }

    LaunchedEffect(Unit) {
        FirebaseRepository.getJournal { allJournals ->
            journals.clear()
            journals.addAll(allJournals)
        }
    }

    JournalScaffold(title = "Global Journals", journals = journals, navController)
}
