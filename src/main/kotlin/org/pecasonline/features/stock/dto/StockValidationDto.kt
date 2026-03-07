package org.pecasonline.features.stock.dto

data class StockValidationResult(
    val totalLines: Int,
    val validLinesCount: Int,
    val invalidLinesCount: Int,
    val validLines: List<ValidStockLineDto>,
    val invalidLines: List<String>
)

data class ValidStockLineDto(
    val line: String,
    val code: String,
    val quantity: Int,
    val priceInCents: Long,
    val description: String
)
