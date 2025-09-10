package org.pecasonline.features.items

import org.pecasonline.common.exceptions.NotFoundException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service

@Service
class ItemService(
    private val itemRepository: ItemRepository
): IIitemService {

    override fun getAllItems(page: Int?, size: Int?): Page<Item> {
        val pageRequest = PageRequest.of(page ?: 0, size?: 10)
        return itemRepository.findAll(pageRequest)
    }

    override fun findItemById(id: Int): Item {
        return itemRepository
            .findById(id)
            .orElseThrow {
                NotFoundException("Peça não encontrada")
            }
    }

    override fun findItemByDescription(descricao: String, page: Int?, size: Int?): Page<Item> {
        val pageRequest = PageRequest.of(page ?: 0, size?: 10)
        return itemRepository.findItemByDescriptionContainsIgnoreCase(descricao, pageRequest)
    }

    override fun findItemByCode(code: String, page: Int?, size: Int?): Page<Item> {
        val pageRequest = PageRequest.of(page ?: 0, size?: 10)
        return itemRepository.findItemByCode(code, pageRequest)
    }
}