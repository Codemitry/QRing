package com.codemitry.qr_code_generator_lib.qrcode


import com.codemitry.qr_code_generator_lib.qrcode.correction.ErrorCorrectionLevels
import com.codemitry.qr_code_generator_lib.qrcode.correction.ErrorCorrectionLevels.*
import kotlin.math.floor

// x^10 + x^8 + x^5 + x^4 + x^2 + x + 1
private const val generator = "10100110111"

private fun getCorrectionString(correction: ErrorCorrectionLevels): String = when (correction) {
    L -> "01"
    M -> "00"
    Q -> "11"
    H -> "10"
}

private fun getMaskString(mask: Int): String {

    val bin = Integer.toBinaryString(mask)
    return "0".repeat(3 - bin.length) + bin
}

fun addToTail(source: StringBuilder, what: String, resultLen: Int) {
    if (resultLen >= source.length + what.length) {
        source.append(what.repeat((resultLen - source.length) / what.length))
    }
}

fun addToHead(source: StringBuilder, what: String, resultLen: Int) {
    if (resultLen >= source.length + what.length) {
        source.insert(0, what.repeat((resultLen - source.length) / what.length))
    }

}

fun removeAllFromHead(source: StringBuilder, what: String) {
    while (source.indexOf(what) == 0)
        source.delete(0, what.length)
}

fun divide(sequence: StringBuilder) {
    // just only GP
    val paddedGP = StringBuilder(generator)
    addToTail(paddedGP, "0", sequence.length)
    // already padded

    for (i in sequence.indices) {
        sequence.setCharAt(i, if (sequence[i] != paddedGP[i]) '1' else '0')
    }

    removeAllFromHead(sequence, "0")
}

fun getFormatAndVersionInformation(correction: ErrorCorrectionLevels, mask: Int): String {
    require(mask in 0..7)

    val correctionAndMask = getCorrectionString(correction) + getMaskString(mask)
    val sequence = java.lang.StringBuilder(correctionAndMask)

    addToTail(sequence, "0", 15)
    removeAllFromHead(sequence, "0")

    while (sequence.length > 10) {
        divide(sequence)
    }

    if (sequence.length < 10) {
        addToHead(sequence, "0", 10)
    }

    sequence.insert(0, correctionAndMask)

    // The QR code specification says to XOR the result with the following binary string: 101010000010010
    val sample = "101010000010010"
    for (i in sequence.indices) {
        sequence.setCharAt(i, if (sequence[i] != sample[i]) '1' else '0')
    }

    return sequence.toString()
}

val masks = mutableMapOf<Int, (x: Int, y: Int) -> Boolean>(
        0 to { x, y -> (x + y) % 2 == 0 },
        1 to { x, y -> y % 2 == 0 },
        2 to { x, y -> x % 3 == 0 },
        3 to { x, y -> (x + y) % 3 == 0 },
        4 to { x, y -> (floor(y / 2.0) + floor(x / 3.0)).toInt() % 2 == 0 },
        5 to { x, y -> ((y * x) % 2) + ((y * x) % 3) == 0 },
        6 to { x, y -> ( ((y * x) % 2) + ((y * x) % 3) ) % 2 == 0 },
        7 to { x, y -> ( ((y + x) % 2) + ((y * x) % 3) ) % 2 == 0 }
)