package com.codemitry.qr_code_generator_lib.qrcode.encoding

import com.codemitry.qr_code_generator_lib.qrcode.addToHead
import com.codemitry.qr_code_generator_lib.qrcode.correction.charCountIndicatorLength

@ExperimentalUnsignedTypes
fun charCountIndicator(data: String, version: Int, encoding: DataConverter.EncodingMode): String {
    val lengthOfMessage = if (encoding == DataConverter.EncodingMode.BYTE)
        Byte.toBytes(data).size
    else
        data.length
    // raw, only length of message bits after this
    val charCountIndicator = StringBuilder(Integer.toBinaryString(lengthOfMessage))

    val lenCharCountField = charCountIndicatorLength(version, encoding)
    if (charCountIndicator.length < lenCharCountField) {
        // added 0s to achieve necessary length of char count indicator
        addToHead(charCountIndicator, "0", lenCharCountField)
    }
    return charCountIndicator.toString()
}