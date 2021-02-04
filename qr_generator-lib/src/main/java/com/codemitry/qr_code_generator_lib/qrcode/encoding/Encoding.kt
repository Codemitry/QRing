package com.codemitry.qr_code_generator_lib.qrcode.encoding

import com.codemitry.qr_code_generator_lib.qrcode.addToHead
import com.codemitry.qr_code_generator_lib.qrcode.correction.charCountIndicatorLength
import java.io.Serializable

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

interface FormattedData : Serializable {
    val formatted: String
    val displayValue: String
}

data class Text(val text: String) : FormattedData {
    override val formatted = text
    override val displayValue: String
        get() = text
}

data class Url(val url: String) : FormattedData {
    override val formatted = "URL:$url"
    override val displayValue: String
        get() = url
}

data class Phone(val phone: String) : FormattedData {
    override val formatted = "tel:$phone"
    override val displayValue: String
        get() = phone
}

data class Sms(val phone: String, val message: String) : FormattedData {
    override val formatted = "smsto:$phone:$message"
    override val displayValue: String
        get() = "$phone: $message"
}

data class EmailAddress(val address: String) : FormattedData {
    override val formatted = "mailto:$address"
    override val displayValue: String
        get() = address
}

data class Email(val address: String, val subject: String, val message: String) : FormattedData {
    constructor(address: String, message: String) : this(address, "", message)

    override val formatted = "MATMSG:TO:$address;SUB:$subject;Body:$message;;"

    override val displayValue: String
        get() = "$address: $subject $message"
}

data class Location(val latitude: String, val longitude: String) : FormattedData {
    override val formatted = "geo:$latitude,$longitude"

    override val displayValue: String
        get() = "Geo: $latitude; $longitude"

    init {
        if (latitude.toDouble() !in -90.0..90.0 || longitude.toDouble() !in -180.0..180.0)
            throw IllegalArgumentException("Incorrect data! Latitude has to be in -90.0..90.0, longitude has to be in -180.0..180.0")
    }
}

data class WiFi(val encryption: Encryption, val ssid: String, val password: String, val hidden: Boolean) : FormattedData {

    enum class Encryption {
        OPEN, WEP, WPA, WPA2_EAP;

        fun value(): String =
                when (this) {
                    OPEN -> ""
                    WEP -> "WEP"
                    WPA -> "WPA"
                    WPA2_EAP -> "WPA2-EAP"
                }
    }

    override val formatted = "WIFI:T:${encryption.value()};S:$ssid;P:$password;H:$hidden;;"

    override val displayValue: String
        get() = "Wi-Fi: $ssid : $password"
}

data class VCard(val name: String, val surname: String, val phone: String, val email: String,
                 val company: String, val jobTitle: String, val street: String, val city: String, val country: String,
                 val website: String) : FormattedData {
    override val formatted = "BEGIN:VCARD\nVERSION:3.0\nN:$surname;$name\nTEL;TYPE=work,voice:$phone\nEMAIL:$email\nORG:$company\nTITLE:$jobTitle\nADR;TYPE=WORK,PREF:;;$street;$city;;;$country\nURL:$website\nEND:VCARD"

    override val displayValue: String
        get() = "vCard: $name $surname $phone $email $website"
}