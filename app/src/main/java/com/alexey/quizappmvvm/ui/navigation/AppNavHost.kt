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
import com.alexey.quizappmvvm.data.repository.QuestionRepository
import com.alexey.quizappmvvm.navigation.NavRoutes
import com.alexey.quizappmvvm.ui.screens.MenuScreen
import com.alexey.quizappmvvm.ui.screens.QuizScreen
import com.alexey.quizappmvvm.ui.screens.ResultScreen
import com.alexey.quizappmvvm.ui.viewmodel.QuizViewModel
import com.alexey.quizappmvvm.utils.QuizTypes
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

    NavHost(navController = navController, startDestination = NavRoutes.MENU) {
        composable(NavRoutes.MENU) {
            MenuScreen(
                onQuizSelected = {
                    quizViewModel.resetQuiz() // Ensure fresh quiz data
                    quizViewModel.loadQuestions(QuizTypes.EASY)
                    navController.navigate(NavRoutes.QUIZ)
                },
                onDevOpsQuizSelected = {
                    quizViewModel.resetQuiz() // Ensure fresh quiz data
                    quizViewModel.loadQuestions(QuizTypes.NORMAL)
                    navController.navigate(NavRoutes.QUIZ)
                },
                onYandexDevQuizSelected = {
                    quizViewModel.resetQuiz() // Ensure fresh quiz data
                    quizViewModel.loadQuestions(QuizTypes.HARD)
                    navController.navigate(NavRoutes.QUIZ)
                },
                onNasaQuizSelected = {
                    quizViewModel.resetQuiz() // Ensure fresh quiz data
                    quizViewModel.loadQuestions(QuizTypes.INSANE)
                    navController.navigate(NavRoutes.QUIZ)
                },
                onCsvSelected = { uri ->
                    val appContext = context.applicationContext
                    CoroutineScope(Dispatchers.IO).launch {
                        val questionsFromCsv = readQuestionsFromCsv(appContext, uri)
                        dao.insertQuestions(questionsFromCsv)

                        withContext(Dispatchers.Main) {  // Switch to main thread for UI updates
                            quizViewModel.resetQuiz()
                            quizViewModel.loadQuestions(QuizTypes.EASY)
                            navController.navigate(NavRoutes.MENU)
                        }
                    }
                }
            )
        }

        composable(NavRoutes.QUIZ) {
            QuizScreen(
                viewModel = quizViewModel,
                onQuizEnd = {
                    navController.navigate(NavRoutes.RESULT)
                },
                navController = navController
            )
        }

        composable(NavRoutes.RESULT) {
            // Collect the correctCount state from the ViewModel
            val correctCount by quizViewModel.correctCount.collectAsState()
            val totalCount by quizViewModel.totalCount.collectAsState()
            ResultScreen(
                correctCount = correctCount,
                totalQuestions = totalCount,
                onBackToMenu = {
                    navController.navigate(NavRoutes.MENU) {
                        popUpTo(NavRoutes.MENU) { inclusive = true }
                    }
                }
            )
        }
    }
}
