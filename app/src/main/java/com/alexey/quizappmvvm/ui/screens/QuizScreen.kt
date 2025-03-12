package com.alexey.quizappmvvm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alexey.quizappmvvm.ui.viewmodel.QuizViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: QuizViewModel,
    navController: NavController,
    onQuizEnd: () -> Unit
) {
    val questionsState = viewModel.questions.collectAsState()
    val currentIndexState = viewModel.currentQuestionIndex.collectAsState()
    val userAnswerState = viewModel.userAnswer.collectAsState()

    val questions = questionsState.value
    var isLastQuestionAnswered by remember { mutableStateOf(false) }

    if (questions.isEmpty()) {
        // Show loading indicator while questions are being loaded
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CircularProgressIndicator()
        }
        return
    }

    if (currentIndexState.value >= questions.size && !isLastQuestionAnswered) {
        isLastQuestionAnswered = true
        navController.navigate("result") {
            popUpTo("menu") { inclusive = true }  // Avoids stacking multiple ResultScreens
        }
        return
    }

    val currentQuestion = questions[currentIndexState.value]

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "Question ${currentIndexState.value + 1} of ${questions.size}")
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            Text(
                text = currentQuestion.questionText,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Display answer options
            listOf(
                Pair(1, currentQuestion.option1),
                Pair(2, currentQuestion.option2),
                Pair(3, currentQuestion.option3)
            ).forEach { (optionNumber, optionText) ->
                Button(
                    onClick = { viewModel.selectAnswer(optionNumber) },
                    enabled = userAnswerState.value == null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Text(text = optionText)
                }
            }

            if (userAnswerState.value != null) {
                val isCorrect = userAnswerState.value == currentQuestion.correctOption
                Text(
                    text = if (isCorrect) "Correct!" else "Incorrect!",
                    color = if (isCorrect) Color.Green else Color.Red,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                Text(
                    text = "Explanation: ${currentQuestion.explanation}",
                    style = MaterialTheme.typography.bodyMedium ,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(
                    onClick = {
                        if (currentIndexState.value == questions.size - 1) {
                            isLastQuestionAnswered = true
                            navController.navigate("result") {
                                popUpTo("menu") { inclusive = true }
                            }
                        } else {
                            viewModel.nextQuestion()
                        }
                              },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = "Next")
                }
            }
        }
    }
}