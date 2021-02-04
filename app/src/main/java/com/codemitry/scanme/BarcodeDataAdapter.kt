package com.codemitry.scanme

import android.graphics.Bitmap
import android.graphics.Color
import android.view.ViewGroup
import android.widget.CheckBox
import com.codemitry.qr_code_generator_lib.qrcode.Formats
import com.codemitry.qr_code_generator_lib.qrcode.ModuleState
import com.codemitry.qr_code_generator_lib.qrcode.encoding.*
import com.google.android.material.textfield.TextInputLayout

val wifiEncryptions = mapOf(WiFi.Encryption.OPEN to "Open", WiFi.Encryption.WEP to "WEP", WiFi.Encryption.WPA to "WPA", WiFi.Encryption.WPA2_EAP to "WPA2-EAP")
val wifiRevEncryptions = mapOf("Open" to WiFi.Encryption.OPEN, "WEP" to WiFi.Encryption.WEP, "WPA" to WiFi.Encryption.WPA, "WPA2-EAP" to WiFi.Encryption.WPA2_EAP)

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

                Formats.CONTACT_INFO -> {
                    formatted = VCard(
                            layout.findViewById<TextInputLayout>(R.id.nameInput).editText?.text.toString(),
                            layout.findViewById<TextInputLayout>(R.id.surnameInput).editText?.text.toString(),
                            layout.findViewById<TextInputLayout>(R.id.phoneInput).editText?.text.toString(),
                            layout.findViewById<TextInputLayout>(R.id.emailInput).editText?.text.toString(),
                            layout.findViewById<TextInputLayout>(R.id.companyInput).editText?.text.toString(),
                            layout.findViewById<TextInputLayout>(R.id.jobInput).editText?.text.toString(),
                            layout.findViewById<TextInputLayout>(R.id.streetInput).editText?.text.toString(),
                            layout.findViewById<TextInputLayout>(R.id.cityInput).editText?.text.toString(),
                            layout.findViewById<TextInputLayout>(R.id.countryInput).editText?.text.toString(),
                            layout.findViewById<TextInputLayout>(R.id.webSiteInput).editText?.text.toString(),
                    )
                }

                Formats.LOCATION -> {
                    formatted = Location(
                            layout.findViewById<TextInputLayout>(R.id.latitudeInput).editText?.text.toString(),
                            layout.findViewById<TextInputLayout>(R.id.longitudeInput).editText?.text.toString(),
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
                    layout.findViewById<TextInputLayout>(R.id.subjectInput).editText?.setText(data.subject)
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
                is VCard -> {
                    layout.findViewById<TextInputLayout>(R.id.nameInput)?.editText?.setText(data.name)
                    layout.findViewById<TextInputLayout>(R.id.surnameInput)?.editText?.setText(data.surname)
                    layout.findViewById<TextInputLayout>(R.id.phoneInput)?.editText?.setText(data.phone)
                    layout.findViewById<TextInputLayout>(R.id.emailInput)?.editText?.setText(data.email)
                    layout.findViewById<TextInputLayout>(R.id.companyInput)?.editText?.setText(data.company)
                    layout.findViewById<TextInputLayout>(R.id.jobInput)?.editText?.setText(data.company)
                    layout.findViewById<TextInputLayout>(R.id.cityInput)?.editText?.setText(data.jobTitle)
                    layout.findViewById<TextInputLayout>(R.id.streetInput)?.editText?.setText(data.street)
                    layout.findViewById<TextInputLayout>(R.id.webSiteInput)?.editText?.setText(data.website)
                }
                is Location -> {
                    layout.findViewById<TextInputLayout>(R.id.latitudeInput)?.editText?.setText(data.latitude)
                    layout.findViewById<TextInputLayout>(R.id.longitudeInput)?.editText?.setText(data.longitude)
                }
            }
        }


        private const val colorOne = Color.BLACK
        private const val colorZero = Color.WHITE

        fun tableToBitmap(table: Array<Array<ModuleState>>): Bitmap {
            val bitmap = Bitmap.createBitmap(table.size, table[0].size, Bitmap.Config.RGB_565)

            for (x in table.indices) {
                for (y in table[x].indices) {
                    bitmap.setPixel(x, y, if (table[x][y] == ModuleState.TRUE) colorOne else colorZero)
                }
            }
            return bitmap
        }
    }
}
