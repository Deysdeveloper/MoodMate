package com.example.moodmate.model

data class JournalEntry(
    val journalId:String="",
    val userId: String = "",
    val username: String = "",
    val title:String="",
    val mood: String = "",
    val note: String = "",
    val timestamp: Long = 0L
)
