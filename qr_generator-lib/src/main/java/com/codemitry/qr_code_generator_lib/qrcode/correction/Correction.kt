package com.codemitry.qr_code_generator_lib.qrcode.correction

import com.codemitry.qr_code_generator_lib.qrcode.*
import kotlin.math.max
import com.codemitry.qr_code_generator_lib.qrcode.correction.ErrorCorrectionLevels.*
import com.codemitry.qr_code_generator_lib.qrcode.encoding.DataConverter

enum class ErrorCorrectionLevels {
//         % of losses
    L,  // 7%
    M,  // 15%
    Q,  // 25%
    H; // 30%

    companion object {
        fun default(): ErrorCorrectionLevels = M
    }
}


@ExperimentalUnsignedTypes
fun mergeDataAndCorrectionBlocks(blocks: Array<UByteArray>, version: Int, correction: ErrorCorrectionLevels): UByteArray {
    val dataSequence = mutableListOf<UByte>()
    val corrections = getCorrectionBlocks(blocks, version, correction)

    var max = blocks.maxOf { it.size }

    for (byte in 0 until max) {
        for (block in blocks.indices) {
            if (blocks[block].size > byte) dataSequence.add(blocks[block][byte])
        }
    }

    max = corrections.maxOf { it.size }

    for (byte in 0 until max) {
        for (block in corrections.indices) {
            if (corrections[block].size > byte) dataSequence.add(corrections[block][byte])
        }
    }

    return dataSequence.toUByteArray()
}

@ExperimentalUnsignedTypes
fun getCorrectionBlocks(blocks: Array<UByteArray>, version: Int, correction: ErrorCorrectionLevels): Array<UByteArray> =
        Array(blocks.size) { i -> getCorrectionBlock(blocks[i], version, correction) }



@ExperimentalUnsignedTypes
fun getCorrectionBlock(block: UByteArray, version: Int, correction: ErrorCorrectionLevels): UByteArray {
    val correctionByteCount = correctionByteCount(version, correction)

    var gp: IntArray

    // Заполнение начала массива байтов блоком
    val bytes = MutableList(max(correctionByteCount, block.size)) { i -> if (i < block.size) block[i] else 0u }

    var a: UByte
    for (i in block.indices) {
        gp = generatorPolynomial[correctionByteCount]!!

        a = bytes.removeAt(0)
        bytes.add(0u)

        if (a == 0.toUByte()) continue
        val b = reversedGaloisField(a.toInt())
        for (j in 0 until correctionByteCount) {

            var c = gp[j] + b
            if (c > 254)
                c %= 255

            bytes[j] = (galoisField(c) xor bytes[j].toInt()).toUByte()

            // another variant
//            generatorPolynomial[j] = (generatorPolynomial[j] + b) % 255
//            bytes[j] = (Correction.galoisField[generatorPolynomial[j]] xor bytes[j].toInt()).toUByte()
        }

    }

    return bytes.toUByteArray().sliceArray(0 until correctionByteCount)
}

private val correctionByteCount = arrayOf(
        mapOf(), // version 0 does not exist
        mapOf(L to 7, M to 10, Q to 13, H to 17), // version 1
        mapOf(L to 2, M to 16, Q to 22, H to 28),
        mapOf(L to 15, M to 26, Q to 18, H to 22),
        mapOf(L to 20, M to 18, Q to 26, H to 16),
        mapOf(L to 26, M to 24, Q to 18, H to 22),
        mapOf(L to 18, M to 16, Q to 24, H to 28),
        mapOf(L to 20, M to 18, Q to 18, H to 26),
        mapOf(L to 24, M to 22, Q to 22, H to 26),
        mapOf(L to 30, M to 22, Q to 20, H to 24),
        mapOf(L to 18, M to 26, Q to 24, H to 28), // version 10
        mapOf(L to 20, M to 30, Q to 28, H to 24),
        mapOf(L to 24, M to 22, Q to 26, H to 28),
        mapOf(L to 26, M to 22, Q to 24, H to 22),
        mapOf(L to 30, M to 24, Q to 20, H to 24),
        mapOf(L to 22, M to 24, Q to 30, H to 24),
        mapOf(L to 24, M to 28, Q to 24, H to 30),
        mapOf(L to 28, M to 28, Q to 28, H to 28),
        mapOf(L to 30, M to 26, Q to 28, H to 28),
        mapOf(L to 28, M to 26, Q to 26, H to 26),
        mapOf(L to 28, M to 26, Q to 30, H to 28), // version 20
        mapOf(L to 28, M to 26, Q to 28, H to 30),
        mapOf(L to 28, M to 28, Q to 30, H to 24),
        mapOf(L to 30, M to 28, Q to 30, H to 30),
        mapOf(L to 30, M to 28, Q to 30, H to 30),
        mapOf(L to 26, M to 28, Q to 30, H to 30),
        mapOf(L to 28, M to 28, Q to 28, H to 30),
        mapOf(L to 30, M to 28, Q to 30, H to 30),
        mapOf(L to 30, M to 28, Q to 30, H to 30),
        mapOf(L to 30, M to 28, Q to 30, H to 30),
        mapOf(L to 30, M to 28, Q to 30, H to 30), // version 30
        mapOf(L to 30, M to 28, Q to 30, H to 30),
        mapOf(L to 30, M to 28, Q to 30, H to 30),
        mapOf(L to 30, M to 28, Q to 30, H to 30),
        mapOf(L to 30, M to 28, Q to 30, H to 30),
        mapOf(L to 30, M to 28, Q to 30, H to 30),
        mapOf(L to 30, M to 28, Q to 30, H to 30),
        mapOf(L to 30, M to 28, Q to 30, H to 30),
        mapOf(L to 30, M to 28, Q to 30, H to 30),
        mapOf(L to 30, M to 28, Q to 30, H to 30),
        mapOf(L to 30, M to 28, Q to 30, H to 30) // version 40
)

fun correctionByteCount(version: Int, correctionLevel: ErrorCorrectionLevels): Int =
        if (version !in MIN_VERSION..MAX_VERSION)
            throw IllegalArgumentException("Incorrect version: $version")
        else correctionByteCount[version][correctionLevel]!!


fun charCountIndicatorLength(version: Int, encodingMode: DataConverter.EncodingMode): Int =
        when {
            version <= 9 -> {
                when (encodingMode) {
                    DataConverter.EncodingMode.NUMERIC -> 10
                    DataConverter.EncodingMode.ALPHANUMERIC -> 9
                    DataConverter.EncodingMode.BYTE -> 8
                    DataConverter.EncodingMode.KANJI -> 8
                }
            }
            version <= 26 -> {
                when (encodingMode) {
                    DataConverter.EncodingMode.NUMERIC -> 12
                    DataConverter.EncodingMode.ALPHANUMERIC -> 1
                    DataConverter.EncodingMode.BYTE -> 16
                    DataConverter.EncodingMode.KANJI -> 10
                }
                // if version <= 40
            }
            else -> {
                when (encodingMode) {
                    DataConverter.EncodingMode.NUMERIC -> 14
                    DataConverter.EncodingMode.ALPHANUMERIC -> 3
                    DataConverter.EncodingMode.BYTE -> 16
                    DataConverter.EncodingMode.KANJI -> 12
                }
            }
        }
