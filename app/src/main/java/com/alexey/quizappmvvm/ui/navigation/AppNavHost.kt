package com.alexey.quizappmvvm.ui.navigation


import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.alexey.quizappmvvm.data.repository.QuestionRepository
import com.alexey.quizappmvvm.ui.screens.MenuScreen
import com.alexey.quizappmvvm.ui.screens.QuizScreen
import com.alexey.quizappmvvm.ui.screens.ResultScreen
import com.alexey.quizappmvvm.ui.viewmodel.QuizViewModel


@Composable
fun AppNavHost(
    repository: QuestionRepository
) {
    val navController = rememberNavController()
    // Create a shared instance of QuizViewModel.
    val quizViewModel = remember { QuizViewModel(repository) }

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