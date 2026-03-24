package org.pecasonline.features.stock.history

import java.time.LocalDateTime

data class StockUploadHistoryDto(
    val id: Long,
    val supplierName: String?,
    val supplierCnpj: String?,
    val fileName: String,
    val uploadSource: UploadSource,
    val status: UploadStatus,
    val totalLinesProcessed: Int,
    val validLines: Int,
    val invalidLines: Int,
    val errorMessage: String?,
    val createdAt: LocalDateTime,
    val finishedAt: LocalDateTime?
)

fun StockUploadHistory.toDto() = StockUploadHistoryDto(
    id = this.id,
    supplierName = this.supplier?.name,
    supplierCnpj = this.supplier?.cnpj,
    fileName = this.fileName,
    uploadSource = this.uploadSource,
    status = this.status,
    totalLinesProcessed = this.totalLinesProcessed,
    validLines = this.validLines,
    invalidLines = this.invalidLines,
    errorMessage = this.errorMessage,
    createdAt = this.createdAt,
    finishedAt = this.finishedAt
)
