package com.alexey.quizappmvvm.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.unit.sp
import com.alexey.quizappmvvm.R
import com.alexey.quizappmvvm.ui.components.WoodFloatingButton
import com.alexey.quizappmvvm.ui.theme.ButtonText
import com.alexey.quizappmvvm.ui.theme.TopBarColor

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    csvTitle: String? = "",
    onEasySelected: () -> Unit,
    onNormalSelected: () -> Unit,
    onHardSelected: () -> Unit,
    onInsaneSelected: () -> Unit,
    onCsvSelected: (Uri) -> Unit,
    onGenerateAIQuestions: () -> Unit
) {
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                onCsvSelected(uri)
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
                                text = stringResource(id = R.string.menu_title),
                                color = ButtonText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                            containerColor = Color.Transparent
                        )
                    )
                }
        },
            floatingActionButton = {
                val expanded = remember { mutableStateOf(false) }

                Box {
                    FloatingActionButton(
                        onClick = { expanded.value = true },
                        containerColor = Color.Transparent
                    ) {
                        Box(
                            modifier = Modifier.height(55.dp).width(55.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.blankbrownwood),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.matchParentSize()
                            )
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Open Menu",
                                tint = Color.White,
                                modifier = Modifier.align(Alignment.Center).size(30.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Open file from your device") },
                            onClick = {
                                expanded.value = false
                                val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                                    type = "*/*"
                                }
                                filePickerLauncher.launch(intent)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Generate file with AI") },
                            onClick = {
                                expanded.value = false
                                onGenerateAIQuestions() // вот тут вызов
                            }
                        )
                    }
                }
            }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    color = Color.Transparent
                )
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title
            Text(
                text = stringResource(id = R.string.welcome_text),
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 24.dp),
                color = TopBarColor
            )

            // CSV title
            csvTitle?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                    modifier = Modifier.padding(bottom = 24.dp),
                    color = TopBarColor
                )
            }

            // Buttons block
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                WoodFloatingButton (textRes = R.string.easy_label, onClick = onEasySelected)
                WoodFloatingButton (textRes = R.string.normal_label, onClick = onNormalSelected)
                WoodFloatingButton (textRes = R.string.hard_label, onClick = onHardSelected)
                WoodFloatingButton (textRes = R.string.insane_label, onClick = onInsaneSelected)
            }
        }
    }
            }
}

@Preview(showBackground = true)
@Composable
fun MenuScreenPreview() {
    MaterialTheme {
        MenuScreen(
            onEasySelected = {},
            onNormalSelected = {},
            onHardSelected = {},
            onInsaneSelected = {},
            onCsvSelected = {},
            onGenerateAIQuestions = {},
            csvTitle = "Top 100 Timeless Quotes"
        )
    }
}
