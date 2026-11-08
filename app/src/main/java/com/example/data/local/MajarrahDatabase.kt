package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.model.CartItem
import com.example.data.model.ChatMessage
import com.example.data.model.Conversation
import com.example.data.model.BlockedContactEntity
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
        CartItem::class,
        BlockedContactEntity::class
    ],
    version = 10,
    exportSchema = true
)
abstract class MajarrahDatabase : RoomDatabase() {
    abstract fun majarrahDao(): MajarrahDao

    companion object {
        val MIGRATION_7_8 = object : androidx.room.migration.Migration(7, 8) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE user_profile ADD COLUMN isProfileLocked INTEGER NOT NULL DEFAULT 0")
            }
        }

        val MIGRATION_8_9 = object : androidx.room.migration.Migration(8, 9) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE user_profile ADD COLUMN uid TEXT NOT NULL DEFAULT ''")
                db.execSQL("CREATE TABLE IF NOT EXISTS local_blocked_targets (targetId TEXT NOT NULL PRIMARY KEY, blockedAt INTEGER NOT NULL)")
            }
        }

        val MIGRATION_9_10 = object : androidx.room.migration.Migration(9, 10) {
            override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE chat_messages ADD COLUMN remoteId TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE chat_messages ADD COLUMN senderUid TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE chat_messages ADD COLUMN readBy TEXT NOT NULL DEFAULT ''")
                db.execSQL("UPDATE chat_messages SET remoteId = 'legacy_' || id WHERE remoteId = ''")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_chat_messages_conversationId_remoteId ON chat_messages(conversationId, remoteId)")

                db.execSQL("ALTER TABLE posts ADD COLUMN remoteId TEXT NOT NULL DEFAULT ''")
                db.execSQL("ALTER TABLE posts ADD COLUMN authorUid TEXT NOT NULL DEFAULT ''")
                db.execSQL("UPDATE posts SET remoteId = 'legacy_' || id WHERE remoteId = ''")
                db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_posts_remoteId ON posts(remoteId)")
            }
        }

        @Volatile
        private var INSTANCE: MajarrahDatabase? = null

        fun getDatabase(context: Context): MajarrahDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    MajarrahDatabase::class.java,
                    "majarrah_database"
                )
                .addMigrations(MIGRATION_7_8, MIGRATION_8_9, MIGRATION_9_10)
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
