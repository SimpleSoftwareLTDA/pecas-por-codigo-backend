package org.pecasonline.common.exceptions

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.validation.constraints.NotNull
import org.pecasonline.common.exceptions.http.BaseExceptionResponseJson
import org.pecasonline.features.supplier.dto.CreateSupplierDTO
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import kotlin.reflect.KClass

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class ExceptionsAdvisor {

    private val fieldAliases = getJsonAliasesWithJackson(CreateSupplierDTO::class)

    @ExceptionHandler(NotFoundException::class)
    fun handleGenericException(ex: Exception): ResponseEntity<BaseExceptionResponseJson> {
        val response = BaseExceptionResponseJson(HttpStatus.NOT_FOUND.value(), ex.message)
        return ResponseEntity.status(response.httpStatusCode).body(response)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: Exception): ResponseEntity<BaseExceptionResponseJson> {
        val response = BaseExceptionResponseJson(HttpStatus.BAD_REQUEST.value(), ex.message)
        return ResponseEntity.status(response.httpStatusCode).body(response)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): ResponseEntity<Map<String, String?>> {
        val errors = ex.bindingResult.allErrors.associate { error ->
            val fieldName = (error as FieldError).field
            val jsonAlias = fieldAliases[fieldName] ?: fieldName // Use alias if available
            val errorMessage = error.defaultMessage
            println("Mapping field '$fieldName' to alias '$jsonAlias' with message '$errorMessage'")
            jsonAlias to errorMessage
        }
        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }

    private final fun getJsonAliasesWithJackson(clazz: KClass<*>, objectMapper: ObjectMapper = jacksonObjectMapper()): Map<String, String> {
        val fieldAliasMap = mutableMapOf<String, String>()
        val beanDescription = objectMapper.serializationConfig.introspect(objectMapper.constructType(clazz.java))

        for (property in beanDescription.findProperties()) {
            val aliasAnnotation = property.primaryMember?.getAnnotation(JsonAlias::class.java)
            val alias = aliasAnnotation?.value?.firstOrNull()
            fieldAliasMap[property.name] = alias ?: property.name
        }
        return fieldAliasMap
    }
}
