package com.codemitry.scanme

import android.view.ViewGroup
import android.widget.CheckBox
import com.codemitry.qr_code_generator_lib.qrcode.Formats
import com.codemitry.qr_code_generator_lib.qrcode.encoding.*
import com.google.android.material.textfield.TextInputLayout

public val wifiEncryptions = mapOf(WiFi.Encryption.OPEN to "Open", WiFi.Encryption.WEP to "WEP", WiFi.Encryption.WPA to "WPA", WiFi.Encryption.WPA2_EAP to "WPA2-EAP")
public val wifiRevEncryptions = mapOf("Open" to WiFi.Encryption.OPEN, "WEP" to WiFi.Encryption.WEP, "WPA" to WiFi.Encryption.WPA, "WPA2-EAP" to WiFi.Encryption.WPA2_EAP)

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
                Formats.SMS -> {
                    formatted = Sms(
                            layout.findViewById<TextInputLayout>(R.id.numberInput).editText?.text.toString(),
                            layout.findViewById<TextInputLayout>(R.id.messageInput).editText?.text.toString()
                    )
                }

                Formats.WIFI -> {
                    formatted = WiFi(
                            wifiRevEncryptions[layout.findViewById<TextInputLayout>(R.id.encryptionInput)?.editText.toString()]
                                    ?: WiFi.Encryption.OPEN,
                            layout.findViewById<TextInputLayout>(R.id.ssidInput).editText?.text.toString(),
                            layout.findViewById<TextInputLayout>(R.id.passwordInput).editText?.text.toString(),
                            layout.findViewById<CheckBox>(R.id.hiddenCheckBox).isChecked
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
                is Sms -> {
                    layout.findViewById<TextInputLayout>(R.id.numberInput).editText?.setText(data.phone)
                    layout.findViewById<TextInputLayout>(R.id.messageInput).editText?.setText(data.message)

                }
                is WiFi -> {
                    layout.findViewById<TextInputLayout>(R.id.encryptionInput)?.editText?.setText(data.encryption.value())
                    layout.findViewById<TextInputLayout>(R.id.ssidInput).editText?.setText(data.ssid)
                    layout.findViewById<TextInputLayout>(R.id.passwordInput).editText?.setText(data.password)
                    layout.findViewById<CheckBox>(R.id.hiddenCheckBox).isChecked = data.hidden
                }
            }
        }
    }
}
