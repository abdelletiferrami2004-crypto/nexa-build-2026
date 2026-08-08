package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.CartItem
import com.example.data.model.ChatMessage
import com.example.data.model.Conversation
import com.example.data.model.Post
import com.example.data.model.Product
import com.example.data.model.UserProfile

@Database(
    entities = [
        UserProfile::class,
        Product::class,
        Post::class,
        ChatMessage::class,
        Conversation::class,
        CartItem::class
    ],
    version = 3,
    exportSchema = false
)
abstract class MajarrahDatabase : RoomDatabase() {
    abstract fun majarrahDao(): MajarrahDao

    companion object {
        @Volatile
        private var INSTANCE: MajarrahDatabase? = null

        fun getDatabase(context: Context): MajarrahDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MajarrahDatabase::class.java,
                    "majarrah_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
