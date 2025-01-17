package org.pecasonline.features.category

import org.pecasonline.common.Constants.BASE_ENDPOINT
import org.pecasonline.features.category.swagger.CategorySwaggerSpec
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("$BASE_ENDPOINT/categorias")
class CategoryController(
    private val categoryService: ICategoryService
): CategorySwaggerSpec {

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    override fun getAllCategories(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?
    ) = categoryService.getAllCategories(page, size)

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    override fun getCategoryById(@PathVariable id: Int) = categoryService.getCategoryById(id)

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    override fun getCategoryByName(
        @RequestParam name: String,
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?
    ) = categoryService.searchCategory(name, page, size)

}