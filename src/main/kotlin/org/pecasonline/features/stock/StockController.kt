package org.pecasonline.features.stock

import io.micrometer.core.instrument.MeterRegistry
import io.swagger.v3.oas.annotations.tags.Tag
import org.pecasonline.common.Constants.BASE_ENDPOINT
import org.pecasonline.features.stock.swagger.StockSwaggerSpec
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.*

import org.hibernate.validator.constraints.br.CNPJ
@Validated
@RestController
@RequestMapping("$BASE_ENDPOINT/estoque")
@Tag(name = "Estoque", description = "Operações relacionadas ao estoque")
class StockController(
    val stockService: IStockService,
    private val meterRegistry: MeterRegistry,
    private val stringRedisTemplate: org.springframework.data.redis.core.StringRedisTemplate,
    private val objectMapper: com.fasterxml.jackson.databind.ObjectMapper
) : StockSwaggerSpec {

    @GetMapping
    override fun stock(
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ) = stockService.getAllStocks(page, size)

    @GetMapping("/{id}")
    override fun findStockById(
        @PathVariable("id") id: Int
    ) = stockService.findStockById(id)

    @GetMapping("/item")
    override fun findStockByItemDescription(
        @RequestParam("descricao") descricao: String,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ) = stockService.findStockByItemDescription(descricao, page, size).also {
        meterRegistry.counter("stock.search.description", "description", descricao).increment()
    }

    @GetMapping("/item/{id}")
    override fun findStockByItemId(
        @PathVariable("id") id: Int,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ) = stockService.findStockByItemId(id, page, size)

    @GetMapping("/codigo/{codigo}")
    override fun findStockByItemCode(
        @PathVariable("codigo") code: String,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ) = stockService.findStockByItemCode(code, page, size).also {
        meterRegistry.counter("stock.search.code", "code", code).increment()
    }

    @GetMapping("/fornecedor/{id}")
    override fun findStockBySupplierId(
        @PathVariable("id") id: Int,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ) = stockService.findStockBySupplierId(id, page, size).also {
        meterRegistry.counter("stock.search.supplierId", "id", id.toString()).increment()
    }

    @GetMapping("/fornecedor")
    override fun findStockBySupplierName(
        @RequestParam("nome") name: String,
        @RequestParam("page") page: Int?,
        @RequestParam("size") size: Int?
    ) = stockService.findStockBySupplierName(name, page, size).also {
        meterRegistry.counter("stock.search.supplierName", "name", name).increment()
    }

    @PostMapping(consumes = ["multipart/form-data"])
    @ResponseStatus(HttpStatus.ACCEPTED)
    override fun createItem(
        @RequestPart file: MultipartFile,
        @RequestParam token: String
    ) {
        MDC.putCloseable("token", token).use {
            MDC.putCloseable("tid", UUID.randomUUID().toString()).use {
                val tempDir = System.getProperty("java.io.tmpdir")

                val uploadDir = File(tempDir, "meus-arquivos-temporarios").apply { mkdirs() }
                val tempFile = File(uploadDir, "upload_${UUID.randomUUID()}.tmp")

                // Convert uploaded content to UTF-8 to avoid issues with ANSI encodings
                val utf8Bytes = org.pecasonline.common.encoding.EncodingUtils.toUtf8Bytes(file.bytes)
                tempFile.outputStream().use { it.write(utf8Bytes) }

                stockService.createStock(file = tempFile, token = token, originalFileName = file.originalFilename)
            }
        }
    }

    @PostMapping(path = ["/estoque-by-cnpj"], consumes = ["multipart/form-data"])
    @ResponseStatus(HttpStatus.ACCEPTED)
    override fun createItemStockByCNPJ(
        @RequestPart file: MultipartFile,
        @RequestParam cnpj: String
    ) {
        val normalizedCnpj = org.pecasonline.common.formatCnpj(cnpj)
        MDC.putCloseable("token", normalizedCnpj).use {
            MDC.putCloseable("tid", UUID.randomUUID().toString()).use {
                val tempDir = System.getProperty("java.io.tmpdir")

                val uploadDir = File(tempDir, "meus-arquivos-temporarios").apply { mkdirs() }
                val tempFile = File(uploadDir, "upload_${UUID.randomUUID()}.tmp")

                // Convert uploaded content to UTF-8 to avoid issues with ANSI encodings
                val utf8Bytes = org.pecasonline.common.encoding.EncodingUtils.toUtf8Bytes(file.bytes)
                tempFile.outputStream().use { it.write(utf8Bytes) }

                stockService.createStock(file = tempFile, cnpj = normalizedCnpj, originalFileName = file.originalFilename)
            }
        }
    }

    @PostMapping(path = ["/validate"], consumes = ["multipart/form-data"])
    @ResponseStatus(HttpStatus.OK)
    override fun validateStockFile(
        @RequestPart file: MultipartFile
    ): org.pecasonline.features.stock.dto.StockValidationResult {
        val fileBytes = file.bytes
        val fileHash = java.security.MessageDigest.getInstance("SHA-256")
            .digest(fileBytes)
            .joinToString("") { "%02x".format(it) }
        
        val cacheKey = "ppc:stock_validation:$fileHash"

        // Check if validation result is already cached in Redis
        val cachedContent = stringRedisTemplate.opsForValue().get(cacheKey)
        if (cachedContent != null) {
            org.slf4j.LoggerFactory.getLogger(StockController::class.java).info("Validating stock file via Redis cache HIT. (Hash: $fileHash)")
            return objectMapper.readValue(cachedContent, org.pecasonline.features.stock.dto.StockValidationResult::class.java)
        }

        org.slf4j.LoggerFactory.getLogger(StockController::class.java).info("Validating stock file natively (Cache MISS).")
        val tempDir = System.getProperty("java.io.tmpdir")
        val uploadDir = java.io.File(tempDir, "meus-arquivos-temporarios").apply { mkdirs() }
        val tempFile = java.io.File(uploadDir, "upload_validate_${java.util.UUID.randomUUID()}.tmp")

        try {
            val utf8Bytes = org.pecasonline.common.encoding.EncodingUtils.toUtf8Bytes(fileBytes)
            tempFile.outputStream().use { it.write(utf8Bytes) }

            val result = stockService.validateStockFile(tempFile)

            // Save to cache for 1 hour
            stringRedisTemplate.opsForValue().set(
                cacheKey,
                objectMapper.writeValueAsString(result),
                java.time.Duration.ofHours(1)
            )

            return result
        } finally {
            tempFile.delete()
        }
    }

    @PostMapping(path = ["/format"], consumes = ["multipart/form-data"], produces = ["text/csv"])
    @ResponseStatus(HttpStatus.OK)
    override fun formatStockFile(
        @RequestPart file: MultipartFile,
        @RequestParam codeCol: Int,
        @RequestParam qtyCol: Int,
        @RequestParam priceCol: Int,
        @RequestParam descCol: Int,
        @RequestParam(defaultValue = ";") delimiter: String
    ): org.springframework.http.ResponseEntity<org.springframework.core.io.Resource> {
        val tempDir = System.getProperty("java.io.tmpdir")
        val uploadDir = File(tempDir, "meus-arquivos-temporarios").apply { mkdirs() }
        val tempFile = File(uploadDir, "upload_format_${UUID.randomUUID()}.tmp")

        try {
            val utf8Bytes = org.pecasonline.common.encoding.EncodingUtils.toUtf8Bytes(file.bytes)
            tempFile.outputStream().use { it.write(utf8Bytes) }

            val formattedFile = stockService.formatStockFile(tempFile, codeCol, qtyCol, priceCol, descCol, delimiter)

            val resource = org.springframework.core.io.FileSystemResource(formattedFile)
            val headers = org.springframework.http.HttpHeaders()
            headers.add(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"formatted_stock.txt\"")

            return org.springframework.http.ResponseEntity.ok()
                .headers(headers)
                .contentLength(formattedFile.length())
                .contentType(org.springframework.http.MediaType.parseMediaType("text/csv"))
                .body(resource)
        } finally {
            tempFile.delete()
        }
    }
}
