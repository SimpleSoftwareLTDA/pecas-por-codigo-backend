package org.pecasonline.common.exceptions

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import jakarta.servlet.http.HttpServletResponse
import org.pecasonline.common.exceptions.http.BaseExceptionResponseJson
import org.pecasonline.features.subscription.entities.InvalidSubscriptionException
import org.pecasonline.features.subscription.entities.InvalidTokenException
import org.pecasonline.features.subscription.entities.PaymentLateException
import org.pecasonline.features.subscription.entities.SubscriptionInactiveException
import org.pecasonline.features.subscription.entities.SupplierNotFoundException
import org.pecasonline.features.supplier.dto.CreateSupplierDTO
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException
import kotlin.reflect.KClass

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class ExceptionsAdvisor {

    private val fieldAliases = getJsonAliasesWithJackson(CreateSupplierDTO::class)

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception, response: HttpServletResponse): ResponseEntity<Pair<String, String?>> {

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("error" to ex.message)
    }


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
            val jsonAlias = fieldAliases[fieldName] ?: fieldName
            val errorMessage = error.defaultMessage
            println("Mapping field '$fieldName' to alias '$jsonAlias' with message '$errorMessage'")
            jsonAlias to errorMessage
        }
        return ResponseEntity(errors, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(org.springframework.web.method.annotation.HandlerMethodValidationException::class)
    fun handleHandlerMethodValidationException(ex: org.springframework.web.method.annotation.HandlerMethodValidationException): ResponseEntity<Pair<String, String?>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("error" to "Validation failure")
    }

    @ExceptionHandler(InvalidTokenException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleInvalidTokenException(ex: InvalidTokenException): ResponseEntity<JsonNode> {
        val response: JsonNode = ObjectMapper().createObjectNode()
            .put("status", 401)
            .put("message", ex.message ?: "Token inválido")
        return ResponseEntity(response, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(SupplierNotFoundException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleSupplierNotFoundException(ex: SupplierNotFoundException): ResponseEntity<JsonNode> {
        val response: JsonNode = ObjectMapper().createObjectNode()
            .put("status", 401)
            .put("message", ex.message)
        return ResponseEntity(response, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(SubscriptionInactiveException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleSubscriptionInactiveException(ex: SubscriptionInactiveException): ResponseEntity<JsonNode> {
        val response: JsonNode = ObjectMapper().createObjectNode()
            .put("status", 401)
            .put("message", ex.message)
        return ResponseEntity(response, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(PaymentLateException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handlePaymentLateException(ex: PaymentLateException): ResponseEntity<JsonNode> {
        val response: JsonNode = ObjectMapper().createObjectNode()
            .put("status", 401)
            .put("message", ex.message)
        return ResponseEntity(response, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(InvalidSubscriptionException::class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    fun handleInvalidSubscriptionException(ex: InvalidSubscriptionException): ResponseEntity<JsonNode> {
        val response: JsonNode = ObjectMapper().createObjectNode()
            .put("status", 401)
            .put("message", ex.message)
        return ResponseEntity(response, HttpStatus.UNAUTHORIZED)
    }

    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun handleMaxUploadSizeExceededException(ex: MaxUploadSizeExceededException): ResponseEntity<BaseExceptionResponseJson> {
        val response = BaseExceptionResponseJson(
            HttpStatus.PAYLOAD_TOO_LARGE.value(),
            "O arquivo enviado é muito grande. O limite é de 100MB."
        )
        return ResponseEntity.status(response.httpStatusCode).body(response)
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
