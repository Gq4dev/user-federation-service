package com.paypertic.userfederationservice.infrastructure.adapter.`in`.controller

import com.paypertic.userfederationservice.application.service.KeycloakUserService
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/api/keycloak/users")
class KeycloakUserController(
    private val service: KeycloakUserService
) {
    @GetMapping("/exist")
    fun existsByEmail(@RequestParam @NotBlank @Email email: String): ResponseEntity<Any> =
        ResponseEntity.ok(service.existsByEmail(email))

    @PutMapping("/verify-email")
    fun sendVerifyEmail(
        @RequestParam @NotBlank @Email email: String,
        @RequestParam(required = false) clientId: String?,
        @RequestParam(required = false) redirectUri: String?
    ): ResponseEntity<Any> =
        ResponseEntity.ok(service.sendVerificationIfNeeded(email, clientId, redirectUri))
}
