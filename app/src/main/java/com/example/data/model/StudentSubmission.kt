package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "student_submissions")
data class StudentSubmission(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val studentName: String,
    val studentId: String,
    val department: String,
    val sessionDate: String = "4 October 2026",
    val completedAt: Long = System.currentTimeMillis(),
    val sqliScore: Int = 0,
    val xssScore: Int = 0,
    val mitmScore: Int = 0,
    val cryptoScore: Int = 0,
    val phishingScore: Int = 0,
    val totalScore: Int = 0,
    val gradeTier: String = "Merit",
    val flagsCaptured: String = "",
    val instructorRemarks: String = "",
    val sandboxVerificationHash: String = ""
)
