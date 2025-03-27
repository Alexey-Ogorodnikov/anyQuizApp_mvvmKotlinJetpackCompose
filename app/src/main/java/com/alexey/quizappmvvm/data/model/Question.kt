package com.alexey.quizappmvvm.data.model

import androidx.compose.runtime.Immutable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Immutable
@Entity(tableName = "questions")
data class Question(
    @PrimaryKey val id: Int,
    val questionText: String,
    val option1: String,
    val option2: String,
    val option3: String,
    val correctOption: Int, // Valid values: 1, 2, or 3
    val explanation: String
)