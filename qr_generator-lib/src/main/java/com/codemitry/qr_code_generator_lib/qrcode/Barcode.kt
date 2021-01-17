package com.codemitry.qr_code_generator_lib.qrcode

import com.codemitry.qr_code_generator_lib.qrcode.correction.ErrorCorrectionLevels
import com.codemitry.qr_code_generator_lib.qrcode.correction.mergeDataAndCorrectionBlocks
import com.codemitry.qr_code_generator_lib.qrcode.encoding.*
import com.codemitry.qr_code_generator_lib.qrcode.encoding.DataConverter.EncodingMode
import com.codemitry.qr_code_generator_lib.qrcode.encoding.DataConverter.getEncodingMode
import com.codemitry.qr_code_generator_lib.qrcode.encoding.DataConverter.getModeIndicator
import java.util.*

enum class Formats {
    TEXT, URL, EMAIL, CONTACT_INFO, PHONE, SMS, WIFI, GEO, CALENDAR_EVENT, DRIVER_LICENSE, ISBN, PRODUCT
}

enum class ModuleTypes {
    FINDER, ALIGNMENT, TIMING, DATA, DARK_MODULE, CORRECTION_MASK, VERSION
}

enum class ModuleState {
    TRUE, FALSE
}

const val MIN_VERSION = 1
const val MAX_VERSION = 40

@ExperimentalUnsignedTypes
class Barcode(data: String, val correction: ErrorCorrectionLevels, var mask: Int) {
    constructor(data: String, correction: ErrorCorrectionLevels) : this(data, correction, 0)

    var version = 0
        private set

    private val modulesType = mutableMapOf<Pair<Int, Int>, ModuleTypes>()
    private val modulesState = mutableMapOf<Pair<Int, Int>, ModuleState>()

    private val size: Int

    private fun free(x: Int, y: Int): Boolean = !modulesType.containsKey(x to y)

    private fun drawFinderPattern(x: Int, y: Int) {
        // Внутренний закрашенный квадрат 3x3
        for (i in x - 1..x + 1) {
            for (j in y - 1..y + 1) {

                modulesType[i to j] = ModuleTypes.FINDER
                modulesState[i to j] = ModuleState.TRUE

            }
        }

        // Незакрашенная обертка 5х5
        for (i in x - 2..x + 2) {

            modulesType[i to y - 2] = ModuleTypes.FINDER
            modulesState[i to y - 2] = ModuleState.FALSE

            modulesType[i to y + 2] = ModuleTypes.FINDER
            modulesState[i to y + 2] = ModuleState.FALSE
        }
        for (j in y - 1..y + 1) {

            modulesType[x - 2 to j] = ModuleTypes.FINDER
            modulesState[x - 2 to j] = ModuleState.FALSE

            modulesType[x + 2 to j] = ModuleTypes.FINDER
            modulesState[x + 2 to j] = ModuleState.FALSE
        }

        // Закрашенная обертка 7х7
        for (i in x - 3..x + 3) {

            modulesType[i to y - 3] = ModuleTypes.FINDER
            modulesState[i to y - 3] = ModuleState.TRUE

            modulesType[i to y + 3] = ModuleTypes.FINDER
            modulesState[i to y + 3] = ModuleState.TRUE
        }
        for (j in y - 2..y + 2) {

            modulesType[x - 3 to j] = ModuleTypes.FINDER
            modulesState[x - 3 to j] = ModuleState.TRUE

            modulesType[x + 3 to j] = ModuleTypes.FINDER
            modulesState[x + 3 to j] = ModuleState.TRUE
        }

        // Если узор является левым верхним, то справа и снизу рисуется обертка
        if (x - 3 == 0 && y - 3 == 0) {
            for (i in x - 3..x + 4) {

                modulesType[i to y + 4] = ModuleTypes.FINDER
                modulesState[i to y + 4] = ModuleState.FALSE
            }
            for (j in y - 3..y + 3) {

                modulesType[x + 4 to j] = ModuleTypes.FINDER
                modulesState[x + 4 to j] = ModuleState.FALSE
            }
            // Если узор является правым верхним, то слева и снизу рисуется обертка
        } else if (x + 3 == size - 1 && y - 3 == 0) {
            for (i in x - 4..x + 3) {

                modulesType[i to y + 4] = ModuleTypes.FINDER
                modulesState[i to y + 4] = ModuleState.FALSE
            }
            for (j in y - 3..y + 3) {

                modulesType[x - 4 to j] = ModuleTypes.FINDER
                modulesState[x - 4 to j] = ModuleState.FALSE

            }
            // Если узор является левым нижним, то справа и сверху рисуется обертка
        } else if (x - 3 == 0 && y + 3 == size - 1) {
            for (i in x - 3..x + 4) {

                modulesType[i to y - 4] = ModuleTypes.FINDER
                modulesState[i to y - 4] = ModuleState.FALSE

            }
            for (j in y - 3..y + 3) {

                modulesType[x + 4 to j] = ModuleTypes.FINDER
                modulesState[x + 4 to j] = ModuleState.FALSE

            }
        }
    }

    val timingX = 6
    val timingY = 6

    private fun drawTimingPatterns() {
        val colors = arrayOf(ModuleState.FALSE, ModuleState.TRUE)

        // горизонтальный узор
        var colorIdx = 1
        for (i in 8..size - 8) {

            modulesType[i to timingY] = ModuleTypes.TIMING
            modulesState[i to timingY] = colors[colorIdx]

            colorIdx = (colorIdx + 1) % 2
        }

        // вертикальный узор
        colorIdx = 1
        for (j in 8..size - 8) {

            modulesType[timingX to j] = ModuleTypes.TIMING
            modulesState[timingX to j] = colors[colorIdx]

            colorIdx = (colorIdx + 1) % 2
        }
    }

    private fun drawDarkModule() {
        modulesType[8 to size - 8] = ModuleTypes.DARK_MODULE
        modulesState[8 to size - 8] = ModuleState.TRUE
    }

    private fun drawAllAlignmentPatterns(version: Int) {
        if (version == 1) return
        val coords: MutableList<IntArray> = ArrayList()
        val arrayCoords = alignmentPatternsCenters(version)
        combinations(2, arrayCoords, coords)
        if (version > 6) {
            // Удаление правого верхнего выравнивающего узора
            coords.removeAt(arrayCoords.size * (arrayCoords.size - 1))
            // Удаление левого нижнего выравнивающего узора
            coords.removeAt(arrayCoords.size - 1)
            // Удаление левого верхнего выравнивающего узора
            coords.removeAt(0)
        }
        for (current in coords) {
            drawAlignmentPattern(current[0], current[1])
        }
    }

    private fun drawAlignmentPattern(x: Int, y: Int) {
        // Закрашенный модуль посередине

        modulesType[x to y] = ModuleTypes.ALIGNMENT
        modulesState[x to y] = ModuleState.TRUE

        // Вокруг него незакрашенная рамка
        for (i in x - 1..x + 1) {

            modulesType[i to y - 1] = ModuleTypes.ALIGNMENT
            modulesState[i to y - 1] = ModuleState.FALSE

            modulesType[i to y + 1] = ModuleTypes.ALIGNMENT
            modulesState[i to y + 1] = ModuleState.FALSE
        }

        modulesType[x - 1 to y] = ModuleTypes.ALIGNMENT
        modulesState[x - 1 to y] = ModuleState.FALSE

        modulesType[x + 1 to y] = ModuleTypes.ALIGNMENT
        modulesState[x + 1 to y] = ModuleState.FALSE

        // Вокруг этой рамки закрашенная рамка
        for (i in x - 2..x + 2) {

            modulesType[i to y - 2] = ModuleTypes.ALIGNMENT
            modulesState[i to y - 2] = ModuleState.TRUE

            modulesType[i to y + 2] = ModuleTypes.ALIGNMENT
            modulesState[i to y + 2] = ModuleState.TRUE
        }
        for (j in y - 1..y + 1) {

            modulesType[x - 2 to j] = ModuleTypes.ALIGNMENT
            modulesState[x - 2 to j] = ModuleState.TRUE

            modulesType[x + 2 to j] = ModuleTypes.ALIGNMENT
            modulesState[x + 2 to j] = ModuleState.TRUE
        }
    }

    private fun drawVersionCodes() {
        val x = 0
        val y = size - 11
        val versionCode = versionCodes[version]
        for (j in y until y + 3) {
            val mask = versionCode shr (2 - (j - y)) * 6 and 63
            for (i in x until x + 6) {
                val bit = mask shr 5 - i and 1
                val state = if (bit == 1) ModuleState.TRUE else ModuleState.FALSE

                modulesType[i to j] = ModuleTypes.VERSION
                modulesState[i to j] = state

                // И в (j, i) отобразить код версии

                modulesType[j to i] = ModuleTypes.VERSION
                modulesState[j to i] = state
            }
        }
    }

    @ExperimentalUnsignedTypes
    private fun drawData(dataSequence: UByteArray) {
        fun getBits(byte: UByte): Array<Boolean> = Array(8) { bit ->
            ((byte.toInt() shr bit) and 1) == 1
        }

        var dataIdx = 0
        var bit = 8
        var bits = getBits(dataSequence[dataIdx])

        fun nextBit(): Boolean {
            if (bit == 0) {
                if (dataIdx == dataSequence.lastIndex) {
                    return false
                }
                dataIdx++
                bit = 7
                bits = getBits(dataSequence[dataIdx])
            } else {
                bit--
            }

            return bits[bit]
        }

        // direction of vertical moving
        var from = size - 1
        var to = 0

        var j = size - 1

        while (j > 0) {

            // skip vertical timing pattern
            if (j == timingX)
                j--

            val range = if (from < to) from..to else from downTo to
            for (i in range) {

                if (free(j, i)) {
                    val state = if (nextBit()) ModuleState.TRUE else ModuleState.FALSE
                    modulesType[j to i] = ModuleTypes.DATA
                    modulesState[j to i] = state
                }

                if (free(j - 1, i)) {
                    val state = if (nextBit()) ModuleState.TRUE else ModuleState.FALSE

                    modulesType[j - 1 to i] = ModuleTypes.DATA
                    modulesState[j - 1 to i] = state
                }
            }
            val tmp = from
            from = to
            to = tmp

            j -= 2
        }

    }

    private fun reserveFormatInformationArea() {

        // bottom left
        for (y in size - 7 until size) {

            modulesType[8 to y] = ModuleTypes.CORRECTION_MASK
        }

        // top right
        for (x in size - 8 until size) {
            modulesType[x to 8] = ModuleTypes.CORRECTION_MASK

        }

        // top left
        // top right of block
        for (y in 0..5) {
            modulesType[8 to y] = ModuleTypes.CORRECTION_MASK
        }
        // bottom left of block
        for (x in 0..5) {
            modulesType[x to 8] = ModuleTypes.CORRECTION_MASK
        }

        // 3 pixels in bottom right

        modulesType[7 to 8] = ModuleTypes.CORRECTION_MASK
        modulesType[8 to 8] = ModuleTypes.CORRECTION_MASK
        modulesType[8 to 7] = ModuleTypes.CORRECTION_MASK
    }

    private fun encode(string: String, correctionLevel: ErrorCorrectionLevels): String {
        val encode = getEncodingMode(string)
        val encodedString: String
        val modeIndicator: String
        when (encode) {
            EncodingMode.NUMERIC -> {
                encodedString = Numeric.numericEncoding(string)
                modeIndicator = getModeIndicator(DataConverter.ModeIndicator.NUMERIC)
            }
            EncodingMode.ALPHANUMERIC -> {
                encodedString = AlphaNumeric.alphaNumericEncoding(string)
                modeIndicator = getModeIndicator(DataConverter.ModeIndicator.ALPHANUMERIC)
            }
//            EncodingMode.BYTE -> {
//                encodedString = Byte.byteEncoding(string)
//                modeIndicator = DataConverter.getModeIndicator(DataConverter.ModeIndicator.BYTE)
//            }
            else -> {
                encodedString = com.codemitry.qr_code_generator_lib.qrcode.encoding.Byte.byteEncoding(string)

                // ECI often unsupported by QR scanners
                val mode = getModeIndicator(DataConverter.ModeIndicator.BYTE)
//                for (c in string) {
//                    if (c !in 'a'..'z' && c !in 'A'..'Z') {
//                        mode = DataConverter.getModeIndicator(DataConverter.ModeIndicator.ECI)
//                        break
//                    }
//                }

                modeIndicator = mode
            }
        }

        var version = minVersion(encodedString.length, correctionLevel)

        var charCountIndicator = StringBuilder(charCountIndicator(string, version, encode))

        var resultString = StringBuilder(modeIndicator + charCountIndicator + encodedString)

        // Если вылезли за границы по количеству данных, нужно увеличить версию
        if (version < minVersion(resultString.length, correctionLevel)) {
            version++

            charCountIndicator = StringBuilder(charCountIndicator(string, version, encode))
            resultString = StringBuilder(modeIndicator + charCountIndicator + encodedString)
        }

        // Установка версии данного экземпляра
        this.version = version

        // Теперь последовательность нужно дополнить нулями справа до целого количества байт
        val needZeros = (8 - resultString.length % 8) % 8

        addToTail(resultString, "0", resultString.length + needZeros)

        println("mode: $modeIndicator, charcount: $charCountIndicator")
        // Чередующиеся цепочки байт для заполнения данными до границы вместимости по версии
        val alternation = arrayOf("11101100", "00010001")
        val necessaryBytes = (maxBits(version, correctionLevel) - resultString.length) / 8
        for (i in 0 until necessaryBytes) {
            resultString.append(alternation[i % 2])
        }

        return resultString.toString()
    }

    private fun applyMask(mask: (x: Int, y: Int) -> Boolean) {

        fun invert(x: Int, y: Int) {
            when (modulesState[x to y]) {
                ModuleState.TRUE -> modulesState[x to y] = ModuleState.FALSE
                ModuleState.FALSE -> modulesState[x to y] = ModuleState.TRUE
            }
        }

        val data = modulesType.filter { it.value == ModuleTypes.DATA }

        for (entry in data.keys) {
            if (mask(entry.first, entry.second)) invert(entry.first, entry.second)
        }
    }

    private fun drawFormatAndVersionInformation() {

        fun state(bit: Char) = if (bit == '1') ModuleState.TRUE else ModuleState.FALSE
        val sequence = getFormatAndVersionInformation(correction, mask)
        var idx = 0

        // bottom left
        for (y in size - 1 downTo size - 7) {

            modulesState[8 to y] = state(sequence[idx++])
        }

        // top right
        for (x in size - 8 until size) {
            modulesState[x to 8] = state(sequence[idx++])

        }

        idx = 0

        // top left

        // bottom left of block
        for (i in 0..5) {
            modulesState[i to 8] = state(sequence[idx++])
        }

        // 3 pixels in bottom right

        modulesState[7 to 8] = state(sequence[idx++])
        modulesState[8 to 8] = state(sequence[idx++])
        modulesState[8 to 7] = state(sequence[idx++])

        // top right of block
        for (j in 5 downTo 0) {
            modulesState[8 to j] = state(sequence[idx++])
        }
    }

    fun get(): Map<Pair<Int, Int>, ModuleState> = modulesState

    init {
        val encoded = encode(data, correction)

        this.size = qrCodeSize(version)

        drawFinderPattern(3, 3)
        drawFinderPattern(size - 4, 3)
        drawFinderPattern(3, size - 4)
        drawAllAlignmentPatterns(version)
        drawTimingPatterns()
        drawDarkModule()
        reserveFormatInformationArea()

        val dataSequence = mergeDataAndCorrectionBlocks(getBlocks(encoded, blockCount(version, correction), version, correction), version, correction)

        if (this.version >= 7)
            drawVersionCodes()

        drawData(dataSequence)
        applyMask(masks[mask]!!)

        drawFormatAndVersionInformation()

//        for (entry in modulesState) {
//            image.setPixel(entry.key.first, entry.key.second, entry.value)
//        }
    }
}