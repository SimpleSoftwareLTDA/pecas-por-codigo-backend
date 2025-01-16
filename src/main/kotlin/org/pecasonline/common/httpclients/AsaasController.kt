package org.pecasonline.common.httpclients


import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class AsaasController(
    private val asaasService: AsaasService
) {

    @PostMapping("/customers")
    fun createCustomer(@RequestBody request: CreateClientRequest): ResponseEntity<CreateClientResponse> {
        val response = asaasService.createCustomer(request)
        return ResponseEntity.ok(response)
    }
}