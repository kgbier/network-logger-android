package dev.kgbier.util.networklogger.sample

import dev.kgbier.util.networklogger.repository.HttpLoggingRepository
import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.util.UUID

class NetworkLoggingOkHttpInterceptor(
    private val loggingRepo: HttpLoggingRepository,
) : Interceptor {

    private fun Headers.toBlock() = joinToString("\n") { (key, value) ->
        "$key: $value"
    }


    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val requestTransactionId = UUID.randomUUID().toString()

        val bodyBuffer = Buffer()
        request.body?.writeTo(bodyBuffer)

        loggingRepo.logRequest(
            requestTransactionId,
            request.url.toString(),
            request.method,
            request.headers.toBlock(),
            bodyBuffer.readString(Charsets.UTF_8),
            System.currentTimeMillis(),
        )

        val response = chain.proceed(request)

        bodyBuffer.clear()
        response.body?.source()?.let { source ->
            // Reading from the source directly will take it away from the downstream as well
            source.request(Long.MAX_VALUE) // TODO: Is this needed?
            source.buffer.copyTo(bodyBuffer)
        }

        loggingRepo.logResponse(
            requestTransactionId,
            response.code,
            response.headers.toBlock(),
            bodyBuffer.readString(Charsets.UTF_8),
            System.currentTimeMillis(),
        )

        return response
    }
}