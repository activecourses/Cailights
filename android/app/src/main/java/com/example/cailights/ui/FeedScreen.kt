package com.example.cailights.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.cailights.domain.model.Post
import com.example.cailights.ui.theme.CailightsTheme
import org.koin.androidx.compose.koinViewModel
import java.time.format.DateTimeFormatter

@Composable
fun FeedRoot(
    viewModel: FeedViewModel = koinViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    
    FeedScreen(
        stateProvider = { state },
        onAction = viewModel::onAction
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    stateProvider: () -> FeedState,
    onAction: (FeedAction) -> Unit
) {
    val state = stateProvider()
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Feed", fontWeight = FontWeight.Bold) })
        }
    ) { padding ->
        val pullToRefreshState = rememberPullToRefreshState()
        
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            FilterBar(
                availableTags = state.availableTags,
                selectedTag = state.selectedTag,
                onTagSelected = { onAction(FeedAction.OnTagSelected(it)) }
            )

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
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.filteredPosts, key = { it.id }) { post ->
                            PostCard(post = post)
                            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), thickness = 0.5.dp, color = Color.LightGray)
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
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(availableTags) { tag ->
            val isSelected = tag == selectedTag
            Text(
                text = tag,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.clickable { onTagSelected(tag) }
            )
        }
    }
}

@Composable
fun PostCard(post: Post) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // Title
        Text(
            text = post.title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(8.dp))

        // Author and Info
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = post.author.username,
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            Text(
                text = " / ${post.createdAt.format(DateTimeFormatter.ofPattern("d 'days ago'"))}",
                style = MaterialTheme.typography.labelMedium,
                color = Color.Gray
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                post.tags.forEach { tag ->
                    Text(
                        text = "#${tag.name} ",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                }
                if (post.isVerified) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Verified User",
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Content Row
        Row(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = post.content,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                maxLines = 3
            )
            
            // Placeholder for Image (as seen in design)
            Surface(
                modifier = Modifier.size(100.dp, 60.dp),
                color = Color.Black,
                shape = MaterialTheme.shapes.small
            ) {
                Box(contentAlignment = Alignment.Center) {
                   // In a real app, use Coil here
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Upvote",
                modifier = Modifier.size(24.dp),
                tint = Color.Gray
            )
            Text(
                text = "12",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
                modifier = Modifier.padding(start = 4.dp, end = 16.dp)
            )
            Icon(
                imageVector = Icons.Default.BookmarkBorder,
                contentDescription = "Bookmark",
                modifier = Modifier.size(24.dp),
                tint = Color.Gray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FeedScreenPreview() {
    CailightsTheme {
        FeedScreen(
            stateProvider = { FeedState() },
            onAction = {}
        )
    }
}
