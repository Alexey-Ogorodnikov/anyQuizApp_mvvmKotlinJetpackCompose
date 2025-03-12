package com.alexey.quizappmvvm.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.alexey.quizappmvvm.data.model.Question

@Database(entities = [Question::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun questionDao(): QuestionDao
}