package com.sarthak.tufassessment.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sarthak.tufassessment.data.model.CallType
import com.sarthak.tufassessment.data.model.ChatContact
import com.sarthak.tufassessment.data.model.ChatUiState
import com.sarthak.tufassessment.data.model.ContactPreview
import com.sarthak.tufassessment.data.model.MessageStatus
import com.sarthak.tufassessment.data.model.StatusStory
import com.sarthak.tufassessment.ui.components.CallActionDialog
import com.sarthak.tufassessment.ui.components.ChatSwipeItem
import com.sarthak.tufassessment.ui.components.NewContactDialog
import com.sarthak.tufassessment.ui.components.StatusStoriesRow
import com.sarthak.tufassessment.ui.theme.TUFAssessmentTheme
import com.sarthak.tufassessment.viewmodel.ChatViewModel

@Composable
fun ContactsScreen(
    onContactSelected: (String) -> Unit,
    viewModel: ChatViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    ContactsScreenContent(
        uiState = uiState,
        onContactSelected = onContactSelected,
        onCreateContact = { name -> viewModel.createContact(name) },
        onArchiveContact = { contactId -> viewModel.archiveContact(contactId) }
    )
}

@Composable
private fun ContactsScreenContent(
    uiState: ChatUiState,
    onContactSelected: (String) -> Unit,
    onCreateContact: (String) -> String?,
    onArchiveContact: (String) -> Unit
) {
    var showNewChatDialog by remember { mutableStateOf(false) }
    var newContactName by remember { mutableStateOf("") }
    var callDialogContact by remember { mutableStateOf<ChatContact?>(null) }
    var callDialogType by remember { mutableStateOf<CallType?>(null) }

    val filteredContacts = uiState.contactPreviews.filter { preview ->
        preview.contact.id !in uiState.archivedContactIds
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showNewChatDialog = true }) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "New chat"
                )
            }
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item(key = "header") {
                Column(modifier = Modifier.fillMaxWidth()) {
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Uchat",
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        androidx.compose.foundation.layout.Row {
                            IconButton(onClick = { }) {
                                Icon(
                                    imageVector = Icons.Outlined.Search,
                                    contentDescription = "Search"
                                )
                            }
                            IconButton(onClick = { }) {
                                Icon(
                                    imageVector = Icons.Outlined.MoreVert,
                                    contentDescription = "Menu"
                                )
                            }
                        }
                    }
                }
            }

            item(key = "status-title") {
                Text(
                    text = "Status",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            item(key = "status-strip") {
                StatusStoriesRow(
                    stories = uiState.statusStories,
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                )
            }

            item(key = "chats-title") {
                Text(
                    text = "Chats",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            items(
                items = filteredContacts,
                key = { it.contact.id }
            ) { preview ->
                ChatSwipeItem(
                    contact = preview,
                    onVideoCall = {
                        callDialogContact = preview.contact
                        callDialogType = CallType.VIDEO
                    },
                    onVoiceCall = {
                        callDialogContact = preview.contact
                        callDialogType = CallType.VOICE
                    },
                    onArchive = { onArchiveContact(preview.contact.id) },
                    onTap = { onContactSelected(preview.contact.id) }
                )
            }
        }
    }

    if (showNewChatDialog) {
        NewContactDialog(
            name = newContactName,
            onNameChange = { newContactName = it },
            onDismiss = {
                showNewChatDialog = false
                newContactName = ""
            },
            onCreateContact = {
                val createdContactId = onCreateContact(newContactName)
                if (createdContactId != null) {
                    onContactSelected(createdContactId)
                }
                showNewChatDialog = false
                newContactName = ""
            }
        )
    }

    if (callDialogContact != null && callDialogType != null) {
        CallActionDialog(
            contactName = callDialogContact!!.name,
            callType = callDialogType!!,
            onDismiss = {
                callDialogContact = null
                callDialogType = null
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ContactsScreenPreview() {
    TUFAssessmentTheme {
        Surface {
            ContactsScreenContent(
                uiState = ChatUiState(
                    contactPreviews = listOf(
                        ContactPreview(
                            contact = ChatContact(
                                id = "maria",
                                name = "Maria Lopez",
                                about = "online"
                            ),
                            lastMessagePreview = "Uploading it now.",
                            lastMessageTime = System.currentTimeMillis(),
                            lastMessageStatus = MessageStatus.READ
                        )
                    ),
                    statusStories = listOf(
                        StatusStory(
                            id = "my-status",
                            name = "My status",
                            subtitle = "Tap to add update",
                            isMyStatus = true
                        ),
                        StatusStory(
                            id = "maria",
                            name = "Maria",
                            subtitle = "online"
                        )
                    )
                ),
                onContactSelected = { },
                onCreateContact = { null },
                onArchiveContact = { }
            )
        }
    }
}
