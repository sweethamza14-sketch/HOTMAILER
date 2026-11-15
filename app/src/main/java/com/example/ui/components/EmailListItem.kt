package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.MarkEmailRead
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmailEntity
import com.example.ui.theme.HotmailerRed
import com.example.ui.theme.StarGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class, ExperimentalLayoutApi::class)
@Composable
fun EmailListItem(
    email: EmailEntity,
    isSelected: Boolean,
    onEmailClick: () -> Unit,
    onEmailLongClick: () -> Unit,
    onToggleStar: () -> Unit,
    onToggleRead: () -> Unit,
    onArchive: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            when (dismissValue) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    onArchive()
                    true
                }
                SwipeToDismissBoxValue.EndToStart -> {
                    onDelete()
                    true
                }
                SwipeToDismissBoxValue.Settled -> false
            }
        }
    )

    SwipeToDismissBox(
        state = dismissState,
        modifier = modifier.fillMaxWidth(),
        backgroundContent = {
            val direction = dismissState.dismissDirection
            val color by animateColorAsState(
                targetValue = when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.StartToEnd -> Color(0xFF2E7D32) // Archive green
                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFC62828) // Delete red
                    SwipeToDismissBoxValue.Settled -> Color.Transparent
                },
                label = "swipe_bg"
            )

            val alignment = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Alignment.CenterStart
                else -> Alignment.CenterEnd
            }

            val icon = when (direction) {
                SwipeToDismissBoxValue.StartToEnd -> Icons.Default.Archive
                else -> Icons.Default.Delete
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 24.dp),
                contentAlignment = alignment
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color.White
                )
            }
        }
    ) {
        val backgroundColor by animateColorAsState(
            targetValue = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
            } else if (!email.isRead) {
                MaterialTheme.colorScheme.surface
            } else {
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            },
            label = "card_bg"
        )

        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .combinedClickable(
                    onClick = onEmailClick,
                    onLongClick = onEmailLongClick
                )
                .testTag("email_item_${email.id}"),
            color = backgroundColor
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Sender Avatar / Selection Icon
                val avatarColor = try {
                    Color(android.graphics.Color.parseColor(email.avatarColorHex))
                } catch (e: Exception) {
                    HotmailerRed
                }

                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) HotmailerRed else avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Selected",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    } else {
                        val initial = (email.senderName.firstOrNull() ?: 'U').uppercaseChar()
                        Text(
                            text = initial.toString(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Email content column
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    // Sender Name + Unread Dot Indicator + Time Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!email.isRead) {
                            Box(
                                modifier = Modifier
                                    .size(9.dp)
                                    .clip(CircleShape)
                                    .background(HotmailerRed)
                                    .testTag("unread_dot_${email.id}")
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        Text(
                            text = email.senderName,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = if (!email.isRead) FontWeight.ExtraBold else FontWeight.Normal,
                                fontSize = 15.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f),
                            color = if (!email.isRead) {
                                MaterialTheme.colorScheme.onSurface
                            } else {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.78f)
                            }
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = formatTimestamp(email.timestamp),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = if (!email.isRead) FontWeight.Bold else FontWeight.Normal,
                                color = if (!email.isRead) HotmailerRed else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    // Subject line
                    Text(
                        text = email.subject,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (!email.isRead) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = if (!email.isRead) {
                            MaterialTheme.colorScheme.onSurface
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.82f)
                        }
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    // Snippet
                    Text(
                        text = email.snippet,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = if (!email.isRead) FontWeight.Medium else FontWeight.Normal,
                            color = if (!email.isRead) {
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
                            },
                            fontSize = 13.sp
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )

                    // Badges row: Labels, Attachment, and @Hotmailer badge
                    if (email.hasHotmailerSignature || email.hasAttachment || email.labelList.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            // @Hotmailer badge
                            if (email.hasHotmailerSignature) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = HotmailerRed.copy(alpha = 0.12f)
                                ) {
                                    Text(
                                        text = "@Hotmailer",
                                        color = HotmailerRed,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            // Attachment chip
                            if (email.hasAttachment) {
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AttachFile,
                                            contentDescription = "Attachment",
                                            modifier = Modifier.size(12.dp),
                                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.width(2.dp))
                                        val firstAttachment = email.attachmentList.firstOrNull() ?: "File"
                                        Text(
                                            text = firstAttachment,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1
                                        )
                                    }
                                }
                            }

                            // Label chips
                            email.labelList.forEach { label ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.width(4.dp))

                // Read/Unread Icon Indicator + Star icon column
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    IconButton(
                        onClick = onToggleRead,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("read_status_icon_${email.id}")
                    ) {
                        Icon(
                            imageVector = if (!email.isRead) {
                                Icons.Filled.MarkEmailUnread
                            } else {
                                Icons.Outlined.MarkEmailRead
                            },
                            contentDescription = if (!email.isRead) {
                                "Unread email, tap to mark as read"
                            } else {
                                "Read email, tap to mark as unread"
                            },
                            tint = if (!email.isRead) {
                                HotmailerRed
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                            },
                            modifier = Modifier.size(19.dp)
                        )
                    }

                    IconButton(
                        onClick = onToggleStar,
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("star_button_${email.id}")
                    ) {
                        Icon(
                            imageVector = if (email.isStarred) Icons.Filled.Star else Icons.Outlined.StarBorder,
                            contentDescription = if (email.isStarred) "Starred" else "Not starred",
                            tint = if (email.isStarred) StarGold else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    val oneMinute = 60 * 1000L
    val oneHour = 60 * oneMinute
    val oneDay = 24 * oneHour

    return when {
        diff < oneMinute -> "Just now"
        diff < oneHour -> "${diff / oneMinute}m"
        diff < oneDay -> SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
        diff < 2 * oneDay -> "Yesterday"
        diff < 7 * oneDay -> SimpleDateFormat("EEE", Locale.getDefault()).format(Date(timestamp))
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
    }
}
