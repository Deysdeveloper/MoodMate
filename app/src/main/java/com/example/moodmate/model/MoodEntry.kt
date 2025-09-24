package com.example.moodmate.model

data class MoodEntry(
    val moodId: String = "",
    val userId: String = "",
    val moodLevel: String = "", // "Excellent", "Good", "Okay", "Low", "Very Low"
    val moodScore: Float = 0f, // Average score from 1-5
    val responses: Map<String, Int> = emptyMap(), // Question -> Answer mapping
    val timestamp: Long = 0L
)