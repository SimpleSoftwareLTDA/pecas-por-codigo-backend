package org.pecasonline.features.items

import io.micrometer.core.annotation.Timed
import io.micrometer.core.instrument.MeterRegistry
import io.swagger.v3.oas.annotations.tags.Tag
import org.pecasonline.common.Constants.BASE_ENDPOINT
import org.pecasonline.features.items.swagger.ItemSwaggerSpec
import org.springframework.web.bind.annotation.*

@Tag(name = "Peças", description = "Operações relacionadas às peças")
@RestController
@RequestMapping("$BASE_ENDPOINT/pecas")
class ItemsController(
    private val itemsService: IIitemService,
) : ItemSwaggerSpec {

    @Timed(value = "items.getAll", description = "Time taken to return all items")
    @GetMapping
    override fun items(
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ) = itemsService.getAllItems(page, size)

    @Timed(value = "items.getById", description = "Time taken to return an item by id")
    @GetMapping("/{id}")
    override fun findItemById(
        @PathVariable("id") id: Int
    ) = itemsService.findItemById(id)

    @Timed(value = "items.getByDescription", description = "Time taken to return an item by description")
    @GetMapping("/descricao")
    override fun findItemByDescription(
        @RequestParam("descricao") descricao: String,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ) = itemsService.findItemByDescription(descricao, page, size)

    @Timed(value = "items.getByCode", description = "Time taken to return an item by code")
    @GetMapping("/codigo/{codigo}")
    override fun findItemByCode(
        @PathVariable("codigo") codigo: String,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ) = itemsService.findItemByCode(codigo, page, size)
    
}
