package dev.kgbier.util.networklogger.sample.di

import android.content.Context
import android.util.Log
import dev.kgbier.util.networklogger.NetworkLoggerRepository
import dev.kgbier.util.networklogger.okhttp.NetworkLoggerOkHttpInterceptor
import dev.kgbier.util.networklogger.repository.NetworkLoggerRepository
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.logging.*
import io.ktor.client.plugins.observer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.util.*
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.*

private val RequestTransactionId = AttributeKey<String>("NetworkLoggerRequestTransactionId")

class MainDependencyGraph(val context: Context) {

    private object KtorAndroidDebugLogger : Logger {
        override fun log(message: String) {
            Log.d("KTOR", message)
        }
    }

    val okHttpLoggingInterceptor: HttpLoggingInterceptor
        get() = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

    val networkLoggerOkHttpInterceptor by lazy {
        NetworkLoggerOkHttpInterceptor(context)
    }

    class NetworkLoggerKtorPlugin(
        private val loggingRepo: NetworkLoggerRepository,
    ) {

        private class LoggedContent(
            private val originalContent: OutgoingContent,
            private val channel: ByteReadChannel
        ) : OutgoingContent.ReadChannelContent() {

            override val contentType: ContentType? = originalContent.contentType
            override val contentLength: Long? = originalContent.contentLength
            override val status: HttpStatusCode? = originalContent.status
            override val headers: Headers = originalContent.headers

            override fun <T : Any> getProperty(key: AttributeKey<T>): T? =
                originalContent.getProperty(key)

            override fun <T : Any> setProperty(key: AttributeKey<T>, value: T?) =
                originalContent.setProperty(key, value)

            override fun readFrom(): ByteReadChannel = channel
        }

        private fun install(scope: HttpClient) {
            scope.sendPipeline.intercept(HttpSendPipeline.Monitoring) {
                val requestTransactionId = UUID.randomUUID().toString()
                context.attributes.put(RequestTransactionId, requestTransactionId)

                val content = context.body as? OutgoingContent

                val charset = content?.contentType?.charset() ?: Charsets.UTF_8

                val (body: String, newContent: OutgoingContent?) = when (content) {
                    is OutgoingContent.ByteArrayContent -> content.bytes()
                        .toString(charset) to content
                    is OutgoingContent.ReadChannelContent -> {
                        val (loggingBytes, backupBytes) = content.readFrom().split(this)

                        loggingBytes.readRemaining().readText(charset = charset) to
                                LoggedContent(content, backupBytes)
                    }
                    is OutgoingContent.WriteChannelContent -> {
                        val byteChannel = ByteChannel()
                        content.writeTo(byteChannel)
                        val (loggingBytes, backupBytes) = byteChannel.split(this)

                        loggingBytes.readRemaining().readText(charset = charset) to
                                LoggedContent(content, backupBytes)
                    }
                    else -> "" to content
                }

                loggingRepo.logRequest(
                    transactionId = requestTransactionId,
                    url = context.url.buildString(),
                    method = context.method.value,
                    headers = context.headers.build().flattenEntries(),
                    body = body,
                    timestamp = System.currentTimeMillis(),
                )
                proceedWith(newContent ?: subject)
            }

            ResponseObserver.install(
                plugin = ResponseObserver(responseHandler = { response ->
                    val requestTransactionId = response.call.attributes[RequestTransactionId]

                    loggingRepo.logResponse(
                        transactionId = requestTransactionId,
                        statusCode = response.status.value,
                        headers = response.headers.flattenEntries(),
                        body = response.bodyAsText(),
                        timestamp = System.currentTimeMillis(),
                    )
                }),
                scope = scope,
            )
        }

        class NetworkLoggerKtorPluginInstaller(
            private val networkLoggerRepository: NetworkLoggerRepository,
        ) : HttpClientPlugin<Nothing, NetworkLoggerKtorPlugin> {

            override val key: AttributeKey<NetworkLoggerKtorPlugin> =
                AttributeKey("NetworkLoggerKtorPlugin")

            override fun prepare(block: Nothing.() -> Unit): NetworkLoggerKtorPlugin =
                NetworkLoggerKtorPlugin(networkLoggerRepository)

            override fun install(plugin: NetworkLoggerKtorPlugin, scope: HttpClient) =
                plugin.install(scope)
        }
    }

    fun NetworkLoggerKtorPlugin(context: Context): HttpClientPlugin<Nothing, NetworkLoggerKtorPlugin> =
        NetworkLoggerKtorPlugin.NetworkLoggerKtorPluginInstaller(NetworkLoggerRepository(context))

    val networkLoggerKtorPlugin by lazy {
        NetworkLoggerKtorPlugin(context)
    }

    val ktorHttpClient by lazy {
        HttpClient(CIO) {
            install(Logging) {
                logger = KtorAndroidDebugLogger
                level = LogLevel.ALL
            }
            install(networkLoggerKtorPlugin)
        }
    }

    val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(okHttpLoggingInterceptor)
            .addInterceptor(networkLoggerOkHttpInterceptor)
            .build()
    }
}