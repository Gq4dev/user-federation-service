package com.paypertic.userfederationservice.application.service

import com.paypertic.userfederationservice.infrastructure.adapter.out.client.KeycloakAdminClient
import com.paypertic.userfederationservice.infrastructure.adapter.out.client.dto.KeycloakCreateUserRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class KeycloakUserService(
    private val kc: KeycloakAdminClient,
    @Value("\${app.keycloak.source-realm}") private val sourceRealm: String,
    @Value("\${app.keycloak.target-realm}") private val targetRealm: String
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

    data class MigrationResult(
        val status: String,       // "created" | "already_in_target" | "not_found_source" | "not_verified"
        val sourceUserId: String? = null,
        val targetUserId: String? = null,
        val email: String? = null
    )


    fun existsByEmail(email: String): UserExistence {
        val user = kc.findUsersByEmail(sourceRealm, email, exact = true).firstOrNull()
        return if (user?.id != null) {
            UserExistence(
                true,
                user.id,
                user.email,
                user.emailVerified,
                user.username,
                user.firstName,
                user.lastName
            )
        } else {
            UserExistence(false)
        }
    }

    fun sendVerificationIfNeeded(
        email: String,
        clientId: String? = null,
        redirectUri: String? = null
    ): VerificationResult {
        val user = kc.findUsersByEmail(sourceRealm, email, exact = true).firstOrNull()
            ?: return VerificationResult("not_found")

        if (user.emailVerified == true) {
            return VerificationResult(status = "already_verified", userId = user.id, email = user.email)
        }
        kc.sendVerifyEmail(sourceRealm, user.id!!, clientId, redirectUri)
        return VerificationResult(status = "sent", userId = user.id, email = user.email)
    }


    fun migrateToTargetIfVerified(email: String): MigrationResult {

        val src = kc.findUsersByEmail(sourceRealm, email, exact = true).firstOrNull()
            ?: return MigrationResult("not_found_source")

        if (src.emailVerified != true) {
            return MigrationResult("not_verified", sourceUserId = src.id, email = src.email)
        }


        val tgt = kc.findUsersByEmail(targetRealm, email, exact = true).firstOrNull()
        if (tgt?.id != null) {
            return MigrationResult("already_in_target", sourceUserId = src.id, targetUserId = tgt.id, email = email)
        }


        val req = KeycloakCreateUserRequest(
            username = (src.username ?: email),
            email = email,
            enabled = true,
            emailVerified = true,
            firstName = src.firstName,
            lastName = src.lastName
        )
        val newId = kc.createUser(targetRealm, req)
        return MigrationResult("created", sourceUserId = src.id, targetUserId = newId, email = email)
    }
}
