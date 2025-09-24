package com.example.moodmate.model

data class MoodOption(
    val text: String,
    val value: Int, // Score for mood calculation
    val emoji: String
)