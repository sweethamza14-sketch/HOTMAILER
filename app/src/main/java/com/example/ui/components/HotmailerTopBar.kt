package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.MarkEmailUnread
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HotmailerRed

@Composable
fun HotmailerTopBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onToggleSearch: (Boolean) -> Unit,
    onOpenDrawer: () -> Unit,
    onOpenAccountSwitcher: () -> Unit,
    selectedCount: Int,
    onClearSelection: () -> Unit,
    onDeleteSelected: () -> Unit,
    onMarkSelectedRead: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    if (selectedCount > 0) {
        // Multi-selection Action Mode Top Bar
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .height(64.dp)
                .shadow(4.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onClearSelection,
                    modifier = Modifier.testTag("clear_selection_button")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Clear selection")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "$selectedCount",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(
                    onClick = { onMarkSelectedRead(true) },
                    modifier = Modifier.testTag("mark_read_button")
                ) {
                    Icon(Icons.Default.MarkEmailRead, contentDescription = "Mark as read")
                }
                IconButton(
                    onClick = { onMarkSelectedRead(false) },
                    modifier = Modifier.testTag("mark_unread_button")
                ) {
                    Icon(Icons.Outlined.MarkEmailUnread, contentDescription = "Mark as unread")
                }
                IconButton(
                    onClick = onDeleteSelected,
                    modifier = Modifier.testTag("delete_selected_button")
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete selected")
                }
            }
        }
    } else {
        // Floating pill Search Bar (iconic Gmail style)
        Surface(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .shadow(elevation = 3.dp, shape = RoundedCornerShape(28.dp)),
            shape = RoundedCornerShape(28.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f),
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (isSearchActive || searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            onToggleSearch(false)
                            onSearchQueryChange("")
                        },
                        modifier = Modifier.testTag("back_search_button")
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back from search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    IconButton(
                        onClick = onOpenDrawer,
                        modifier = Modifier.testTag("menu_drawer_button")
                    ) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Open navigation drawer",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Search in Hotmailer",
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                                fontSize = 16.sp
                            )
                        )
                    }

                    BasicTextField(
                        value = searchQuery,
                        onValueChange = {
                            onSearchQueryChange(it)
                            if (it.isNotEmpty()) onToggleSearch(true)
                        },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 16.sp
                        ),
                        cursorBrush = SolidColor(HotmailerRed),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = { onToggleSearch(true) }),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("search_text_input")
                    )
                }

                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchQueryChange("") },
                        modifier = Modifier.testTag("clear_search_text_button")
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Profile Avatar Button
                Box(
                    modifier = Modifier
                        .padding(end = 4.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(HotmailerRed)
                        .clickable(onClick = onOpenAccountSwitcher)
                        .testTag("account_avatar_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "H",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}
