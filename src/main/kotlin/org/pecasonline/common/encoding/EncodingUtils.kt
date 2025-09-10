package org.pecasonline.common.encoding

import java.nio.ByteBuffer
import java.nio.charset.CodingErrorAction
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

object EncodingUtils {
    private val UTF8: Charset = StandardCharsets.UTF_8
    private val WIN1252: Charset = Charset.forName("windows-1252")

    /**
     * Try to decode as UTF-8 strictly. If it fails, fallback to Windows-1252 (common "ANSI" on Windows).
     */
    fun detectCharset(bytes: ByteArray): Charset {
        val decoder = UTF8.newDecoder()
        decoder.onMalformedInput(CodingErrorAction.REPORT)
        decoder.onUnmappableCharacter(CodingErrorAction.REPORT)
        return try {
            decoder.decode(ByteBuffer.wrap(bytes))
            UTF8
        } catch (_: Exception) {
            // Fallback to Windows-1252 when not valid UTF-8
            WIN1252
        }
    }

    /**
     * Returns a String decoded using UTF-8 if valid; otherwise decodes using Windows-1252 and returns the text.
     */
    fun decodeBestEffort(bytes: ByteArray): String {
        val cs = detectCharset(bytes)
        return String(bytes, cs)
    }

    /**
     * Ensures the returned bytes are encoded in UTF-8. If the input is ANSI/Windows-1252 the content is transcoded.
     */
    fun toUtf8Bytes(bytes: ByteArray): ByteArray = decodeBestEffort(bytes).toByteArray(UTF8)
}