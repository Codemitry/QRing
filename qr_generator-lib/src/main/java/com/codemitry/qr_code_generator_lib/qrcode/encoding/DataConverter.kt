package com.codemitry.qr_code_generator_lib.qrcode.encoding

import com.codemitry.qr_code_generator_lib.qrcode.correction.ErrorCorrectionLevels
import com.codemitry.qr_code_generator_lib.qrcode.maxBits
import java.nio.charset.Charset


@ExperimentalUnsignedTypes
fun getBlocks(data: UByteArray, blockCount: Int, version: Int, correction: ErrorCorrectionLevels): Array<UByteArray> {

    val complement = maxBits(version, correction) / 8 % blockCount
    val defaultLen = maxBits(version, correction) / 8 / blockCount

    var dataIdx = 0
    // объявление крайних справа массивов байтов с дополнением:
    // blockCount - 1 downTo blockCount - complement
    // ...
    // объявление оставшихся массивов байтов:
    // blockCount - complement - 1 downTo 0

    return Array(blockCount) { i ->
        UByteArray(
                if (i in 0 until blockCount-complement) defaultLen else defaultLen + 1) {
            data[dataIdx++]
        }
    }
}

@ExperimentalUnsignedTypes
fun getBlocks(encodedData: String, blockCount: Int, version: Int, correction: ErrorCorrectionLevels): Array<UByteArray> {

    val complement = maxBits(version, correction) / 8 % blockCount
    val defaultLen = maxBits(version, correction) / 8 / blockCount

    val data = DataConverter.parseByteArrayFromBits(encodedData)

    var dataIdx = 0
    // объявление крайних справа массивов байтов с дополнением:
    // blockCount - 1 downTo blockCount - complement
    // ...
    // объявление оставшихся массивов байтов:
    // blockCount - complement - 1 downTo 0

    return Array(blockCount) { i ->
        UByteArray(
                if (i in 0 until blockCount-complement) defaultLen else defaultLen + 1) {
            data[dataIdx++]
        }
    }


}

object DataConverter {
    fun getModeIndicator(indicator: ModeIndicator?): String = when (indicator) {
        ModeIndicator.NUMERIC -> "0001"
        ModeIndicator.ALPHANUMERIC -> "0010"
        ModeIndicator.BYTE -> "0100"
        ModeIndicator.KANJI -> "1000"
        ModeIndicator.ECI -> "0111"
        else -> "ERROR"
    }

    private fun isDigit(c: Char): Boolean = c in '0'..'9'

    fun getEncodingMode(string: String): EncodingMode {
        var encoding = EncodingMode.NUMERIC
        val len = string.length
        var i = 0
        while (i < len && isDigit(string[i])) {
            i++
        }

        while (i < len && string[i] in AlphaNumeric.alphaNumericTable) {
            encoding = EncodingMode.ALPHANUMERIC
            i++
        }

        if (i < len) encoding = EncodingMode.BYTE

        return encoding
    }

    @ExperimentalUnsignedTypes
    fun parseByteArrayFromBits(bits: String): UByteArray {
        val arrayLen = bits.length / 8 + if (bits.length % 8 != 0) 1 else 0
        val bytes = UByteArray(arrayLen)
        for (i in bytes.indices) {
            bytes[i] = bits.substring(i * 8, i * 8 + 8).toInt(2).toUByte()
        }
        return bytes
    }

    enum class EncodingMode {
        NUMERIC, ALPHANUMERIC, BYTE, KANJI
    }

    enum class ModeIndicator {
        NUMERIC, ALPHANUMERIC, BYTE, KANJI, ECI
    }
}

internal object Numeric {
    private const val BINARY_TRIAD_LEN = 10
    private const val BINARY_BIN_LEN = 7
    private const val BINARY_ONE_LEN = 4
    fun numericEncoding(numbers: String): String {
        val result = StringBuilder()

        // Количество триад
        val count = numbers.length / 3
        val triadsLen = count * 3
        // перевести все триады по 10 бит в двоичное представление и конкатенировать
        var i = 0
        while (i < triadsLen) {
            val binTriad = StringBuilder(decimalToBinary(numbers.substring(i, i + 3).toInt()))
            while (binTriad.length < BINARY_TRIAD_LEN) {
                binTriad.insert(0, "0")
            }
            result.append(binTriad)
            i += 3
        }
        // Если остались биты в конце числа (< 3), то конкатенирует их двоичное представление
        val residueIdx = count * 3
        if (residueIdx < numbers.length) {
            val decResidue = numbers.substring(count * 3)
            val residue = StringBuilder(decimalToBinary(decResidue.toInt()))
            var necessary = 0
            if (decResidue.length == 2) {
                necessary = BINARY_BIN_LEN
            } else if (decResidue.length == 1) {
                necessary = BINARY_ONE_LEN
            }
            while (residue.length < necessary) {
                residue.insert(0, "0")
            }
            result.append(residue)
        }
        return result.toString()
    }

    // decimal has to be greater than or equal 0
    fun decimalToBinary(decimal: Int): String {
        var decimal = decimal
        val result = StringBuilder()
        while (decimal > 0) {
            result.insert(0, decimal % 2)
            decimal /= 2
        }
        return result.toString()
    }
}

internal object AlphaNumeric {
    // idx - number that represents the symbol, value - symbol
    val alphaNumericTable = listOf(
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
            'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J',
            'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T',
            'U', 'V', 'W', 'X', 'Y', 'Z',
            ' ', '$', '%', '*', '+', '-', '.', '/', ':')
    private const val BINARY_BIN_LEN = 11
    private const val BINARY_ONE_LEN = 6
    fun alphaNumericEncoding(string: String): String {
        val result = StringBuilder()
        val count = string.length / 2
        val binsLen = count * 2
        var i = 0
        while (i < binsLen) {
            val code = alphaNumericTable.indexOf(string[i]) * 45 +
                    alphaNumericTable.indexOf(string[i + 1])
            val chain = StringBuilder(Numeric.decimalToBinary(code))
            while (chain.length < BINARY_BIN_LEN) {
                chain.insert(0, "0")
            }
            result.append(chain)
            i += 2
        }

        // если остался последний символ без пары
        if (binsLen != string.length) {
            val residue = StringBuilder(Numeric.decimalToBinary(alphaNumericTable.indexOf(string[string.length - 1])))
            while (residue.length < BINARY_ONE_LEN) residue.insert(0, "0")
            result.append(residue)
        }
        return result.toString()
    }
}

internal object Byte {
    private const val BYTE = 8

    @ExperimentalUnsignedTypes
    fun toBytes(data: String): UByteArray = data.toByteArray(Charset.forName("UTF-8")).toUByteArray()

    @ExperimentalUnsignedTypes
    fun byteEncoding(string: String): String {

        val bytes = toBytes(string)

        val result = StringBuilder()
        for (aByte in bytes) {
            val bin = toBinary(aByte)
            result.append(appendZeroesIfNecessary(StringBuilder(bin)))
        }

        return result.toString()
    }

    private fun appendZeroesIfNecessary(string: StringBuilder): String {
        while (string.length < BYTE) {
            string.insert(0, "0")
        }
        return string.toString()
    }

    @ExperimentalUnsignedTypes
    private fun toBinary(x: UByte): String = Integer.toBinaryString(x.toInt())
}