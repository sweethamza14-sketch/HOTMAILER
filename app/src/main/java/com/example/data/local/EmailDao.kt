package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.EmailEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmailDao {

    @Query("SELECT * FROM emails WHERE folder = :folder AND isDeleted = 0 AND isArchived = 0 ORDER BY timestamp DESC")
    fun getEmailsByFolder(folder: String): Flow<List<EmailEntity>>

    @Query("SELECT * FROM emails WHERE folder = 'INBOX' AND category = :category AND isDeleted = 0 AND isArchived = 0 ORDER BY timestamp DESC")
    fun getEmailsByCategory(category: String): Flow<List<EmailEntity>>

    @Query("SELECT * FROM emails WHERE isStarred = 1 AND isDeleted = 0 ORDER BY timestamp DESC")
    fun getStarredEmails(): Flow<List<EmailEntity>>

    @Query("SELECT * FROM emails WHERE isArchived = 1 AND isDeleted = 0 ORDER BY timestamp DESC")
    fun getArchivedEmails(): Flow<List<EmailEntity>>

    @Query("SELECT * FROM emails WHERE isDeleted = 1 ORDER BY timestamp DESC")
    fun getTrashEmails(): Flow<List<EmailEntity>>

    @Query("SELECT * FROM emails WHERE (subject LIKE '%' || :query || '%' OR snippet LIKE '%' || :query || '%' OR senderName LIKE '%' || :query || '%' OR senderEmail LIKE '%' || :query || '%' OR body LIKE '%' || :query || '%') AND isDeleted = 0 ORDER BY timestamp DESC")
    fun searchEmails(query: String): Flow<List<EmailEntity>>

    @Query("SELECT * FROM emails WHERE id = :id LIMIT 1")
    fun getEmailById(id: Long): Flow<EmailEntity?>

    @Query("SELECT * FROM emails WHERE id = :id LIMIT 1")
    suspend fun getEmailByIdSync(id: Long): EmailEntity?

    @Query("SELECT COUNT(*) FROM emails WHERE folder = :folder AND isRead = 0 AND isDeleted = 0 AND isArchived = 0")
    fun getUnreadCount(folder: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM emails WHERE category = :category AND isRead = 0 AND isDeleted = 0 AND isArchived = 0")
    fun getCategoryUnreadCount(category: String): Flow<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(email: EmailEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(emails: List<EmailEntity>)

    @Update
    suspend fun update(email: EmailEntity)

    @Delete
    suspend fun delete(email: EmailEntity)

    @Query("DELETE FROM emails WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("UPDATE emails SET isStarred = :isStarred WHERE id = :id")
    suspend fun setStarred(id: Long, isStarred: Boolean)

    @Query("UPDATE emails SET isRead = :isRead WHERE id = :id")
    suspend fun setRead(id: Long, isRead: Boolean)

    @Query("UPDATE emails SET isArchived = :isArchived WHERE id = :id")
    suspend fun setArchived(id: Long, isArchived: Boolean)

    @Query("UPDATE emails SET isDeleted = :isDeleted WHERE id = :id")
    suspend fun setDeleted(id: Long, isDeleted: Boolean)

    @Query("UPDATE emails SET folder = :folder WHERE id = :id")
    suspend fun setFolder(id: Long, folder: String)

    @Query("SELECT COUNT(*) FROM emails")
    suspend fun getCount(): Int

    @Query("DELETE FROM emails")
    suspend fun clearAll()
}
