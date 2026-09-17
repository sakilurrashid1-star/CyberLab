package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class ForumCategory(val label: String, val iconName: String) {
    ALL("All Discussions", "Forum"),
    QUESTION("Questions ❓", "Help"),
    ATTACK_METHODOLOGY("Attack Methods ⚔️", "Security"),
    LAB_FINDING("Lab Findings 🔍", "Search"),
    DEFENSE_TIPS("Defense & Remediation 🛡️", "Shield")
}

@Entity(tableName = "forum_messages")
data class ForumMessage(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val authorName: String,
    val authorRole: String = "STUDENT", // "SPEAKER", "STUDENT", "MODERATOR"
    val studentId: String = "",
    val category: String = ForumCategory.QUESTION.name,
    val topicTag: String = "General", // e.g. "Securing Accounts", "SQLi", "XSS", "MITM", "Privacy"
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val upvotes: Int = 0,
    val isPinned: Boolean = false,
    val replyCount: Int = 0
)
