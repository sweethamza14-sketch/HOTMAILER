package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Drafts
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Report
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HotmailerOrange
import com.example.ui.theme.HotmailerRed
import com.example.ui.viewmodel.FolderType

@Composable
fun HotmailerDrawer(
    selectedFolder: FolderType,
    inboxUnreadCount: Int,
    onFolderSelected: (FolderType) -> Unit,
    onOpenSettings: () -> Unit,
    onCloseDrawer: () -> Unit,
    userEmail: String,
    modifier: Modifier = Modifier
) {
    ModalDrawerSheet(
        modifier = modifier.width(320.dp),
        drawerContainerColor = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(vertical = 12.dp)
        ) {
            // Header: Hotmailer Brand Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(HotmailerRed),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Hotmailer logo",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Hotmailer",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp,
                                color = HotmailerRed
                            )
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = HotmailerRed.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "@Hotmailer",
                                color = HotmailerRed,
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = userEmail,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Main Folders
            DrawerItem(
                label = "Inbox",
                icon = Icons.Default.Inbox,
                selected = selectedFolder == FolderType.INBOX,
                badge = if (inboxUnreadCount > 0) inboxUnreadCount.toString() else null,
                onClick = {
                    onFolderSelected(FolderType.INBOX)
                    onCloseDrawer()
                },
                testTag = "drawer_inbox"
            )

            DrawerItem(
                label = "Starred",
                icon = Icons.Default.Star,
                selected = selectedFolder == FolderType.STARRED,
                onClick = {
                    onFolderSelected(FolderType.STARRED)
                    onCloseDrawer()
                },
                testTag = "drawer_starred"
            )

            DrawerItem(
                label = "Sent",
                icon = Icons.Default.Send,
                selected = selectedFolder == FolderType.SENT,
                subtitle = "Signed with @Hotmailer",
                onClick = {
                    onFolderSelected(FolderType.SENT)
                    onCloseDrawer()
                },
                testTag = "drawer_sent"
            )

            DrawerItem(
                label = "Drafts",
                icon = Icons.Default.Drafts,
                selected = selectedFolder == FolderType.DRAFTS,
                onClick = {
                    onFolderSelected(FolderType.DRAFTS)
                    onCloseDrawer()
                },
                testTag = "drawer_drafts"
            )

            DrawerItem(
                label = "Archive",
                icon = Icons.Default.Archive,
                selected = selectedFolder == FolderType.ARCHIVE,
                onClick = {
                    onFolderSelected(FolderType.ARCHIVE)
                    onCloseDrawer()
                },
                testTag = "drawer_archive"
            )

            DrawerItem(
                label = "Spam",
                icon = Icons.Default.Report,
                selected = selectedFolder == FolderType.SPAM,
                onClick = {
                    onFolderSelected(FolderType.SPAM)
                    onCloseDrawer()
                },
                testTag = "drawer_spam"
            )

            DrawerItem(
                label = "Trash",
                icon = Icons.Default.Delete,
                selected = selectedFolder == FolderType.TRASH,
                onClick = {
                    onFolderSelected(FolderType.TRASH)
                    onCloseDrawer()
                },
                testTag = "drawer_trash"
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Labels Section
            Text(
                text = "ALL LABELS",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                    letterSpacing = 1.sp
                ),
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )

            listOf("Work", "Personal", "Receipts", "Travel").forEach { label ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .clickable { onCloseDrawer() }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Label,
                        contentDescription = label,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Settings & Tools
            DrawerItem(
                label = "Settings",
                icon = Icons.Default.Settings,
                selected = false,
                onClick = {
                    onCloseDrawer()
                    onOpenSettings()
                },
                testTag = "drawer_settings"
            )

            DrawerItem(
                label = "Help & feedback",
                icon = Icons.Default.Help,
                selected = false,
                onClick = { onCloseDrawer() },
                testTag = "drawer_help"
            )
        }
    }
}

@Composable
private fun DrawerItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    badge: String? = null,
    subtitle: String? = null
) {
    NavigationDrawerItem(
        label = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
                        )
                    )
                    if (subtitle != null) {
                        Text(
                            text = subtitle,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = HotmailerRed,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
                if (badge != null) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (selected) HotmailerRed else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        },
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (selected) HotmailerRed else MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        selected = selected,
        onClick = onClick,
        modifier = Modifier
            .padding(NavigationDrawerItemDefaults.ItemPadding)
            .testTag(testTag),
        colors = NavigationDrawerItemDefaults.colors(
            selectedContainerColor = HotmailerRed.copy(alpha = 0.12f),
            selectedTextColor = HotmailerRed,
            selectedIconColor = HotmailerRed
        )
    )
}
