package com.example.moodmate.model

import com.example.moodmate.screens.MoodOption

data class MoodQuestion(
    val question: String,
    val options: List<MoodOption>
)
