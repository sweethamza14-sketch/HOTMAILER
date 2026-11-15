package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "emails")
data class EmailEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val senderName: String,
    val senderEmail: String,
    val recipientName: String,
    val recipientEmail: String,
    val subject: String,
    val snippet: String,
    val body: String,
    val timestamp: Long,
    val isRead: Boolean = false,
    val isStarred: Boolean = false,
    val isArchived: Boolean = false,
    val isDeleted: Boolean = false, // In Trash
    val isSpam: Boolean = false,
    val isDraft: Boolean = false,
    val isSent: Boolean = false,
    val folder: String = "INBOX", // INBOX, SENT, DRAFTS, TRASH, SPAM, ARCHIVE
    val category: String = "PRIMARY", // PRIMARY, PROMOTIONS, SOCIAL, UPDATES
    val labels: String = "", // e.g., "Work,Important"
    val hasAttachment: Boolean = false,
    val attachmentNames: String = "", // e.g., "Project_Report.pdf,Design_Spec.png"
    val avatarColorHex: String = "#D9381E",
) {
    val hasHotmailerSignature: Boolean
        get() = body.contains("@Hotmailer", ignoreCase = true)

    val labelList: List<String>
        get() = if (labels.isBlank()) emptyList() else labels.split(",").map { it.trim() }

    val attachmentList: List<String>
        get() = if (attachmentNames.isBlank()) emptyList() else attachmentNames.split(",").map { it.trim() }
}
