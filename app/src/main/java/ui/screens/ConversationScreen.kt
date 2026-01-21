package com.english.tutor.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.english.tutor.ui.viewmodel.ConversationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConversationScreen(viewModel: ConversationViewModel) {

    val messages by viewModel.messages.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val translatedTexts by viewModel.translatedTexts.collectAsState()

    var userInput by remember { mutableStateOf(TextFieldValue("")) }

    val context = LocalContext.current
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) viewModel.startVoiceRecognition()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("English Voice Tutor") },
                actions = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(end = 8.dp),
                                strokeWidth = 2.dp
                            )
                        }
                        Text(
                            text = when {
                                isListening -> "🎤 Escuchando"
                                isSpeaking -> "🔊 Hablando"
                                isLoading -> "🤔 Pensando..."
                                else -> ""
                            },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 16.dp)
                        )
                    }
                }
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {

            /* -------------------- MENSAJES -------------------- */

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {

                items(messages, key = { it.hashCode() }) { msg ->

                    val isUser = msg.role == "user"
                    val isAssistant = msg.role == "assistant"
                    val messageId = msg.hashCode().toString()
                    val translated = translatedTexts[messageId]

                    var showTranslation by remember { mutableStateOf(false) }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement =
                            if (isUser) Arrangement.End else Arrangement.Start
                    ) {

                        Column(
                            horizontalAlignment =
                                if (isUser) Alignment.End else Alignment.Start
                        ) {

                            Surface(
                                shape = MaterialTheme.shapes.medium,
                                color = when {
                                    isUser -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.secondaryContainer
                                }
                            ) {
                                Text(
                                    text =
                                        if (showTranslation && translated != null)
                                            translated
                                        else
                                            msg.content,
                                    color =
                                        if (isUser)
                                            MaterialTheme.colorScheme.onPrimary
                                        else
                                            MaterialTheme.colorScheme.onSecondaryContainer,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }

                            if (isAssistant && !isLoading) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {

                                    IconButton(
                                        onClick = { viewModel.repeatLastMessage() },
                                        modifier = Modifier.size(32.dp),
                                        enabled = !isLoading
                                    ) {
                                        Icon(
                                            Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = "Repeat",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            viewModel.toggleTranslation(
                                                messageId,
                                                msg.content,
                                                showTranslation
                                            )
                                            showTranslation = !showTranslation
                                        },
                                        modifier = Modifier.size(32.dp),
                                        enabled = !isLoading
                                    ) {
                                        Icon(
                                            Icons.Default.Translate,
                                            contentDescription = "Translate",
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            /* -------------------- INPUT -------------------- */

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                IconButton(
                    onClick = {
                        when {
                            isListening -> viewModel.stopVoiceRecognition()
                            isSpeaking -> viewModel.stopSpeaking()
                            else -> {
                                if (ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.RECORD_AUDIO
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    viewModel.startVoiceRecognition()
                                } else {
                                    micPermissionLauncher.launch(
                                        Manifest.permission.RECORD_AUDIO
                                    )
                                }
                            }
                        }
                    },
                    enabled = !isLoading
                ) {
                    Icon(
                        imageVector =
                            if (isListening) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = "Mic",
                        tint =
                            if (isListening)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.primary
                    )
                }

                TextField(
                    value = userInput,
                    onValueChange = { userInput = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Type or speak...") },
                    enabled = !isListening && !isLoading,
                    singleLine = true
                )

                IconButton(
                    onClick = {
                        val text = userInput.text.trim()
                        if (text.isNotEmpty()) {
                            viewModel.sendMessage(text)
                            userInput = TextFieldValue("")
                        }
                    },
                    enabled = userInput.text.isNotBlank() && !isListening && !isLoading
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send"
                    )
                }
            }
        }
    }
}