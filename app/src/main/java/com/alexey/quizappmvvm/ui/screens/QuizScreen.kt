package com.alexey.quizappmvvm.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.alexey.quizappmvvm.R
import com.alexey.quizappmvvm.navigation.NavRoutes
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
        // Loading
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
        navController.navigate(NavRoutes.RESULT) {
            popUpTo(NavRoutes.MENU) { inclusive = true }
        }
        return
    }

    val currentQuestion = questions[currentIndexState.value]

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.question_counter, currentIndexState.value + 1, questions.size
                        )
                    )
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
                    text = if (isCorrect) stringResource(R.string.correct) else stringResource(R.string.incorrect),
                    color = if (isCorrect) Color.Green else Color.Red,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
                Text(
                    text = stringResource(R.string.explanation, currentQuestion.explanation),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Button(
                    onClick = {
                        if (currentIndexState.value == questions.size - 1) {
                            isLastQuestionAnswered = true
                            navController.navigate(NavRoutes.RESULT) {
                                popUpTo(NavRoutes.MENU) { inclusive = true }
                            }
                        } else {
                            viewModel.nextQuestion()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.next))
                }
            }
        }
    }
}
