package org.pecasonline.common.httpclients

import com.fasterxml.jackson.databind.ObjectMapper
import org.pecasonline.common.Constants.CAP_SOLVER_API_KEY
import org.pecasonline.common.Constants.SITE_KEY
import org.pecasonline.common.Constants.SITE_URL
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

object ReCaptchaV2 {
    private val objectMapper = ObjectMapper()
    private var okHttpClient: OkHttpClient? = null // Use a nullable OkHttpClient

    // Added a setter for OkHttpClient.  This is crucial for injecting
    // the configured client from Spring.
    fun setOkHttpClient(client: OkHttpClient) {
        okHttpClient = client
    }

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

    @Throws(IOException::class)
    private fun requestPost(url: String, rawBody: String): String {
        // Use OkHttpClient if available, otherwise, use HttpURLConnection
        return if (okHttpClient != null) {
            val mediaType = "application/json; charset=utf-8".toMediaType()
            val body = rawBody.toRequestBody(mediaType)
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()
            val response = okHttpClient!!.newCall(request).execute()  // !! is safe here, we check for null
            if (!response.isSuccessful) {
                throw IOException("Failed to execute request: $response")
            }
            response.body?.string() ?: ""
        } else {
            val ipapi = URL(url)
            val c = ipapi.openConnection() as HttpURLConnection
            c.requestMethod = "POST"
            c.doOutput = true

            c.outputStream.use { os ->
                val bodyBytes = rawBody.toByteArray(Charsets.UTF_8)
                os.write(bodyBytes)
                os.flush()
            }

            c.connect()
            val reader = BufferedReader(InputStreamReader(c.inputStream))
            val sb = StringBuilder()
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                sb.append(line)
            }

            sb.toString()
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
