package org.pecasonline.features.items

import org.pecasonline.common.exceptions.NotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class ItemService(
    private val itemRepository: ItemRepository,
    private val metrics: ItemsMetricsService
): IIitemService {

    override fun getAllItems(page: Int?, size: Int?): Page<Item> {
        val pageRequest = PageRequest.of(page ?: 0, size?: 10)
        metrics.incrementRequests("getAll", "GET")
        return itemRepository.findAll(pageRequest)
    }

    override fun findItemById(id: Int): Item {
        metrics.incrementRequests("getById", "GET", mapOf("item_id" to id.toString()))
        metrics.incrementDemand(id)
        return itemRepository
            .findById(id)
            .orElseThrow {
                NotFoundException("Peça não encontrada")
            }
    }

    override fun findItemByDescription(descricao: String, page: Int?, size: Int?): Page<Item> {
        val pageRequest = PageRequest.of(page ?: 0, size?: 10)
        metrics.incrementRequests("getByDescription", "GET")
        return itemRepository.findItemByDescriptionContainsIgnoreCase(descricao, pageRequest)
    }

    override fun findItemByCode(code: String, page: Int?, size: Int?): Page<Item> {
        val pageRequest = PageRequest.of(page ?: 0, size?: 10)
        metrics.incrementRequests("getByCode", "GET", mapOf("codigo" to code))
        return itemRepository.findItemByCode(code, pageRequest)
    }
}