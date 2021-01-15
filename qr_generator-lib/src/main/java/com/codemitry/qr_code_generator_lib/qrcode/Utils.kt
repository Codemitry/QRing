package com.codemitry.qr_code_generator_lib.qrcode

import com.codemitry.qr_code_generator_lib.qrcode.correction.ErrorCorrectionLevels
import com.codemitry.qr_code_generator_lib.qrcode.correction.ErrorCorrectionLevels.*
import kotlin.math.pow

fun qrCodeSize(version: Int): Int {
    if (version == 1) return 21
    val alignmentPatterns = alignmentPatternsCenters(version)
    // От самого крайнего выравнивающего узора до края QR-кода 6 модулей (+ 1, т.к. счет от 1)
    return alignmentPatterns[alignmentPatterns.size - 1] + 7
}

val versionCodes = intArrayOf(
        0, 0, 0, 0, 0, 0, 0,  // для версий по 6 включительно не было кодов версий
        10150,  // version 7
        71480,
        226820,
        171904,  // version 10
        65212,
        55578,
        178214,
        217506,
        77982,
        115804,
        238944,
        150756,
        11736,
        2686,  // version 20
        158530,
        230086,
        123898,
        54116,
        176728,
        220124,
        80608,
        73030,
        228474,
        169470,  // version 30
        62658,
        165421,
        59153,
        69269,
        225193,
        215055,
        76083,
        49335,
        172427,
        233749
)

private val alignmentPatternsCenters = arrayOf(
        intArrayOf(), // 0 version does not exist
        intArrayOf(), // for version 1 alignment patterns are not required
        intArrayOf(18), // version 2
        intArrayOf(22),
        intArrayOf(26),
        intArrayOf(30),
        intArrayOf(34),
        intArrayOf(6, 22, 38),
        intArrayOf(6, 24, 42),
        intArrayOf(6, 26, 46),
        intArrayOf(6, 28, 50),
        intArrayOf(6, 30, 54),
        intArrayOf(6, 32, 58),
        intArrayOf(6, 34, 62),
        intArrayOf(6, 26, 46, 66),
        intArrayOf(6, 26, 48, 70),
        intArrayOf(6, 26, 50, 74),
        intArrayOf(6, 30, 54, 78),
        intArrayOf(6, 30, 56, 82),
        intArrayOf(6, 30, 58, 86),
        intArrayOf(6, 34, 62, 90),
        intArrayOf(6, 28, 50, 72, 94),
        intArrayOf(6, 26, 50, 74, 98),
        intArrayOf(6, 30, 54, 78, 102),
        intArrayOf(6, 28, 54, 80, 106),
        intArrayOf(6, 32, 58, 84, 110),
        intArrayOf(6, 30, 58, 86, 114),
        intArrayOf(6, 34, 62, 90, 118),
        intArrayOf(6, 26, 50, 74, 98, 122),
        intArrayOf(6, 30, 54, 78, 102, 126),
        intArrayOf(6, 26, 52, 78, 104, 130),
        intArrayOf(6, 30, 56, 82, 108, 134),
        intArrayOf(6, 34, 60, 86, 112, 138),
        intArrayOf(6, 30, 58, 86, 114, 142),
        intArrayOf(6, 34, 62, 90, 118, 146),
        intArrayOf(6, 30, 54, 78, 102, 126, 150),
        intArrayOf(6, 24, 50, 76, 102, 128, 154),
        intArrayOf(6, 28, 54, 80, 106, 132, 158),
        intArrayOf(6, 32, 58, 84, 110, 136, 162),
        intArrayOf(6, 26, 54, 82, 110, 138, 166),
        intArrayOf(6, 30, 58, 86, 114, 142, 170)    // 40 lvl
)

fun alignmentPatternsCenters(version: Int): IntArray =
        if (version !in MIN_VERSION..MAX_VERSION)
            throw IllegalArgumentException("Incorrect version: $version")
        else alignmentPatternsCenters[version]

private val maxBits = arrayOf(
        mapOf(), // 0 version does not exist
        mapOf(L to 152, M to 128, Q to 104, H to 72),
        mapOf(L to 272, M to 224, Q to 176, H to 128),
        mapOf(L to 440, M to 352, Q to 272, H to 208),
        mapOf(L to 640, M to 512, Q to 384, H to 288),
        mapOf(L to 864, M to 688, Q to 496, H to 368),
        mapOf(L to 1088, M to 864, Q to 608, H to 480),
        mapOf(L to 1248, M to 992, Q to 704, H to 528),
        mapOf(L to 1552, M to 1232, Q to 880, H to 688),
        mapOf(L to 1856, M to 1456, Q to 1056, H to 800),
        mapOf(L to 2192, M to 1728, Q to 1232, H to 976),
        mapOf(L to 2592, M to 2032, Q to 1440, H to 1120),
        mapOf(L to 2960, M to 2320, Q to 1648, H to 1264),
        mapOf(L to 3424, M to 2672, Q to 1952, H to 1440),
        mapOf(L to 3688, M to 2920, Q to 2088, H to 1576),
        mapOf(L to 4184, M to 3320, Q to 2360, H to 1784),
        mapOf(L to 4712, M to 3624, Q to 2600, H to 2024),
        mapOf(L to 5176, M to 4056, Q to 2936, H to 2264),
        mapOf(L to 5768, M to 4504, Q to 3176, H to 2504),
        mapOf(L to 6360, M to 5016, Q to 3560, H to 2728),
        mapOf(L to 6888, M to 5352, Q to 3880, H to 3080), // 20 version
        mapOf(L to 7456, M to 5712, Q to 4096, H to 3248),
        mapOf(L to 8048, M to 6256, Q to 4544, H to 3536),
        mapOf(L to 8752, M to 6880, Q to 4912, H to 3712),
        mapOf(L to 9392, M to 7312, Q to 5312, H to 4112),
        mapOf(L to 10208, M to 8000, Q to 5744, H to 4304),
        mapOf(L to 10960, M to 8496, Q to 6032, H to 4768),
        mapOf(L to 11744, M to 9024, Q to 6464, H to 5024),
        mapOf(L to 12248, M to 9544, Q to 6968, H to 5288),
        mapOf(L to 13048, M to 10136, Q to 7288, H to 5608),
        mapOf(L to 13880, M to 10984, Q to 7880, H to 5960), // 30 version
        mapOf(L to 14744, M to 11640, Q to 8264, H to 6344),
        mapOf(L to 15640, M to 12328, Q to 8920, H to 6760),
        mapOf(L to 16568, M to 13048, Q to 9368, H to 7208),
        mapOf(L to 17528, M to 13800, Q to 9848, H to 7688),
        mapOf(L to 18448, M to 14496, Q to 10288, H to 7888),
        mapOf(L to 19472, M to 15312, Q to 10832, H to 8432),
        mapOf(L to 20528, M to 15936, Q to 11408, H to 8768),
        mapOf(L to 21616, M to 16816, Q to 12016, H to 9136),
        mapOf(L to 22496, M to 17728, Q to 12656, H to 9776),
        mapOf(L to 23648, M to 18672, Q to 13328, H to 10208)   // 40 version
)


fun maxBits(version: Int, correctionLevel: ErrorCorrectionLevels): Int =
        if (version !in MIN_VERSION..MAX_VERSION) 
            throw IllegalArgumentException("Incorrect version: $version")
        else maxBits[version][correctionLevel]!!


private val blockCount = arrayOf(
        mapOf(), // 0 version does not exist
        mapOf(L to 1, M to 1, Q to 1, H to 1), // version 1
        mapOf(L to 1, M to 1, Q to 1, H to 1),
        mapOf(L to 1, M to 1, Q to 2, H to 2),
        mapOf(L to 1, M to 2, Q to 2, H to 4),
        mapOf(L to 1, M to 2, Q to 4, H to 4),
        mapOf(L to 2, M to 4, Q to 4, H to 4),
        mapOf(L to 2, M to 4, Q to 6, H to 5),
        mapOf(L to 2, M to 4, Q to 6, H to 6),
        mapOf(L to 2, M to 5, Q to 8, H to 8),
        mapOf(L to 4, M to 5, Q to 8, H to 8), // version 10
        mapOf(L to 4, M to 5, Q to 8, H to 11),
        mapOf(L to 4, M to 8, Q to 10, H to 11),
        mapOf(L to 4, M to 9, Q to 12, H to 16),
        mapOf(L to 4, M to 9, Q to 16, H to 16),
        mapOf(L to 6, M to 10, Q to 12, H to 18),
        mapOf(L to 6, M to 10, Q to 17, H to 16),
        mapOf(L to 6, M to 11, Q to 16, H to 19),
        mapOf(L to 6, M to 13, Q to 18, H to 21),
        mapOf(L to 7, M to 14, Q to 21, H to 25),
        mapOf(L to 8, M to 16, Q to 20, H to 25), // version 20
        mapOf(L to 8, M to 17, Q to 23, H to 25),
        mapOf(L to 9, M to 17, Q to 23, H to 34),
        mapOf(L to 9, M to 18, Q to 25, H to 30),
        mapOf(L to 10, M to 20, Q to 27, H to 32),
        mapOf(L to 12, M to 21, Q to 29, H to 35),
        mapOf(L to 12, M to 23, Q to 34, H to 37),
        mapOf(L to 12, M to 25, Q to 34, H to 40),
        mapOf(L to 13, M to 26, Q to 35, H to 42),
        mapOf(L to 14, M to 28, Q to 38, H to 45),
        mapOf(L to 15, M to 29, Q to 40, H to 48), // version 30
        mapOf(L to 16, M to 31, Q to 43, H to 51),
        mapOf(L to 17, M to 33, Q to 45, H to 54),
        mapOf(L to 18, M to 35, Q to 48, H to 57),
        mapOf(L to 19, M to 37, Q to 51, H to 60),
        mapOf(L to 19, M to 38, Q to 53, H to 63),
        mapOf(L to 20, M to 40, Q to 56, H to 66),
        mapOf(L to 21, M to 43, Q to 59, H to 70),
        mapOf(L to 22, M to 45, Q to 62, H to 74),
        mapOf(L to 24, M to 47, Q to 65, H to 77),
        mapOf(L to 25, M to 49, Q to 68, H to 81) // version 40
)


fun blockCount(version: Int, correctionLevel: ErrorCorrectionLevels): Int =
        if (version !in MIN_VERSION..MAX_VERSION)
            throw IllegalArgumentException("Incorrect version: $version")
        else blockCount[version][correctionLevel]!!


// correlation of generator polynomial depends on correction byte count
val generatorPolynomial = mapOf(
        7 to intArrayOf(87, 229, 146, 149, 238, 102, 21),
        10 to intArrayOf(251, 67, 46, 61, 118, 70, 64, 94, 32, 45),
        13 to intArrayOf(74, 152, 176, 100, 86, 100, 106, 104, 130, 218, 206, 140, 78),
        15 to intArrayOf(8, 183, 61, 91, 202, 37, 51, 58, 58, 237, 140, 124, 5, 99, 105),
        16 to intArrayOf(120, 104, 107, 109, 102, 161, 76, 3, 91, 191, 147, 169, 182, 194, 225, 120),
        17 to intArrayOf(43, 139, 206, 78, 43, 239, 123, 206, 214, 147, 24, 99, 150, 39, 243, 163, 136),
        18 to intArrayOf(215, 234, 158, 94, 184, 97, 118, 170, 79, 187, 152, 148, 252, 179, 5, 98, 96, 153),
        20 to intArrayOf(17, 60, 79, 50, 61, 163, 26, 187, 202, 180, 221, 225, 83, 239, 156, 164, 212, 212, 188, 190),
        22 to intArrayOf(210, 171, 247, 242, 93, 230, 14, 109, 221, 53, 200, 74, 8, 172, 98, 80, 219, 134, 160, 105, 165, 231),
        24 to intArrayOf(229, 121, 135, 48, 211, 117, 251, 126, 159, 180, 169, 152, 192, 226, 228, 218, 111, 0, 117, 232, 87, 96, 227, 21),
        26 to intArrayOf(173, 125, 158, 2, 103, 182, 118, 17, 145, 201, 111, 28, 165, 53, 161, 21, 245, 142, 13, 102, 48, 227, 153, 145, 218, 70),
        28 to intArrayOf(168, 223, 200, 104, 224, 234, 108, 180, 110, 190, 195, 147, 205, 27, 232, 201, 21, 43, 245, 87, 42, 195, 212, 119, 242, 37, 9, 123),
        30 to intArrayOf(41, 173, 145, 152, 216, 31, 179, 182, 50, 48, 110, 86, 239, 96, 222, 125, 42, 173, 226, 193, 224, 130, 156, 37, 251, 216, 238, 40, 192, 180),
)

private val galoisField = IntArray(256) { -1 }

fun galoisField(i: Int): Int {
    if (galoisField[i] == -1) {
        galoisField[i] = when (i) {
            0 -> 1
            1 -> 2
            255 -> 0
            else -> {
                val value = galoisField(i - 1) * 2
                if (value > 255) value xor 285 else value  // Нужно делать XOR с 285 при переполнении
            }
        }

    }
    return galoisField[i]
}

fun reversedGaloisField(value: Int): Int {
    var result = 0
    for (i in galoisField.indices) {
        if (galoisField(i) == value)
            result = i
    }
    return result
}


fun minVersion(bitCount: Int, errorCorrectionLevel: ErrorCorrectionLevels): Int {
    var version = 1
    while (bitCount > maxBits(version, errorCorrectionLevel) ?: throw IllegalArgumentException("Too many information. You can't encode this with QR-coe")) {
        version++
    }

    return version
}

fun combinations(n: Int, arr: IntArray, list: MutableList<IntArray>) {
    // Calculate the number of arrays we should create
    val numArrays = arr.size.toDouble().pow(n.toDouble()).toInt()
    // Create each array
    for (i in 0 until numArrays) {
        val current = IntArray(n)
        // Calculate the correct item for each position in the array
        for (j in 0 until n) {
            // This is the period with which this position changes, i.e.
            // a period of 5 means the value changes every 5th array
            val period = arr.size.toDouble().pow(n - j - 1.toDouble()).toInt()
            // Get the correct item and set it
            val index = i / period % arr.size
            current[j] = arr[index]
        }
        list.add(current)
    }
}