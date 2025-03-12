package com.alexey.quizappmvvm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    onQuizSelected: () -> Unit,
    onDevOpsQuizSelected: () -> Unit,
    onYandexDevQuizSelected: () -> Unit,
    onNasaQuizSelected: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = buildAnnotatedString {
                            val titleText = "Quiz App Menu"
                            val colors = listOf(
                                Color.Red, Color.Blue, Color.Green, Color.Magenta,
                                Color.Yellow, Color.Cyan, Color.Gray, Color.Black,
                                Color.DarkGray, Color.LightGray, Color.Blue, Color.Red
                            )

                            titleText.forEachIndexed { index, char ->
                                withStyle(style = SpanStyle(color = colors[index % colors.size], fontWeight = FontWeight.Bold, fontSize = 24.sp)) {
                                    append(char)
                                }
                            }
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
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
                text = "Welcome to the Quiz App!",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Button(
                onClick = onQuizSelected,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = "EASY : Questions 1-10")
            }

            // Button for DevOps Quiz
            Button(
                onClick = onDevOpsQuizSelected,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = "NORMAL : Questions 1-20")
            }

            // Button for DevOps Quiz
            Button(
                onClick = onYandexDevQuizSelected,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = "HARD : Questions 1-50")
            }

            // Button for DevOps Quiz
            Button(
                onClick = onNasaQuizSelected,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                Text(text = "INSANE! : Questions 1-100")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    // If you have your own theme, replace `MaterialTheme` with it (e.g., MyAppTheme).
    MaterialTheme {
        MenuScreen(
            onQuizSelected = {},
            onDevOpsQuizSelected = {},
            onYandexDevQuizSelected = {},
            onNasaQuizSelected = {}
        )
    }
}