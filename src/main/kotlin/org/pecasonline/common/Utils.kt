package org.pecasonline.common

fun List<String>.isInvalidColumnSize(): Boolean =  this.size != 4

fun warnWithoutStacktrace(message: String): Nothing {
    throw object : IllegalStateException(message) {
        override fun fillInStackTrace(): Throwable = this
    }
}
