package com.example.moodmate.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moodmate.model.JournalEntry
import com.example.moodmate.model.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JournalViewModel : ViewModel() {
    private val _userJournals = MutableStateFlow<List<JournalEntry>>(emptyList())
    val userJournals: StateFlow<List<JournalEntry>> = _userJournals

    private val _latestJournal = MutableStateFlow<JournalEntry?>(null)
    val latestJournal: StateFlow<JournalEntry?> = _latestJournal

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadUserJournals()
    }

    private fun loadUserJournals() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            _isLoading.value = true
            FirebaseRepository.getJournalForUser(userId) { journals ->
                _userJournals.value = journals
                _latestJournal.value =
                    journals.firstOrNull() // First entry is the latest due to sorting
                _isLoading.value = false
            }
        }
    }

    fun refreshJournals() {
        loadUserJournals()
    }
}