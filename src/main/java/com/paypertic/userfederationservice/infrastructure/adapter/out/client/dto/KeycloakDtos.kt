package com.paypertic.userfederationservice.infrastructure.adapter.out.client.dto

data class KeycloakTokenResponse(
    val access_token: String,
    val token_type: String? = null,
    val expires_in: Long? = null,
    val scope: String? = null
)

data class KeycloakUserRep(
    val id: String? = null,
    val username: String? = null,
    val email: String? = null,
    val emailVerified: Boolean? = null
)
