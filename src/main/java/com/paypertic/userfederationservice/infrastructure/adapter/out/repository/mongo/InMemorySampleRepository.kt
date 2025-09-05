package com.paypertic.userfederationservice.infrastructure.adapter.out.repository.mongo
import com.paypertic.userfederationservice.domain.models.Sample
import com.paypertic.userfederationservice.domain.port.repository.SampleRepository
import org.springframework.stereotype.Repository
import java.util.concurrent.ConcurrentHashMap
@Repository
class InMemorySampleRepository: SampleRepository {
    private val storage = ConcurrentHashMap<String, Sample>()
    override fun findById(id: String): Sample? = storage[id]
    override fun save(sample: Sample): Sample {
        storage[sample.id] = sample
        return sample
    }
}
