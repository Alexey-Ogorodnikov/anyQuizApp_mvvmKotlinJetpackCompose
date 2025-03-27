package com.alexey.quizappmvvm.ui.screens

import android.net.Uri
import android.widget.Toast
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
import com.alexey.quizappmvvm.utils.saveCsvWithSAF
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
    onCsvSelected: (Uri) -> Unit,
    onBack: () -> Unit
) {
    var topic by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var csvData by remember { mutableStateOf<String?>(null) }

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

                Toast.makeText(context, "CSV saved successfully!", Toast.LENGTH_LONG).show()

                onCsvSelected(uri)

            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.oakwood),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )

        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Custom TopBar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
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
                            text = stringResource(id = R.string.generate_questions_screen),
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

            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
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

                Text(
                    text = "Enter Topic any topic to generate your personalized 100 questions in any language!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = TopBarColor,
                    modifier = Modifier.padding(bottom = 24.dp),
                    textAlign = TextAlign.Center
                )

                TextField(
                    value = topic,
                    onValueChange = { topic = it },
                    placeholder = {
                        Text("hint : Cars or Kotlin or DevOps or ... anything!\nTry your imagination!")
                    },
                    modifier = Modifier.fillMaxWidth()
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
                        .padding(bottom = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                val keyboardController = LocalSoftwareKeyboardController.current

                WoodFloatingButton(
                    text = "Generate questions with AI",
                    onClick = {
                        keyboardController?.hide()

                        val sanitizedName = if (topic.isNotBlank()) {
                            topic.trim().replace("\\s+".toRegex(), "_").uppercase()
                        } else {
                            "quiz".uppercase()
                        }

                        val fileName = "${sanitizedName}_quiz.csv"
                        val downloadsDir = context.getExternalFilesDir(android.os.Environment.DIRECTORY_DOWNLOADS)
                        val file = java.io.File(downloadsDir, fileName)

                        if (file.exists()) {
                            Toast.makeText(
                                context,
                                "File '$fileName' already exists!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            isLoading = true
                            scope.launch {
                                val result = generateQuestionsWithAI(topic, language) { errorMsg ->
                                    Toast.makeText(context, errorMsg, Toast.LENGTH_LONG).show()
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
                        .height(50.dp)
                )
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
                    """
    Generate 50 quiz questions about: $topic in $language.
        
        Format the output as CSV with the very strict rule :
        first line always the topic name with symbole # $topic,
        
        second line a header row:
        id,questionText,option1,option2,option3,correctOption,explanation

        all next lines questions for example line 3 will be: 
        1,In the middle of difficulty lies opportunity.,Albert Einstein,Seneca,Immanuel Kant,1,This quote belongs to Albert Einstein.
        
        correctOption should be a digit from 1 to 3 indicating the correct answer.

        Use the following format as an example:

        # Top 100 Timeless Quotes
        id,questionText,option1,option2,option3,correctOption,explanation
        1,Be the change that you wish to see in the world.,Confucius,Sun Tzu,Mahatma Gandhi,3,This quote belongs to Mahatma Gandhi.
        2,The only thing we have to fear is fear itself.,Seneca,Franklin D. Roosevelt,Immanuel Kant,2,This quote belongs to Franklin D. Roosevelt.
        3,In the middle of difficulty lies opportunity.,Albert Einstein,Seneca,Immanuel Kant,1,This quote belongs to Albert Einstein.
        4,"Do not go where the path may lead, go instead where there is no path and leave a trail.",Seneca,Epictetus,Ralph Waldo Emerson,3,This quote belongs to Ralph Waldo Emerson.
        5,The journey of a thousand miles begins with a single step.,Socrates,Sun Tzu,Lao Tzu,3,This quote belongs to Lao Tzu.
        """.trimIndent()
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




@Preview(showSystemUi = true, showBackground = true)
@Composable
fun AIQuestionGeneratorScreenPreviewFilled() {
    MaterialTheme {
        var topic by remember { mutableStateOf("Space Exploration") }
        AIQuestionGeneratorScreen(
            onCsvSelected = {},
            onBack = {}
        )
    }
}
