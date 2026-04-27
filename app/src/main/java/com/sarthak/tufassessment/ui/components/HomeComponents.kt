package com.sarthak.tufassessment.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Archive
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Videocam
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.sarthak.tufassessment.data.model.CallDirection
import com.sarthak.tufassessment.data.model.CallEntry
import com.sarthak.tufassessment.data.model.CallType
import com.sarthak.tufassessment.data.model.ContactPreview
import com.sarthak.tufassessment.data.model.StatusStory
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun ChatSwipeItem(
    contact: ContactPreview,
    onVideoCall: () -> Unit,
    onVoiceCall: () -> Unit,
    onArchive: () -> Unit,
    onTap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()
    var itemSize by remember { mutableStateOf(androidx.compose.ui.unit.IntSize.Zero) }
    val offsetX = remember { Animatable(0f) }
    var isVisible by remember { mutableStateOf(true) }

    val leftActionWidthPx = with(density) { 160.dp.toPx() }
    val rightActionWidthPx = with(density) { 100.dp.toPx() }

    if (!isVisible) return

    Box(
        modifier = modifier
            .onSizeChanged { itemSize = it }
            .draggable(
                orientation = Orientation.Horizontal,
                state = rememberDraggableState { delta ->
                    scope.launch {
                        val maxLeft = -(itemSize.width.toFloat() * 0.9f)
                        val maxRight = itemSize.width.toFloat() * 0.9f
                        val newOffset = (offsetX.value + delta * 0.7f).coerceIn(maxLeft, maxRight)
                        offsetX.snapTo(newOffset)
                    }
                },
                onDragStopped = { velocity ->
                    scope.launch {
                        when {
                            offsetX.value < -leftActionWidthPx * 0.5f || velocity < -1000f ->
                                offsetX.animateTo(-leftActionWidthPx, spring())

                            offsetX.value > rightActionWidthPx * 0.5f || velocity > 1000f ->
                                offsetX.animateTo(rightActionWidthPx, spring())

                            else ->
                                offsetX.animateTo(0f, spring())
                        }
                    }
                }
            )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(92.dp)
                .clip(RoundedCornerShape(22.dp))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .padding(horizontal = 18.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {
                        scope.launch {
                            offsetX.animateTo(0f, spring())
                            onVoiceCall()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Call,
                            contentDescription = "Voice call",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    IconButton(onClick = {
                        scope.launch {
                            offsetX.animateTo(0f, spring())
                            onVideoCall()
                        }
                    }) {
                        Icon(
                            imageVector = Icons.Outlined.Videocam,
                            contentDescription = "Video call",
                            tint = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(100.dp)
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(start = 12.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                IconButton(onClick = {
                    scope.launch {
                        isVisible = false
                        onArchive()
                    }
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Archive,
                        contentDescription = "Archive",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { onTap() }
            ) {
                ChatRowContent(chatItem = contact)
            }
        }
    }
}

@Composable
fun ContactRowItem(
    preview: ContactPreview,
    onTap: () -> Unit
) {
    ChatSwipeItem(
        contact = preview,
        onVideoCall = { },
        onVoiceCall = { },
        onArchive = { },
        onTap = onTap
    )
}

@Composable
private fun ChatRowContent(chatItem: ContactPreview) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 92.dp)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ContactAvatar(
            name = chatItem.contact.name,
            modifier = Modifier.size(56.dp)
        )
        Spacer(modifier = Modifier.width(14.dp))
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = chatItem.contact.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
                if (chatItem.lastMessageTime > 0L) {
                    Text(
                        text = formatHomeTime(chatItem.lastMessageTime),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = chatItem.lastMessagePreview,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}

@Composable
fun StatusStoriesRow(
    stories: List<StatusStory>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        stories.forEach { story ->
            StatusStoryItem(story = story)
        }
    }
}

@Composable
private fun StatusStoryItem(story: StatusStory) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(76.dp)
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(
                    width = if (story.isMyStatus) 0.dp else 2.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
                .background(
                    if (story.isMyStatus) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = story.name.take(2).uppercase(),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = story.name,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 1
        )
        Text(
            text = story.subtitle,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 1
        )
    }
}

@Composable
fun CallActionDialog(
    contactName: String,
    callType: CallType,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (callType == CallType.VIDEO) "Video call" else "Voice call",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = "Starting a local ${if (callType == CallType.VIDEO) "video" else "voice"} call with $contactName.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = "Close")
            }
        }
    )
}

@Composable
fun CallHistoryRow(entry: CallEntry) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ContactAvatar(
                name = entry.contact.name,
                modifier = Modifier.size(52.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 8.dp)
            ) {
                Text(
                    text = entry.contact.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = buildCallSubtitle(entry),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = if (entry.callType == CallType.VIDEO) Icons.Outlined.Videocam else Icons.Outlined.Call,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

private fun buildCallSubtitle(entry: CallEntry): String {
    val direction = when (entry.direction) {
        CallDirection.INCOMING -> "Incoming"
        CallDirection.OUTGOING -> "Outgoing"
        CallDirection.MISSED -> "Missed"
    }
    val type = if (entry.callType == CallType.VIDEO) "video" else "voice"
    return "$direction $type call - ${formatHomeTime(entry.timestampMillis)}"
}

private fun formatHomeTime(timestampMillis: Long): String {
    val formatter = java.text.SimpleDateFormat("h:mm a", java.util.Locale.getDefault())
    return formatter.format(java.util.Date(timestampMillis))
}
