package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.dao.ForumDao
import com.example.data.dao.NotificationDao
import com.example.data.dao.StudentSubmissionDao
import com.example.data.model.ForumMessage
import com.example.data.model.InAppNotification
import com.example.data.model.StudentSubmission

@Database(
    entities = [StudentSubmission::class, ForumMessage::class, InAppNotification::class],
    version = 3,
    exportSchema = false
)
abstract class CyberLabDatabase : RoomDatabase() {
    abstract fun studentSubmissionDao(): StudentSubmissionDao
    abstract fun forumDao(): ForumDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: CyberLabDatabase? = null

        fun getDatabase(context: Context): CyberLabDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CyberLabDatabase::class.java,
                    "cyberlab_pu.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
