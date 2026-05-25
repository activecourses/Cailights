package com.example.cailights.data.feed

import com.example.cailights.domain.feed.FeedRepository
import com.example.cailights.domain.model.Post
import com.example.cailights.domain.model.Role
import com.example.cailights.domain.model.Tag
import com.example.cailights.domain.model.User
import com.example.cailights.ui.Result
import kotlinx.coroutines.delay
import java.time.ZonedDateTime

class FakeFeedRepository : FeedRepository {
    
    private val fakeUsers = listOf(
        User("1", "alex_dev", "alex@example.com", Role("1", "Developer")),
        User("2", "sarah_design", "sarah@example.com", Role("2", "Designer")),
        User("3", "mike_tech", "mike@example.com", Role("1", "Developer"))
    )

    private val fakePosts = listOf(
        Post(
            id = "p1",
            title = "Modern Android Development",
            content = "Compose is changing everything! MVI + Koin is the way to go.",
            isVerified = true,
            isPublic = true,
            createdAt = ZonedDateTime.now().minusDays(1),
            updatedAt = ZonedDateTime.now().minusDays(1),
            author = fakeUsers[0],
            tags = listOf(Tag("1", "android"), Tag("2", "jetback_compose")),
            attachments = emptyList()
        ),
        Post(
            id = "p2",
            title = "KMP is Awesome",
            content = "Shared logic between Android and iOS is finally easy.",
            isVerified = false,
            isPublic = true,
            createdAt = ZonedDateTime.now().minusHours(5),
            updatedAt = ZonedDateTime.now().minusHours(5),
            author = fakeUsers[0],
            tags = listOf(Tag("1", "android"), Tag("3", "kotlin")),
            attachments = emptyList()
        ),
        Post(
            id = "p3",
            title = "UI/UX Principles",
            content = "Keep it simple and consistent. User experience is king.",
            isVerified = true,
            isPublic = true,
            createdAt = ZonedDateTime.now().minusDays(2),
            updatedAt = ZonedDateTime.now().minusDays(2),
            author = fakeUsers[1],
            tags = listOf(Tag("4", "design")),
            attachments = emptyList()
        ),
        Post(
            id = "p4",
            title = "Design Systems",
            content = "Why you need a design system for your next big app.",
            isVerified = true,
            isPublic = true,
            createdAt = ZonedDateTime.now().minusHours(2),
            updatedAt = ZonedDateTime.now().minusHours(2),
            author = fakeUsers[1],
            tags = listOf(Tag("4", "design")),
            attachments = emptyList()
        ),
        Post(
            id = "p5",
            title = "Backend with Ktor",
            content = "Building lightweight backends in Kotlin with Ktor is a joy.",
            isVerified = false,
            isPublic = true,
            createdAt = ZonedDateTime.now().minusDays(3),
            updatedAt = ZonedDateTime.now().minusDays(3),
            author = fakeUsers[2],
            tags = listOf(Tag("3", "kotlin"), Tag("5", "backend")),
            attachments = emptyList()
        ),
        Post(
            id = "p6",
            title = "Microservices Architecture",
            content = "Handling scalability with microservices.",
            isVerified = true,
            isPublic = true,
            createdAt = ZonedDateTime.now().minusMinutes(30),
            updatedAt = ZonedDateTime.now().minusMinutes(30),
            author = fakeUsers[2],
            tags = listOf(Tag("5", "backend"), Tag("6", "microservice")),
            attachments = emptyList()
        )
    )

    override suspend fun getFeed(): Result<List<Post>, com.example.cailights.ui.Error> {
        delay(1000)
        return Result.Success(fakePosts.sortedByDescending { it.createdAt })
    }
}
