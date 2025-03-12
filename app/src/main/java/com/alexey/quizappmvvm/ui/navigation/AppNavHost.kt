package com.alexey.quizappmvvm.ui.navigation


import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.alexey.quizappmvvm.data.db.AppDatabase
import com.alexey.quizappmvvm.data.model.Question
import com.alexey.quizappmvvm.data.repository.QuestionRepository
import com.alexey.quizappmvvm.ui.screens.MenuScreen
import com.alexey.quizappmvvm.ui.screens.QuizScreen
import com.alexey.quizappmvvm.ui.screens.ResultScreen
import com.alexey.quizappmvvm.ui.viewmodel.QuizViewModel
import com.alexey.quizappmvvm.utils.readQuestionsFromCsv
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@Composable
fun AppNavHost(
    repository: QuestionRepository
) {

    val navController = rememberNavController()
    // Create a shared instance of QuizViewModel.
    val context = LocalContext.current
    val quizViewModel = remember {
        QuizViewModel(
            repository = repository
        )
    }

    val database = remember {
        Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "quiz_app_database"
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    val dao = remember { database.questionDao() }  // Now safe to call

    NavHost(navController = navController, startDestination = "menu") {
        composable("menu") {
            MenuScreen(
                onQuizSelected = {
                    quizViewModel.resetQuiz() // Ensure fresh quiz data
                    quizViewModel.loadQuestions("mobile")
                    navController.navigate("quiz")
                },
                onDevOpsQuizSelected = {
                    quizViewModel.resetQuiz() // Ensure fresh quiz data
                    quizViewModel.loadQuestions("devops")
                    navController.navigate("quiz")
                },
                onYandexDevQuizSelected = {
                    quizViewModel.resetQuiz() // Ensure fresh quiz data
                    quizViewModel.loadQuestions("yandex")
                    navController.navigate("quiz")
                },
                onNasaQuizSelected = {
                    quizViewModel.resetQuiz() // Ensure fresh quiz data
                    quizViewModel.loadQuestions("nasa")
                    navController.navigate("quiz")
                },
                onCsvSelected = { uri ->
                    val context = context.applicationContext
                    CoroutineScope(Dispatchers.IO).launch {
                        val questionsFromCsv = readQuestionsFromCsv(context, uri)
                        dao.insertQuestions(questionsFromCsv)

                        withContext(Dispatchers.Main) {  // Switch to main thread for UI updates
                            quizViewModel.resetQuiz()
                            quizViewModel.loadQuestions("mobile")
                            navController.navigate("menu")
                        }
                    }
                }
            )
        }

        composable("quiz") {
            QuizScreen(viewModel = quizViewModel, onQuizEnd = {
                navController.navigate("result")
            }, navController = navController)
        }
        composable("result") {
            // Collect the correctCount state from the ViewModel
            val correctCount by quizViewModel.correctCount.collectAsState()
            val totalCount by quizViewModel.totalCount.collectAsState()
            ResultScreen(
                correctCount = correctCount,
                totalQuestions = totalCount,
                onBackToMenu = {
                    navController.navigate("menu") {
                        popUpTo("menu") { inclusive = true }
                    }
                }
            )
        }
    }
}