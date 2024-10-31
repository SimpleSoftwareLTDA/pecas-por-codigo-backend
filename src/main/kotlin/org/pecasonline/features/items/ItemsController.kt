package org.pecasonline.features.items

import io.swagger.v3.oas.annotations.tags.Tag
import org.pecasonline.features.Constants.Companion.BASE_ENDPOINT
import org.pecasonline.features.items.swagger.ItemSwaggerSpec
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@Tag(name = "Peças", description = "Operações relacionadas às peças")
@RestController
@RequestMapping("$BASE_ENDPOINT/pecas")
class ItemsController(
    private val itemsService: IIitemService
): ItemSwaggerSpec {

    @GetMapping
    override fun items(
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ) = itemsService.getAllItems(page, size)

    @GetMapping("/{id}")
    override fun findItemById(
        @PathVariable("id") id: Int
    ) = itemsService.findItemById(id)

    @GetMapping("/descricao")
    override fun findItemByDescription(
        @RequestParam("descricao") descricao: String,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ) = itemsService.findItemByDescription(descricao, page, size)

    @GetMapping("/codigo/{codigo}")
    override fun findItemByCode(
        @PathVariable("codigo") codigo: String,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ) = itemsService.findItemByCode(codigo, page, size)

}