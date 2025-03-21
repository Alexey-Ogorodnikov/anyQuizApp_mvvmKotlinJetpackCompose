package com.alexey.quizappmvvm.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.alexey.quizappmvvm.R
import com.alexey.quizappmvvm.data.model.Question
import com.alexey.quizappmvvm.navigation.NavRoutes
import com.alexey.quizappmvvm.ui.components.WoodButton
import com.alexey.quizappmvvm.ui.components.WoodFloatingButton
import com.alexey.quizappmvvm.ui.theme.ButtonText
import com.alexey.quizappmvvm.ui.theme.CorrectColor
import com.alexey.quizappmvvm.ui.theme.IncorrectColor
import com.alexey.quizappmvvm.ui.theme.TopBarColor
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

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.oakwood),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Box(
                    modifier = Modifier.fillMaxWidth().height(64.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.blankbrownwood),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.matchParentSize()
                    )
                    CenterAlignedTopAppBar(
                        title = {
                            Text(
                                text = stringResource(id = R.string.quiz_screen_title),
                                color = ButtonText,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.question_counter, currentIndexState.value + 1, questions.size),
                    style = MaterialTheme.typography.titleLarge,
                    color = TopBarColor,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                Text(
                    text = currentQuestion.questionText,
                    style = MaterialTheme.typography.titleMedium,
                    color = TopBarColor,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                listOf(
                    Pair(1, currentQuestion.option1),
                    Pair(2, currentQuestion.option2),
                    Pair(3, currentQuestion.option3)
                ).forEach { (optionNumber, optionText) ->
                    WoodFloatingButton(
                        text = optionText,
                        onClick = { viewModel.selectAnswer(optionNumber) },
                        enabled = userAnswerState.value == null
                    )
                }

                if (userAnswerState.value != null) {
                    val isCorrect = userAnswerState.value == currentQuestion.correctOption
                    Text(
                        text = if (isCorrect) stringResource(R.string.correct) else stringResource(R.string.incorrect),
                        color = if (isCorrect) CorrectColor else IncorrectColor,
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                    Text(
                        text = stringResource(R.string.explanation, currentQuestion.explanation),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TopBarColor,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    WoodButton(
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
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.next)
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun QuizScreenPreview() {
    // mock ViewModel
    val fakeViewModel = object : QuizViewModel(null) {
        init {
            _questions.value = listOf(
                Question(
                    id = 1,
                    questionText = "What is the capital of France?",
                    option1 = "Paris",
                    option2 = "London",
                    option3 = "Berlin",
                    correctOption = 1,
                    explanation = "Paris is the capital of France."
                )
            )
            _userAnswer.value = null
        }
    }

    // mock NavController
    val fakeNavController = rememberNavController()

    MaterialTheme {
        QuizScreen(
            viewModel = fakeViewModel,
            navController = fakeNavController,
            onQuizEnd = {}
        )
    }
}
