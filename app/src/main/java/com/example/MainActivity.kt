package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmailEntity
import com.example.ui.components.AccountSwitcherDialog
import com.example.ui.components.CategoryTabs
import com.example.ui.components.ComposeScreen
import com.example.ui.components.EmailDetailScreen
import com.example.ui.components.EmailListItem
import com.example.ui.components.HotmailerDrawer
import com.example.ui.components.HotmailerTopBar
import com.example.ui.components.SettingsDialog
import com.example.ui.theme.HotmailerOrange
import com.example.ui.theme.HotmailerRed
import com.example.ui.theme.HotmailerTheme
import com.example.ui.viewmodel.FolderType
import com.example.ui.viewmodel.HotmailerViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private val viewModel: HotmailerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Handle external mailto: / SEND intents
        handleIncomingIntent(intent)

        setContent {
            HotmailerTheme {
                HotmailerApp(viewModel = viewModel)
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent == null) return
        if (intent.action == Intent.ACTION_SENDTO || intent.action == Intent.ACTION_SEND) {
            val uri = intent.data
            var recipient = ""
            if (uri != null && uri.scheme == "mailto") {
                recipient = uri.schemeSpecificPart.substringBefore("?")
            }
            val subject = intent.getStringExtra(Intent.EXTRA_SUBJECT) ?: ""
            val body = intent.getStringExtra(Intent.EXTRA_TEXT) ?: ""

            viewModel.startNewCompose()
            if (recipient.isNotBlank()) viewModel.updateComposeTo(recipient)
            if (subject.isNotBlank()) viewModel.updateComposeSubject(subject)
            if (body.isNotBlank()) viewModel.updateComposeBody(body)
        }
    }
}

@Composable
fun HotmailerApp(viewModel: HotmailerViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val currentFolder by viewModel.currentFolder.collectAsState()
    val currentCategory by viewModel.currentCategory.collectAsState()
    val activeFilter by viewModel.activeFilter.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val isSearchActive by viewModel.isSearchActive.collectAsState()
    val selectedEmailIds by viewModel.selectedEmailIds.collectAsState()
    val emails by viewModel.emails.collectAsState()
    val inboxUnreadCount by viewModel.inboxUnreadCount.collectAsState()
    val categoryUnreadCounts by viewModel.categoryUnreadCounts.collectAsState()

    val activeEmail by viewModel.activeEmail.collectAsState()
    val isComposeOpen by viewModel.isComposeOpen.collectAsState()
    val composeTo by viewModel.composeTo.collectAsState()
    val composeSubject by viewModel.composeSubject.collectAsState()
    val composeBody by viewModel.composeBody.collectAsState()
    val composeAttachments by viewModel.composeAttachments.collectAsState()
    val customSignature by viewModel.customSignature.collectAsState()

    val isAccountSwitcherOpen by viewModel.isAccountSwitcherOpen.collectAsState()
    val isSettingsOpen by viewModel.isSettingsOpen.collectAsState()

    // Collect snackbar events
    LaunchedEffect(Unit) {
        viewModel.snackbarEvent.collectLatest { event ->
            val result = snackbarHostState.showSnackbar(
                message = event.message,
                actionLabel = event.actionLabel,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                event.onAction?.invoke()
            }
        }
    }

    // Detail Screen
    if (activeEmail != null) {
        EmailDetailScreen(
            email = activeEmail!!,
            onBack = { viewModel.closeEmailDetail() },
            onToggleStar = { viewModel.toggleStar(activeEmail!!) },
            onArchive = { viewModel.archiveEmail(activeEmail!!) },
            onDelete = { viewModel.trashEmail(activeEmail!!) },
            onMarkUnread = {
                viewModel.markAsRead(activeEmail!!.id, false)
                viewModel.closeEmailDetail()
            },
            onReply = { viewModel.startReply(activeEmail!!) },
            onForward = { viewModel.startForward(activeEmail!!) },
            onQuickReplySend = { replyText ->
                viewModel.sendQuickReply(activeEmail!!, replyText)
            }
        )
        return
    }

    // Compose Screen
    if (isComposeOpen) {
        ComposeScreen(
            to = composeTo,
            onToChange = { viewModel.updateComposeTo(it) },
            subject = composeSubject,
            onSubjectChange = { viewModel.updateComposeSubject(it) },
            body = composeBody,
            onBodyChange = { viewModel.updateComposeBody(it) },
            attachments = composeAttachments,
            onAddAttachment = { viewModel.addAttachment(it) },
            onRemoveAttachment = { viewModel.removeAttachment(it) },
            customSignature = customSignature,
            onSend = { viewModel.sendEmail() },
            onDiscardOrSaveDraft = { viewModel.saveDraftAndClose() },
            senderEmail = viewModel.userEmail
        )
        return
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HotmailerDrawer(
                selectedFolder = currentFolder,
                inboxUnreadCount = inboxUnreadCount,
                onFolderSelected = { folder ->
                    viewModel.selectFolder(folder)
                },
                onOpenSettings = {
                    viewModel.toggleSettings(true)
                },
                onCloseDrawer = {
                    scope.launch { drawerState.close() }
                },
                userEmail = viewModel.userEmail
            )
        }
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                HotmailerTopBar(
                    searchQuery = searchQuery,
                    onSearchQueryChange = { viewModel.setSearchQuery(it) },
                    isSearchActive = isSearchActive,
                    onToggleSearch = { viewModel.toggleSearchActive(it) },
                    onOpenDrawer = { scope.launch { drawerState.open() } },
                    onOpenAccountSwitcher = { viewModel.toggleAccountSwitcher(true) },
                    selectedCount = selectedEmailIds.size,
                    onClearSelection = { viewModel.clearSelection() },
                    onDeleteSelected = { viewModel.deleteSelected() },
                    onMarkSelectedRead = { viewModel.markSelectedRead(it) },
                    modifier = Modifier.statusBarsPadding()
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.startNewCompose() },
                    containerColor = HotmailerRed,
                    contentColor = Color.White,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Compose mail",
                            tint = Color.White
                        )
                    },
                    text = {
                        Text(
                            text = "Compose",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.testTag("compose_fab")
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // If in non-Inbox folder, show folder banner
                if (currentFolder != FolderType.INBOX) {
                    FolderHeader(folder = currentFolder, count = emails.size)
                }

                // Category Tabs & Filter Chips (show categories only in INBOX)
                CategoryTabs(
                    selectedCategory = currentCategory,
                    onCategorySelected = { viewModel.selectCategory(it) },
                    activeFilter = activeFilter,
                    onFilterSelected = { viewModel.selectFilter(it) },
                    categoryUnreadCounts = categoryUnreadCounts,
                    totalUnreadCount = inboxUnreadCount,
                    showCategories = currentFolder == FolderType.INBOX && searchQuery.isEmpty()
                )

                // Email list or empty state
                if (emails.isEmpty()) {
                    EmptyFolderView(
                        folder = currentFolder,
                        searchQuery = searchQuery,
                        onResetSample = { viewModel.resetData() }
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .testTag("email_list"),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(emails, key = { it.id }) { email ->
                            EmailListItem(
                                email = email,
                                isSelected = selectedEmailIds.contains(email.id),
                                onEmailClick = {
                                    if (selectedEmailIds.isNotEmpty()) {
                                        viewModel.toggleEmailSelection(email.id)
                                    } else {
                                        viewModel.openEmailDetail(email)
                                    }
                                },
                                onEmailLongClick = {
                                    viewModel.toggleEmailSelection(email.id)
                                },
                                onToggleStar = { viewModel.toggleStar(email) },
                                onToggleRead = { viewModel.toggleRead(email) },
                                onArchive = { viewModel.archiveEmail(email) },
                                onDelete = { viewModel.trashEmail(email) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Account Switcher Dialog
    if (isAccountSwitcherOpen) {
        AccountSwitcherDialog(
            userEmail = viewModel.userEmail,
            userName = viewModel.userName,
            signature = customSignature,
            onDismiss = { viewModel.toggleAccountSwitcher(false) },
            onOpenSettings = {
                viewModel.toggleAccountSwitcher(false)
                viewModel.toggleSettings(true)
            }
        )
    }

    // Settings Dialog
    if (isSettingsOpen) {
        SettingsDialog(
            currentSignature = customSignature,
            onSaveSignature = { newSig -> viewModel.updateSignature(newSig) },
            onResetData = { viewModel.resetData() },
            onDismiss = { viewModel.toggleSettings(false) }
        )
    }
}

@Composable
private fun FolderHeader(folder: FolderType, count: Int) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = folder.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            if (folder == FolderType.SENT) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = HotmailerRed.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = "Signed with @Hotmailer",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = HotmailerRed
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = "$count messages",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun EmptyFolderView(
    folder: FolderType,
    searchQuery: String,
    onResetSample: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(HotmailerRed.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (searchQuery.isNotEmpty()) Icons.Default.Mail else Icons.Default.Inbox,
                contentDescription = null,
                tint = HotmailerRed,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = if (searchQuery.isNotEmpty()) "No results for \"$searchQuery\"" else "No mail in ${folder.title}",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (searchQuery.isNotEmpty()) "Check your spelling or try searching for another keyword" else "You're all caught up! Compose a new email with @Hotmailer signature.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(20.dp))

        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.clickable { onResetSample() }
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = HotmailerRed
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Load Sample Hotmailer Messages",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = HotmailerRed
                    )
                )
            }
        }
    }
}
