package dev.kgbier.util.networklogger.sample

import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class MainViewModel(
    private val okHttpClient: OkHttpClient,
) {

    fun makeRequest(
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
}