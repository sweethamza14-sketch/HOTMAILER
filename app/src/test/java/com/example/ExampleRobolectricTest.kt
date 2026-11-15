package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.HotmailerDatabase
import com.example.data.model.EmailEntity
import com.example.data.repository.EmailRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private lateinit var db: HotmailerDatabase
  private lateinit var repository: EmailRepository

  @Before
  fun setup() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    db = Room.inMemoryDatabaseBuilder(context, HotmailerDatabase::class.java)
      .allowMainThreadQueries()
      .build()
    repository = EmailRepository(db.emailDao())
  }

  @After
  fun teardown() {
    db.close()
  }

  @Test
  fun `read string from context matches Hotmailer`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Hotmailer", appName)
  }

  @Test
  fun `email entity identifies hotmailer signature correctly`() {
    val emailWithSig = EmailEntity(
      id = 1L,
      senderName = "Me",
      senderEmail = "sweethamza14@Hotmailer",
      recipientName = "Recipient",
      recipientEmail = "test@example.com",
      subject = "Hello",
      snippet = "Hello world",
      body = "Hello world\n\n---\nSent via @Hotmailer",
      timestamp = System.currentTimeMillis()
    )
    assertTrue(emailWithSig.hasHotmailerSignature)
  }

  @Test
  fun `sendEmail appends @Hotmailer to sender address and email body`() = runTest {
    val sentId = repository.sendEmail(
      to = "colleague@example.com",
      subject = "Project Update",
      body = "Everything is on track.",
      senderName = "Hamza",
      senderEmail = "sweethamza14@gmail.com"
    )

    val saved = repository.getEmailById(sentId).first()!!
    assertTrue(saved.senderEmail.endsWith("@Hotmailer"))
    assertEquals("sweethamza14@Hotmailer", saved.senderEmail)
    assertTrue(saved.body.trimEnd().endsWith("@Hotmailer"))
    assertTrue(saved.isRead)
    assertEquals("SENT", saved.folder)
  }

  @Test
  fun `read and unread status tracking updates unread count and email state`() = runTest {
    val unreadEmail = EmailEntity(
      senderName = "Alice",
      senderEmail = "alice@example.com",
      recipientName = "Me",
      recipientEmail = "sweethamza14@Hotmailer",
      subject = "Unread Message",
      snippet = "Please read this",
      body = "Full email body content",
      timestamp = System.currentTimeMillis(),
      isRead = false,
      folder = "INBOX",
      category = "PRIMARY"
    )
    val id = db.emailDao().insert(unreadEmail)

    assertEquals(1, repository.getUnreadCount("INBOX").first())
    assertFalse(repository.getEmailById(id).first()!!.isRead)

    repository.setRead(id, true)
    assertEquals(0, repository.getUnreadCount("INBOX").first())
    assertTrue(repository.getEmailById(id).first()!!.isRead)

    repository.setRead(id, false)
    assertEquals(1, repository.getUnreadCount("INBOX").first())
    assertFalse(repository.getEmailById(id).first()!!.isRead)
  }
}
