package com.example.smartcampuscompanion.screens

import android.content.SharedPreferences
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smartcampuscompanion.data.local.Announcement
import com.example.smartcampuscompanion.ui.viewmodel.AnnouncementViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementScreen(
    viewModel: AnnouncementViewModel,
    navController: NavController,
    prefs: SharedPreferences
) {
    val announcements by viewModel.announcements.collectAsState()

    // Sync Theme with Dashboard
    val isDark = remember { prefs.getBoolean("is_dark_mode", true) }
    val startGradient by animateColorAsState(if (isDark) Color(0xFF1A1A2E) else Color(0xFFF0F2F5), tween(500), label = "")
    val endGradient by animateColorAsState(if (isDark) Color(0xFF121212) else Color(0xFFFFFFFF), tween(500), label = "")
    val textColor = if (isDark) Color.White else Color(0xFF1A1A1A)
    val subTextColor = textColor.copy(alpha = 0.6f)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Campus Notices", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = textColor,
                    navigationIconContentColor = textColor
                )
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(startGradient, endGradient)))
                .padding(innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 2. ADD THE FILTER CHIPS HERE
                item {
                    Text(
                        text = "Latest Updates",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // NEW FILTER SECTION
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val filterOptions = listOf("All", "Unread", "Read")
                        filterOptions.forEach { filterTag ->
                            FilterChip(
                                selected = viewModel.currentFilter.value == filterTag,
                                onClick = { viewModel.setFilter(filterTag) },
                                label = { Text(filterTag) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Color(0xFF8E2DE2),
                                    selectedLabelColor = Color.White,
                                    labelColor = subTextColor
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                // 3. SHOW EMPTY STATE OR LIST
                if (announcements.isEmpty()) {
                    item {
                        Box(modifier = Modifier.fillParentMaxHeight(0.7f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                            EmptyAnnouncementsState(textColor)
                        }
                    }
                } else {
                    items(announcements) { announcement ->
                        AnnouncementItem(
                            announcement = announcement,
                            isDark = isDark,
                            onRead = { viewModel.markAsRead(announcement) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnnouncementItem(announcement: Announcement, isDark: Boolean, onRead: () -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }
    val containerColor = if (isDark) Color(0xFF1E1E2C) else Color.White
    val textColor = if (isDark) Color.White else Color(0xFF1A1A1A)
    val accentColor = Color(0xFF8E2DE2)

    // Automatically mark as read when expanded
    LaunchedEffect(isExpanded) {
        if (isExpanded && !announcement.isRead) {
            onRead()
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize() // Smoothly grow/shrink
            .clickable { isExpanded = !isExpanded },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = containerColor),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = if (isDark) 0.dp else 2.dp),
        // Subtle border for unread items
        border = if (!announcement.isRead) androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.5f)) else null
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Category Icon
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            if (announcement.isRead) Color.Gray.copy(alpha = 0.1f) else accentColor.copy(alpha = 0.1f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = null,
                        tint = if (announcement.isRead) Color.Gray else accentColor,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = announcement.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = if (announcement.isRead) FontWeight.SemiBold else FontWeight.ExtraBold,
                            color = if (announcement.isRead) textColor.copy(alpha = 0.7f) else textColor
                        )
                    )
                    Text(
                        text = "Posted 2 hours ago", // Placeholder for actual date
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = announcement.content,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isDark) Color.LightGray else Color.DarkGray,
                maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 20.sp
            )

            if (!announcement.isRead && !isExpanded) {
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.size(8.dp).background(accentColor, CircleShape))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New", color = accentColor, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun EmptyAnnouncementsState(textColor: Color) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.NotificationsNone,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = Color.Gray.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("No new notices", color = textColor, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text("We'll notify you when something comes up.", color = textColor.copy(alpha = 0.5f))
    }
}