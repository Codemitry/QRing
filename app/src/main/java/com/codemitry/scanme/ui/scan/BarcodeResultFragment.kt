package com.codemitry.scanme.ui.scan

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.codemitry.qr_code_generator_lib.qrcode.Barcode
import com.codemitry.qr_code_generator_lib.qrcode.Formats
import com.codemitry.qr_code_generator_lib.qrcode.encoding.*
import com.codemitry.scanme.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

private const val REQUEST_CODE_PERMISSION = 1344


interface OnCancelListener {
    fun onCancel()
}

class BarcodeResultFragment(private val qrCode: Barcode) : BottomSheetDialogFragment() {

    var onCancelListener: OnCancelListener? = null

    override fun onCancel(dialog: DialogInterface) {
        super.onCancel(dialog)
        onCancelListener?.onCancel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(inflatedView(qrCode.format), container)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (qrCode.format) {
            Formats.TEXT -> {
                view.findViewById<TextView>(R.id.text).text = qrCode.displayValue
                view.findViewById<View>(R.id.copy).setOnClickListener {
                    copyToClipboard(qrCode.displayValue)
                    showCopiedMessage()
                }
            }
            Formats.URL -> {
                view.findViewById<TextView>(R.id.link).text = (qrCode.data as Url).url

                view.findViewById<View>(R.id.copy).setOnClickListener {
                    copyToClipboard(qrCode.displayValue)
                    showCopiedMessage()
                }
                view.findViewById<View>(R.id.openLink).setOnClickListener {
                    openUrl((qrCode.data as Url).url)
                }
            }
            Formats.WIFI -> {
                view.findViewById<TextView>(R.id.ssid).text = (qrCode.data as WiFi).ssid

                view.findViewById<View>(R.id.connectWifi).setOnClickListener {
                    connectToWifi(qrCode.data as WiFi)
                }
            }

            Formats.EMAIL -> {
                val email = qrCode.data as Email
                view.findViewById<TextView>(R.id.address).text = email.address


                if (email.message.isEmpty()) {
                    view.findViewById<View>(R.id.messageLayout).visibility = View.GONE
                } else {
                    view.findViewById<TextView>(R.id.message).text = (email.message)
                }

                if (email.topic.isEmpty()) {
                    view.findViewById<View>(R.id.subjectLayout).visibility = View.GONE
                } else {
                    view.findViewById<TextView>(R.id.subject).text = email.topic
                }

                view.findViewById<View>(R.id.sendEmail).setOnClickListener {
                    sendEmail(email)
                }

            }

            Formats.SMS -> {
                val sms = qrCode.data as Sms
                if (sms.phone.isEmpty()) {
                    view.findViewById<View>(R.id.numberLayout).visibility = View.GONE
                } else {
                    view.findViewById<TextView>(R.id.number).text = sms.phone
                }

                if (sms.message.isEmpty()) {
                    view.findViewById<View>(R.id.messageLayout).visibility = View.GONE
                } else {
                    view.findViewById<TextView>(R.id.message).text = sms.message
                }

                view.findViewById<View>(R.id.sendSms).setOnClickListener {
                    sendSms(sms)
                }
            }

            Formats.CONTACT_INFO -> {
                val vcard = qrCode.data as VCard

                println("contact:")
                println("$vcard")
                view.findViewById<TextView>(R.id.name).text = ("${vcard.name} ${vcard.surname}")

                if (vcard.phone.isEmpty())
                    view.findViewById<View>(R.id.number).visibility = View.GONE
                else
                    view.findViewById<TextView>(R.id.number).text = vcard.phone

                if (vcard.email.isEmpty()) {
                    view.findViewById<View>(R.id.emailLayout).visibility = View.GONE
                } else {
                    view.findViewById<TextView>(R.id.email).text = vcard.email
                }

                if (vcard.company.isEmpty()) {
                    view.findViewById<View>(R.id.companyLayout).visibility = View.GONE
                } else {
                    view.findViewById<TextView>(R.id.company).text = vcard.company
                }

                if (vcard.jobTitle.isEmpty()) {
                    view.findViewById<View>(R.id.jobLayout).visibility = View.GONE
                } else {
                    view.findViewById<TextView>(R.id.job).text = vcard.jobTitle
                }

                if (vcard.street.isEmpty() && vcard.city.isEmpty() && vcard.country.isEmpty()) {
                    view.findViewById<View>(R.id.addressLayout).visibility = View.GONE
                } else {
                    view.findViewById<TextView>(R.id.address).text = ("${vcard.country} ${vcard.city} ${vcard.street}")
                }

                if (vcard.website.isEmpty()) {
                    view.findViewById<View>(R.id.websiteLayout).visibility = View.GONE
                } else {
                    view.findViewById<TextView>(R.id.website).text = vcard.website
                }


                view.findViewById<View>(R.id.addContact).setOnClickListener {
                    addContact(vcard)
                }
            }

            Formats.LOCATION -> {
                val location = qrCode.data as Location
                view.findViewById<TextView>(R.id.latitude).text = location.latitude
                view.findViewById<TextView>(R.id.longitude).text = location.longitude


                view.findViewById<View>(R.id.openLocation).setOnClickListener {
                    openLocation(location)
                }
            }

            // TODO: Make calendar event
//            Formats.CALENDAR_EVENT -> {
//                val event = qrCode.data as
//                (view.findViewById<TextView>(R.id.date)).text = (
//                        barcode.calendarEvent.start.day + "." +
//                                barcode.calendarEvent.start.month + "." +
//                                barcode.calendarEvent.start.year + " " +
//                                getString(R.string.at) + " " +
//                                barcode.calendarEvent.start.hours + ":" +
//                                barcode.calendarEvent.start.minutes
//                        ));
//
//                (view.findViewById<TextView>(R.id.summary)).setText(
//                        (barcode.calendarEvent.summary + "\n" + barcode.calendarEvent.description));
//
//
//                view.findViewById(R.id.addToCalendar).setOnClickListener((View v)
//                -> {
//                    addToCalendar(barcode.calendarEvent);
//                });
//            }

            else -> {
                view.findViewById<TextView>(R.id.text).text = qrCode.displayValue
                view.findViewById<View>(R.id.copy).setOnClickListener {
                    copyToClipboard(qrCode.displayValue)
                    showCopiedMessage()
                }
            }
        }
    }

    private fun showCopiedMessage() {
        Toast.makeText(context, R.string.copied, Toast.LENGTH_SHORT).show()
    }

    private fun copyToClipboard(charSequence: CharSequence) {
        val clipboard = context?.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("", charSequence)
        clipboard.setPrimaryClip(clip)
    }

    private fun openUrl(url: String) {
        val i = Intent(Intent.ACTION_VIEW)
        i.data = Uri.parse(url)
        startActivity(i)
    }

    private fun inflatedView(format: Formats) = when (format) {
        Formats.TEXT -> R.layout.text
        Formats.URL -> R.layout.url
        Formats.WIFI -> R.layout.wifi
        Formats.EMAIL -> R.layout.email
        Formats.SMS -> R.layout.sms
        Formats.CONTACT_INFO -> R.layout.vcard
        Formats.LOCATION -> R.layout.location

        else -> R.layout.text
    }

    private fun connectToWifi(wifi: WiFi) {
        // TODO: Возможно неожиданное поведение на Android 8.1, 9, 10 + ?
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val wifiManager = requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            if (!wifiManager.isWifiEnabled) {
                val panelIntent = Intent(Settings.Panel.ACTION_WIFI)
                startActivity(panelIntent)
            }
            val suggestionBuilder = WifiNetworkSuggestion.Builder()
                    .setSsid(wifi.ssid)
            if (wifi.encryption === WiFi.Encryption.WPA) {
                suggestionBuilder.setWpa2Passphrase(wifi.password)
            }

            val suggestion = suggestionBuilder.build()
            val suggestions: MutableList<WifiNetworkSuggestion> = ArrayList()
            suggestions.add(suggestion)
            val result = wifiManager.addNetworkSuggestions(suggestions)
            if (result == WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_DUPLICATE) {
                wifiManager.removeNetworkSuggestions(suggestions)
                wifiManager.addNetworkSuggestions(suggestions)

            }
        } else {
            val wifiConfiguration = WifiConfiguration()
            wifiConfiguration.SSID = "\"" + wifi.ssid + "\""
            when (wifi.encryption) {
                WiFi.Encryption.WEP -> {
                    wifiConfiguration.wepKeys[0] = "\"" + wifi.password + "\""
                    wifiConfiguration.wepTxKeyIndex = 0
                    wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
                    wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40)
                }
                WiFi.Encryption.WPA -> wifiConfiguration.preSharedKey = "\"" + wifi.password + "\""
                WiFi.Encryption.OPEN -> wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE)
            }
            val wifiManager = requireContext().applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiManager.addNetwork(wifiConfiguration)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE_PERMISSION)
                return
            }
            if (!wifiManager.isWifiEnabled) {
                wifiManager.isWifiEnabled = true
            }
            val netID = wifiManager.addNetwork(wifiConfiguration)
            wifiManager.disconnect()
            wifiManager.enableNetwork(netID, true)
            wifiManager.reconnect()
        }
    }

    private fun sendSms(sms: Sms) {
        val smsIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + sms.phone))
        smsIntent.putExtra("sms_body", sms.message)
        startActivity(smsIntent)
    }

    private fun sendEmail(email: Email) {
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email.address, null))
        //        emailIntent.setType("plain/text");

        // address
        emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(email.address))

        // subject
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, email.topic)

        // message
        emailIntent.putExtra(Intent.EXTRA_TEXT, email.message)
        startActivity(Intent.createChooser(emailIntent, getString(R.string.send_email)))
    }

    private fun addContact(contact: VCard) {
        val intent = Intent(ContactsContract.Intents.Insert.ACTION)
        intent.type = ContactsContract.RawContacts.CONTENT_TYPE
        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, contact.email)
                .putExtra(ContactsContract.Intents.Insert.PHONE, contact.phone)
                .putExtra(ContactsContract.Intents.Insert.NAME, "${contact.name} ${contact.surname}")
//                .putExtra(ContactsContract.Intents.Insert.COMPANY, contact.organization)
//                .putExtra(ContactsContract.Intents.Insert.JOB_TITLE, contact.title)
                .putExtra(ContactsContract.CommonDataKinds.Website.URL, contact.website)
        startActivity(intent)
    }

    private fun openLocation(geoPoint: Location) {
        val uri = java.lang.String.format(Locale.ENGLISH, "geo:%f,%f", geoPoint.latitude, geoPoint.longitude)
        val intentLocation = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
        startActivity(intentLocation)
    }

    // TODO: Adding to calendar
//    private fun addToCalendar(calendarEvent: Calendar) {
////        ContentValues event = new ContentValues();
////
////        event.put("calendar_id", 1);
////        event.put("title", calendarEvent.summary);
////        event.put("description", calendarEvent.description);
////        event.put("eventLocation", calendarEvent.location);
//        val startDate = GregorianCalendar(
//                calendarEvent.start.year,
//                calendarEvent.start.month,
//                calendarEvent.start.day,
//                calendarEvent.start.hours,
//                calendarEvent.start.minutes,
//                calendarEvent.start.seconds)
//        val endDate = GregorianCalendar(
//                calendarEvent.end.year,
//                calendarEvent.end.month,
//                calendarEvent.end.day,
//                calendarEvent.end.hours,
//                calendarEvent.end.minutes,
//                calendarEvent.end.seconds)
//
////        event.put("dtstart", startDate.getTimeInMillis());
////        event.put("dtend", endDate.getTimeInMillis());
////        event.put("eventStatus", calendarEvent.status);
//
//
////        Calendar calendar = Calendar.getInstance();
//        val i = Intent(Intent.ACTION_EDIT)
//        i.type = "vnd.android.cursor.item/event"
//        i.putExtra("beginTime", startDate.timeInMillis)
//        i.putExtra("endTime", endDate.timeInMillis)
//        i.putExtra("title", calendarEvent.summary)
//        i.putExtra("description", calendarEvent.description)
//        i.putExtra("status", calendarEvent.status)
//        i.putExtra("location", calendarEvent.location)
//        startActivity(i)
//    }

    // TODO: Request permission on wifi
    /*
        @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE_PERMISSION) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            }
            connectToWifi(barcode.wifi);
        }

    }
     */

}