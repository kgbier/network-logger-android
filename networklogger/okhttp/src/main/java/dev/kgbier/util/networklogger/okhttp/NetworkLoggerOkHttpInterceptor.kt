package dev.kgbier.util.networklogger.okhttp

import dev.kgbier.util.networklogger.repository.NetworkLoggerRepository
import java.util.UUID

class NetworkLoggerOkHttpInterceptor(
    private val loggingRepo: NetworkLoggerRepository,
) : okhttp3.Interceptor {

    override fun intercept(chain: okhttp3.Interceptor.Chain): okhttp3.Response {
        val request = chain.request()

        val requestTransactionId = UUID.randomUUID().toString()

        val bodyBuffer = okio.Buffer()
        request.body?.writeTo(bodyBuffer)

        loggingRepo.logRequest(
            transactionId = requestTransactionId,
            url = request.url.toString(),
            method = request.method,
            headers = request.headers.toList(),
            body = bodyBuffer.readString(Charsets.UTF_8),
            timestamp = System.currentTimeMillis(),
        )

        val response = chain.proceed(request)

        bodyBuffer.clear()

        response.body?.source()?.peek()
            ?.readAll(bodyBuffer)

        loggingRepo.logResponse(
            transactionId = requestTransactionId,
            statusCode = response.code,
            headers = response.headers.toList(),
            body = bodyBuffer.readString(Charsets.UTF_8),
            timestamp = System.currentTimeMillis(),
        )

        return response
    }
}