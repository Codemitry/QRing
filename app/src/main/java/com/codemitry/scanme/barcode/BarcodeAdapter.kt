package com.codemitry.scanme.barcode

import com.codemitry.qr_code_generator_lib.qrcode.Barcode
import com.codemitry.qr_code_generator_lib.qrcode.Formats
import com.codemitry.qr_code_generator_lib.qrcode.correction.ErrorCorrectionLevels
import com.codemitry.qr_code_generator_lib.qrcode.encoding.*

class BarcodeAdapter {

    companion object {
        fun barcode(from: com.google.mlkit.vision.barcode.Barcode): Barcode {
            val format: Formats
            val data: FormattedData =
                    when (from.valueType) {
                        com.google.mlkit.vision.barcode.Barcode.TYPE_CONTACT_INFO -> {
                            format = Formats.CONTACT_INFO

                            VCard(
                                    from.contactInfo?.name?.first ?: "",
                                    from.contactInfo?.name?.last ?: "",
                                    from.contactInfo?.phones?.get(0)?.number ?: "",
                                    from.contactInfo?.emails?.get(0)?.address ?: "",
                                    from.contactInfo?.title ?: "",
                                    "", // don't know
                                    from.contactInfo?.addresses?.get(0)?.addressLines?.get(0)
                                            ?: "", // fix!
                                    from.contactInfo?.urls?.get(0) ?: "",
                                    from.contactInfo?.name?.formattedName ?: ""
                            )
                        }
                        com.google.mlkit.vision.barcode.Barcode.TYPE_EMAIL -> {
                            format = Formats.EMAIL
                            Email(from.email?.address ?: "", from.email?.subject
                                    ?: "", from.email?.body
                                    ?: "")
                        }
                        com.google.mlkit.vision.barcode.Barcode.TYPE_ISBN -> {
                            format = Formats.TEXT; Text("")
                        }
                        com.google.mlkit.vision.barcode.Barcode.TYPE_PHONE -> {
                            format = Formats.PHONE
                            Phone(from.phone?.number ?: "")
//            newBarcode.phone = com.codemitry.scanme.barcode.Barcode.getPhone(from.phone)
                        }
                        com.google.mlkit.vision.barcode.Barcode.TYPE_PRODUCT -> {
                            format = Formats.TEXT; Text("")
                        }
                        com.google.mlkit.vision.barcode.Barcode.TYPE_SMS -> {
                            format = Formats.SMS
                            Sms(from.sms?.phoneNumber ?: "", from.sms?.message ?: "")
                        }
                        com.google.mlkit.vision.barcode.Barcode.TYPE_TEXT -> {
                            format = Formats.TEXT
                            Text(from.displayValue ?: "")
                        }
                        com.google.mlkit.vision.barcode.Barcode.TYPE_URL -> {
                            format = Formats.URL
                            Url(from.url?.url ?: "")
                        }
                        com.google.mlkit.vision.barcode.Barcode.TYPE_WIFI -> {
                            format = Formats.WIFI

                            val encryption = when (from.wifi?.encryptionType) {
                                com.google.mlkit.vision.barcode.Barcode.WiFi.TYPE_OPEN -> WiFi.Encryption.OPEN
                                com.google.mlkit.vision.barcode.Barcode.WiFi.TYPE_WEP -> WiFi.Encryption.WEP
                                com.google.mlkit.vision.barcode.Barcode.WiFi.TYPE_WPA -> WiFi.Encryption.WPA
                                else -> WiFi.Encryption.WPA2_EAP
                            }
                            WiFi(encryption, from.wifi?.ssid ?: "", from.wifi?.password
                                    ?: "", false)
                        }
                        com.google.mlkit.vision.barcode.Barcode.TYPE_GEO -> {
                            format = Formats.LOCATION
                            Location((from.geoPoint?.lat ?: 0.0).toString(), (from.geoPoint?.lng
                                    ?: 0.0).toString())
                        }
                        com.google.mlkit.vision.barcode.Barcode.TYPE_CALENDAR_EVENT -> {
//            format = Formats.CALENDAR_EVENT
                            format = Formats.TEXT
                            // TODO: write event
                            Text("")
//            newBarcode.calendarEvent = com.codemitry.scanme.barcode.Barcode.getCalendarEvent(from.calendarEvent)
                        }
                        com.google.mlkit.vision.barcode.Barcode.TYPE_DRIVER_LICENSE -> {
//            format = Formats.DRIVER_LICENSE
                            format = Formats.TEXT
                            Text("")
                            // TODO: what will do with license?
//            newBarcode.driverLicense = com.codemitry.scanme.barcode.Barcode.getDriverLicence(from.driverLicense)
                        }
                        else -> {
                            format = Formats.TEXT
                            Text("")
                        }
                    }

            // TODO: replace text to format
            return Barcode(data, ErrorCorrectionLevels.default())
        }
    }
}