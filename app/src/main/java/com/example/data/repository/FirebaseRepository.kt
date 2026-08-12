package com.example.data.repository

import com.example.data.model.StoryItem
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirebaseRepository {
    private val db = FirebaseFirestore.getInstance()

    // Fetch Feed / Stories from Firebase Cloud Firestore
    suspend fun fetchStoriesFromCloud(): List<StoryItem> {
        return try {
            val snapshot = db.collection("stories").get().await()
            snapshot.toObjects(StoryItem::class.java)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Add New Story / Post to Database
    suspend fun uploadStoryToCloud(story: StoryItem): Boolean {
        return try {
            db.collection("stories").document(story.id).set(story).await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
