package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.HotmailerDatabase
import com.example.data.model.EmailEntity
import com.example.data.repository.EmailRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class FolderType(val title: String) {
    INBOX("Inbox"),
    STARRED("Starred"),
    SENT("Sent"),
    DRAFTS("Drafts"),
    ARCHIVE("Archive"),
    TRASH("Trash"),
    SPAM("Spam")
}

enum class CategoryType(val title: String) {
    PRIMARY("Primary"),
    PROMOTIONS("Promotions"),
    SOCIAL("Social"),
    UPDATES("Updates")
}

enum class FilterChipType(val label: String) {
    ALL("All"),
    UNREAD("Unread"),
    STARRED("Starred"),
    HAS_ATTACHMENT("Attachment"),
    HOTMAILER("@Hotmailer")
}

data class SnackbarMessage(
    val message: String,
    val actionLabel: String? = null,
    val onAction: (() -> Unit)? = null
)

class HotmailerViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EmailRepository

    init {
        val db = HotmailerDatabase.getInstance(application)
        repository = EmailRepository(db.emailDao())
        viewModelScope.launch {
            repository.checkAndSeedInitialData()
        }
    }

    // Navigation & View States
    private val _currentFolder = MutableStateFlow(FolderType.INBOX)
    val currentFolder: StateFlow<FolderType> = _currentFolder.asStateFlow()

    private val _currentCategory = MutableStateFlow(CategoryType.PRIMARY)
    val currentCategory: StateFlow<CategoryType> = _currentCategory.asStateFlow()

    private val _activeFilter = MutableStateFlow(FilterChipType.ALL)
    val activeFilter: StateFlow<FilterChipType> = _activeFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _isSearchActive = MutableStateFlow(false)
    val isSearchActive: StateFlow<Boolean> = _isSearchActive.asStateFlow()

    // Multi-select
    private val _selectedEmailIds = MutableStateFlow<Set<Long>>(emptySet())
    val selectedEmailIds: StateFlow<Set<Long>> = _selectedEmailIds.asStateFlow()

    // Email Detail
    private val _activeEmail = MutableStateFlow<EmailEntity?>(null)
    val activeEmail: StateFlow<EmailEntity?> = _activeEmail.asStateFlow()

    // Compose State
    private val _isComposeOpen = MutableStateFlow(false)
    val isComposeOpen: StateFlow<Boolean> = _isComposeOpen.asStateFlow()

    private val _composeTo = MutableStateFlow("")
    val composeTo: StateFlow<String> = _composeTo.asStateFlow()

    private val _composeSubject = MutableStateFlow("")
    val composeSubject: StateFlow<String> = _composeSubject.asStateFlow()

    private val _composeBody = MutableStateFlow("")
    val composeBody: StateFlow<String> = _composeBody.asStateFlow()

    private val _composeAttachments = MutableStateFlow<List<String>>(emptyList())
    val composeAttachments: StateFlow<List<String>> = _composeAttachments.asStateFlow()

    private val _composeDraftId = MutableStateFlow<Long?>(null)

    // User Profile / Settings
    val userEmail = "sweethamza14@Hotmailer"
    val userName = "Hamza"
    
    private val _customSignature = MutableStateFlow("Sent via @Hotmailer")
    val customSignature: StateFlow<String> = _customSignature.asStateFlow()

    private val _isAccountSwitcherOpen = MutableStateFlow(false)
    val isAccountSwitcherOpen: StateFlow<Boolean> = _isAccountSwitcherOpen.asStateFlow()

    private val _isSettingsOpen = MutableStateFlow(false)
    val isSettingsOpen: StateFlow<Boolean> = _isSettingsOpen.asStateFlow()

    // Snackbar events
    private val _snackbarEvent = MutableSharedFlow<SnackbarMessage>()
    val snackbarEvent: SharedFlow<SnackbarMessage> = _snackbarEvent.asSharedFlow()

    // Reactive email stream
    @OptIn(ExperimentalCoroutinesApi::class)
    val emails: StateFlow<List<EmailEntity>> = combine(
        _currentFolder,
        _currentCategory,
        _searchQuery,
        _isSearchActive,
        _activeFilter
    ) { folder, category, query, searchActive, filter ->
        Triple(folder, category, query)
    }.flatMapLatest { (folder, category, query) ->
        if (query.isNotBlank()) {
            repository.searchEmails(query)
        } else {
            when (folder) {
                FolderType.INBOX -> repository.getEmailsByCategory(category.name)
                FolderType.STARRED -> repository.getStarredEmails()
                FolderType.SENT -> repository.getEmailsByFolder("SENT")
                FolderType.DRAFTS -> repository.getEmailsByFolder("DRAFTS")
                FolderType.ARCHIVE -> repository.getArchivedEmails()
                FolderType.TRASH -> repository.getTrashEmails()
                FolderType.SPAM -> repository.getEmailsByFolder("SPAM")
            }
        }
    }.combine(_activeFilter) { list, filter ->
        when (filter) {
            FilterChipType.ALL -> list
            FilterChipType.UNREAD -> list.filter { !it.isRead }
            FilterChipType.STARRED -> list.filter { it.isStarred }
            FilterChipType.HAS_ATTACHMENT -> list.filter { it.hasAttachment }
            FilterChipType.HOTMAILER -> list.filter { it.hasHotmailerSignature }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val inboxUnreadCount: StateFlow<Int> = repository.getUnreadCount("INBOX")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val categoryUnreadCounts: StateFlow<Map<CategoryType, Int>> = combine(
        repository.getCategoryUnreadCount(CategoryType.PRIMARY.name),
        repository.getCategoryUnreadCount(CategoryType.PROMOTIONS.name),
        repository.getCategoryUnreadCount(CategoryType.SOCIAL.name),
        repository.getCategoryUnreadCount(CategoryType.UPDATES.name)
    ) { primary, promo, social, updates ->
        mapOf(
            CategoryType.PRIMARY to primary,
            CategoryType.PROMOTIONS to promo,
            CategoryType.SOCIAL to social,
            CategoryType.UPDATES to updates
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    // Actions
    fun selectFolder(folder: FolderType) {
        _currentFolder.value = folder
        _selectedEmailIds.value = emptySet()
        _activeEmail.value = null
    }

    fun selectCategory(category: CategoryType) {
        _currentCategory.value = category
        _selectedEmailIds.value = emptySet()
    }

    fun selectFilter(filter: FilterChipType) {
        _activeFilter.value = if (_activeFilter.value == filter) FilterChipType.ALL else filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun toggleSearchActive(active: Boolean) {
        _isSearchActive.value = active
        if (!active) {
            _searchQuery.value = ""
        }
    }

    fun openEmailDetail(email: EmailEntity) {
        _activeEmail.value = email
        if (!email.isRead) {
            markAsRead(email.id, true)
        }
    }

    fun closeEmailDetail() {
        _activeEmail.value = null
    }

    fun toggleStar(email: EmailEntity) {
        viewModelScope.launch {
            repository.toggleStar(email.id, email.isStarred)
            if (_activeEmail.value?.id == email.id) {
                _activeEmail.update { it?.copy(isStarred = !email.isStarred) }
            }
        }
    }

    fun markAsRead(id: Long, isRead: Boolean) {
        viewModelScope.launch {
            repository.setRead(id, isRead)
            if (_activeEmail.value?.id == id) {
                _activeEmail.update { it?.copy(isRead = isRead) }
            }
        }
    }

    fun toggleRead(email: EmailEntity) {
        markAsRead(email.id, !email.isRead)
    }

    fun archiveEmail(email: EmailEntity) {
        viewModelScope.launch {
            repository.moveToArchive(email.id)
            if (_activeEmail.value?.id == email.id) {
                _activeEmail.value = null
            }
            _snackbarEvent.emit(
                SnackbarMessage(
                    message = "Conversation archived",
                    actionLabel = "Undo",
                    onAction = {
                        viewModelScope.launch { repository.unarchive(email.id) }
                    }
                )
            )
        }
    }

    fun trashEmail(email: EmailEntity) {
        viewModelScope.launch {
            repository.moveToTrash(email.id)
            if (_activeEmail.value?.id == email.id) {
                _activeEmail.value = null
            }
            _snackbarEvent.emit(
                SnackbarMessage(
                    message = "Moved to Trash",
                    actionLabel = "Undo",
                    onAction = {
                        viewModelScope.launch { repository.restoreFromTrash(email.id) }
                    }
                )
            )
        }
    }

    fun toggleEmailSelection(id: Long) {
        _selectedEmailIds.update { set ->
            if (set.contains(id)) set - id else set + id
        }
    }

    fun clearSelection() {
        _selectedEmailIds.value = emptySet()
    }

    fun deleteSelected() {
        val ids = _selectedEmailIds.value
        viewModelScope.launch {
            ids.forEach { repository.moveToTrash(it) }
            clearSelection()
            _snackbarEvent.emit(
                SnackbarMessage(
                    message = "${ids.size} conversations moved to Trash",
                    actionLabel = "Undo",
                    onAction = {
                        viewModelScope.launch {
                            ids.forEach { repository.restoreFromTrash(it) }
                        }
                    }
                )
            )
        }
    }

    fun markSelectedRead(isRead: Boolean) {
        val ids = _selectedEmailIds.value
        viewModelScope.launch {
            ids.forEach { repository.setRead(it, isRead) }
            clearSelection()
        }
    }

    // Compose Handling
    fun startNewCompose() {
        _composeTo.value = ""
        _composeSubject.value = ""
        _composeBody.value = ""
        _composeAttachments.value = emptyList()
        _composeDraftId.value = null
        _isComposeOpen.value = true
    }

    fun startReply(email: EmailEntity) {
        _composeTo.value = if (email.senderEmail == userEmail) email.recipientEmail else email.senderEmail
        _composeSubject.value = if (email.subject.startsWith("Re:", ignoreCase = true)) email.subject else "Re: ${email.subject}"
        _composeBody.value = "\n\n--- On ${email.senderName} wrote ---\n> ${email.snippet}"
        _composeAttachments.value = emptyList()
        _composeDraftId.value = null
        _isComposeOpen.value = true
    }

    fun startForward(email: EmailEntity) {
        _composeTo.value = ""
        _composeSubject.value = if (email.subject.startsWith("Fwd:", ignoreCase = true)) email.subject else "Fwd: ${email.subject}"
        _composeBody.value = "\n\n---------- Forwarded message ---------\nFrom: ${email.senderName} <${email.senderEmail}>\nSubject: ${email.subject}\n\n${email.body}"
        _composeAttachments.value = email.attachmentList
        _composeDraftId.value = null
        _isComposeOpen.value = true
    }

    fun updateComposeTo(to: String) { _composeTo.value = to }
    fun updateComposeSubject(subj: String) { _composeSubject.value = subj }
    fun updateComposeBody(body: String) { _composeBody.value = body }

    fun addAttachment(fileName: String) {
        _composeAttachments.update { it + fileName }
    }

    fun removeAttachment(fileName: String) {
        _composeAttachments.update { it - fileName }
    }

    fun sendEmail() {
        val to = _composeTo.value.trim()
        val subject = _composeSubject.value.trim()
        val body = _composeBody.value
        val attachments = _composeAttachments.value
        val signature = _customSignature.value

        if (to.isBlank()) {
            viewModelScope.launch {
                _snackbarEvent.emit(SnackbarMessage("Please specify at least one recipient"))
            }
            return
        }

        viewModelScope.launch {
            repository.sendEmail(
                to = to,
                subject = subject,
                body = body,
                senderName = userName,
                senderEmail = userEmail,
                attachments = attachments,
                customSignature = signature
            )
            _isComposeOpen.value = false
            _snackbarEvent.emit(
                SnackbarMessage(
                    message = "Message sent from $userEmail",
                    actionLabel = "View Sent",
                    onAction = {
                        selectFolder(FolderType.SENT)
                    }
                )
            )
        }
    }

    fun sendQuickReply(originalEmail: EmailEntity, replyText: String) {
        val trimmed = replyText.trim()
        if (trimmed.isBlank()) return
        val recipient = if (originalEmail.senderEmail == userEmail || originalEmail.isSent) {
            originalEmail.recipientEmail
        } else {
            originalEmail.senderEmail
        }
        val replySubject = if (originalEmail.subject.startsWith("Re:", ignoreCase = true)) {
            originalEmail.subject
        } else {
            "Re: ${originalEmail.subject}"
        }
        viewModelScope.launch {
            repository.sendEmail(
                to = recipient,
                subject = replySubject,
                body = trimmed,
                senderName = userName,
                senderEmail = userEmail,
                customSignature = _customSignature.value
            )
            _activeEmail.value = null
            _snackbarEvent.emit(
                SnackbarMessage(
                    message = "Reply sent from $userEmail",
                    actionLabel = "View Sent",
                    onAction = {
                        selectFolder(FolderType.SENT)
                    }
                )
            )
        }
    }

    fun saveDraftAndClose() {
        val to = _composeTo.value.trim()
        val subject = _composeSubject.value.trim()
        val body = _composeBody.value
        val attachments = _composeAttachments.value
        val draftId = _composeDraftId.value ?: 0L

        if (to.isNotBlank() || subject.isNotBlank() || body.isNotBlank()) {
            viewModelScope.launch {
                repository.saveDraft(
                    to = to,
                    subject = subject,
                    body = body,
                    draftId = draftId,
                    attachments = attachments
                )
                _snackbarEvent.emit(SnackbarMessage("Draft saved"))
            }
        }
        _isComposeOpen.value = false
    }

    fun discardCompose() {
        _isComposeOpen.value = false
    }

    // Dialog toggles
    fun toggleAccountSwitcher(open: Boolean) { _isAccountSwitcherOpen.value = open }
    fun toggleSettings(open: Boolean) { _isSettingsOpen.value = open }
    fun updateSignature(newSig: String) {
        // Enforce @Hotmailer presence
        val validated = if (newSig.contains("@Hotmailer", ignoreCase = true)) {
            newSig
        } else {
            "$newSig\n@Hotmailer"
        }
        _customSignature.value = validated
    }

    fun resetData() {
        viewModelScope.launch {
            repository.resetToSampleData()
            _activeEmail.value = null
            _selectedEmailIds.value = emptySet()
            _snackbarEvent.emit(SnackbarMessage("Reset to sample mailbox data"))
        }
    }
}
