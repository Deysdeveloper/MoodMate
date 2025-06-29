package com.example.moodmate.model.repository

import android.util.Log
import com.example.moodmate.model.JournalEntry
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

object FirebaseRepository {

    private val db= FirebaseDatabase.getInstance().getReference("journals")

    fun addJournal(journalEntry: JournalEntry) {
        val journalId = db.push().key ?: return
        val entryWithId = journalEntry.copy(journalId = journalId)
        db.child(journalId).setValue(entryWithId)
    }

    fun getJournal(onDataChange: (List<JournalEntry>) -> Unit) {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val journalList = mutableListOf<JournalEntry>()
                snapshot.children.forEach {
                    val journal = it.getValue(JournalEntry::class.java)
                    val journalId = it.key ?: ""
                    if (journal != null) {
                        journalList.add(journal.copy(journalId = journalId))
                    }
                }
                onDataChange(journalList.sortedByDescending { it.timestamp })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseRepository", "Failed to fetch journals: ${error.message}")
                onDataChange(emptyList())
            }
        })
    }

    fun getJournalForUser(userId: String, onDataChange: (List<JournalEntry>) -> Unit) {
        db.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val journalList = mutableListOf<JournalEntry>()
                snapshot.children.forEach {
                    val journal = it.getValue(JournalEntry::class.java)
                    val journalId = it.key ?: ""
                    if (journal != null && journal.userId == userId) {
                        journalList.add(journal.copy(journalId = journalId))
                    }
                }
                onDataChange(journalList.sortedByDescending { it.timestamp })
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("FirebaseRepository", "Failed to fetch: ${error.message}")
                onDataChange(emptyList())
            }
        })
    }
    fun deleteJournal(journalId: String, onComplete: (Boolean) -> Unit) {
        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return onComplete(false)

        db.child(journalId).get().addOnSuccessListener { snapshot ->
            val journal = snapshot.getValue(JournalEntry::class.java)
            if (journal != null && journal.userId == currentUserId) {
                db.child(journalId).removeValue()
                    .addOnSuccessListener { onComplete(true) }
                    .addOnFailureListener { onComplete(false) }
            } else {
                onComplete(false)
            }
        }.addOnFailureListener {
            onComplete(false)
        }
    }


}