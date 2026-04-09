package org.pecasonline.features.stock

import org.springaicommunity.mcp.annotation.McpTool
import org.springframework.stereotype.Component

/**
 * Servidor MCP para exposição de ferramentas de consulta ao estoque.
 * Expõe apenas operações de leitura — mutações (upload de arquivos) não são
 * adequadas para o contexto conversacional do MCP.
 */
@Component
class StockMcpServer(
    private val stockService: IStockService
) {

    @McpTool(description = "Busca itens em estoque pelo código do fabricante da peça. Retorna quantidade disponível e fornecedor.")
    fun findStockByCode(
        code: String,
        page: Int? = 0,
        size: Int? = 10
    ): List<Stock> = stockService.findStockByItemCode(code, page, size).content

    @McpTool(description = "Busca itens em estoque pela descrição da peça (busca parcial, sem distinção de maiúsculas/minúsculas).")
    fun findStockByDescription(
        descricao: String,
        page: Int? = 0,
        size: Int? = 10
    ): List<Stock> = stockService.findStockByItemDescription(descricao, page, size).content

    @McpTool(description = "Busca todos os itens em estoque de um fornecedor específico pelo nome (busca parcial).")
    fun findStockBySupplierName(
        nome: String,
        page: Int? = 0,
        size: Int? = 10
    ): List<Stock> = stockService.findStockBySupplierName(nome, page, size).content

    @McpTool(description = "Busca todos os itens em estoque de um fornecedor específico pelo ID do fornecedor.")
    fun findStockBySupplierId(
        id: Int,
        page: Int? = 0,
        size: Int? = 10
    ): List<Stock> = stockService.findStockBySupplierId(id, page, size).content
}
