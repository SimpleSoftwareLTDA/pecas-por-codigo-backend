package org.pecasonline.features.stock

import io.github.oshai.kotlinlogging.KotlinLogging
import org.pecasonline.common.exceptions.NotFoundException
import org.pecasonline.common.httpclients.dto.PecaDTO
import org.pecasonline.common.isInvalidColumnSize
import org.pecasonline.common.service.OldPecasService
import org.pecasonline.features.category.Category
import org.pecasonline.features.category.ICategoryService
import org.pecasonline.features.items.Item
import org.pecasonline.features.items.ItemRepository
import org.pecasonline.features.stock.dto.StockValidationResult
import org.pecasonline.features.stock.dto.ValidStockLineDto
import org.pecasonline.features.stock.email.receiver.RegexPatterns.whitespaceRegex
import org.pecasonline.features.stock.email.sender.EmailSenderService
import org.pecasonline.features.subscription.service.SubscriptionService
import org.pecasonline.features.supplier.domain.Supplier
import org.pecasonline.features.supplier.repository.SupplierRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.streams.asSequence

private val logger = KotlinLogging.logger {}

@Service
class StockService(
    private val stockRepository: StockRepository,
    private val itemRepository: ItemRepository,
    private val supplierRepository: SupplierRepository,
    private val categoryService: ICategoryService,
    private val emailSenderService: EmailSenderService,
    private val subscriptionService: SubscriptionService,
    private val oldPecasService: OldPecasService
) : IStockService {

    override fun getAllStocks(page: Int?, size: Int?): Page<Stock> =
        stockRepository.findAll(PageRequest.of(page ?: 0, size ?: 10))

    override fun findStockById(id: Int): Stock =
        stockRepository.findById(id).orElseThrow { NotFoundException("Estoque não encontrado") }

    override fun findStockByItemDescription(description: String, page: Int?, size: Int?): Page<Stock> =
        stockRepository.findStockByItemDescriptionContainsIgnoreCase(description, PageRequest.of(page ?: 0, size ?: 10))

    override fun findStockByItemId(id: Int, page: Int?, size: Int?): Page<Stock> =
        stockRepository.findStockByItemId(id, PageRequest.of(page ?: 0, size ?: 10))

    override fun findStockByItemCode(code: String, page: Int?, size: Int?): Page<Stock> {
        val pageable = PageRequest.of(page ?: 0, size ?: 10)

        logger.info { "Buscando estoque pelo código do item: $code, página: $page, tamanho: $size" }

        val stockPage = stockRepository.findByItemCode(code, pageable)
        logger.info { "Estoque encontrado no banco de dados: ${stockPage.content.size} itens" }

        logger.info { "Nenhum DTO encontrado no site antigo. Retornando apenas os dados do banco. ${stockPage.size}" }

        return PageImpl(stockPage.content.distinctBy { it.supplier?.id }, pageable, stockPage.totalElements)
    }

    override fun findStockBySupplierId(id: Int, page: Int?, size: Int?): Page<Stock> =
        stockRepository.findStockBySupplierId(id, PageRequest.of(page ?: 0, size ?: 10))

    override fun findStockBySupplierName(name: String, page: Int?, size: Int?): Page<Stock> =
        stockRepository.findStockBySupplierNameContainsIgnoreCase(name, PageRequest.of(page ?: 0, size ?: 10))

    @Async
    override fun createStock(file: File, emailAddress: String, token: String?, cnpj: String?, originalFileName: String?) {
        val resolvedCnpj = token?.let {
            getSupplierByToken(token)
        } ?: cnpj ?: supplierRepository.findSupplierCnpjByEmail(emailAddress)

        if (resolvedCnpj == null) {
            throw IllegalArgumentException("CNPJ não encontrado para o token ou email fornecido.")
        }

        token?.let {
            if (!supplierRepository.isTokenAssociatedWithCnpj(resolvedCnpj, token)) {
                throw IllegalArgumentException("Token inválido ou não associado ao fornecedor com CNPJ: $resolvedCnpj.")
            }
        }

        val supplier = supplierRepository.findSupplierByCnpj(resolvedCnpj)
            ?: throw IllegalArgumentException("Fornecedor com CNPJ: $resolvedCnpj não encontrado.")

        subscriptionService.checkIfSubscriptionIsActiveOrThrow(supplier, resolvedCnpj)

        val dateSuffix = java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("ddMMYYYY"))
        val baseFileName = originalFileName?.substringBeforeLast(".") ?: file.name.substringBeforeLast(".")
        val extension = originalFileName?.substringAfterLast(".", "").let { if (it.isNullOrEmpty()) "" else ".$it" }
        val displayFileName = "${baseFileName}_$dateSuffix$extension"

        if (file.length() == 0L) {
            val errorMessage = "Arquivo vazio. Selecione um arquivo de estoque com dados para upload."

            emailSenderService.sendStockProcessingErrorNotification(
                supplierEmail = emailAddress,
                fileName = displayFileName,
                errorMessage = errorMessage
            )
            throw IllegalArgumentException(errorMessage)
        }

        logger.info { "Iniciando atualização de estoque para o fornecedor CNPJ: $resolvedCnpj (Arquivo: $displayFileName)" }

        emailSenderService.sendStockProcessingStartNotification(
            supplierEmail = supplier.contact.itemsEmail,
            supplierName = "${supplier.name} - ${supplier.cnpj}",
            fileName = displayFileName
        )

        try {
            val normalizedFile = autoNormalizeFile(file)
            var totalProcessed = 0
            val updatedIds = mutableSetOf<Long>()
            val invalidLines = mutableListOf<String>()
            val validStockLines = mutableListOf<Stock>()

            try {
                Files.lines(normalizedFile.toPath()).use { lines ->
                    lines.asSequence()
                        .map { it.trim() }
                        .filter { it.isNotEmpty() }
                        .forEach { line ->
                            val stockLine = parseStockLine(line)
                            if (stockLine == null) {
                                invalidLines.add(line)
                            } else {
                                validStockLines.add(stockLine)
                                if (validStockLines.size >= 1000) {
                                    processBatch(validStockLines, supplier, updatedIds)
                                    totalProcessed += validStockLines.size
                                    validStockLines.clear()
                                    logger.info { "Lote processado. Itens acumulados: $totalProcessed" }
                                }
                            }
                        }
                }
            } finally {
                if (normalizedFile.absolutePath != file.absolutePath) {
                    normalizedFile.delete()
                    logger.debug { "Arquivo normalizado temporário removido: ${normalizedFile.path}" }
                }
            }

            // Process remaining valid lines
            if (validStockLines.isNotEmpty()) {
                processBatch(validStockLines, supplier, updatedIds)
                totalProcessed += validStockLines.size
                validStockLines.clear()
            }

            logger.info { "Processamento finalizado. Válidos: $totalProcessed, Inválidos: ${invalidLines.size}" }

            var errorFile: File? = null
            if (invalidLines.isNotEmpty()) {
                errorFile = File.createTempFile("errors_", "_${file.name}")
                errorFile.writeText(invalidLines.joinToString("\n"))
                logger.info { "Gerado arquivo de erros com ${invalidLines.size} linhas: ${errorFile.path}" }
            }

            emailSenderService.sendStockProcessingCompletionNotification(
                supplierEmail = supplier.contact.itemsEmail,
                supplierName = "${supplier.name} - ${supplier.cnpj}",
                fileName = displayFileName,
                updatedItemCount = updatedIds.size,
                attachment = errorFile
            )

            logger.info { "Atualização de estoque concluída para o fornecedor CNPJ: $resolvedCnpj. Total Processado: $totalProcessed. Erros: ${invalidLines.size}" }
            
            errorFile?.let { 
                logger.debug { "Error file generated. Deletion will be handled by EmailSenderService after sending." }
            }
        } finally {
            file.delete()
            logger.info { "Arquivo temporário removido: ${file.path}" }
        }
    }

    override fun validateStockFile(file: File): StockValidationResult {
        val normalizedFile = autoNormalizeFile(file)
        val invalidLines = mutableListOf<String>()
        val validLines = mutableListOf<ValidStockLineDto>()
        var totalLines = 0

        try {
            Files.lines(normalizedFile.toPath()).use { lines ->
                lines.asSequence()
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .forEach { line ->
                        totalLines++
                        val stockLine = parseStockLine(line)
                        if (stockLine == null) {
                            invalidLines.add(line)
                        } else {
                            validLines.add(
                                ValidStockLineDto(
                                    line = line,
                                    code = stockLine.item.code,
                                    quantity = stockLine.quantity,
                                    priceInCents = stockLine.item.priceInCents ?: 0L,
                                    description = stockLine.item.description ?: ""
                                )
                            )
                        }
                    }
            }
        } finally {
            if (normalizedFile.absolutePath != file.absolutePath) {
                normalizedFile.delete()
            }
        }

        return StockValidationResult(
            totalLines = totalLines,
            validLinesCount = validLines.size,
            invalidLinesCount = invalidLines.size,
            validLines = validLines,
            invalidLines = invalidLines
        )
    }

    override fun formatStockFile(file: File, codeCol: Int, qtyCol: Int, priceCol: Int, descCol: Int, delimiter: String): File {
        val tempDir = System.getProperty("java.io.tmpdir")
        val uploadDir = File(tempDir, "meus-arquivos-temporarios").apply { mkdirs() }
        val formattedFile = File(uploadDir, "formatted_stock_${java.util.UUID.randomUUID()}.txt")

        val actualDelimiter = if (delimiter == "\\t" || delimiter == "tab") "\t" else delimiter

        formattedFile.bufferedWriter(Charsets.UTF_8).use { writer ->
            Files.lines(file.toPath()).use { lines ->
                lines.asSequence()
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .forEach { line ->
                        val columns = line.split(actualDelimiter)
                        if (columns.size > maxOf(codeCol, qtyCol, priceCol, descCol)) {
                            val code = columns[codeCol].trim()
                            val qty = columns[qtyCol].trim()
                            val price = columns[priceCol].trim()
                            val desc = columns[descCol].trim()
                            
                            if (code.isNotEmpty() && desc.isNotEmpty()) {
                                writer.write("$code;$qty;$price;$desc\n")
                            }
                        }
                    }
            }
        }
        return formattedFile
    }

    private fun autoNormalizeFile(originalFile: File): File {
        val lines = originalFile.readLines().map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.isEmpty()) return originalFile // empty

        val delimiters = listOf(";", "\t", ",", "|")
        var bestDelimiter = ";"
        var maxConsistency = 0

        for (delimiter in delimiters) {
            val counts = lines.take(20).map { splitCsvLine(it, delimiter[0]).size }
            val commonCountEntry = counts.groupBy { it }.maxByOrNull { it.value.size }
            if (commonCountEntry != null && commonCountEntry.key >= 3) {
                if (commonCountEntry.value.size > maxConsistency) {
                    maxConsistency = commonCountEntry.value.size
                    bestDelimiter = delimiter
                }
            }
        }

        val tempDir = System.getProperty("java.io.tmpdir")
        val uploadDir = File(tempDir, "meus-arquivos-temporarios").apply { mkdirs() }
        val normalizedFile = File(uploadDir, "normalized_stock_${java.util.UUID.randomUUID()}.txt")

        normalizedFile.bufferedWriter(Charsets.UTF_8).use { writer ->
            for (line in lines) {
                val cols = splitCsvLine(line, bestDelimiter[0])
                if (cols.size >= 3) {
                    writer.write(cols.joinToString(";") + "\n")
                } else {
                    writer.write(line + "\n")
                }
            }
        }

        logger.info { "Arquivo normalizado automaticamente utilizando delimitador '$bestDelimiter'." }
        return normalizedFile
    }

    private fun splitCsvLine(line: String, delimiter: Char): List<String> {
        val result = mutableListOf<String>()
        var current = StringBuilder()
        var inQuotes = false
        for (char in line) {
            if (char == '\"') {
                inQuotes = !inQuotes
            } else if (char == delimiter && !inQuotes) {
                result.add(current.toString().trim())
                current = StringBuilder()
            } else {
                current.append(char)
            }
        }
        result.add(current.toString().trim())
        return result
    }

    private fun processBatch(batchItems: List<Stock>, supplier: Supplier, updatedIds: MutableSet<Long>) {
        val items = batchItems.map { it.item }
        val processedItemsMap = processAllItems(items)

        val itemCodes = processedItemsMap.values.map { it.code }
        val existingStocks = stockRepository.findBySupplierIdAndItemCodeIn(supplier.id!!, itemCodes)
        val existingStocksMap = existingStocks.associateBy { it.item.code }

        val updatedStocks = mutableListOf<Stock>()
        val newStocks = mutableListOf<Stock>()

        batchItems.forEach { stockLine ->
            val itemProcessed = processedItemsMap[stockLine.item.hash]
                ?: return@forEach

            val existingStock = existingStocksMap[itemProcessed.code]

            if (existingStock != null) {
                updatedStocks.add(existingStock.copy(quantity = stockLine.quantity))
            } else {
                newStocks.add(stockLine.copy(item = itemProcessed, supplier = supplier))
            }
        }

        if (updatedStocks.isNotEmpty()) {
            val saved = stockRepository.saveAll(updatedStocks)
            saved.mapNotNull { it.id }.let { updatedIds.addAll(it) }
        }
        if (newStocks.isNotEmpty()) {
            val saved = stockRepository.saveAll(newStocks)
            saved.mapNotNull { it.id }.let { updatedIds.addAll(it) }
        }
    }

    private fun processAllItems(allItems: List<Item>): Map<String, Item> {
        val itemsByHash = allItems.associateBy { it.hash }
        val allHashes = itemsByHash.keys

        if (allHashes.isEmpty()) return emptyMap()

        // Fetch existing items in batches to avoid huge IN() queries that can hang or be very slow
        val fetchBatchSize = 1000
        val existingList = mutableListOf<Item>()
        allHashes.chunked(fetchBatchSize).forEachIndexed { idx, chunk ->
            val fetched = itemRepository.findAllByHashIn(chunk)
            existingList += fetched
            if (idx % 10 == 0) {
                logger.info { "processAllItems: fetched batch ${idx + 1}, size=${chunk.size}, accumulatedExisting=${existingList.size}" }
            }
        }

        // Build map defensively to avoid crashes if legacy rows have null/blank hashes
        val existingMap = mutableMapOf<String, Item>()
        var skippedNullOrBlank = 0

        existingList.forEach { itm ->
            try {
                val h = itm.hash
                if (h.isNotBlank()) {
                    existingMap[h] = itm
                } else {
                    skippedNullOrBlank++
                }
            } catch (e: NullPointerException) {
                // Some legacy records may have null hash despite Kotlin non-null type
                skippedNullOrBlank++
            }
        }

        if (skippedNullOrBlank > 0) {
            logger.info { "processAllItems: skipped $skippedNullOrBlank item(s) with null/blank hash from existingList" }
        }

        val newHashes = allHashes - existingMap.keys
        if (newHashes.isEmpty()) return existingMap

        val newItems = newHashes.mapNotNull { hash -> itemsByHash[hash] }
        if (newItems.isEmpty()) return existingMap

        // Save new items in smaller batches to reduce persistence overhead
        val saveBatchSize = 500
        val savedNewMap = mutableMapOf<String, Item>()
        newItems.chunked(saveBatchSize).forEachIndexed { idx, chunk ->
            val saved = itemRepository.saveAll(chunk)
            saved.forEach { savedNewMap[it.hash] = it }
            logger.debug { "processAllItems: saved batch ${idx + 1}, size=${chunk.size}, accumulatedSaved=${savedNewMap.size}" }
        }

        return existingMap + savedNewMap
    }

    private fun getOrCreateCategory(description: String?): Category {
        val safeDesc = description?.trim().takeUnless { it.isNullOrEmpty() } ?: "Uncategorized"
        val formattedName = safeDesc.replaceFirstChar { it.uppercaseChar() }

        val existing = categoryService.findByNameIgnoreCase(formattedName)

        if (existing != null) return existing

        val newCat = categoryService.addCategory(Category(name = formattedName))

        logger.debug { "Criando nova categoria: ${newCat.name}" }

        return newCat
    }

    private fun getSupplierByToken(token: String): String =
        supplierRepository.findCnpjByToken(token)
            ?: throw NotFoundException("Fornecedor não encontrado a partir desse token")

    private fun saveTempFile(file: MultipartFile, tmpDir: Path): Path =
        Files.createTempFile(tmpDir, "tmp_", "_${file.originalFilename ?: "unknown"}").also {
            Files.copy(file.inputStream, it, StandardCopyOption.REPLACE_EXISTING)
        }

    internal fun parseStockLine(line: String): Stock? {
        val trimmedLine = line.trim()
        if (trimmedLine.isEmpty()) return null

        val columns: List<String>
        if (trimmedLine.contains(';')) {
            columns = trimmedLine.split(';').map { it.trim() }
        } else if (trimmedLine.contains('\t')) {
            columns = trimmedLine.split('\t').map { it.trim() }
        } else {
            // Regex Option 1: Safely extracts exactly 4 groups correctly regardless of space lengths
            // Group 1: Code (starts with non-white, until quantity)
            // Group 2: Quantity (digits, dot, commas - e.g. 2.467)
            // Group 3: Price (digits, dot, commas)
            // Group 4: Description (everything else)
            val match = Regex("^(\\S.*?)\\s+([\\d.,]+)\\s+([\\d.,]+)(?:\\s+(.*))?$").find(trimmedLine)
            columns = if (match != null) {
                listOf(
                    match.groupValues[1].trim(),
                    match.groupValues[2].trim(),
                    match.groupValues[3].trim(),
                    match.groupValues[4].trim()
                )
            } else {
                emptyList()
            }
        }

        if (columns.size < 4) return null

        val code = columns[0]

        val quantityStr = columns[1]
        val priceStr = columns[2]
        val description = columns[3]
        
        // Detect scientific notation corruption (e.g., "7,90E+12"). 
        // These are lossy conversions from Excel and should be treated as errors.
        if (code.contains("E+", ignoreCase = true) || code.isEmpty() || description.isEmpty()) {
            return null
        }

        val quantity = quantityStr.toIntOrNull() ?: 0
        val priceInCents = if (priceStr.isBlank()) 0L else parseMonetaryToCents(priceStr)

        val item = Item.buildFromMinimalProperties(
            code = code,
            priceInCents = priceInCents,
            description = description
        )

        return Stock(quantity = quantity, item = item)
    }

    /**
     * Parses monetary values with thousand/decimal separators in either style:
     * - "1,005.47" (US style)
     * - "1.005,47" (BR/EU style)
     * - "999.86", "999,86", "1000", "1.000"
     * Returns the value in cents as Long, using robust, locale-agnostic rules:
     * - Consider the last separator ('.' or ',') a decimal separator only if it has exactly 2 digits after it.
     * - Otherwise, treat all separators as thousands separators (no decimals) and multiply by 100.
     */
    private fun parseMonetaryToCents(raw: String): Long {
        if (raw.isBlank()) return 0L

        // Keep only digits and separators to simplify handling; remove currency symbols and spaces
        val cleaned = raw.trim()
            .replace("\u00A0", "") // non-breaking space
            .filter { it.isDigit() || it == ',' || it == '.' }

        if (cleaned.isEmpty()) return 0L

        // Find the last separator and decide if it's a decimal separator (exactly 2 digits after it)
        var lastSepIndex = -1
        var lastSepChar = '\u0000'
        for (i in cleaned.length - 1 downTo 0) {
            val ch = cleaned[i]
            if (ch == ',' || ch == '.') {
                lastSepIndex = i
                lastSepChar = ch
                break
            }
        }

        val hasDecimal = if (lastSepIndex != -1) {
            val digitsAfter = cleaned.substring(lastSepIndex + 1).count { it.isDigit() }
            digitsAfter == 2
        } else false

        val digitsOnly = cleaned.filter { it.isDigit() }
        if (digitsOnly.isEmpty()) return 0L

        return try {
            val base = digitsOnly.toLong()
            if (hasDecimal) base else base * 100
        } catch (e: NumberFormatException) {
            // In case the number is too large for Long (unlikely for prices), fall back to 0
            0L
        }
    }
}

fun parseFornecedor(descricao: String): Triple<String, String, String> {
    // Regex que pega:
    //  ^ => início da string
    //  (.+?) => captura nome até encontrar espaço + parêntese (modo não guloso)
    //  \s*\( => ignora espaços opcionais e abre parêntese
    //  ([^)]*) => captura tudo até fechar parêntese
    //  \)\s+ => fecha parêntese e ignora espaços
    //  (.+)$ => captura o restante (telefone)
    val regex = Regex("""^(.+?)\s*\(([^)]*)\)\s+(.+)$""")

    val matchResult = regex.matchEntire(descricao)

    return if (matchResult != null) {
        val (nome, cidadeUf, telefone) = matchResult.destructured
        Triple(nome.trim(), cidadeUf.trim(), telefone.trim())
    } else {
        // Se não bater o padrão, retornamos tudo no primeiro campo
        // ou tratamos de outra forma
        Triple(descricao, "", "")
    }
}

private fun getItemWithPriceInCents(dto: PecaDTO): Item {
    val precoStr = dto.preco ?: ""

    val precoEmCentavos = if (precoStr.isBlank() || precoStr == "Valor não informado") {
        0L
    } else precoStr.replace(",", ".").toLongOrNull() ?: 0L

    val item = Item(
        code = dto.codigo,
        hash = "",
        description = dto.descricao,
        priceInCents = precoEmCentavos
    )
    return item
}
