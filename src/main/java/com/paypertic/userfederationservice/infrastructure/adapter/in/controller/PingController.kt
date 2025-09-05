package com.paypertic.userfederationservice.infrastructure.adapter.`in`.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PingController {
    @GetMapping("/api/health/ping")
    fun ping() = mapOf("status" to "ok")
}
