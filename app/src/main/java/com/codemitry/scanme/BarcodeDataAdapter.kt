package com.codemitry.scanme

import android.view.View
import android.widget.EditText
import com.codemitry.qr_code_generator_lib.qrcode.Formats
import com.codemitry.qr_code_generator_lib.qrcode.encoding.FormattedByte
import com.codemitry.qr_code_generator_lib.qrcode.encoding.Text

class BarcodeDataAdapter(private val layout: View, val format: Formats) {
    val formatted: FormattedByte
        get() {
            val formatted: FormattedByte
            when (format) {
                Formats.TEXT -> {
                    formatted = Text(layout.findViewById<EditText>(R.id.text).text.toString())
                }
                else -> formatted = Text("")
            }
            return formatted
        }
}