package org.pecasonline.features.items

import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Service

@Service
class ItemsMetricsService(
    private val meterRegistry: MeterRegistry
): IItemsMetricsService {
    override fun incrementRequests(endpoint: String, method: String, extraLabels: Map<String, String>) {
        val labels = mutableListOf("endpoint", endpoint, "method", method)

        extraLabels.forEach { (k, v) ->
            labels.add(k)
            labels.add(v)
        }

        meterRegistry.counter("custom.items.requests", *labels.toTypedArray()).increment()
    }

    override fun incrementDemand(itemId: Int) {
        meterRegistry.counter(
            "custom.items.demand",
            "item_id", itemId.toString()
        ).increment()
    }
}