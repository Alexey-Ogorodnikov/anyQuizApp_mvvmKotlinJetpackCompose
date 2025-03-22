package com.alexey.quizappmvvm.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alexey.quizappmvvm.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.util.concurrent.TimeUnit

@Composable
fun AIQuestionGeneratorScreen(
    onBack: () -> Unit
) {
    var topic by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var csvData by remember { mutableStateOf<String?>(null) }

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val createCsvLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/csv")
    ) { uri: Uri? ->
        if (uri != null && csvData != null) {
            scope.launch {
                saveCsvWithSAF(context, uri, csvData!!)
                csvData = null

                val result = snackbarHostState.showSnackbar(
                    message = "CSV saved successfully!",
                    actionLabel = "Open",
                    duration = SnackbarDuration.Long
                )

                if (result == SnackbarResult.ActionPerformed) {
                    val openIntent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(uri, "text/csv")
                        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(openIntent)
                } else {
                    val shareResult = snackbarHostState.showSnackbar(
                        message = "Want to share it?",
                        actionLabel = "Share",
                        duration = SnackbarDuration.Short
                    )

                    if (shareResult == SnackbarResult.ActionPerformed) {
                        val shareIntent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/csv"
                            putExtra(Intent.EXTRA_STREAM, uri)
                            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK
                        }
                        context.startActivity(
                            Intent.createChooser(shareIntent, "Share CSV via")
                        )
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "Enter Topic",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                TextField(
                    value = topic,
                    onValueChange = { topic = it },
                    placeholder = { Text("e.g. Space Exploration") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                val keyboardController = LocalSoftwareKeyboardController.current

                Button(
                    onClick = {
                        //hide keyboard
                        keyboardController?.hide()

                        isLoading = true
                        scope.launch {
                            val result = generateQuestionsWithAI(topic) { errorMsg ->
                                scope.launch {
                                    snackbarHostState.showSnackbar(errorMsg)
                                }
                            }
                            isLoading = false
                            if (result != null) {
                                csvData = result
                                createCsvLauncher.launch("quiz_${System.currentTimeMillis()}.csv")
                            }
                        }
                    },
                    enabled = topic.isNotBlank() && !isLoading
                ) {
                    Text("Generate questions with AI")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { onBack() }
                ) {
                    Text("Back to Menu")
                }
            }

            if (isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0x80000000))
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

suspend fun generateQuestionsWithAI(
    topic: String,
    onError: suspend (String) -> Unit
): String? {

    val apiKey = BuildConfig.OPENAI_API_KEY
    val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .build()

    val messagesArray = JSONArray().apply {
        put(
            JSONObject().apply {
                put("role", "user")
                put(
                    "content",
                    "Generate 100 quiz questions about: $topic. Format as CSV with columns: id,questionText,option1,option2,option3,correctOption,explanation"
                )
            }
        )
    }

    val jsonBody = JSONObject().apply {
        put("model", "gpt-4o")
        put("messages", messagesArray)
        put("temperature", 0.7)
    }

    val body = RequestBody.create(
        "application/json".toMediaTypeOrNull(),
        jsonBody.toString()
    )

    return withContext(Dispatchers.IO) {
        try {
            val request = Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .addHeader("Authorization", "Bearer $apiKey")
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    when (response.code) {
                        401 -> onError("Unauthorized. Check your API key.")
                        429 -> onError("Too many requests. Try again later.")
                        500, 502, 503 -> onError("Server error. Try again later.")
                        else -> onError("Unexpected error: ${response.code}")
                    }
                    return@withContext null
                }

                val responseBody = response.body?.string()
                val json = JSONObject(responseBody ?: "")
                val csvContent = json.getJSONArray("choices")
                    .getJSONObject(0)
                    .getJSONObject("message")
                    .getString("content")
                csvContent.trim()
            }
        } catch (e: java.net.SocketTimeoutException) {
            onError("Request timed out. Try again later.")
            null
        } catch (e: Exception) {
            onError("An error occurred: ${e.localizedMessage}")
            null
        }
    }
}

suspend fun saveCsvWithSAF(context: Context, uri: Uri, csvData: String) {
    withContext(Dispatchers.IO) {
        context.contentResolver.openOutputStream(uri)?.use { outputStream ->
            outputStream.write(csvData.toByteArray())
        }
    }
}
