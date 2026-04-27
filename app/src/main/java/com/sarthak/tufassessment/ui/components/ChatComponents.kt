package com.sarthak.tufassessment.ui.components

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.outlined.AttachFile
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DoneAll
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.PhotoCamera
import androidx.compose.material.icons.outlined.Mic
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sarthak.tufassessment.data.model.AttachmentKind
import com.sarthak.tufassessment.data.model.ChatAttachment
import com.sarthak.tufassessment.data.model.ChatMessage
import com.sarthak.tufassessment.data.model.ContactPreview
import com.sarthak.tufassessment.data.model.MessageStatus
import java.text.DecimalFormat
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ContactListItem(
    preview: ContactPreview,
    onClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick(preview.contact.id) }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ContactAvatar(
                name = preview.contact.name,
                modifier = Modifier.size(52.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 28.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = preview.contact.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (preview.lastMessageTime > 0L) {
                        Text(
                            text = formatTime(preview.lastMessageTime),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = preview.lastMessagePreview,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
        Icon(
            imageVector = Icons.Outlined.CheckCircle,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.align(Alignment.CenterEnd)
        )
    }
}

@Composable
fun ContactAvatar(
    name: String,
    modifier: Modifier = Modifier
) {
    val initials = remember(name) {
        name.split(" ")
            .mapNotNull { part -> part.firstOrNull()?.uppercaseChar() }
            .take(2)
            .joinToString("")
    }

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
    }
}

@Composable
fun ChatMessageBubble(
    message: ChatMessage,
    isOutgoing: Boolean
) {
    val bubbleColor = if (isOutgoing) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surface
    }
    val contentColor = if (isOutgoing) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isOutgoing) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 18.dp,
                        topEnd = 18.dp,
                        bottomStart = if (isOutgoing) 18.dp else 4.dp,
                        bottomEnd = if (isOutgoing) 4.dp else 18.dp
                    )
                )
                .background(bubbleColor)
                .padding(horizontal = 12.dp, vertical = 10.dp)
        ) {
            if (message.attachment != null) {
                MessageAttachmentPreview(
                    attachment = message.attachment,
                    contentColor = contentColor
                )
                if (message.text.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                }
            }

            if (message.text.isNotBlank()) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = contentColor
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = formatTime(message.timestampMillis),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (isOutgoing) {
                    MessageStatusIcon(status = message.status)
                }
            }
        }
    }
}

@Composable
fun MessageStatusIcon(status: MessageStatus) {
    val icon = when (status) {
        MessageStatus.SENT -> Icons.Outlined.Check
        MessageStatus.DELIVERED -> Icons.Outlined.DoneAll
        MessageStatus.READ -> Icons.Outlined.DoneAll
    }
    val tint = when (status) {
        MessageStatus.READ -> MaterialTheme.colorScheme.primary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    Icon(
        imageVector = icon,
        contentDescription = status.name,
        tint = tint,
        modifier = Modifier.size(14.dp)
    )
}

@Composable
fun MessageAttachmentPreview(
    attachment: ChatAttachment,
    contentColor: androidx.compose.ui.graphics.Color
) {
    if (attachment.kind == AttachmentKind.IMAGE) {
        AttachmentImagePreview(uri = attachment.uri)
    } else {
        FileAttachmentChip(
            attachment = attachment,
            contentColor = contentColor
        )
    }
}

@Composable
private fun AttachmentImagePreview(uri: String) {
    val context = LocalContext.current
    val bitmap by produceState<ImageBitmap?>(initialValue = null, key1 = uri) {
        value = runCatching {
            context.contentResolver.openInputStream(Uri.parse(uri))?.use { stream ->
                BitmapFactory.decodeStream(stream)?.asImageBitmap()
            }
        }.getOrNull()
    }

    if (bitmap != null) {
        androidx.compose.foundation.Image(
            bitmap = bitmap!!,
            contentDescription = "Attached image",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1.35f)
                .clip(RoundedCornerShape(14.dp))
        )
    } else {
        FileAttachmentChip(
            attachment = ChatAttachment(
                uri = uri,
                displayName = "Image",
                mimeType = "image/*",
                kind = AttachmentKind.IMAGE,
                sizeBytes = 0L
            ),
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun FileAttachmentChip(
    attachment: ChatAttachment,
    contentColor: androidx.compose.ui.graphics.Color
) {
    AssistChip(
        onClick = { },
        label = {
            Column {
                Text(
                    text = attachment.displayName,
                    style = MaterialTheme.typography.labelLarge,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (attachment.sizeBytes > 0L) {
                    Text(
                        text = formatSize(attachment.sizeBytes),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.AttachFile,
                contentDescription = null
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    )
}

@Composable
fun ChatComposer(
    text: String,
    onTextChange: (String) -> Unit,
    attachment: ChatAttachment?,
    onPickImage: () -> Unit,
    onPickFile: () -> Unit,
    onClearAttachment: () -> Unit,
    onSend: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp)
            .navigationBarsPadding()
            .imePadding()
    ) {
        if (attachment != null) {
            SelectedAttachmentPreview(
                attachment = attachment,
                onClearAttachment = onClearAttachment
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        OutlinedTextField(
            value = text,
            onValueChange = onTextChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(text = "Message")
            },
            leadingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onPickImage) {
                        Icon(
                            imageVector = Icons.Outlined.Image,
                            contentDescription = "Pick image"
                        )
                    }
                    IconButton(onClick = onPickFile) {
                        Icon(
                            imageVector = Icons.Outlined.AttachFile,
                            contentDescription = "Pick file"
                        )
                    }
                }
            },
            trailingIcon = {
                IconButton(onClick = onSend) {
                    if (text.isNotBlank() || attachment != null) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.Send,
                            contentDescription = "Send"
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Outlined.Mic,
                            contentDescription = "Voice message"
                        )
                    }
                }
            },
            maxLines = 4,
            shape = RoundedCornerShape(26.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                cursorColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
fun SelectedAttachmentPreview(
    attachment: ChatAttachment,
    onClearAttachment: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (attachment.kind == AttachmentKind.IMAGE) {
                Icon(
                    imageVector = Icons.Outlined.PhotoCamera,
                    contentDescription = null
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.AttachFile,
                    contentDescription = null
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 40.dp)
            ) {
                Text(
                    text = attachment.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (attachment.sizeBytes > 0L) {
                    Text(
                        text = formatSize(attachment.sizeBytes),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        IconButton(
            onClick = onClearAttachment,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Icon(
                imageVector = Icons.Outlined.Cancel,
                contentDescription = "Remove attachment"
            )
        }
    }
}

@Composable
fun NewContactDialog(
    name: String,
    onNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onCreateContact: () -> Unit
) {
    androidx.compose.material3.AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "New chat",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column {
                Text(
                    text = "Start a new conversation with a contact.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(text = "Contact name") },
                    singleLine = true
                )
            }
        },
        confirmButton = {
            androidx.compose.material3.TextButton(onClick = onCreateContact) {
                Text(text = "Create")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text(text = "Cancel")
            }
        }
    )
}

private fun formatTime(timestampMillis: Long): String {
    val formatter = DateTimeFormatter.ofPattern("h:mm a")
    return Instant.ofEpochMilli(timestampMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalTime()
        .format(formatter)
}

private fun formatSize(sizeBytes: Long): String {
    if (sizeBytes <= 0L) return "Unknown size"
    val units = arrayOf("B", "KB", "MB", "GB")
    var size = sizeBytes.toDouble()
    var unitIndex = 0
    while (size >= 1024 && unitIndex < units.lastIndex) {
        size /= 1024
        unitIndex++
    }
    return "${DecimalFormat("#.#").format(size)} ${units[unitIndex]}"
}
