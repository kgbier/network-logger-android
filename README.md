# network-logger
### Konrad Biernacki (<kgbier@gmail.com>)

A lightweight utility for capturing network traffic in Android apps.

| | |
| - | - |
| <img src="docs/screenshot-list.png" width="256"> | <img src="docs/screenshot-event.png" width="256"> |

## Sample
A sample Android app is included under `sample`

## Use

Comes with batteries-included when including in an OkHttp client.
Depend on the `okhttp` flavour of this library to receive the `NetworkLoggerOkHttpInterceptor`:

```kotlin
OkHttpClient.Builder()
    .addInterceptor(NetworkLoggerOkHttpInterceptor(context))
    .build()
```