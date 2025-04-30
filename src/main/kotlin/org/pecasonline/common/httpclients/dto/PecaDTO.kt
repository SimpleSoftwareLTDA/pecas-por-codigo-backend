package org.pecasonline.common.httpclients.dto

data class PecaDTO(
    val fabricante: String,
    val codigo: String,
    val fornecedor: String,
    val qtd: Int,
    val preco: String?,
    val descricao: String,
    val atualizacao: String
)