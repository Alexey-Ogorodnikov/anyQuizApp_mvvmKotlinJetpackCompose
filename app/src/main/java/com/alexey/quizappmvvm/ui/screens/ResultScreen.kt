package com.alexey.quizappmvvm.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alexey.quizappmvvm.utils.TOTAL_QUESTIONS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    correctCount: Int,
    totalQuestions: Int,
    onBackToMenu: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "Quiz Results") })
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
                text = "Quiz Completed!",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Text(
                text = "Correct Answers: $correctCount",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Text(
                text = "Incorrect Answers: ${totalQuestions - correctCount}",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            Button(
                onClick = onBackToMenu,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Back to Menu")
            }
        }
    }
}