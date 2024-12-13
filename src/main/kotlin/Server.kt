/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import kotlinx.rpc.krpc.ktor.server.RPC
import kotlinx.rpc.krpc.ktor.server.rpc
import kotlinx.rpc.krpc.serialization.json.json
import kotlinx.rpc.krpc.serialization.protobuf.protobuf
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
fun main() {
    embeddedServer(Netty, port = 8080) {
        module()
    }.start(wait = true)
}


@ExperimentalSerializationApi
fun Application.module() {
    install(RPC)

    routing {
        rpc("/image-recognizer") {
            rpcConfig {
                serialization {
//                    json()
                    protobuf()
                }
            }

            registerService<ImageRecognizer> { ctx -> ImageRecognizerService(ctx) }
        }
    }
}
