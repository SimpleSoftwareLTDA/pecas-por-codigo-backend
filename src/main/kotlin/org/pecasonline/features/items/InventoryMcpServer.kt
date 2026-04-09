package org.pecasonline.features.items

import org.springaicommunity.mcp.annotation.McpTool
import org.springframework.stereotype.Component

/**
 * Servidor MCP para exposição de ferramentas de inventário.
 * [do lat. *inventarium* "lista de bens"]
 */
@Component
class InventoryMcpServer(
    private val itemService: ItemService
) {

    @McpTool(description = "Busca peças no catálogo do Peças Online utilizando o código do fabricante")
    fun searchByCode(
        code: String,
        page: Int? = 0,
        size: Int? = 10
    ): List<Item> = itemService.findItemByCode(code, page, size).content
}
