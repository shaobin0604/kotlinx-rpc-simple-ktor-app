/*
 * Copyright 2023-2024 JetBrains s.r.o and contributors. Use of this source code is governed by the Apache 2.0 license.
 */

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.rpc.RemoteService
import kotlinx.rpc.RPCEagerField
import kotlinx.rpc.annotations.Rpc
import kotlinx.serialization.Serializable
import java.util.ArrayList
import kotlin.coroutines.CoroutineContext

@Suppress("ArrayInDataClass")
@Serializable
data class Image(val data: ByteArray, val ts: Long = System.currentTimeMillis()) {
//    @OptIn(ExperimentalStdlibApi::class)
//    override fun toString(): String {
//        return "Image(${data.joinToString("") { it.toHexString() }})"
//    }

    override fun toString(): String {
        return "Image(ts=$ts)"
    }
}

@Serializable
data class Rect(val left: Float, val top: Float, val right: Float, val bottom: Float)

@Serializable
data class Point(val x: Float, val y: Float)

@Serializable
data class PersonInfo(
    /**
     * 编号，从 0 开始
     */
    val tIndex: Int? = null,
    /**
     * 基于输入画面的点位坐标
     */
    val tPoint: Point? = null,
    /**
     * 透视畸变调整后的检测框
     */
    val dBox: Rect? = null,
    /**
     * 透视畸变调整后骨骼点 17 个点
     */
    val dKpts: List<Point>? = null,
    /**
     * 透视畸变还原后检测框
     */
    val rBox: Rect? = null,
    /**
     * 透视畸变还原后骨骼点 17 个点
     */
    val rKpts: List<Point>? = null,
    /**
     * 计数
     */
    val count: Int? = null,
    /**
     * 置信度
     */
    val confidence: Float? = null,
)

@Serializable
data class PersonInfoList(
    /**
     * 检测到的人列表
     */
    val items: List<PersonInfo>? = null,
    /**
     * 视频帧编号
     */
    val frameIndex: Int? = null,

    val ts: Long = System.currentTimeMillis(),
)

enum class Category {
    CAT, DOG
}

@Rpc
interface ImageRecognizer : RemoteService {
    @RPCEagerField
    val currentlyProcessedImage: StateFlow<Image?>

    suspend fun recognize(image: Image): Category

    suspend fun recognizeAll(images: Flow<Image>): Flow<Category>

    suspend fun benchmark(size: Int, count: Int, delay: Long): Flow<Image>

    suspend fun benchmark2(): Image

    suspend fun benchmark3(count: Int): PersonInfoList
}

class ImageRecognizerService(override val coroutineContext: CoroutineContext) : ImageRecognizer {
    override val currentlyProcessedImage: MutableStateFlow<Image?> = MutableStateFlow(null)

    val mockPersonInfo = PersonInfo(
        tIndex = 0,
        tPoint = Point(0.1f, 0.2f),
        dBox = Rect(0.1f, 0.2f, 0.3f, 0.4f),
        dKpts = listOf(
            Point(0.1f, 0.2f), Point(0.3f, 0.4f), Point(0.5f, 0.6f), Point(0.7f, 0.8f),
            Point(0.9f, 1.0f), Point(1.1f, 1.2f), Point(1.3f, 1.4f), Point(1.5f, 1.6f),
            Point(1.7f, 1.8f), Point(1.9f, 2.0f), Point(2.1f, 2.2f), Point(2.3f, 2.4f),
            Point(2.5f, 2.6f), Point(2.7f, 2.8f), Point(2.9f, 3.0f), Point(3.1f, 3.2f)
        ),
        rBox = Rect(0.1f, 0.2f, 0.3f, 0.4f),
        rKpts = listOf(
            Point(0.1f, 0.2f), Point(0.3f, 0.4f), Point(0.5f, 0.6f), Point(0.7f, 0.8f),
            Point(0.9f, 1.0f), Point(1.1f, 1.2f), Point(1.3f, 1.4f), Point(1.5f, 1.6f),
            Point(1.7f, 1.8f), Point(1.9f, 2.0f), Point(2.1f, 2.2f), Point(2.3f, 2.4f),
            Point(2.5f, 2.6f), Point(2.7f, 2.8f), Point(2.9f, 3.0f), Point(3.1f, 3.2f)
        ),
        count = 1,
        confidence = 0.9f,
    )

    val mockPersonInfoList = PersonInfoList(
        items = listOf(
            PersonInfo(
                tIndex = 0,
                tPoint = Point(0.1f, 0.2f),
                dBox = Rect(0.1f, 0.2f, 0.3f, 0.4f),
                dKpts = listOf(
                    Point(0.1f, 0.2f), Point(0.3f, 0.4f), Point(0.5f, 0.6f), Point(0.7f, 0.8f),
                    Point(0.9f, 1.0f), Point(1.1f, 1.2f), Point(1.3f, 1.4f), Point(1.5f, 1.6f),
                    Point(1.7f, 1.8f), Point(1.9f, 2.0f), Point(2.1f, 2.2f), Point(2.3f, 2.4f),
                    Point(2.5f, 2.6f), Point(2.7f, 2.8f), Point(2.9f, 3.0f), Point(3.1f, 3.2f)
                ),
                rBox = Rect(0.1f, 0.2f, 0.3f, 0.4f),
                rKpts = listOf(
                    Point(0.1f, 0.2f), Point(0.3f, 0.4f), Point(0.5f, 0.6f), Point(0.7f, 0.8f),
                    Point(0.9f, 1.0f), Point(1.1f, 1.2f), Point(1.3f, 1.4f), Point(1.5f, 1.6f),
                    Point(1.7f, 1.8f), Point(1.9f, 2.0f), Point(2.1f, 2.2f), Point(2.3f, 2.4f),
                    Point(2.5f, 2.6f), Point(2.7f, 2.8f), Point(2.9f, 3.0f), Point(3.1f, 3.2f)
                ),
                count = 1,
                confidence = 0.9f,
            ),
            PersonInfo(
                tIndex = 1,
                tPoint = Point(0.3f, 0.4f),
                dBox = Rect(0.3f, 0.4f, 0.5f, 0.6f),
                dKpts = listOf(
                    Point(0.1f, 0.2f), Point(0.3f, 0.4f), Point(0.5f, 0.6f), Point(0.7f, 0.8f),
                    Point(0.9f, 1.0f), Point(1.1f, 1.2f), Point(1.3f, 1.4f), Point(1.5f, 1.6f),
                    Point(1.7f, 1.8f), Point(1.9f, 2.0f), Point(2.1f, 2.2f), Point(2.3f, 2.4f),
                    Point(2.5f, 2.6f), Point(2.7f, 2.8f), Point(2.9f, 3.0f), Point(3.1f, 3.2f)
                ),
                rBox = Rect(0.3f, 0.4f, 0.5f, 0.6f),
                rKpts = listOf(
                    Point(0.1f, 0.2f), Point(0.3f, 0.4f), Point(0.5f, 0.6f), Point(0.7f, 0.8f),
                    Point(0.9f, 1.0f), Point(1.1f, 1.2f), Point(1.3f, 1.4f), Point(1.5f, 1.6f),
                    Point(1.7f, 1.8f), Point(1.9f, 2.0f), Point(2.1f, 2.2f), Point(2.3f, 2.4f),
                    Point(2.5f, 2.6f), Point(2.7f, 2.8f), Point(2.9f, 3.0f), Point(3.1f, 3.2f)
                ),
                count = 2,
                confidence = 0.8f,
            ),
        ),
        frameIndex = 10,
    )

    override suspend fun recognize(image: Image): Category {
        currentlyProcessedImage.value = image
        val byte = image.data[0].toInt()
        delay(100) // heavy processing
        val result = if (byte == 0) Category.CAT else Category.DOG
        currentlyProcessedImage.value = null
        return result
    }

    override suspend fun recognizeAll(images: Flow<Image>): Flow<Category> {
        return images.map { recognize(it) }
    }

    override suspend fun benchmark(size: Int, count: Int, delay: Long): Flow<Image> {
        return flow {
            repeat(count) {
                delay(delay)
                emit(Image(ByteArray(size) { 0x00 }))
            }
        }
    }

    override suspend fun benchmark2(): Image {
        return Image(ByteArray(1280 * 720 * 4) { 0x00 })
    }

    override suspend fun benchmark3(count: Int): PersonInfoList {
        return PersonInfoList(items = List(count) { mockPersonInfo }, 0)
    }
}
