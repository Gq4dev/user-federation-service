package com.paypertic.userfederationservice.application.service

import com.paypertic.userfederationservice.infrastructure.adapter.out.client.KeycloakAdminClient
import org.springframework.stereotype.Service

@Service
class KeycloakUserService(
    private val kc: KeycloakAdminClient
) {
    data class UserExistence(
        val exists: Boolean,
        val userId: String? = null,
        val email: String? = null,
        val emailVerified: Boolean? = null,
        val username: String? = null,
        val firstName: String? = null,
        val lastName: String? = null
    )

    data class VerificationResult(
        val status: String,           // "sent" | "already_verified" | "not_found"
        val userId: String? = null,
        val email: String? = null
    )

    fun existsByEmail(email: String): UserExistence {
        val user = kc.findUsersByEmail(email, exact = true).firstOrNull()
        return if (user?.id != null) UserExistence(
            true,
            user.id,
            user.email,
            user.emailVerified,
            user.username,
            user.firstName,
            user.lastName
        )
        else UserExistence(false)
    }

    fun sendVerificationIfNeeded(
        email: String,
        clientId: String? = null,
        redirectUri: String? = null
    ): VerificationResult {
        val user = kc.findUsersByEmail(email, exact = true).firstOrNull()
            ?: return VerificationResult("not_found")

        if (user.emailVerified == true) {
            return VerificationResult(status = "already_verified", userId = user.id, email = user.email)
        }
        kc.sendVerifyEmail(user.id!!, clientId, redirectUri)
        return VerificationResult(status = "sent", userId = user.id, email = user.email)
    }
}
