package com.alexey.quizappmvvm


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.room.Room
import com.alexey.quizappmvvm.data.db.AppDatabase
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
        enableEdgeToEdge()
        // State to be passed to Composables
        val csvTitleState = mutableStateOf<String?>(null)

        // Initialize Room database.
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "quiz_app_database"
        )
            .fallbackToDestructiveMigration()
            .build()

        repository = QuestionRepository(database.questionDao())

        // Prefill the database from CSV if it's empty.
        CoroutineScope(Dispatchers.IO).launch {
            val dao = database.questionDao()
            val filename = "questions.csv"
            val result = readQuestionsFromCsv(applicationContext, filename)
            dao.insertQuestions(result.questions)
            csvTitleState.value = result.title ?: "Untitled Quiz"
        }

        // Elegant single call to setContent
        setContent {
            QuizAppMVVMTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    AppNavHost(
                        repository = repository,
                        initialCsvTitle = csvTitleState.value
                    )
                }
            }
        }
    }
}
