package org.pecasonline.features.category

import io.micrometer.core.annotation.Timed
import org.pecasonline.common.Constants.BASE_ENDPOINT
import org.pecasonline.features.category.swagger.CategorySwaggerSpec
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("$BASE_ENDPOINT/categorias")
class CategoryController(
    private val categoryService: ICategoryService
): CategorySwaggerSpec {

    @Timed(value = "categories.getAll", description = "Time taken to return all categories")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    override fun getAllCategories(
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?
    ) = categoryService.getAllCategories(page, size)

    @Timed(value = "categories.getById", description = "Time taken to return a category by id")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    override fun getCategoryById(@PathVariable id: Int) = categoryService.getCategoryById(id)

    @Timed(value = "categories.getByName", description = "Time taken to return a category by name")
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    override fun getCategoryByName(
        @RequestParam name: String,
        @RequestParam(required = false) page: Int?,
        @RequestParam(required = false) size: Int?
    ) = categoryService.searchCategory(name, page, size)

}
