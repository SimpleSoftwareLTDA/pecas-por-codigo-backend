package org.pecasonline.common.httpclients

import org.jsoup.Jsoup
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.*
import kotlin.collections.*

/**
 * 1) Definição do cliente Feign para chamar a URL do site
 */
@FeignClient(name = "pecasOnlineClient", url = "http://www.pecas-on-line.com.br")
interface PecasOnlineFeignClient {

    @GetMapping("/consultacod.php4")
    fun consultarPeca(
        @RequestParam("definirFabricante") definirFabricante: String = "todos",
        @RequestParam("Fabricante") fabricante: String = "AGCO",
        @RequestParam("PartNumber") partNumber: String,
        @RequestParam("Ordem") ordem: String = "Cidade",
        @RequestParam("Pesquisar") pesquisar: String = "Pesquisar"
    ): String
}

/**
 * 2) Serviço que usa o FeignClient para obter o HTML
 */
@Service
class PecaService(
    private val pecasOnlineFeignClient: PecasOnlineFeignClient
) {
    fun buscarPeca(partNumber: String): String {
        return pecasOnlineFeignClient.consultarPeca(partNumber = partNumber)
    }
}

/**
 * 3) DTO para armazenar os campos extraídos da tabela
 */
data class PecaDTO(
    val fabricante: String,
    val codigo: String,
    val fornecedor: String,
    val qtd: Int,
    val preco: String?,
    val descricao: String,
    val atualizacao: String
)

/**
 * 4) Função auxiliar que faz o parse do HTML via Jsoup e extrai a lista de PecaDTO
 */
fun parseResultadoPesquisa(html: String): List<PecaDTO> {
    // 4.1) Faz o parse do HTML
    val doc = Jsoup.parse(html)

    // 4.2) Localiza a tabela de resultados (verificamos o "bgcolor=#FFFFFF" e a div com style="overflow-x:auto")
    val tables = doc.select("div[style~=.*overflow-x:auto.*] table[bgcolor=#FFFFFF]")
    if (tables.isEmpty()) {
        // Se não encontrar a tabela esperada, retornamos vazio (ou pode lançar exceção)
        return emptyList()
    }

    val table = tables.first()

    // 4.3) Pegamos todas as linhas <tr> dentro da tabela
    val rows = table!!.select("tr")
    // A primeira linha (ou mais) pode ser cabeçalho. Aqui, pulamos apenas 1.
    val dataRows = rows.drop(1)

    // 4.4) Montamos a lista de DTOs
    val listaDTO = mutableListOf<PecaDTO>()
    for (row in dataRows) {
        val cols = row.select("td")
        if (cols.size < 7) {
            // Se não tiver pelo menos 7 colunas, pode não ser uma linha de dados
            continue
        }

        val fabricante = cols[0].text().trim()
        val codigo = cols[1].text().trim()
        val fornecedor = cols[2].text().trim()
        val qtdStr = cols[3].text().trim()
        val qtd = qtdStr.toIntOrNull() ?: 0
        val precoStr = cols[4].text().trim()
        val valorEmDouble = precoStr.replace(",", ".").toDoubleOrNull() ?: 0.0
        val priceInCents = (valorEmDouble * 100).toLong()
        val descricao = cols[5].text().trim()
        val atualizacao = cols[6].text().trim()

        val finalValueOrDefault = when {
            precoStr.isBlank() || priceInCents.toDouble() == 0.0 -> 0.0

            else -> priceInCents.toString()
        }

        listaDTO.add(
            PecaDTO(
                fabricante = fabricante,
                codigo = codigo,
                fornecedor = fornecedor,
                qtd = qtd,
                preco = finalValueOrDefault.toString(),
                descricao = descricao,
                atualizacao = atualizacao
            )
        )
    }

    return listaDTO
}

/**
 * 5) Controller que expõe um endpoint para consumir essa lógica
 */
@RestController
@RequestMapping("v1/api/old")
class OldController(private val service: PecaService) {

    @GetMapping("/{code}")
    fun getOldCatalog(@PathVariable code: String): List<PecaDTO> {
        val html = service.buscarPeca(partNumber = code)

        return parseResultadoPesquisa(html)
    }
}
