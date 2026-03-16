package org.pecasonline.features.system

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.pecasonline.features.stock.email.sender.EmailSenderService
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/system")
@Tag(name = "System", description = "Endpoints de diagnóstico e utilitários do sistema")
class SystemController(
    private val emailSenderService: EmailSenderService
) {

    @PostMapping("/test-email")
    @Operation(summary = "Envia um e-mail de teste para verificar a conectividade SMTP")
    fun sendTestEmail(@RequestParam to: String): Map<String, String> {
        emailSenderService.sendTestEmail(to)
        return mapOf("message" to "Solicitação de e-mail de teste enviada com sucesso para $to")
    }
}
