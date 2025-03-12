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

    suspend fun getMobileQuestions(): List<Question> {
        return withContext(Dispatchers.IO) {
            val allQuestions = questionDao.getAllQuestions().filter { it.id in 1..10 }
            allQuestions.shuffled()
        }
    }

    suspend fun getDevOpsQuestions(): List<Question> {
        return withContext(Dispatchers.IO) {
            val allQuestions = questionDao.getAllQuestions().filter { it.id in 1..20 }
            allQuestions.shuffled()
        }
    }

    suspend fun getYandexQuestions(): List<Question> {
        return withContext(Dispatchers.IO) {
            val allQuestions = questionDao.getAllQuestions().filter { it.id in 1..50 }
            allQuestions.shuffled()
        }
    }

    suspend fun getNasaQuestions(): List<Question> {
        return withContext(Dispatchers.IO) {
            val allQuestions = questionDao.getAllQuestions().filter { it.id in 1..100 }
            allQuestions.shuffled()
        }
    }
}