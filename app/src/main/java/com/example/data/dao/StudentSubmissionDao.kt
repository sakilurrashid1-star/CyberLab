package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.StudentSubmission
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentSubmissionDao {
    @Query("SELECT * FROM student_submissions ORDER BY completedAt DESC")
    fun getAllSubmissions(): Flow<List<StudentSubmission>>

    @Query("SELECT * FROM student_submissions WHERE id = :id")
    fun getSubmissionById(id: Long): Flow<StudentSubmission?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubmission(submission: StudentSubmission): Long

    @Update
    suspend fun updateSubmission(submission: StudentSubmission)

    @Delete
    suspend fun deleteSubmission(submission: StudentSubmission)

    @Query("DELETE FROM student_submissions")
    suspend fun clearAllSubmissions()
}
