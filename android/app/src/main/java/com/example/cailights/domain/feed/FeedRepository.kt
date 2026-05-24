package com.example.cailights.domain.feed

import com.example.cailights.domain.model.Post
import com.example.cailights.ui.Result

interface FeedRepository {
    suspend fun getFeed(): Result<List<Post>, com.example.cailights.ui.Error>
}
