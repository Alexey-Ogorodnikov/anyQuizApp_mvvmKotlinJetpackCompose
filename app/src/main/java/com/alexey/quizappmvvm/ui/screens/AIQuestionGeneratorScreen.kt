package com.alexey.quizappmvvm.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.alexey.quizappmvvm.BuildConfig
import com.alexey.quizappmvvm.R
import com.alexey.quizappmvvm.ui.components.WoodButton
import com.alexey.quizappmvvm.ui.components.WoodFloatingButton
import com.alexey.quizappmvvm.ui.theme.ButtonText
import com.alexey.quizappmvvm.ui.theme.TopBarColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.net.SocketTimeoutException
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
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
    var language by remember { mutableStateOf("English") }

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
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = Color.Transparent,
            topBar = {
                Box(
                    modifier = Modifier.fillMaxWidth()
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

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(16.dp)
                ) {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Enter Topic any topic to generate you personalized 100 questions in any language!",
                            style = MaterialTheme.typography.headlineSmall,
                            color = TopBarColor,
                            modifier = Modifier.padding(bottom = 24.dp),
                            textAlign = TextAlign.Center
                        )

                        TextField(
                            value = topic,
                            onValueChange = { topic = it },
                            placeholder = { Text("hint : Cars or Kotlin or DevOps or ... anything!\nTry your imagination!") },
                            modifier = Modifier.fillMaxWidth(),
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Enter Language",
                            color = TopBarColor,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )

                        TextField(
                            value = language,
                            onValueChange = { language = it },
                            placeholder = { Text("e.g. English, Spanish, French...") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),

                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        val keyboardController = LocalSoftwareKeyboardController.current

                        WoodFloatingButton(
                            text = "Generate questions with AI",
                            onClick = {
                                //hide keyboard
                                keyboardController?.hide()

                                val sanitizedName = if (topic.isNotBlank()) {
                                    topic.trim().replace("\\s+".toRegex(), "_").lowercase()
                                } else {
                                    "quiz"
                                }

                                val fileName = "${sanitizedName}.csv"
                                val downloadsDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS)
                                val file = java.io.File(downloadsDir, fileName)

                                if (file.exists()) {
                                    //file exist already
                                    scope.launch {
                                        snackbarHostState.showSnackbar("File '$fileName' already exists!")
                                    }
                                } else {
                                    //make api call
                                    isLoading = true
                                    scope.launch {
                                        val result = generateQuestionsWithAI(topic, language) { errorMsg ->
                                            scope.launch { snackbarHostState.showSnackbar(errorMsg) }
                                        }
                                        isLoading = false
                                        if (result != null) {
                                            csvData = result
                                            createCsvLauncher.launch(fileName)
                                        }
                                    }
                                }
                            },
                            enabled = topic.isNotBlank() && !isLoading
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        WoodButton(
                            onClick = { onBack() },
                            text = "Back to Menu",
                            modifier = Modifier
                                .padding(top = 50.dp)
                                .width(200.dp)
                                .height(50.dp),
                        )
                    }
                }
            }
        }
    }
}

suspend fun generateQuestionsWithAI(
    topic: String,
    language: String,
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
                    "Generate 51 quiz questions about: $topic in $language.. Format as CSV with columns: id,questionText,option1,option2,option3,correctOption,explanation"
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
        } catch (e: SocketTimeoutException) {
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


@Preview(showSystemUi = true, showBackground = true)
@Composable
fun AIQuestionGeneratorScreenPreviewFilled() {
    MaterialTheme {
        var topic by remember { mutableStateOf("Space Exploration") }
        AIQuestionGeneratorScreen(
            onBack = {}
        )
    }
}
