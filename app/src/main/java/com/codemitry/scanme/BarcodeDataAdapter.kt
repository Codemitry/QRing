package com.codemitry.scanme

import android.view.ViewGroup
import com.codemitry.qr_code_generator_lib.qrcode.Formats
import com.codemitry.qr_code_generator_lib.qrcode.encoding.Email
import com.codemitry.qr_code_generator_lib.qrcode.encoding.FormattedData
import com.codemitry.qr_code_generator_lib.qrcode.encoding.Text
import com.codemitry.qr_code_generator_lib.qrcode.encoding.Url
import com.google.android.material.textfield.TextInputLayout

class BarcodeDataAdapter(private val layout: ViewGroup, val format: Formats) {
    val formatted: FormattedData
        get() {
            val formatted: FormattedData
            when (format) {
                Formats.TEXT -> {
                    formatted = Text(layout.findViewById<TextInputLayout>(R.id.textInput).editText?.text.toString())
                }
                Formats.URL -> {
                    formatted = Url(layout.findViewById<TextInputLayout>(R.id.linkInput).editText?.text.toString())
                }
                Formats.EMAIL -> {
                    formatted = Email(
                            layout.findViewById<TextInputLayout>(R.id.addressInput).editText?.text.toString(),
                            layout.findViewById<TextInputLayout>(R.id.subjectInput).editText?.text.toString(),
                            layout.findViewById<TextInputLayout>(R.id.messageInput).editText?.text.toString()
                    )
                }
                else -> formatted = Text("")
            }
            return formatted
        }


    companion object {
        fun fillLayout(data: FormattedData, layout: ViewGroup) {
            when (data) {
                is Text -> layout.findViewById<TextInputLayout>(R.id.textInput).editText?.setText(data.text)
                is Url -> layout.findViewById<TextInputLayout>(R.id.linkInput).editText?.setText(data.url)
                is Email -> {
                    layout.findViewById<TextInputLayout>(R.id.addressInput).editText?.setText(data.address)
                    layout.findViewById<TextInputLayout>(R.id.subjectInput).editText?.setText(data.topic)
                    layout.findViewById<TextInputLayout>(R.id.messageInput).editText?.setText(data.message)
                }
            }
        }
    }
}
