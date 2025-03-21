package com.alexey.quizappmvvm.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexey.quizappmvvm.R
import com.alexey.quizappmvvm.ui.components.WoodFloatingButton

import com.alexey.quizappmvvm.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    correctCount: Int,
    totalQuestions: Int,
    onBackToMenu: () -> Unit
) {
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
                                text = stringResource(id = R.string.quiz_results_title),
                                color = ButtonText,
                                fontWeight = FontWeight.Bold
                            )
                        },
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth(),

                        )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(id = R.string.quiz_completed),
                    style = MaterialTheme.typography.headlineMedium,
                    color = TopBarColor,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                Text(
                    text = stringResource(id = R.string.correct_answers, correctCount),
                    style = MaterialTheme.typography.titleMedium,
                    color = TopBarColor,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = stringResource(id = R.string.incorrect_answers, totalQuestions - correctCount),
                    style = MaterialTheme.typography.titleMedium,
                    color = TopBarColor,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                WoodFloatingButton(
                    textRes = R.string.back_to_menu,
                    onClick = onBackToMenu
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResultScreenPreview() {
    MaterialTheme {
        ResultScreen(
            correctCount = 7,
            totalQuestions = 10,
            onBackToMenu = {}
        )
    }
}