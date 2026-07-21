package com.example.cailights.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cailights.R
import com.example.cailights.domain.model.Attachment
import com.example.cailights.domain.model.Post
import com.example.cailights.domain.model.PostType
import com.example.cailights.ui.theme.CailightsTheme
import org.koin.androidx.compose.koinViewModel
import java.time.Duration
import java.time.ZonedDateTime

@Composable
fun FeedRoot(
    onNavigateToMessages: () -> Unit,
    viewModel: FeedViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    FeedScreen(
        stateProvider = { state },
        onAction = viewModel::onAction,
        onMessagesClick = onNavigateToMessages
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    stateProvider: () -> FeedState,
    onAction: (FeedAction) -> Unit,
    onMessagesClick: () -> Unit
) {
    val state = stateProvider()
    
    var isFilterBarVisible by remember { mutableStateOf(true) }
    val nestedScrollConnection = remember {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // If we are scrolling down (available.y < 0), hide the bar
                if (available.y < -1) {
                    isFilterBarVisible = false
                }
                // If we are scrolling up (available.y > 0), show the bar
                if (available.y > 1) {
                    isFilterBarVisible = true
                }
                return Offset.Zero
            }
        }
    }

    Scaffold(
        modifier = Modifier.nestedScroll(nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Feed", fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onMessagesClick) {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Messages"
                        )
                    }
                }
            )
        }
    ) { padding ->
        val pullToRefreshState = rememberPullToRefreshState()
        
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            AnimatedVisibility(
                visible = isFilterBarVisible,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                FilterBar(
                    availableTags = state.availableTags,
                    selectedTag = state.selectedTag,
                    onTagSelected = { onAction(FeedAction.OnTagSelected(it)) }
                )
            }

            PullToRefreshBox(
                isRefreshing = state.isLoading,
                onRefresh = { onAction(FeedAction.OnRefresh) },
                state = pullToRefreshState,
                modifier = Modifier.fillMaxSize()
            ) {
                if (state.filteredPosts.isEmpty() && !state.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No posts available")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(state.filteredPosts, key = { it.id }) { post ->
                            PostCard(post = post)
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                thickness = 0.5.dp,
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FilterBar(
    availableTags: List<String>,
    selectedTag: String,
    onTagSelected: (String) -> Unit
) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(availableTags) { tag ->
            val isSelected = tag == selectedTag
            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.05f else 1f,
                label = "chip_scale"
            )

            ElevatedFilterChip(
                selected = isSelected,
                onClick = { onTagSelected(tag) },
                label = { 
                    Text(
                        text = tag,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    ) 
                },
                modifier = Modifier.scale(scale),
                colors = FilterChipDefaults.elevatedFilterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                elevation = FilterChipDefaults.elevatedFilterChipElevation(
                    elevation = 2.dp,
                    pressedElevation = 4.dp
                )
            )
        }
    }
}

@Composable
fun PostCard(post: Post) {
    val isHighlight = post.type == PostType.HIGHLIGHT
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .then(
                if (isHighlight) {
                    Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                        .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                } else Modifier
            )
    ) {
        // Author Row
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(androidx.compose.foundation.shape.CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().padding(4.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = post.author.username,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (post.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified User",
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    if (isHighlight) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Highlight",
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFFFBC02D) // Gold color
                        )
                    }
                }
                Text(
                    text = formatRelativeTime(post.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Title
        Text(
            text = post.title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Content
        Text(
            text = post.content,
            style = MaterialTheme.typography.bodyMedium,
            fontStyle = if (isHighlight) FontStyle.Italic else FontStyle.Normal,
            color = if (isHighlight) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Attachments
        if (post.attachments.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            post.attachments.forEach { attachment ->
                AttachmentPreview(attachment)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tags Row
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            post.tags.forEach { tag ->
                Text(
                    text = "#${tag.name}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun AttachmentPreview(attachment: Attachment) {
    when (attachment) {
        is Attachment.Photo -> {
            androidx.compose.foundation.Image(
                painter = painterResource(id = R.drawable.cailights_splash_icon), // Placeholder
                contentDescription = "Attached Photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.LightGray)
            )
        }
        is Attachment.Link -> {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { /* Open link */ },
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                border = androidx.compose.foundation.BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Link,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = attachment.url,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }
            }
        }
        else -> Unit
    }
}

private fun formatRelativeTime(timestamp: ZonedDateTime): String {
    val now = ZonedDateTime.now()
    val duration = Duration.between(timestamp, now)
    return when {
        duration.toDays() > 365 -> "${duration.toDays() / 365} years ago"
        duration.toDays() > 30 -> "${duration.toDays() / 30} months ago"
        duration.toDays() > 0 -> "${duration.toDays()} days ago"
        duration.toHours() > 0 -> "${duration.toHours()} hours ago"
        else -> "${duration.toMinutes()} mins ago"
    }
}

@Preview(showBackground = true)
@Composable
private fun FeedScreenPreview() {
    CailightsTheme {
        FeedScreen(
            stateProvider = { 
                FeedState(
                    availableTags = listOf("#", "android", "kotlin", "design"),
                    selectedTag = "android"
                ) 
            },
            onAction = {},
            onMessagesClick = {}
        )
    }
}
