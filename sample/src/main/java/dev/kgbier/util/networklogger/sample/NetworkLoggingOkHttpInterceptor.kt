package dev.kgbier.util.networklogger.sample

import dev.kgbier.util.networklogger.repository.HttpLoggingRepository
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.util.UUID

class NetworkLoggingOkHttpInterceptor(
    private val loggingRepo: HttpLoggingRepository,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val requestTransactionId = UUID.randomUUID().toString()

        val bodyBuffer = Buffer()
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