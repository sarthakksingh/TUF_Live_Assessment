package com.sarthak.tufassessment.data.model

enum class AttachmentKind {
    IMAGE,
    FILE
}

enum class MessageStatus {
    SENT,
    DELIVERED,
    READ
}

enum class CallType {
    VOICE,
    VIDEO
}

enum class CallDirection {
    INCOMING,
    OUTGOING,
    MISSED
}

data class ChatContact(
    val id: String,
    val name: String,
    val about: String
)

data class ChatAttachment(
    val uri: String,
    val displayName: String,
    val mimeType: String,
    val kind: AttachmentKind,
    val sizeBytes: Long
)

data class ChatMessage(
    val id: String,
    val senderId: String,
    val text: String,
    val attachment: ChatAttachment? = null,
    val timestampMillis: Long,
    val status: MessageStatus
)

data class ChatStore(
    val contacts: List<ChatContact>,
    val conversations: Map<String, List<ChatMessage>>,
    val archivedContactIds: List<String> = emptyList()
) {
    companion object {
        const val MyUserId = "me"

        fun seed(now: Long = System.currentTimeMillis()): ChatStore {
            val contacts = listOf(
                ChatContact(
                    id = "maria",
                    name = "Maria Lopez",
                    about = "online"
                ),
                ChatContact(
                    id = "alex",
                    name = "Alex Carter",
                    about = "last seen recently"
                ),
                ChatContact(
                    id = "sana",
                    name = "Sana Khan",
                    about = "typing..."
                ),
                ChatContact(
                    id = "nora",
                    name = "Nora Patel",
                    about = "available"
                )
            )

            val conversations = mapOf(
                "maria" to listOf(
                    ChatMessage(
                        id = "m-1",
                        senderId = "maria",
                        text = "Can you share the latest build?",
                        timestampMillis = now - 3_600_000,
                        status = MessageStatus.READ
                    ),
                    ChatMessage(
                        id = "m-2",
                        senderId = MyUserId,
                        text = "Uploading it now.",
                        timestampMillis = now - 3_300_000,
                        status = MessageStatus.READ
                    )
                ),
                "alex" to listOf(
                    ChatMessage(
                        id = "a-1",
                        senderId = "alex",
                        text = "Great work on the Compose layout.",
                        timestampMillis = now - 7_200_000,
                        status = MessageStatus.READ
                    )
                ),
                "sana" to listOf(
                    ChatMessage(
                        id = "s-1",
                        senderId = MyUserId,
                        text = "I added the new chat screen.",
                        timestampMillis = now - 1_900_000,
                        status = MessageStatus.READ
                    ),
                    ChatMessage(
                        id = "s-2",
                        senderId = "sana",
                        text = "Looks clean. Send me a screenshot later.",
                        timestampMillis = now - 1_800_000,
                        status = MessageStatus.READ
                    )
                ),
                "nora" to listOf(
                    ChatMessage(
                        id = "n-1",
                        senderId = "nora",
                        text = "I'll review the conversation flow this afternoon.",
                        timestampMillis = now - 86_400_000,
                        status = MessageStatus.READ
                    )
                )
            )

            return ChatStore(
                contacts = contacts,
                conversations = conversations,
                archivedContactIds = emptyList()
            )
        }
    }
}

data class ContactPreview(
    val contact: ChatContact,
    val lastMessagePreview: String,
    val lastMessageTime: Long,
    val lastMessageStatus: MessageStatus?
)

data class StatusStory(
    val id: String,
    val name: String,
    val subtitle: String,
    val isMyStatus: Boolean = false
)

data class CallEntry(
    val id: String,
    val contact: ChatContact,
    val callType: CallType,
    val direction: CallDirection,
    val timestampMillis: Long
)

data class ChatUiState(
    val contactPreviews: List<ContactPreview> = emptyList(),
    val selectedContact: ChatContact? = null,
    val messages: List<ChatMessage> = emptyList(),
    val statusStories: List<StatusStory> = emptyList(),
    val recentCalls: List<CallEntry> = emptyList(),
    val archivedContactIds: List<String> = emptyList(),
    val isLoading: Boolean = true
)
