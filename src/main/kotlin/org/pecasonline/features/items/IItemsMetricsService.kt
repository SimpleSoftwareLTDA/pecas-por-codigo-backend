package org.pecasonline.features.items

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Service

@Service
interface IItemsMetricsService {
    fun incrementRequests(endpoint: String, method: String, extraLabels: Map<String, String> = emptyMap())
    fun incrementDemand(itemId: Int)
}