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

    streamScoped {
        var max = 0L
        var min = 0L
        var sum = 0L
        var count = 0

        recognizer.benchmark().collect {
            val diff = System.currentTimeMillis() - it.ts
            println("Received image - diff: $diff, size: ${it.data.size}")

            max = max(diff, max)
            min = min(diff, min)
            sum += diff
            count++
        }

        println("Max: $max, Min: $min, Avg: ${sum / count}")
    }

//    val duration = measureTime {
//        repeat(10) {
//            val image = recognizer.benchmark2()
//            println("Received image: ${image.data.size}")
//        }
//    }
//
//    println("Duration: $duration")


    recognizer.cancel()
    ktorClient.close()
//    stateJob.cancel()
}
