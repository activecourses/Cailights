package com.example.cailights.domain.model

import java.time.ZonedDateTime

data class JobOffer(
    val id: String,
    val title: String,
    val message: String,
    val status: JobOfferStatus,
    val createdAt: ZonedDateTime,
    val updatedAt: ZonedDateTime,
    val respondedAt: ZonedDateTime?,
    val recruiter: User,
    val candidate: User
)

enum class JobOfferStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    CANCELLED
}
