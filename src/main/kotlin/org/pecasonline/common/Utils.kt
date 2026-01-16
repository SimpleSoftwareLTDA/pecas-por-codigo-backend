package org.pecasonline.common

import org.jsoup.Jsoup
import org.pecasonline.common.httpclients.dto.PecaDTO

fun List<String>.isInvalidColumnSize(): Boolean =  this.size != 4

fun warnWithoutStacktrace(message: String): Nothing {
    throw object : IllegalStateException(message) {
        override fun fillInStackTrace(): Throwable = this
    }
}

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
            precoStr.isBlank() || priceInCents.toDouble() == 0.0 -> 0

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

fun formatCnpj(cnpj: String): String {
    val digits = cnpj.filter { it.isDigit() }
    if (digits.length != 14) return cnpj
    return "${digits.substring(0, 2)}.${digits.substring(2, 5)}.${digits.substring(5, 8)}/${digits.substring(8, 12)}-${digits.substring(12, 14)}"
}
