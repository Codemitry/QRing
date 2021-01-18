package com.codemitry.scanme

import android.view.ViewGroup
import android.widget.EditText
import com.codemitry.qr_code_generator_lib.qrcode.Formats
import com.codemitry.qr_code_generator_lib.qrcode.encoding.FormattedData
import com.codemitry.qr_code_generator_lib.qrcode.encoding.Text

class BarcodeDataAdapter(private val layout: ViewGroup, val format: Formats) {
    val formatted: FormattedData
        get() {
            val formatted: FormattedData
            when (format) {
                Formats.TEXT -> {
                    formatted = Text(layout.findViewById<EditText>(R.id.textInput).text.toString())
                }
                else -> formatted = Text("")
            }
            return formatted
        }


    companion object {
        fun fillLayout(data: FormattedData, layout: ViewGroup) {
            when (data) {
                is Text -> layout.findViewById<EditText>(R.id.textInput).setText(data.text)
            }
        }
    }
}
