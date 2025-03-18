package com.alexey.quizappmvvm.data.repository

import com.alexey.quizappmvvm.data.db.QuestionDao
import com.alexey.quizappmvvm.data.model.Question
import com.alexey.quizappmvvm.utils.TOTAL_QUESTIONS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuestionRepository(private val questionDao: QuestionDao) {

    //    suspend fun getRandomQuestions(count: Int = TOTAL_QUESTIONS): List<Question> {
    //        return withContext(Dispatchers.IO) {
    //            val allQuestions = questionDao.getAllQuestions()
    //            allQuestions.shuffled().take(count)
    //        }
    //    }

    suspend fun getEasyQuestions(): List<Question> {
        return withContext(Dispatchers.IO) {
            val allQuestions = questionDao.getAllQuestions().filter { it.id in 1..10 }
            allQuestions.shuffled()
        }
    }

    suspend fun getNormalQuestions(): List<Question> {
        return withContext(Dispatchers.IO) {
            val allQuestions = questionDao.getAllQuestions().filter { it.id in 1..20 }
            allQuestions.shuffled()
        }
    }

    suspend fun getHardQuestions(): List<Question> {
        return withContext(Dispatchers.IO) {
            val allQuestions = questionDao.getAllQuestions().filter { it.id in 1..50 }
            allQuestions.shuffled()
        }
    }

    suspend fun getInsaneQuestions(): List<Question> {
        return withContext(Dispatchers.IO) {
            val allQuestions = questionDao.getAllQuestions().filter { it.id in 1..100 }
            allQuestions.shuffled()
        }
    }

    suspend fun replaceQuestions(questions: List<Question>) {
        withContext(Dispatchers.IO) {
            questionDao.insertQuestions(questions)
        }
    }
}