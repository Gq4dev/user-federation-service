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
        val emailVerified: Boolean? = null
    )

    data class VerificationResult(
        val status: String,           // "sent" | "already_verified" | "not_found"
        val userId: String? = null,
        val email: String? = null
    )

    fun existsByEmail(email: String): UserExistence {
        val u = kc.findUsersByEmail(email, exact = true).firstOrNull()
        return if (u?.id != null) UserExistence(true, u.id, u.email, u.emailVerified)
        else UserExistence(false)
    }

    fun sendVerificationIfNeeded(email: String, clientId: String? = null, redirectUri: String? = null): VerificationResult {
        val u = kc.findUsersByEmail(email, exact = true).firstOrNull()
            ?: return VerificationResult("not_found")

        if (u.emailVerified == true) {
            return VerificationResult(status = "already_verified", userId = u.id, email = u.email)
        }
        kc.sendVerifyEmail(u.id!!, clientId, redirectUri)
        return VerificationResult(status = "sent", userId = u.id, email = u.email)
    }
}
