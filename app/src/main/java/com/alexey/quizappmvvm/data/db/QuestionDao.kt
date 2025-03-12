package com.alexey.quizappmvvm.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alexey.quizappmvvm.data.model.Question

@Dao
interface QuestionDao {
    @Query("SELECT * FROM questions")
    suspend fun getAllQuestions(): List<Question>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestions(questions: List<Question>)
}