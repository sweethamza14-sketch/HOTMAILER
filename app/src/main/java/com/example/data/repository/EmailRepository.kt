package com.example.data.repository

import com.example.data.local.EmailDao
import com.example.data.local.SampleData
import com.example.data.model.EmailEntity
import kotlinx.coroutines.flow.Flow

class EmailRepository(private val dao: EmailDao) {

    companion object {
        const val DEFAULT_SIGNATURE = "@Hotmailer"
    }

    fun getEmailsByFolder(folder: String): Flow<List<EmailEntity>> =
        dao.getEmailsByFolder(folder)

    fun getEmailsByCategory(category: String): Flow<List<EmailEntity>> =
        dao.getEmailsByCategory(category)

    fun getStarredEmails(): Flow<List<EmailEntity>> =
        dao.getStarredEmails()

    fun getArchivedEmails(): Flow<List<EmailEntity>> =
        dao.getArchivedEmails()

    fun getTrashEmails(): Flow<List<EmailEntity>> =
        dao.getTrashEmails()

    fun searchEmails(query: String): Flow<List<EmailEntity>> =
        dao.searchEmails(query)

    fun getEmailById(id: Long): Flow<EmailEntity?> =
        dao.getEmailById(id)

    fun getUnreadCount(folder: String = "INBOX"): Flow<Int> =
        dao.getUnreadCount(folder)

    fun getCategoryUnreadCount(category: String): Flow<Int> =
        dao.getCategoryUnreadCount(category)

    suspend fun toggleStar(id: Long, currentStar: Boolean) {
        dao.setStarred(id, !currentStar)
    }

    suspend fun setRead(id: Long, isRead: Boolean) {
        dao.setRead(id, isRead)
    }

    suspend fun moveToArchive(id: Long) {
        dao.setArchived(id, true)
    }

    suspend fun unarchive(id: Long) {
        dao.setArchived(id, false)
    }

    suspend fun moveToTrash(id: Long) {
        dao.setDeleted(id, true)
    }

    suspend fun restoreFromTrash(id: Long) {
        dao.setDeleted(id, false)
    }

    suspend fun deletePermanently(id: Long) {
        dao.deleteById(id)
    }

    fun ensureHotmailerAddress(address: String): String {
        val trimmed = address.trim()
        if (trimmed.isBlank()) return "user@Hotmailer"
        val localPart = trimmed.substringBefore("@")
        return if (trimmed.endsWith("@Hotmailer", ignoreCase = false)) {
            trimmed
        } else {
            "$localPart@Hotmailer"
        }
    }

    suspend fun sendEmail(
        to: String,
        subject: String,
        body: String,
        senderName: String = "Me",
        senderEmail: String = "sweethamza14@Hotmailer",
        attachments: List<String> = emptyList(),
        customSignature: String = DEFAULT_SIGNATURE
    ): Long {
        val normalizedSender = ensureHotmailerAddress(senderEmail)

        val finalSignature = if (customSignature.contains("@Hotmailer", ignoreCase = true)) {
            customSignature
        } else {
            "$customSignature\n@Hotmailer"
        }

        val trimmedBody = body.trimEnd()
        val finalBody = if (trimmedBody.contains("@Hotmailer", ignoreCase = true)) {
            trimmedBody
        } else {
            "$trimmedBody\n\n---\n$finalSignature"
        }

        val snippet = if (trimmedBody.length > 90) {
            trimmedBody.take(90) + "..."
        } else trimmedBody

        val email = EmailEntity(
            senderName = senderName,
            senderEmail = normalizedSender,
            recipientName = to.substringBefore("@").replace(".", " ").capitalizeWords(),
            recipientEmail = to,
            subject = if (subject.isBlank()) "(No Subject)" else subject,
            snippet = snippet,
            body = finalBody,
            timestamp = System.currentTimeMillis(),
            isRead = true,
            isSent = true,
            folder = "SENT",
            category = "PRIMARY",
            labels = "Sent",
            hasAttachment = attachments.isNotEmpty(),
            attachmentNames = attachments.joinToString(","),
            avatarColorHex = "#D9381E"
        )
        return dao.insert(email)
    }

    suspend fun saveDraft(
        to: String,
        subject: String,
        body: String,
        draftId: Long = 0,
        attachments: List<String> = emptyList()
    ): Long {
        val email = EmailEntity(
            id = draftId,
            senderName = "Me",
            senderEmail = "sweethamza14@hotmailer.com",
            recipientName = if (to.isBlank()) "Draft" else to,
            recipientEmail = to,
            subject = if (subject.isBlank()) "(Draft)" else subject,
            snippet = if (body.isBlank()) "Draft message" else body.take(90),
            body = body,
            timestamp = System.currentTimeMillis(),
            isRead = true,
            isDraft = true,
            folder = "DRAFTS",
            category = "PRIMARY",
            labels = "Draft",
            hasAttachment = attachments.isNotEmpty(),
            attachmentNames = attachments.joinToString(","),
            avatarColorHex = "#D9381E"
        )
        return dao.insert(email)
    }

    suspend fun checkAndSeedInitialData() {
        if (dao.getCount() == 0) {
            dao.insertAll(SampleData.initialEmails)
        }
    }

    suspend fun resetToSampleData() {
        dao.clearAll()
        dao.insertAll(SampleData.initialEmails)
    }

    private fun String.capitalizeWords(): String =
        split(" ").joinToString(" ") { word ->
            word.lowercase().replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
}
