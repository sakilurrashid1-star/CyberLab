package com.example.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.ForumMessage
import kotlinx.coroutines.flow.Flow

@Dao
interface ForumDao {
    @Query("SELECT * FROM forum_messages ORDER BY isPinned DESC, timestamp DESC")
    fun getAllMessages(): Flow<List<ForumMessage>>

    @Query("SELECT * FROM forum_messages WHERE category = :category ORDER BY isPinned DESC, timestamp DESC")
    fun getMessagesByCategory(category: String): Flow<List<ForumMessage>>

    @Query("SELECT * FROM forum_messages WHERE topicTag = :topic ORDER BY isPinned DESC, timestamp DESC")
    fun getMessagesByTopic(topic: String): Flow<List<ForumMessage>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ForumMessage): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(messages: List<ForumMessage>)

    @Query("UPDATE forum_messages SET upvotes = upvotes + 1 WHERE id = :id")
    suspend fun upvoteMessage(id: Long)

    @Query("DELETE FROM forum_messages WHERE id = :id")
    suspend fun deleteMessage(id: Long)

    @Query("SELECT COUNT(*) FROM forum_messages")
    suspend fun getMessageCount(): Int
}
