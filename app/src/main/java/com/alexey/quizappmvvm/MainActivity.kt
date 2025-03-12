package com.alexey.quizappmvvm


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.room.Room
import com.alexey.quizappmvvm.data.db.AppDatabase
import com.alexey.quizappmvvm.data.model.Question
import com.alexey.quizappmvvm.data.repository.QuestionRepository
import com.alexey.quizappmvvm.ui.navigation.AppNavHost
import com.alexey.quizappmvvm.ui.theme.QuizAppMVVMTheme
import com.alexey.quizappmvvm.utils.readQuestionsFromCsv
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var repository: QuestionRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize Room database.
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "quiz_app_database"
        )
        .fallbackToDestructiveMigration()
        .build()

        // Prefill the database from CSV if it's empty.
        CoroutineScope(Dispatchers.IO).launch {
            val dao = database.questionDao()
            //if (dao.getAllQuestions().isEmpty()) {
                // Read questions from the CSV file
                var filename = "questions_formated.csv"
                val questionsFromCsv: List<Question> = readQuestionsFromCsv(applicationContext, filename)
                // Insert the questions into the database
                dao.insertQuestions(questionsFromCsv)
            //}
        }


        // Initialize the repository using the DAO from the database.
        repository = QuestionRepository(database.questionDao())

        setContent {
            QuizAppMVVMTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavHost(repository = repository)
                }
            }
        }
    }
}
