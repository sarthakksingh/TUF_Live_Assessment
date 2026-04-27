package com.sarthak.tufassessment.viewmodel

import android.app.Application
import android.content.Intent
import android.net.Uri
import android.provider.OpenableColumns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sarthak.tufassessment.data.model.AttachmentKind
import com.sarthak.tufassessment.data.model.CallDirection
import com.sarthak.tufassessment.data.model.CallEntry
import com.sarthak.tufassessment.data.model.CallType
import com.sarthak.tufassessment.data.model.ChatAttachment
import com.sarthak.tufassessment.data.model.ChatContact
import com.sarthak.tufassessment.data.model.ChatMessage
import com.sarthak.tufassessment.data.model.ChatStore
import com.sarthak.tufassessment.data.model.ChatUiState
import com.sarthak.tufassessment.data.model.ContactPreview
import com.sarthak.tufassessment.data.model.MessageStatus
import com.sarthak.tufassessment.data.model.StatusStory
import com.sarthak.tufassessment.data.repo.ChatRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.collections.orEmpty
import kotlin.collections.plus

class ChatViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ChatRepository(application.applicationContext)
    private val appContext = application.applicationContext

    private val _store = MutableStateFlow(repository.loadStore() ?: ChatStore.Companion.seed())
    private val _selectedContactId = MutableStateFlow<String?>(null)
    private val _uiState = MutableStateFlow(buildUiState(_store.value, _selectedContactId.value))

    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    init {
        repository.saveStore(_store.value)
        viewModelScope.launch {
            combine(_store, _selectedContactId) { store, selectedId ->
                buildUiState(store, selectedId)
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun openContact(contactId: String) {
        _selectedContactId.value = contactId
        markConversationAsRead(contactId)
    }

    fun archiveContact(contactId: String) {
        updateStore { current ->
            if (contactId in current.archivedContactIds) {
                current
            } else {
                current.copy(archivedContactIds = current.archivedContactIds + contactId)
            }
        }
        if (_selectedContactId.value == contactId) {
            _selectedContactId.value = null
        }
    }

    fun createContact(name: String): String? {
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return null

        val contactId = trimmed.lowercase().replace(" ", "-") + "-" + UUID.randomUUID().toString().take(6)
        val newContact = ChatContact(
            id = contactId,
            name = trimmed,
            about = "just now"
        )

        updateStore { current ->
            current.copy(
                contacts = current.contacts + newContact,
                conversations = current.conversations + (contactId to emptyList())
            )
        }
        openContact(contactId)
        return contactId
    }

    fun sendMessage(contactId: String, text: String, attachment: ChatAttachment?) {
        val trimmed = text.trim()
        if (trimmed.isEmpty() && attachment == null) return

        val outgoingMessage = ChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = ChatStore.Companion.MyUserId,
            text = trimmed,
            attachment = attachment,
            timestampMillis = System.currentTimeMillis(),
            status = MessageStatus.SENT
        )

        appendMessage(contactId, outgoingMessage)
        openContact(contactId)

        viewModelScope.launch {
            delay(350)
            updateMessageStatus(contactId, outgoingMessage.id, MessageStatus.DELIVERED)
        }

        viewModelScope.launch {
            delay(1200)
            val reply = buildAutoReply(contactId, trimmed, attachment)
            appendIncomingMessage(contactId, reply)
        }
    }

    fun resolveAttachment(uri: Uri): ChatAttachment? {
        return runCatching {
            appContext.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )

            val cursor = appContext.contentResolver.query(
                uri,
                arrayOf(OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE),
                null,
                null,
                null
            )

            var displayName = "Attachment"
            var sizeBytes = 0L

            cursor?.use {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                val sizeIndex = it.getColumnIndex(OpenableColumns.SIZE)
                if (it.moveToFirst()) {
                    if (nameIndex >= 0) {
                        displayName = it.getString(nameIndex) ?: displayName
                    }
                    if (sizeIndex >= 0) {
                        sizeBytes = it.getLong(sizeIndex)
                    }
                }
            }

            val mimeType = appContext.contentResolver.getType(uri).orEmpty()
            val kind = if (mimeType.startsWith("image/")) {
                AttachmentKind.IMAGE
            } else {
                AttachmentKind.FILE
            }

            ChatAttachment(
                uri = uri.toString(),
                displayName = displayName,
                mimeType = mimeType,
                kind = kind,
                sizeBytes = sizeBytes
            )
        }.getOrNull()
    }

    private fun buildUiState(store: ChatStore, selectedContactId: String?): ChatUiState {
        val contactPreviews = store.contacts.map { contact ->
            val messages = store.conversations[contact.id].orEmpty()
            val lastMessage = messages.lastOrNull()
            ContactPreview(
                contact = contact,
                lastMessagePreview = lastMessage?.let { messagePreview(it) } ?: "Start a new chat",
                lastMessageTime = lastMessage?.timestampMillis ?: 0L,
                lastMessageStatus = lastMessage?.status
            )
        }.sortedByDescending { it.lastMessageTime }

        val selectedContact = store.contacts.firstOrNull { it.id == selectedContactId }
        val messages = if (selectedContactId != null) {
            store.conversations[selectedContactId].orEmpty().sortedBy { it.timestampMillis }
        } else {
            emptyList()
        }

        return ChatUiState(
            contactPreviews = contactPreviews,
            selectedContact = selectedContact,
            messages = messages,
            statusStories = buildStatusStories(store),
            recentCalls = buildRecentCalls(contactPreviews),
            archivedContactIds = store.archivedContactIds,
            isLoading = false
        )
    }

    private fun buildStatusStories(store: ChatStore): List<StatusStory> {
        val recentContacts = store.contacts.take(5)
        return buildList {
            add(
                StatusStory(
                    id = "my-status",
                    name = "My status",
                    subtitle = "Tap to add update",
                    isMyStatus = true
                )
            )
            recentContacts.forEach { contact ->
                add(
                    StatusStory(
                        id = contact.id,
                        name = contact.name,
                        subtitle = contact.about,
                        isMyStatus = false
                    )
                )
            }
        }
    }

    private fun buildRecentCalls(previews: List<ContactPreview>): List<CallEntry> {
        val now = System.currentTimeMillis()
        return previews.take(6).mapIndexed { index, preview ->
            CallEntry(
                id = "call-${preview.contact.id}",
                contact = preview.contact,
                callType = if (index % 2 == 0) CallType.VOICE else CallType.VIDEO,
                direction = when (index % 3) {
                    0 -> CallDirection.INCOMING
                    1 -> CallDirection.OUTGOING
                    else -> CallDirection.MISSED
                },
                timestampMillis = (preview.lastMessageTime.takeIf { it > 0L }
                    ?: now) - index * 120000L
            )
        }
    }

    private fun messagePreview(message: ChatMessage): String {
        val attachment = message.attachment
        return when {
            attachment == null -> message.text
            message.text.isBlank() && attachment.kind == AttachmentKind.IMAGE -> "Photo"
            message.text.isBlank() -> attachment.displayName
            attachment.kind == AttachmentKind.IMAGE -> "${message.text} - Photo"
            else -> "${message.text} - ${attachment.displayName}"
        }
    }

    private fun buildAutoReply(
        contactId: String,
        text: String,
        attachment: ChatAttachment?
    ): ChatMessage {
        val contact = _store.value.contacts.firstOrNull { it.id == contactId }
        val replyText = when {
            attachment?.kind == AttachmentKind.IMAGE -> "Nice photo. Thanks!"
            attachment != null -> "Got the file, I'll open it shortly."
            text.contains("build", ignoreCase = true) -> "Perfect, I'll check the build."
            text.contains("screen", ignoreCase = true) -> "The screen flow looks good."
            contact?.name?.contains("Maria", ignoreCase = true) == true -> "Thanks, that helps a lot."
            else -> "Seen. I'll reply in a minute."
        }

        return ChatMessage(
            id = UUID.randomUUID().toString(),
            senderId = contactId,
            text = replyText,
            attachment = null,
            timestampMillis = System.currentTimeMillis(),
            status = MessageStatus.READ
        )
    }

    private fun appendIncomingMessage(contactId: String, message: ChatMessage) {
        appendMessage(contactId, message)
        if (_selectedContactId.value == contactId) {
            markConversationAsRead(contactId)
        }
    }

    private fun appendMessage(contactId: String, message: ChatMessage) {
        updateStore { current ->
            val messages = current.conversations[contactId].orEmpty() + message
            current.copy(conversations = current.conversations + (contactId to messages))
        }
    }

    private fun updateMessageStatus(contactId: String, messageId: String, status: MessageStatus) {
        updateStore { current ->
            val messages = current.conversations[contactId].orEmpty().map { message ->
                if (message.id == messageId) {
                    message.copy(status = status)
                } else {
                    message
                }
            }
            current.copy(conversations = current.conversations + (contactId to messages))
        }
    }

    private fun markConversationAsRead(contactId: String) {
        updateStore { current ->
            val messages = current.conversations[contactId].orEmpty().map { message ->
                if (message.senderId == ChatStore.Companion.MyUserId) {
                    message.copy(status = MessageStatus.READ)
                } else {
                    message
                }
            }
            current.copy(conversations = current.conversations + (contactId to messages))
        }
    }

    private fun updateStore(transform: (ChatStore) -> ChatStore) {
        val updated = transform(_store.value)
        _store.value = updated
        repository.saveStore(updated)
    }
}