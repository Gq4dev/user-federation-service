package com.paypertic.userfederationservice.infrastructure.adapter.out.client

import com.paypertic.userfederationservice.infrastructure.adapter.out.client.dto.KeycloakTokenResponse
import com.paypertic.userfederationservice.infrastructure.adapter.out.client.dto.KeycloakUserRep
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

@Component
class KeycloakAdminClient(
    private val restTemplate: RestTemplate,
    @Value("\${app.keycloak.base-url}") private val baseUrl: String,
    @Value("\${app.keycloak.admin-realm}") private val adminRealm: String,
    @Value("\${app.keycloak.target-realm}") private val targetRealm: String,
    @Value("\${app.keycloak.client-id}") private val clientId: String,
    @Value("\${app.keycloak.client-secret}") private val clientSecret: String
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private fun bearer(): String {
        val url = "${baseUrl.trimEnd('/')}/realms/$adminRealm/protocol/openid-connect/token"
        val body = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "client_credentials")
            add("client_id", clientId)
            add("client_secret", clientSecret)
        }
        val headers = HttpHeaders().apply { contentType = MediaType.APPLICATION_FORM_URLENCODED }
        val res = restTemplate.postForEntity(url, HttpEntity(body, headers), KeycloakTokenResponse::class.java)
        val token = res.body?.access_token ?: error("access_token vacío")
        return "Bearer $token"
    }

    fun findUsersByEmail(email: String, exact: Boolean = true): List<KeycloakUserRep> {
        val url = UriComponentsBuilder
            .fromHttpUrl("${baseUrl.trimEnd('/')}/admin/realms/$targetRealm/users")
            .queryParam("email", email)
            .queryParam("exact", exact)
            .toUriString()

        val headers = HttpHeaders().apply {
            accept = listOf(MediaType.APPLICATION_JSON)
            set(HttpHeaders.AUTHORIZATION, bearer())
        }

        return try {
            val res = restTemplate.exchange(url, HttpMethod.GET, HttpEntity<Void>(headers), Array<KeycloakUserRep>::class.java)
            res.body?.toList() ?: emptyList()
        } catch (e: HttpStatusCodeException) {
            val reason = e.statusText
            log.error("Users error {} {} -> {}", e.statusCode.value(), reason, e.responseBodyAsString)
            throw e
        }
    }

    fun sendVerifyEmail(userId: String, clientId: String? = null, redirectUri: String? = null, lifespanSec: Long? = null) {
        val builder = UriComponentsBuilder
            .fromHttpUrl("${baseUrl.trimEnd('/')}/admin/realms/$targetRealm/users/$userId/send-verify-email")

        if (!clientId.isNullOrBlank())  builder.queryParam("client_id", clientId)
        if (!redirectUri.isNullOrBlank()) builder.queryParam("redirect_uri", redirectUri)
        if (lifespanSec != null)         builder.queryParam("lifespan", lifespanSec)

        val headers = HttpHeaders().apply { set(HttpHeaders.AUTHORIZATION, bearer()) }

        restTemplate.exchange(builder.toUriString(), HttpMethod.PUT, HttpEntity<Void>(headers), Void::class.java)
    }
}
