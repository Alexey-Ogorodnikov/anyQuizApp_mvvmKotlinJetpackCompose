package com.alexey.quizappmvvm.utils

import android.content.Context
import android.net.Uri
import com.alexey.quizappmvvm.data.model.Question
import java.io.BufferedReader
import java.io.InputStreamReader

fun readQuestionsFromCsv(context: Context, filename: String): List<Question> {
    val questions = mutableListOf<Question>()
    try {
        // Open the CSV file from the assets folder
        val inputStream = context.assets.open(filename)
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            // Skip the header row
            reader.readLine()
            reader.forEachLine { line ->
                // Split the line by comma. Adjust if your CSV uses a different separator.
                val tokens = line.split(",")
                if (tokens.size >= 7) {
                    val question = Question(
                        id = tokens[0].toIntOrNull() ?: 0,
                        questionText = tokens[1].replace("#^",","),
                        option1 = tokens[2].replace("#^",","),
                        option2 = tokens[3].replace("#^",","),
                        option3 = tokens[4].replace("#^",","),
                        correctOption = tokens[5].toIntOrNull() ?: 1,
                        explanation = tokens[6].replace("#^",",")
                    )
                    questions.add(question)
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return questions
}

fun readQuestionsFromCsv(context: Context, uri: Uri): List<Question> {
    val questions = mutableListOf<Question>()
    try {
        // Open input stream from the provided Uri
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                reader.readLine() // Skip header row
                reader.forEachLine { line ->
                    val tokens = line.split(",")
                    if (tokens.size >= 7) {
                        val question = Question(
                            id = tokens[0].toIntOrNull() ?: 0,
                            questionText = tokens[1].replace("#^", ","),
                            option1 = tokens[2].replace("#^", ","),
                            option2 = tokens[3].replace("#^", ","),
                            option3 = tokens[4].replace("#^", ","),
                            correctOption = tokens[5].toIntOrNull() ?: 1,
                            explanation = tokens[6].replace("#^", ",")
                        )
                        questions.add(question)
                    }
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return questions
}