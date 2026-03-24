package org.pecasonline.features.stock.history

import jakarta.persistence.*
import org.pecasonline.features.supplier.domain.Supplier
import java.time.LocalDateTime

@Entity
@Table(name = "stock_upload_history")
class StockUploadHistory(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    var supplier: Supplier? = null,

    @Column(name = "file_name", nullable = false)
    val fileName: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "upload_source", nullable = false)
    val uploadSource: UploadSource,

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    var status: UploadStatus,

    @Column(name = "total_lines_processed")
    var totalLinesProcessed: Int = 0,

    @Column(name = "valid_lines")
    var validLines: Int = 0,

    @Column(name = "invalid_lines")
    var invalidLines: Int = 0,

    @Column(name = "error_message")
    var errorMessage: String? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "finished_at")
    var finishedAt: LocalDateTime? = null
)

enum class UploadSource {
    EMAIL,
    API,
    ADMIN_PANEL
}

enum class UploadStatus {
    PROCESSING,
    SUCCESS,
    PARTIAL_SUCCESS,
    FAILED
}
