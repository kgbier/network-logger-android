package dev.kgbier.util.networklogger.sample

import android.util.Log
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.*
import java.io.IOException

class MainViewModel(
    private val okHttpClient: OkHttpClient,
    private val ktorHttpClient: HttpClient,
) {

    fun makeRequestOkHttp(
        onStart: () -> Unit,
        onSuccess: () -> Unit,
        onError: (error: Throwable) -> Unit,
    ) {
        onStart()

        val request = Request.Builder()
            .get()
            .url("https://swapi.dev/api/people/1")
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) = onSuccess()
            override fun onFailure(call: Call, e: IOException) = onError(e)
        })
    }

    fun makeRequestKtor(
        onStart: () -> Unit,
        onSuccess: () -> Unit,
        onError: (error: Throwable) -> Unit,
    ) {
        onStart()

        @OptIn(DelicateCoroutinesApi::class)
        GlobalScope.launch {
            runCatching { ktorHttpClient.request("https://swapi.dev/api/people/1") }
                .onSuccess { onSuccess() }
                .onFailure { onError(it) }
        }
    }
}