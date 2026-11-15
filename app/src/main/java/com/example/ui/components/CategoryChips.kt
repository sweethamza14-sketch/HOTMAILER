package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.MarkEmailUnread
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HotmailerOrange
import com.example.ui.theme.HotmailerRed
import com.example.ui.theme.PromoGreen
import com.example.ui.theme.SocialBlue
import com.example.ui.theme.UpdatesOrange
import com.example.ui.viewmodel.CategoryType
import com.example.ui.viewmodel.FilterChipType

@Composable
fun CategoryTabs(
    selectedCategory: CategoryType,
    onCategorySelected: (CategoryType) -> Unit,
    activeFilter: FilterChipType,
    onFilterSelected: (FilterChipType) -> Unit,
    categoryUnreadCounts: Map<CategoryType, Int> = emptyMap(),
    totalUnreadCount: Int = 0,
    showCategories: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (showCategories) {
            ScrollableTabRow(
                selectedTabIndex = selectedCategory.ordinal,
                edgePadding = 16.dp,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    if (selectedCategory.ordinal < tabPositions.size) {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedCategory.ordinal]),
                            color = HotmailerRed,
                            height = 3.dp
                        )
                    }
                },
                divider = {}
            ) {
                CategoryType.values().forEach { category ->
                    val isSelected = category == selectedCategory
                    val unreadForCategory = categoryUnreadCounts[category] ?: 0
                    val (icon, color) = when (category) {
                        CategoryType.PRIMARY -> Icons.Default.Inbox to HotmailerRed
                        CategoryType.PROMOTIONS -> Icons.Default.LocalOffer to PromoGreen
                        CategoryType.SOCIAL -> Icons.Default.Group to SocialBlue
                        CategoryType.UPDATES -> Icons.Default.Info to UpdatesOrange
                    }

                    Tab(
                        selected = isSelected,
                        onClick = { onCategorySelected(category) },
                        modifier = Modifier.testTag("tab_${category.name.lowercase()}"),
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = category.title,
                                    modifier = Modifier.size(18.dp),
                                    tint = if (isSelected) color else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = category.title,
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = if (isSelected || unreadForCategory > 0) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 14.sp
                                    ),
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                if (unreadForCategory > 0) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = color.copy(alpha = 0.16f)
                                    ) {
                                        Text(
                                            text = unreadForCategory.toString(),
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            ),
                                            color = color,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                                        )
                                    }
                                }
                            }
                        }
                    )
                }
            }
        }

        // Quick Filter row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChipType.values().forEach { filter ->
                val isSelected = activeFilter == filter
                val leadingIcon = when (filter) {
                    FilterChipType.ALL -> null
                    FilterChipType.UNREAD -> Icons.Default.MarkEmailUnread
                    FilterChipType.STARRED -> Icons.Default.Star
                    FilterChipType.HAS_ATTACHMENT -> Icons.Default.AttachFile
                    FilterChipType.HOTMAILER -> Icons.Default.Campaign
                }
                val chipLabel = if (filter == FilterChipType.UNREAD && totalUnreadCount > 0) {
                    "${filter.label} ($totalUnreadCount)"
                } else {
                    filter.label
                }

                FilterChip(
                    selected = isSelected,
                    onClick = { onFilterSelected(filter) },
                    label = {
                        Text(
                            text = chipLabel,
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        )
                    },
                    leadingIcon = leadingIcon?.let { icon ->
                        {
                            Icon(
                                imageVector = icon,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = HotmailerRed.copy(alpha = 0.12f),
                        selectedLabelColor = HotmailerRed,
                        selectedLeadingIconColor = HotmailerRed
                    ),
                    modifier = Modifier.testTag("filter_${filter.name.lowercase()}")
                )
            }
        }
    }
}
