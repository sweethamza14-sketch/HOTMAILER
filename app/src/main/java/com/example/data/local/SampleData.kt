package com.example.data.local

import com.example.data.model.EmailEntity

object SampleData {
    val initialEmails: List<EmailEntity> by lazy {
        val now = System.currentTimeMillis()
        val oneHour = 3600 * 1000L
        val oneDay = 24 * oneHour

        listOf(
            // Primary - Unread Work thread
            EmailEntity(
                senderName = "Sarah Jenkins",
                senderEmail = "sarah.jenkins@designlab.io",
                recipientName = "Me",
                recipientEmail = "sweethamza14@hotmailer.com",
                subject = "Hotmailer Mobile App UI Feedback & Assets",
                snippet = "Hey! I reviewed the latest design iteration for Hotmailer. The flame-red accent and the Gmail-style clean layout look...",
                body = """Hi Hamza,

I reviewed the latest design iteration for the Hotmailer client. The flame-red brand accent paired with the clean Material 3 layout looks exceptionally sharp!

Here are a few quick notes from the design sync:
1. The floating search pill matches modern Android ergonomic standards.
2. The automatic '@Hotmailer' email signature at the end of outgoing emails adds a great touch of brand identity.
3. The folder drawer navigation feels swift and natural.

Attached are the finalized vector icons and color tokens for your build. Let me know if you need any adjustments before the upcoming release!

Best regards,
Sarah Jenkins
Head of Product Design, DesignLab
sarah.jenkins@designlab.io""",
                timestamp = now - 25 * 60 * 1000L, // 25 mins ago
                isRead = false,
                isStarred = true,
                folder = "INBOX",
                category = "PRIMARY",
                labels = "Work,Important",
                hasAttachment = true,
                attachmentNames = "Design_Specs_v2.pdf,Hotmailer_Palette.png",
                avatarColorHex = "#E91E63"
            ),

            // Primary - Tech newsletter / Security
            EmailEntity(
                senderName = "Google Cloud Security",
                senderEmail = "no-reply@cloud.google.com",
                recipientName = "Me",
                recipientEmail = "sweethamza14@hotmailer.com",
                subject = "Security alert: New sign-in detected on Android Device",
                snippet = "Your Google Cloud account was accessed from a new device running Android 14 in Mountain View, CA...",
                body = """Google Cloud Security Alert

We noticed a new sign-in to your Google Cloud developer console:

• Device: Android Emulator / Pixel 8 Pro
• Location: Mountain View, CA, United States
• Time: Just now

If this was you, you don't need to do anything. If you didn't initiate this action, please visit your Cloud Security Console immediately to secure your credentials.

Thank you,
The Google Cloud Security Team""",
                timestamp = now - 2 * oneHour,
                isRead = false,
                isStarred = false,
                folder = "INBOX",
                category = "PRIMARY",
                labels = "Security",
                hasAttachment = false,
                avatarColorHex = "#1A73E8"
            ),

            // Primary - Friend catching up
            EmailEntity(
                senderName = "Alex Rivera",
                senderEmail = "alex.rivera@techpulse.org",
                recipientName = "Me",
                recipientEmail = "sweethamza14@hotmailer.com",
                subject = "Coffee & catching up this weekend?",
                snippet = "Are you around this Saturday morning? Would love to show you the new project we've been hacking on...",
                body = """Hey Hamza!

Long time no talk! Are you around this Saturday morning around 10:30 AM? 

There's a great new artisan roast cafe downtown near the tech center. I'd love to catch up and show you the open-source libraries we've been building for Android.

Let me know if that time works or if Sunday afternoon is better for you!

Cheers,
Alex""",
                timestamp = now - 5 * oneHour,
                isRead = true,
                isStarred = false,
                folder = "INBOX",
                category = "PRIMARY",
                labels = "Personal",
                hasAttachment = false,
                avatarColorHex = "#4CAF50"
            ),

            // Sent mail - Demonstrating the @Hotmailer signature!
            EmailEntity(
                senderName = "Me",
                senderEmail = "sweethamza14@hotmailer.com",
                recipientName = "David Chen",
                recipientEmail = "david.chen@ventures.io",
                subject = "Re: Hotmailer Q4 Roadmap & Pitch Deck",
                snippet = "Thanks David, I've updated the roadmap slide with our November release schedule and active user metrics...",
                body = """Hi David,

Thanks for your feedback on yesterday's call. I've updated the roadmap slide with our November release schedule and early user retention metrics.

Key highlights:
- Native Kotlin Jetpack Compose architecture
- Instant email search with zero latency
- Custom signature branding with @Hotmailer on all outbound correspondence
- Full folder and label management

Let's reconnect on Thursday to review the term sheet.

Best,
Hamza

---
Sent via @Hotmailer""",
                timestamp = now - 8 * oneHour,
                isRead = true,
                isStarred = true,
                isSent = true,
                folder = "SENT",
                category = "PRIMARY",
                labels = "Important,Work",
                hasAttachment = true,
                attachmentNames = "Hotmailer_PitchDeck_Q4.pdf",
                avatarColorHex = "#D9381E"
            ),

            // Another Sent Mail with @Hotmailer signature
            EmailEntity(
                senderName = "Me",
                senderEmail = "sweethamza14@hotmailer.com",
                recipientName = "Emma Watson",
                recipientEmail = "emma.watson@acme.com",
                subject = "Re: Project kickoff meeting notes",
                snippet = "Attached are the meeting minutes and action items from this morning's kickoff...",
                body = """Hi Emma,

Attached are the finalized action items and meeting minutes from this morning's kickoff. We are targeting code freeze by next Friday.

Please review the assigned tasks and confirm your timeline.

Regards,
Hamza

---
@Hotmailer""",
                timestamp = now - 1 * oneDay,
                isRead = true,
                isStarred = false,
                isSent = true,
                folder = "SENT",
                category = "PRIMARY",
                labels = "Work",
                hasAttachment = false,
                avatarColorHex = "#D9381E"
            ),

            // Social Category
            EmailEntity(
                senderName = "GitHub",
                senderEmail = "notifications@github.com",
                recipientName = "Me",
                recipientEmail = "sweethamza14@hotmailer.com",
                subject = "[GitHub] sweethamza14 starred Jetpack-Compose-Samples",
                snippet = "sweethamza14 starred android/compose-samples. Check out the latest release v1.7.0...",
                body = """GitHub Notification

You starred android/compose-samples!

Check out related repositories:
• androidx/androidx
• google/accompanist
• material-components/material-components-android

Manage your notification preferences at https://github.com/settings/notifications""",
                timestamp = now - 12 * oneHour,
                isRead = false,
                isStarred = false,
                folder = "INBOX",
                category = "SOCIAL",
                labels = "GitHub",
                hasAttachment = false,
                avatarColorHex = "#24292E"
            ),

            // Social - LinkedIn
            EmailEntity(
                senderName = "LinkedIn Updates",
                senderEmail = "messages-noreply@linkedin.com",
                recipientName = "Me",
                recipientEmail = "sweethamza14@hotmailer.com",
                subject = "Marcus Vance and 4 others viewed your profile this week",
                snippet = "See who is looking at your profile and see job opportunities matching your skills in Android and Compose...",
                body = """LinkedIn Weekly Summary

Your profile appeared in 42 searches this week!

People from these companies viewed your profile:
• Google
• Microsoft
• Stripe

Keep your profile up to date to get discovered by recruiters looking for Senior Android Engineers.""",
                timestamp = now - 1 * oneDay,
                isRead = true,
                isStarred = false,
                folder = "INBOX",
                category = "SOCIAL",
                labels = "Social",
                hasAttachment = false,
                avatarColorHex = "#0077B5"
            ),

            // Promotions Category
            EmailEntity(
                senderName = "Steam Store",
                senderEmail = "noreply@steampowered.com",
                recipientName = "Me",
                recipientEmail = "sweethamza14@hotmailer.com",
                subject = "An item from your wishlist is on sale! (Save 65%)",
                snippet = "Cyberpunk 2077 & Phantom Liberty bundle is now on discount for the Autumn Steam Festival...",
                body = """Steam Autumn Sale Alert!

An item on your Steam Wishlist is on sale:
Cyberpunk 2077: Ultimate Edition is now 65% off until Monday at 10 AM Pacific.

Click through to inspect the offer before the special ends!""",
                timestamp = now - 14 * oneHour,
                isRead = false,
                isStarred = false,
                folder = "INBOX",
                category = "PROMOTIONS",
                labels = "Offers",
                hasAttachment = false,
                avatarColorHex = "#171A21"
            ),

            // Promotions - Flight deals
            EmailEntity(
                senderName = "Emirates Airlines",
                senderEmail = "specialoffers@emirates.com",
                recipientName = "Me",
                recipientEmail = "sweethamza14@hotmailer.com",
                subject = "Explore Tokyo, Paris & Dubai with Exclusive Autumn Fares",
                snippet = "Book by November 15 to take advantage of premium economy fares and bonus Skywards miles...",
                body = """Fly Better with Emirates

Special Autumn Fares for your next getaway:
• New York to Dubai from $799 roundtrip
• Chicago to Tokyo from $849 roundtrip
• Los Angeles to Paris from $699 roundtrip

Enjoy world-class dining, 6,500 channels of entertainment on ice, and seamless connectivity in the skies.""",
                timestamp = now - 2 * oneDay,
                isRead = true,
                isStarred = false,
                folder = "INBOX",
                category = "PROMOTIONS",
                labels = "Travel",
                hasAttachment = false,
                avatarColorHex = "#D71921"
            ),

            // Updates Category
            EmailEntity(
                senderName = "Figma Team",
                senderEmail = "updates@figma.com",
                recipientName = "Me",
                recipientEmail = "sweethamza14@hotmailer.com",
                subject = "Introducing Figma AI: Design faster than ever",
                snippet = "We're rolling out AI tools to help you find what you need, make realistic prototypes, and generate first drafts...",
                body = """What's new in Figma:

Today we are excited to introduce Figma AI tools:
1. First Draft: Jumpstart designs with AI-generated layouts and components.
2. Visual Search: Find existing design system components instantly.
3. Realistic Copy & Translation: Populate wireframes with authentic text in seconds.

Check out our interactive tutorial and playground file in your Figma workspace!""",
                timestamp = now - 18 * oneHour,
                isRead = false,
                isStarred = true,
                folder = "INBOX",
                category = "UPDATES",
                labels = "Tech,Design",
                hasAttachment = false,
                avatarColorHex = "#A259FF"
            ),

            // Updates - Stripe receipt
            EmailEntity(
                senderName = "Stripe Invoicing",
                senderEmail = "invoice+statements@stripe.com",
                recipientName = "Me",
                recipientEmail = "sweethamza14@hotmailer.com",
                subject = "Receipt for Invoice #STR-89214 ($49.00 USD)",
                snippet = "Your payment for AI Studio Cloud Hosting Subscription has processed successfully...",
                body = """Stripe Receipt

Amount Paid: $49.00 USD
Date: Yesterday
Payment method: Visa ending in 4242
Description: Monthly Cloud Compute & App Engine Hosting

A PDF copy of this invoice has been attached for your accounting records.""",
                timestamp = now - 1 * oneDay - 4 * oneHour,
                isRead = true,
                isStarred = false,
                folder = "INBOX",
                category = "UPDATES",
                labels = "Receipts",
                hasAttachment = true,
                attachmentNames = "Invoice_STR89214.pdf",
                avatarColorHex = "#635BFF"
            ),

            // Drafts folder
            EmailEntity(
                senderName = "Me",
                senderEmail = "sweethamza14@hotmailer.com",
                recipientName = "Product Team",
                recipientEmail = "team@hotmailer.internal",
                subject = "Sprint 14 Retrospective & Release Checklist",
                snippet = "Draft: Team, ahead of tomorrow's retro here are the key deliverables we completed...",
                body = """Team,

Ahead of tomorrow's retrospective, here is our key deliverables status:
- Hotmailer v1.0 release candidate build verified
- Automated @Hotmailer email signature footer enabled for all accounts
- Room offline database indexing completed

Looking forward to hearing everyone's thoughts tomorrow.

Best,
Hamza

---
@Hotmailer""",
                timestamp = now - 3 * oneHour,
                isRead = true,
                isDraft = true,
                folder = "DRAFTS",
                category = "PRIMARY",
                labels = "Draft,Work",
                hasAttachment = false,
                avatarColorHex = "#D9381E"
            )
        )
    }
}
