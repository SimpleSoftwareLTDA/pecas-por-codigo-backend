package org.pecasonline.features.stock

import io.github.oshai.kotlinlogging.KotlinLogging
import org.jsoup.nodes.Document
import org.pecasonline.common.exceptions.NotFoundException
import org.pecasonline.common.httpclients.dto.PecaDTO
import org.pecasonline.common.isInvalidColumnSize
import org.pecasonline.common.parseResultadoPesquisa
import org.pecasonline.common.service.OldPecasService
import org.pecasonline.features.address.domain.Address
import org.pecasonline.features.address.domain.BrazilianState
import org.pecasonline.features.category.Category
import org.pecasonline.features.category.ICategoryService
import org.pecasonline.features.items.Item
import org.pecasonline.features.items.ItemRepository
import org.pecasonline.features.stock.email.receiver.RegexPatterns.whitespaceRegex
import org.pecasonline.features.stock.email.sender.EmailSenderService
import org.pecasonline.features.subscription.service.SubscriptionService
import org.pecasonline.features.supplier.domain.Contact
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

        return PageImpl(stockPage.content.distinctBy { it.item.hash }, pageable, stockPage.totalElements)
    }

    override fun findStockBySupplierId(id: Int, page: Int?, size: Int?): Page<Stock> =
        stockRepository.findStockBySupplierId(id, PageRequest.of(page ?: 0, size ?: 10))

    override fun findStockBySupplierName(name: String, page: Int?, size: Int?): Page<Stock> =
        stockRepository.findStockBySupplierNameContainsIgnoreCase(name, PageRequest.of(page ?: 0, size ?: 10))

    @Async
    override fun createStock(file: File, emailAddress: String, token: String?, cnpj: String?) {
        val cnpj = token?.let {
            getSupplierByToken(token)
        } ?: cnpj ?: supplierRepository.findSupplierCnpjByEmail(emailAddress)

        if (cnpj == null) {
            throw IllegalArgumentException("CNPJ não encontrado para o token ou email fornecido.")
        }

        token?.let {
            if (!supplierRepository.isTokenAssociatedWithCnpj(cnpj, token)) {
                throw IllegalArgumentException("Token inválido ou não associado ao fornecedor com CNPJ: $cnpj.")
            }
        }

        val supplier = supplierRepository.findSupplierByCnpj(cnpj)

        subscriptionService.checkIfSubscriptionIsActiveOrThrow(supplier, cnpj)

        if (file.length() == 0L) {
            val errorMessage = "Arquivo vazio. Selecione um arquivo de estoque com dados para upload."

            emailSenderService.sendStockProcessingErrorNotification(
                supplierEmail = emailAddress,
                fileName = file.name,
                errorMessage = errorMessage
            )
            throw IllegalArgumentException(errorMessage)
        }

        logger.info { "Iniciando atualização de estoque para o fornecedor CNPJ: $cnpj" }

        emailSenderService.sendStockProcessingStartNotification(
            supplierEmail = supplier.contact.itemsEmail,
            supplierName = "${supplier.name} - ${supplier.cnpj}",
            fileName = file.name
        )

        try {
            val (newStockItems, unprocessedLines) = getFileValuesWithDiagnostics(file)
            logger.info { "Total de itens válidos no arquivo: ${newStockItems.size}" }
            if (unprocessedLines.isNotEmpty()) {
                val report = writeUnprocessedCsv(unprocessedLines, file.name)
                logger.warn { "${unprocessedLines.size} linha(s) não puderam ser processadas. Relatório: ${report?.absolutePath}" }
            }

            // 1) Extrai todos os itens do arquivo e processa em lote (batch)
            val allItemsFromFile = newStockItems.map { it.item }
            val processedItemsMap = processAllItems(allItemsFromFile)

            // 2) Busca os estoques existentes para este fornecedor
            val existingStocks = stockRepository.findStocksBySupplierId(supplier.id!!)
            val existingStocksMap = existingStocks.associateBy { it.item.code }

            // 3) Separa as listas de atualização e criação
            val updatedStocks = mutableListOf<Stock>()
            val newStocks = mutableListOf<Stock>()
            val updatedIds = mutableSetOf<Long>()

            newStockItems.forEachIndexed { index, stockLine ->
                val itemProcessed = processedItemsMap[stockLine.item.hash]
                    ?: error("Item com hash=${stockLine.item.hash} não foi processado corretamente")

                val existingStock = existingStocksMap[itemProcessed.code]

                when {
                    existingStock != null -> {
                        val updatedStock = existingStock.copy(quantity = stockLine.quantity)
                        updatedStocks.add(updatedStock)
                    }

                    else -> newStocks.add(stockLine.copy(item = itemProcessed, supplier = supplier))
                }
                logger.debug { "Processando linha ${index + 1} de ${newStockItems.size}, item code=${stockLine.item.code}, hash=${stockLine.item.hash}" }
            }

            if (updatedStocks.isNotEmpty()) {
                val batchSize = 10_000
                updatedStocks.chunked(batchSize).forEachIndexed { index, batch ->
                    val savedUpdated = stockRepository.saveAll(batch)
                    savedUpdated.mapNotNull { it.id }.let { updatedIds.addAll(it) }

                    logger.info { "Lote: $index -> Foram atualizados ${savedUpdated.size} registros de estoque para o fornecedor ID=${supplier.id}" }
                }
            }

            if (newStocks.isNotEmpty()) {
                val batchSize = 10_000
                val savedIds = mutableSetOf<Long>()

                newStocks.chunked(batchSize).forEach { batch ->
                    val savedBatch = stockRepository.saveAll(batch)
                    savedBatch.mapNotNull { it.id }.let { savedIds.addAll(it) }
                    logger.info { "Vai salvar ${savedBatch.size} no banco." }
                }

                updatedIds.addAll(savedIds)
                logger.info { "Total de novos registros salvos: ${savedIds.size} para o fornecedor ID=${supplier.id}" }
            }

            emailSenderService.sendStockProcessingCompletionNotification(
                supplierEmail = supplier.contact.itemsEmail,
                supplierName = "${supplier.name} - ${supplier.cnpj}",
                fileName = file.name,
                updatedItemCount = updatedIds.size
            )

            logger.info { "Atualização de estoque concluída para o fornecedor CNPJ: $cnpj" }
        } finally {
            file.delete()
            logger.info { "Arquivo temporário removido: ${file.path}" }
        }
    }

    private fun processAllItems(allItems: List<Item>): Map<String, Item> {
        val itemsByHash = allItems.associateBy { it.hash }
        val allHashes = itemsByHash.keys

        val existing = itemRepository.findAllByHashIn(allHashes)

        val existingMap = existing.associateBy { it.hash }

        val newHashes = allHashes - existingMap.keys

        val newItems = newHashes.mapNotNull { hash ->
            itemsByHash[hash]
        }

        val savedNewItems = itemRepository.saveAll(newItems)
        val savedNewMap = savedNewItems.associateBy { it.hash }

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

    private data class UnprocessedLine(val lineNumber: Int, val reason: String, val rawLine: String)

    private fun getFileValuesWithDiagnostics(file: File): Pair<List<Stock>, List<UnprocessedLine>> =
        Files.lines(file.toPath()).use { lines ->
            val successes = mutableListOf<Stock>()
            val failures = mutableListOf<UnprocessedLine>()
            lines.asSequence().forEachIndexed { idx, raw ->
                val lineNumber = idx + 1
                val trimmed = raw.trim()
                if (trimmed.isEmpty()) return@forEachIndexed
                val result = runCatching { parseStockLine(trimmed) }
                result.onFailure { ex ->
                    failures.add(UnprocessedLine(lineNumber, "exception: ${ex.message}", raw))
                }
                result.onSuccess { parsed ->
                    if (parsed == null) {
                        failures.add(UnprocessedLine(lineNumber, "unsupported/empty columns", raw))
                    } else {
                        successes.add(parsed)
                    }
                }
            }
            successes to failures
        }

    private fun writeUnprocessedCsv(unprocessed: List<UnprocessedLine>, originalName: String): File? {
        if (unprocessed.isEmpty()) return null
        val uploadsDir = File("uploads")
        if (!uploadsDir.exists()) uploadsDir.mkdirs()
        val timestamp = java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss").format(java.time.LocalDateTime.now())
        val safeName = originalName.replace(Regex("[^A-Za-z0-9._-]"), "_")
        val outFile = File(uploadsDir, "${safeName.removeSuffix(".csv")}-unprocessed-${timestamp}.csv")
        val header = "lineNumber;reason;rawLine"
        val rows = unprocessed.joinToString(System.lineSeparator()) { rec ->
            val reason = rec.reason.replace("\r", " ").replace("\n", " ")
            val rawEscaped = rec.rawLine.replace("\r", " ").replace("\n", " ")
            "${rec.lineNumber};${reason};${rawEscaped}"
        }
        outFile.writeText(header + System.lineSeparator() + rows, Charsets.UTF_8)
        return outFile
    }


    private fun parseStockLine(line: String): Stock? {
        val rawColumns = when {
            line.contains(";") -> {
                // Preserve empty fields when semicolon-delimited (e.g., code;qty;;description)
                line.split(";").map { it.trim() }
            }
            line.contains("\t") -> {
                // Preserve empty fields for tab-delimited files
                line.split("\t").map { it.trim() }
            }
            !line.contains(";") && !line.contains("\t") && line.contains(",") -> {
                // Preserve empty fields for comma-delimited files
                // Note: In files where comma is used as decimal separator, semicolon is typically used as delimiter.
                // We only treat comma as delimiter when there are no semicolons or tabs.
                line.split(",").map { it.trim() }
            }
            else -> {
                // For whitespace-delimited, drop empties
                line.split(whitespaceRegex).map { it.trim() }.filter { it.isNotEmpty() }
            }
        }

        if (rawColumns.isEmpty()) return null

        val code = rawColumns[0]
        val second = rawColumns.getOrNull(1)
        val third = rawColumns.getOrNull(2)

        fun String.toIntFromFlexible(): Int? {
            val cleaned = this.trim()
            // Accept formats like 96,00 or 96.00 or 96
            val normalized = cleaned.replace(" ", "").replace(".", "").replace(",", ".")
            val asDouble = normalized.toDoubleOrNull() ?: return null
            val rounded = asDouble.toLong()
            return if (kotlin.math.abs(asDouble - rounded.toDouble()) < 1e-9) rounded.toInt() else null
        }
        fun String.isQtyLike(): Boolean = this.toIntFromFlexible() != null
        fun String.normalizePrice(): String = this
            .replace("R$", "", ignoreCase = true)
            .replace(" ", "")
            .replace(".", "") // remove thousands separators like 1.234,56
            .replace(",", ".")
        fun String.isPriceLike(): Boolean {
            val norm = this.normalizePrice()
            return norm.toDoubleOrNull() != null
        }

        // Determine description now that helpers are available
        val description = when {
            rawColumns.size >= 4 -> rawColumns.drop(3).joinToString(" ").trim()
            rawColumns.size == 2 && !(second?.let { it.isQtyLike() || it.isPriceLike() } ?: false) -> second?.trim() ?: ""
            rawColumns.size == 3 && !((second?.let { it.isQtyLike() || it.isPriceLike() } ?: false) || (third?.let { it.isQtyLike() || it.isPriceLike() } ?: false)) -> listOfNotNull(second, third).joinToString(" ").trim()
            else -> ""
        }

        // Detect which column is quantity and which is price
        val (quantityStr, priceStr) = when {
            second?.isQtyLike() == true && third?.isPriceLike() == true -> second to third
            second?.isPriceLike() == true && third?.isQtyLike() == true -> third to second
            // If only one numeric field exists, assign accordingly
            second?.isQtyLike() == true && third == null -> second to null
            second?.isPriceLike() == true && third == null -> null to second
            third?.isQtyLike() == true && second == null -> third to null
            third?.isPriceLike() == true && second == null -> null to third
            // Fallbacks: interpret second as qty, third as price (may be null)
            else -> second to third
        }

        val quantity = quantityStr?.toIntFromFlexible() ?: 0
        val priceDouble = priceStr?.normalizePrice()?.toDoubleOrNull() ?: 0.0
        val priceInCents = (priceDouble * 100).toLong()

        val item = Item.buildFromMinimalProperties(
            code = code,
            priceInCents = priceInCents,
            description = description
        )

        return Stock(quantity = quantity, item = item)
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

private fun getPecasFromOldSite(dtos: List<PecaDTO>): List<Stock> {
    val stocksFromHtml = dtos.map { dto ->
        val (nomeFornecedor, cidadeUf, phoneNumber) = parseFornecedor(dto.fornecedor)

        val lastDashIndex = cidadeUf.lastIndexOf("-")
        val (city, uf) = (if (lastDashIndex >= 0) {
            val cityPart = cidadeUf.substring(0, lastDashIndex).trim()
            val ufPart = cidadeUf.substring(lastDashIndex + 1).trim()

            cityPart to ufPart
        } else cidadeUf to "SP")

        val dynamicState = BrazilianState(
            stateCode = uf,
            stateName = city
        )

        val address = Address(
            street = "Consulte por telefone",
            city = city,
            state = dynamicState,
            cep = "01000-000",
            country = "Brasil"
        )

        val supplier = Supplier(
            name = nomeFornecedor,
            socialName = nomeFornecedor,
            cnpj = "",
            address = address,
            contact = Contact(
                sellerName = phoneNumber,
                itemsEmail = phoneNumber,
                itemsPhone = phoneNumber,
                whatsapp = phoneNumber,
                itemsWhatsapp = phoneNumber
            )
        )

        Stock(
            quantity = dto.qtd,
            supplier = supplier,
            item = getItemWithPriceInCents(dto)
        )
    }
    return stocksFromHtml
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
