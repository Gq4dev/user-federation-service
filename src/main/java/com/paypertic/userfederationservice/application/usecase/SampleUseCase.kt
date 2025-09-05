package com.paypertic.userfederationservice.application.usecase
import com.paypertic.userfederationservice.domain.models.Sample
import com.paypertic.userfederationservice.domain.port.repository.SampleRepository
import org.springframework.stereotype.Service
@Service
class SampleUseCase(private val repository: SampleRepository) {
    fun get(id: String): Sample? = repository.findById(id)
    fun create(sample: Sample): Sample = repository.save(sample)
}
