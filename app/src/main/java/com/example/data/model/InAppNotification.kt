package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class NotificationType(val label: String, val categoryColorHex: Long) {
    SESSION_MILESTONE("Session Milestone", 0xFF00E5FF),
    LAB_AVAILABILITY("Lab Available", 0xFF00E676),
    REPORT_FEEDBACK("Grading Feedback", 0xFFFFB300),
    SECURITY_ALERT("Security Alert", 0xFFFF5252)
}

@Entity(tableName = "in_app_notifications")
data class InAppNotification(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String,
    val type: String, // from NotificationType.name
    val targetScreen: String? = null, // e.g. "SQLI_LAB", "INSTRUCTOR_REPORT", "PRESENTATION", "QUIZ_ASSESSMENT"
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false,
    val priority: String = "HIGH", // "NORMAL", "HIGH", "URGENT"
    val actionLabel: String? = null // e.g. "Launch Lab", "View Feedback", "Open Milestone"
)
