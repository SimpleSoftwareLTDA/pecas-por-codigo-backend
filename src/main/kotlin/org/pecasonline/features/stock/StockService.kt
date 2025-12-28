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
import org.pecasonline.features.stock.email.receiver.RegexPatterns.whitespaceRegex
import org.pecasonline.features.stock.email.sender.EmailSenderService
import org.pecasonline.features.subscription.service.SubscriptionService
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
    override fun createStock(file: File, emailAddress: String, token: String?, cnpj: String?) {
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

        subscriptionService.checkIfSubscriptionIsActiveOrThrow(supplier, resolvedCnpj)

        if (file.length() == 0L) {
            val errorMessage = "Arquivo vazio. Selecione um arquivo de estoque com dados para upload."

            emailSenderService.sendStockProcessingErrorNotification(
                supplierEmail = emailAddress,
                fileName = file.name,
                errorMessage = errorMessage
            )
            throw IllegalArgumentException(errorMessage)
        }

        logger.info { "Iniciando atualização de estoque para o fornecedor CNPJ: $resolvedCnpj" }

        emailSenderService.sendStockProcessingStartNotification(
            supplierEmail = supplier.contact.itemsEmail,
            supplierName = "${supplier.name} - ${supplier.cnpj}",
            fileName = file.name
        )

        try {
            var totalProcessed = 0
            val updatedIds = mutableSetOf<Long>()

            Files.lines(file.toPath()).use { lines ->
                lines.asSequence()
                    .map { it.trim() }
                    .filter { it.isNotEmpty() }
                    .mapNotNull { parseStockLine(it) }
                    .chunked(1000)
                    .forEachIndexed { index, batchItems ->
                        val items = batchItems.map { it.item }
                        val processedItemsMap = processAllItems(items)

                        val itemCodes = processedItemsMap.values.map { it.code }
                        val existingStocks = stockRepository.findBySupplierIdAndItemCodeIn(supplier.id!!, itemCodes)
                        val existingStocksMap = existingStocks.associateBy { it.item.code }

                        val updatedStocks = mutableListOf<Stock>()
                        val newStocks = mutableListOf<Stock>()

                        batchItems.forEach { stockLine ->
                            val itemProcessed = processedItemsMap[stockLine.item.hash]
                                ?: return@forEach // Should not happen if processAllItems parses correctly

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

                        totalProcessed += batchItems.size
                        if (index % 10 == 0) {
                            logger.info { "Lote $index processado. Itens acumulados: $totalProcessed" }
                        }
                    }
            }

            emailSenderService.sendStockProcessingCompletionNotification(
                supplierEmail = supplier.contact.itemsEmail,
                supplierName = "${supplier.name} - ${supplier.cnpj}",
                fileName = file.name,
                updatedItemCount = updatedIds.size
            )

            logger.info { "Atualização de estoque concluída para o fornecedor CNPJ: $resolvedCnpj. Total Processado: $totalProcessed" }
        } finally {
            file.delete()
            logger.info { "Arquivo temporário removido: ${file.path}" }
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

    private fun parseStockLine(line: String): Stock? {
        // Split by tab (\t) or semicolon (;) only — not spaces — to preserve prices like "89,427.27"
        // Split by tab (\t), semicolon (;), or multiple spaces (2+) to handle fixed-width-like files
        val columns = line.split(Regex("[\\t;]+|\\s{2,}")).filter { it.isNotEmpty() }

        if (columns.isInvalidColumnSize()) return null

        val (code, quantityStr, priceStr, description) = columns
        val quantity = quantityStr.toIntOrNull() ?: 0
        val priceInCents = parseMonetaryToCents(priceStr)

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
