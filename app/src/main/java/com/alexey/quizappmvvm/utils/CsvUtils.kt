package com.alexey.quizappmvvm.utils

import android.content.Context
import android.net.Uri
import com.alexey.quizappmvvm.data.model.Question
import com.opencsv.CSVReader
import java.io.BufferedReader
import java.io.InputStreamReader


fun readQuestionsFromCsv(context: Context, filename: String): List<Question> {
    val questions = mutableListOf<Question>()
    try {
        val inputStream = context.assets.open(filename)
        val reader = CSVReader(InputStreamReader(inputStream))

        // Пропустить заголовок
        reader.readNext()

        var line: Array<String>?
        while (reader.readNext().also { line = it } != null) {
            if (line!!.size >= 6) {
                val question = Question(
                    id = line!![0].toIntOrNull() ?: 0,
                    questionText = line!![1],
                    option1 = line!![2],
                    option2 = line!![3],
                    option3 = line!![4],
                    correctOption = line!![5].toIntOrNull() ?: 1,
                    explanation = if (line!!.size > 6) line!![6] else ""
                )
                questions.add(question)
            }
        }
        reader.close()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return questions
}

fun readQuestionsFromCsv(context: Context, uri: Uri): List<Question> {
    val questions = mutableListOf<Question>()
    try {
        // Открываем поток данных из URI
        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            val reader = CSVReader(InputStreamReader(inputStream))

            // Пропустить заголовок
            reader.readNext()

            var line: Array<String>?
            while (reader.readNext().also { line = it } != null) {
                if (line!!.size >= 6) {
                    val question = Question(
                        id = line!![0].toIntOrNull() ?: 0,
                        questionText = line!![1],
                        option1 = line!![2],
                        option2 = line!![3],
                        option3 = line!![4],
                        correctOption = line!![5].toIntOrNull() ?: 1,
                        explanation = if (line!!.size > 6) line!![6] else ""
                    )
                    questions.add(question)
                }
            }
            reader.close()
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return questions
}