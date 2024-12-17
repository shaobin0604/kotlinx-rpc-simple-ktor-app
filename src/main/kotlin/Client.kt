/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import io.ktor.client.*
import io.ktor.client.plugins.websocket.*
import io.ktor.http.*
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.rpc.krpc.ktor.client.rpc
import kotlinx.rpc.krpc.ktor.client.rpcConfig
import kotlinx.rpc.krpc.serialization.protobuf.protobuf
import kotlinx.rpc.krpc.streamScoped
import kotlinx.rpc.withService
import kotlinx.serialization.ExperimentalSerializationApi
import kotlin.math.max
import kotlin.math.min
import kotlin.time.measureTime

@ExperimentalSerializationApi
fun main() = runBlocking {
    val ktorClient = HttpClient {
        install(WebSockets)
    }

    val client = ktorClient.rpc {
        url {
            host = "localhost"
            port = 8080
            encodedPath = "image-recognizer"
        }

        rpcConfig {
            serialization {
//                json()
                protobuf()
            }
        }
    }

    val recognizer: ImageRecognizer = client.withService<ImageRecognizer>()

//    val stateJob = launch {
//        recognizer.currentlyProcessedImage.collect {
//            println("New state, current image: $it")
//        }
//    }

//    val image = Image(byteArrayOf(0, 1, 2, 3))
//    val category = recognizer.recognize(image)
//    println("Recognized category: $category")
//
//    val imageFlow = flow {
//        repeat(10) {
//            emit(image)
//        }
//    }
//
//    streamScoped {
//        val categories = recognizer.recognizeAll(imageFlow)
//        categories.collect { println("Recognized category: $it") }
//    }

    suspend fun benchmark(
        size: Int,
        count: Int,
        delay: Long,
    ) {
        var max = 0L
        var min = 0L
        var sum = 0L

        recognizer.benchmark(size, count, delay).collect {
            val diff = System.currentTimeMillis() - it.ts
//            println("Received image - diff: $diff, size: ${it.data.size}")

            max = max(diff, max)
            min = min(diff, min)
            sum += diff
        }

        println("size: $size, count: $count, delay: $delay -> Max: $max, Min: $min, Avg: ${sum / count}")
    }

    streamScoped {
//        benchmark(size = 1280 * 720 * 4, count = 300, delay = 30)   // 3MB Warm up

//        benchmark(size = 1280 * 720 * 4, count = 300, delay = 30)   // 3MB
//        benchmark(size = 1024 * 1024, count = 300, delay = 30)      // 1MB
//        benchmark(size = 500 * 1024, count = 300, delay = 30)       // 500KB
//        benchmark(size = 100 * 1024, count = 300, delay = 30)       // 100KB
//        benchmark(size = 10 * 1024, count = 300, delay = 30)        // 10KB
        benchmark(size = 1 * 1024, count = 100, delay = 30)         // 1KB
    }

    suspend fun benchmark3(repeat: Int, count: Int) {
        val duration = measureTime {
            repeat(repeat) {
                val personInfoList = recognizer.benchmark3(count)
                println("Received personInfoList latency: ${System.currentTimeMillis() - personInfoList.ts}")
            }
        }
        println("benchmark3 - repeat: $repeat, count: $count -> total duration: $duration, avg: ${duration.inWholeMilliseconds / repeat}")
    }

    benchmark3(repeat = 10, count = 40)

    recognizer.cancel()
    ktorClient.close()
//    stateJob.cancel()
}
