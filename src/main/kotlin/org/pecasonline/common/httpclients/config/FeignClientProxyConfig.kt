package org.pecasonline.common.httpclients.config

import org.springframework.context.annotation.Bean
import java.net.InetSocketAddress
import java.util.concurrent.TimeUnit
import feign.Client
import okhttp3.Authenticator
import okhttp3.Credentials
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.net.Proxy
import java.io.IOException

@Configuration
class FeignClientProxyConfig {

    @Value("\${decodo.proxy.host}")
    private lateinit var proxyHost: String

    @Value("\${decodo.proxy.port}")
    private var proxyPort: Int = 0

    @Value("\${decodo.proxy.username}")
    private lateinit var proxyUsername: String

    @Value("\${decodo.proxy.password}")
    private lateinit var proxyPassword: String

    @Bean
    fun okHttpClientDecodo(): OkHttpClient {
        val proxy = Proxy(Proxy.Type.HTTP, InetSocketAddress(/* hostname = */ proxyHost, /* port = */ proxyPort))

        val builder = OkHttpClient.Builder()
            .proxy(proxy)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)

        if (proxyUsername.isNotEmpty() && proxyPassword.isNotEmpty()) {
            builder.proxyAuthenticator(object : Authenticator {
                @Throws(IOException::class)
                override fun authenticate(route: okhttp3.Route?, response: Response): Request? {
                    val credential = Credentials.basic(proxyUsername, proxyPassword)
                    return response.request.newBuilder()
                        .header("Proxy-Authorization", credential)
                        .build()
                }
            })
        }

        return builder.build()
    }

    @Bean
    fun feignClientDecodo(okHttpClientDecodo: OkHttpClient): Client {
        return feign.okhttp.OkHttpClient(okHttpClientDecodo)
    }
}