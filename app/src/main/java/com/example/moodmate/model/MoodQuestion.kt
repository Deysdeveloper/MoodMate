package com.example.moodmate.model


data class MoodQuestion(
    val question: String,
    val options: List<MoodOption>
)
