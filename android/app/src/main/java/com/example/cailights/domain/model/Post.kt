package com.example.cailights.domain.model

import java.time.ZonedDateTime

data class Post(
    val id: String,
    val title: String,
    val content: String,
    val type: PostType = PostType.NORMAL,
    val isVerified: Boolean,
    val isPublic: Boolean,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime,
    val author: User,
    val tags: List<Tag>,
    val attachments: List<Attachment>
)

enum class PostType {
    NORMAL,
    HIGHLIGHT
}

data class Tag(
    val id: String,
    val name: String
)

sealed interface Attachment {
    val id: String
    val createdAt: ZonedDateTime

    data class Photo(
        override val id: String,
        override val createdAt: ZonedDateTime,
        val url: String
    ) : Attachment

    data class Pdf(
        override val id: String,
        override val createdAt: ZonedDateTime,
        val url: String,
        val fileName: String
    ) : Attachment

    data class Link(
        override val id: String,
        override val createdAt: ZonedDateTime,
        val url: String
    ) : Attachment
}
