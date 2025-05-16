package org.pecasonline.common.httpclients

import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.pecasonline.common.Constants.CAP_SOLVER_API_KEY
import org.pecasonline.common.Constants.SITE_KEY
import org.pecasonline.common.Constants.SITE_URL
import org.springframework.web.servlet.function.RequestPredicates.contentType
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class ReCaptchaV2(private val okHttpClient: OkHttpClient) {
    private val objectMapper = ObjectMapper()
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    fun capSolver(code: String): String {
        val param = mutableMapOf<String, Any>()
        val task = mutableMapOf<String, Any>()

        task["type"] = "ReCaptchaV2TaskProxyLess"
        task["websiteKey"] = SITE_KEY
        task["websiteURL"] = SITE_URL
        param["clientKey"] = CAP_SOLVER_API_KEY
        param["task"] = task

        val taskId = createTask(param)

        if (taskId == "") {
            println("Failed to create task")
            return ""
        }

        println("Got taskId: $taskId / Getting result...")

        while (true) {
            Thread.sleep(1000)
            val token = getTaskResult(taskId)

            return when (token) {
                null -> continue
                "error" -> ""
                else -> token
            }
        }
    }

    private val contentType = "application/x-www-form-urlencoded".toMediaType()

    private fun requestPost(url: String, rawBody: String): String {
        val body = rawBody.toRequestBody(contentType)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        try {
            okHttpClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errorBody = response.body?.string() ?: "Erro desconhecido"
                    throw IOException("Erro na requisição para $url: ${response.code} - $errorBody")
                }
                val responseBody = response.body?.string() ?: "" // Store the response body
                return responseBody
            }
        } catch (e: IOException) {
            throw IOException("Erro ao executar a requisição POST para $url: ${e.message}", e)
        }
    }

    @Throws(IOException::class)
    private fun createTask(param: Map<String, Any>): String {
        val parsedJsonStr = requestPost("https://api.capsolver.com/createTask", objectMapper.writeValueAsString(param))
        val responseJson = objectMapper.readTree(parsedJsonStr)

        return responseJson["taskId"]?.asText() ?: ""
    }

    @Throws(IOException::class)
    private fun getTaskResult(taskId: String): String? {
        val param = mutableMapOf<String, Any>()
        param["clientKey"] = CAP_SOLVER_API_KEY
        param["taskId"] = taskId

        val parsedJsonStr = requestPost("https://api.capsolver.com/getTaskResult", objectMapper.writeValueAsString(param))
        val responseJson = objectMapper.readTree(parsedJsonStr)

        val status = responseJson["status"]?.asText()

        return when (status) {
            "ready" -> responseJson["solution"]?.get("gRecaptchaResponse")?.asText()
            "failed" -> {
                println("Solve failed! response: $parsedJsonStr")
                "error"
            }
            else -> null
        }
    }

    fun performSearch(recaptchaToken: String, code: String): String {
        val payload = "pv1=todos&pv2=AGCO&pv4=$code&pv3=1&pv5=Cidade&Pesquisarbtn=Pesquisar&g-recaptcha-response=${URLEncoder.encode(recaptchaToken, "UTF-8")}&captcha-response=${URLEncoder.encode(recaptchaToken, "UTF-8")}"

        return requestPost(SITE_URL, payload)
    }
}
