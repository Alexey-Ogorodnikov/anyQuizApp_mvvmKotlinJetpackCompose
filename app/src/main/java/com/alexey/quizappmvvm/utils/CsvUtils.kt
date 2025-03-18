package com.alexey.quizappmvvm.utils

import android.content.Context
import android.net.Uri
import com.alexey.quizappmvvm.data.model.Question
import com.opencsv.CSVReader
import java.io.InputStream
import java.io.InputStreamReader

fun readQuestionsFromCsv(context: Context, filename: String): List<Question> {
    val inputStream = context.assets.open(filename)
    return parseCsv(inputStream)
}

fun readQuestionsFromCsv(context: Context, uri: Uri): List<Question> {
    return context.contentResolver.openInputStream(uri)?.use { inputStream ->
        parseCsv(inputStream)
    } ?: emptyList()
}

private fun parseCsv(inputStream: InputStream): List<Question> {
    val questions = mutableListOf<Question>()
    try {
        val reader = CSVReader(InputStreamReader(inputStream))

        // skip header
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
