package com.alexey.quizappmvvm.utils

import android.content.Context
import android.net.Uri
import com.alexey.quizappmvvm.data.model.Question
import com.opencsv.CSVReader
import java.io.InputStream
import java.io.InputStreamReader

data class CsvResult(
    val title: String?,
    val questions: List<Question>
)

fun readQuestionsFromCsv(context: Context, filename: String): CsvResult {
    val inputStream = context.assets.open(filename)
    return parseCsv(inputStream)
}

fun readQuestionsFromCsv(context: Context, uri: Uri): CsvResult {
    return context.contentResolver.openInputStream(uri)?.use { inputStream ->
        parseCsv(inputStream)
    } ?: CsvResult(null, emptyList())
}

private fun parseCsv(inputStream: InputStream): CsvResult {
    val questions = mutableListOf<Question>()
    var title: String? = null
    try {
        val reader = CSVReader(InputStreamReader(inputStream))

        // Check if the first line is title
        val firstLine = reader.readNext()
        if (firstLine != null && firstLine[0].startsWith("#")) {
            title = firstLine[0].removePrefix("#").trim()
        } else {
            // fallback if title line was not found
            reader.readNext() // skip header row if title was missing
        }

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
    return CsvResult(title, questions)
}
