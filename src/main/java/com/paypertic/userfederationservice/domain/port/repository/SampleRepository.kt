package com.paypertic.userfederationservice.domain.port.repository
import com.paypertic.userfederationservice.domain.models.Sample
interface SampleRepository {
    fun findById(id: String): Sample?
    fun save(sample: Sample): Sample
}
