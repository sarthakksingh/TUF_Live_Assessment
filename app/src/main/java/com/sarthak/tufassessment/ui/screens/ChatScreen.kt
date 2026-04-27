package com.sarthak.tufassessment.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarthak.tufassessment.data.model.CallType
import com.sarthak.tufassessment.data.model.ChatAttachment
import com.sarthak.tufassessment.data.model.ChatContact
import com.sarthak.tufassessment.data.model.ChatMessage
import com.sarthak.tufassessment.data.model.ChatStore
import com.sarthak.tufassessment.data.model.ChatUiState
import com.sarthak.tufassessment.data.model.MessageStatus
import com.sarthak.tufassessment.ui.components.CallActionDialog
import com.sarthak.tufassessment.ui.components.ChatComposer
import com.sarthak.tufassessment.ui.components.ChatMessageBubble
import com.sarthak.tufassessment.ui.components.ContactAvatar
import com.sarthak.tufassessment.ui.theme.TUFAssessmentTheme
import com.sarthak.tufassessment.viewmodel.ChatViewModel

@Composable
fun ChatScreen(
    contactId: String,
    onBack: () -> Unit,
    viewModel: ChatViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ChatScreenContent(
        contactId = contactId,
        uiState = uiState,
        onBack = onBack,
        onSendMessage = { text, attachment ->
            viewModel.sendMessage(contactId = contactId, text = text, attachment = attachment)
        },
        onOpenContact = { id -> viewModel.openContact(id) },
        onResolveAttachment = { uri -> viewModel.resolveAttachment(uri) }
    )
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun ChatScreenContent(
    contactId: String,
    uiState: ChatUiState,
    onBack: () -> Unit,
    onSendMessage: (String, ChatAttachment?) -> Unit,
    onOpenContact: (String) -> Unit,
    onResolveAttachment: (android.net.Uri) -> ChatAttachment?
) {
    val messages = uiState.messages
    val listState = rememberLazyListState()
    var draftText by remember { mutableStateOf("") }
    var draftAttachment by remember { mutableStateOf<ChatAttachment?>(null) }
    var activeCallType by remember { mutableStateOf<CallType?>(null) }

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        draftAttachment = uri?.let(onResolveAttachment)
    }

    val filePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        draftAttachment = uri?.let(onResolveAttachment)
    }

    LaunchedEffect(contactId) {
        onOpenContact(contactId)
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    val selectedContact = uiState.selectedContact
    if (selectedContact == null) {
        Surface(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "Conversation not found",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(24.dp)
            )
        }
        return
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    androidx.compose.foundation.layout.Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ContactAvatar(name = selectedContact.name, modifier = Modifier.size(38.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = selectedContact.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = selectedContact.about,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { activeCallType = CallType.VOICE }) {
                        Icon(
                            imageVector = Icons.Outlined.Call,
                            contentDescription = "Voice call"
                        )
                    }
                    IconButton(onClick = { activeCallType = CallType.VIDEO }) {
                        Icon(
                            imageVector = Icons.Outlined.Videocam,
                            contentDescription = "Video call"
                        )
                    }
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = "More"
                        )
                    }
                }
            )
        },
        bottomBar = {
            ChatComposer(
                text = draftText,
                onTextChange = { draftText = it },
                attachment = draftAttachment,
                onPickImage = { imagePicker.launch(arrayOf("image/*")) },
                onPickFile = { filePicker.launch(arrayOf("*/*")) },
                onClearAttachment = { draftAttachment = null },
                onSend = {
                    if (draftText.isNotBlank() || draftAttachment != null) {
                        onSendMessage(draftText, draftAttachment)
                        draftText = ""
                        draftAttachment = null
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            state = listState,
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(
                items = messages,
                key = { it.id }
            ) { message ->
                ChatMessageBubble(
                    message = message,
                    isOutgoing = message.senderId == ChatStore.MyUserId
                )
            }
        }
    }

    if (activeCallType != null) {
        CallActionDialog(
            contactName = selectedContact.name,
            callType = activeCallType!!,
            onDismiss = { activeCallType = null }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ChatScreenPreview() {
    TUFAssessmentTheme {
        Surface {
            ChatScreenContent(
                contactId = "maria",
                uiState = ChatUiState(
                    selectedContact = ChatContact(
                        id = "maria",
                        name = "Maria Lopez",
                        about = "online"
                    ),
                    messages = listOf(
                        ChatMessage(
                            id = "1",
                            senderId = "maria",
                            text = "Can you share the latest build?",
                            timestampMillis = System.currentTimeMillis(),
                            status = MessageStatus.READ
                        ),
                        ChatMessage(
                            id = "2",
                            senderId = ChatStore.MyUserId,
                            text = "Uploading it now.",
                            timestampMillis = System.currentTimeMillis(),
                            status = MessageStatus.DELIVERED
                        )
                    )
                ),
                onBack = { },
                onSendMessage = { _, _ -> },
                onOpenContact = { },
                onResolveAttachment = { null }
            )
        }
    }
}
