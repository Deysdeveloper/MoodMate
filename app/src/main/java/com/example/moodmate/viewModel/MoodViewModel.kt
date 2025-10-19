package com.example.moodmate.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.moodmate.model.MoodEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class MoodViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _latestMoodEntry = MutableStateFlow<MoodEntry?>(null)
    val latestMoodEntry: StateFlow<MoodEntry?> = _latestMoodEntry.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        Log.d("MoodViewModel", "MoodViewModel initialized")
        loadLatestMoodEntry()
    }

    fun saveMoodEntry(
        moodLevel: String,
        moodScore: Float,
        responses: Map<String, Int>,
        onComplete: (Boolean, String?) -> Unit
    ) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("MoodViewModel", "User not authenticated")
            onComplete(false, "User not authenticated")
            return
        }

        Log.d("MoodViewModel", "Saving mood entry: $moodLevel, score: $moodScore")
        _isLoading.value = true

        val moodEntry = MoodEntry(
            moodId = UUID.randomUUID().toString(),
            userId = currentUser.uid,
            moodLevel = moodLevel,
            moodScore = moodScore,
            responses = responses,
            timestamp = System.currentTimeMillis()
        )

        Log.d("MoodViewModel", "Mood entry to save: $moodEntry")

        db.collection("mood_entries")
            .document(moodEntry.moodId)
            .set(moodEntry)
            .addOnSuccessListener {
                Log.d("MoodViewModel", "Mood entry saved successfully")
                _isLoading.value = false
                _latestMoodEntry.value = moodEntry
                Log.d("MoodViewModel", "Updated latestMoodEntry to: ${_latestMoodEntry.value}")
                onComplete(true, null)
            }
            .addOnFailureListener { exception ->
                Log.e("MoodViewModel", "Failed to save mood entry", exception)
                _isLoading.value = false
                onComplete(false, exception.message)
            }
    }

    private fun loadLatestMoodEntry() {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            Log.e("MoodViewModel", "No authenticated user found")
            return
        }

        Log.d("MoodViewModel", "Loading latest mood entry for user: ${currentUser.uid}")
        _isLoading.value = true

        db.collection("mood_entries")
            .whereEqualTo("userId", currentUser.uid)
            .get()
            .addOnSuccessListener { documents ->
                Log.d("MoodViewModel", "Query completed, documents count: ${documents.size()}")
                _isLoading.value = false
                if (!documents.isEmpty) {
                    // Sort documents by timestamp in descending order and get the latest
                    val latestDocument = documents.documents
                        .mapNotNull { it.toObject(MoodEntry::class.java) }
                        .maxByOrNull { it.timestamp }

                    Log.d("MoodViewModel", "Loaded mood entry: $latestDocument")
                    _latestMoodEntry.value = latestDocument
                } else {
                    Log.d("MoodViewModel", "No mood entries found")
                    _latestMoodEntry.value = null
                }
            }
            .addOnFailureListener { exception ->
                Log.e("MoodViewModel", "Failed to load mood entries", exception)
                _isLoading.value = false
                _latestMoodEntry.value = null
            }
    }

    fun refreshMoodData() {
        Log.d("MoodViewModel", "Refreshing mood data")
        loadLatestMoodEntry()
    }
}