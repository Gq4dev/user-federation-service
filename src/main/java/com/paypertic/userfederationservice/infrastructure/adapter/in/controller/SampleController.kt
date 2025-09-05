package com.paypertic.userfederationservice.infrastructure.adapter.`in`.controller
import com.paypertic.userfederationservice.application.usecase.SampleUseCase
import com.paypertic.userfederationservice.domain.models.Sample
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
@RestController
@RequestMapping("/api/samples")
class SampleController(private val useCase: SampleUseCase) {
    @GetMapping("/{id}")
    fun get(@PathVariable id: String): ResponseEntity<Any> {
        val s = useCase.get(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(s)
    }
    data class CreateSampleRequest(@field:NotBlank val id: String, @field:NotBlank val name: String)
    @PostMapping
    fun create(@RequestBody @Valid req: CreateSampleRequest): ResponseEntity<Sample> {
        val saved = useCase.create(Sample(req.id, req.name))
        return ResponseEntity.ok(saved)
    }
}
