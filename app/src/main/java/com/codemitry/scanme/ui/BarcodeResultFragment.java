package com.codemitry.scanme.ui;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiNetworkSuggestion;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.codemitry.scanme.R;
import com.codemitry.scanme.barcode.Barcode;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class BarcodeResultFragment extends BottomSheetDialogFragment {

    private Barcode barcode;

    private OnCancelListener onCancelListener;

    public BarcodeResultFragment(Barcode barcode) {
        this.barcode = barcode;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);

        if (this.onCancelListener != null) {
            this.onCancelListener.onCancel();
        }
    }

    private static final int REQUEST_CODE_PERMISSION = 1344;

    public void setOnCancelListener(OnCancelListener cancelListener) {
        this.onCancelListener = cancelListener;
    }

    public interface OnCancelListener {
        void onCancel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view;

        switch (barcode.getValueType()) {
            case Barcode.TEXT:
                view = inflater.inflate(R.layout.text, container, false);
                ((TextView) view.findViewById(R.id.text)).setText(barcode.getDisplayValue());
                view.findViewById(R.id.copy).setOnClickListener((View v) -> {

                    copyToClipboard(barcode.getDisplayValue());
                    showCopiedMessage();
                });
                break;

            case Barcode.URL:
                view = inflater.inflate(R.layout.url, container, false);
                ((TextView) view.findViewById(R.id.link)).setText((barcode.url.url));

                view.findViewById(R.id.copy).setOnClickListener((View v) -> {

                    copyToClipboard(barcode.getDisplayValue());
                    showCopiedMessage();
                });

                view.findViewById(R.id.openLink).setOnClickListener((View v) -> {
                    openUrl(barcode.url.url);
                });

                break;

            case Barcode.WIFI:
                view = inflater.inflate(R.layout.wifi, container, false);
                ((TextView) view.findViewById(R.id.ssid)).setText((barcode.wifi.ssid));

                view.findViewById(R.id.connectWifi).setOnClickListener((View v) -> {
                    connectToWifi(barcode.wifi);
                });

                break;

            case Barcode.EMAIL:
                view = inflater.inflate(R.layout.email, container, false);

                ((TextView) view.findViewById(R.id.address)).setText((barcode.email.address));

                if (barcode.email.body == null || barcode.email.body.equals("")) {
                    view.findViewById(R.id.messageLayout).setVisibility(View.GONE);
                } else {
                    ((TextView) view.findViewById(R.id.message)).setText((barcode.email.body));
                }

                if (barcode.email.subject == null || barcode.email.subject.equals("")) {
                    view.findViewById(R.id.subjectLayout).setVisibility(View.GONE);
                } else {
                    ((TextView) view.findViewById(R.id.subject)).setText((barcode.email.subject));
                }

                view.findViewById(R.id.sendEmail).setOnClickListener((View v) -> {
                    sendEmail(barcode.email);
                });

                break;

            case Barcode.SMS:
                view = inflater.inflate(R.layout.sms, container, false);

                if (barcode.sms.phoneNumber == null || barcode.sms.phoneNumber.equals("")) {
                    view.findViewById(R.id.numberLayout).setVisibility(View.GONE);
                } else {
                    ((TextView) view.findViewById(R.id.number)).setText((barcode.sms.phoneNumber));
                }

                if (barcode.sms.message == null || barcode.sms.message.equals("")) {
                    view.findViewById(R.id.messageLayout).setVisibility(View.GONE);
                } else {
                    ((TextView) view.findViewById(R.id.message)).setText((barcode.sms.message));
                }

                view.findViewById(R.id.sendSms).setOnClickListener((View v) -> {
//                    dismiss();
//                    onCancel(requireDialog());
                    sendSms(barcode.sms);
                });

                break;

            case Barcode.CONTACT_INFO:
                view = inflater.inflate(R.layout.vcard, container, false);

                ((TextView) view.findViewById(R.id.name)).setText((barcode.contactInfo.name.formattedName));

                ((TextView) view.findViewById(R.id.number)).setText((barcode.contactInfo.phones[0].number));

                if (barcode.contactInfo.emails == null || barcode.contactInfo.emails.length < 1) {
                    view.findViewById(R.id.emailLayout).setVisibility(View.GONE);
                } else {
                    ((TextView) view.findViewById(R.id.email)).setText((barcode.contactInfo.emails[0].address));
                }

                if (barcode.contactInfo.organization == null || barcode.contactInfo.organization.equals("")) {
                    view.findViewById(R.id.companyLayout).setVisibility(View.GONE);
                } else {
                    ((TextView) view.findViewById(R.id.company)).setText((barcode.contactInfo.organization));
                }

                if (barcode.contactInfo.title == null || barcode.contactInfo.title.equals("")) {
                    view.findViewById(R.id.jobLayout).setVisibility(View.GONE);
                } else {
                    ((TextView) view.findViewById(R.id.job)).setText((barcode.contactInfo.title));
                }

                if (barcode.contactInfo.addresses == null || barcode.contactInfo.addresses.length < 1) {
                    view.findViewById(R.id.addressLayout).setVisibility(View.GONE);
                } else {
                    String address = "";
                    for (String ad : barcode.contactInfo.addresses[0].addressLines) {
                        address = address.concat(ad + "\n");
                    }

                    ((TextView) view.findViewById(R.id.address)).setText((address));
                }

                if (barcode.contactInfo.urls == null || barcode.contactInfo.urls.length < 1) {
                    view.findViewById(R.id.websiteLayout).setVisibility(View.GONE);
                } else {
                    ((TextView) view.findViewById(R.id.website)).setText((barcode.contactInfo.urls[0]));
                }


                view.findViewById(R.id.addContact).setOnClickListener((View v) -> {
                    addContact(barcode.contactInfo);
                });


                break;

            case Barcode.GEO:

                view = inflater.inflate(R.layout.location, container, false);

                ((TextView) view.findViewById(R.id.latitude)).setText(String.valueOf(barcode.geoPoint.lat));
                ((TextView) view.findViewById(R.id.longitude)).setText(String.valueOf(barcode.geoPoint.lng));


                view.findViewById(R.id.openLocation).setOnClickListener((View v) -> {
                    openLocation(barcode.geoPoint);
                });

                break;

            case Barcode.CALENDAR_EVENT:
//
                view = inflater.inflate(R.layout.calendar_event, container, false);

                ((TextView) view.findViewById(R.id.date)).setText((
                        barcode.calendarEvent.start.day + "." +
                                barcode.calendarEvent.start.month + "." +
                                barcode.calendarEvent.start.year + " " +
                                getString(R.string.at) + " " +
                                barcode.calendarEvent.start.hours + ":" +
                                barcode.calendarEvent.start.minutes
                ));

                ((TextView) view.findViewById(R.id.summary)).setText(
                        (barcode.calendarEvent.summary + "\n" + barcode.calendarEvent.description));


                view.findViewById(R.id.addToCalendar).setOnClickListener((View v) -> {
                    addToCalendar(barcode.calendarEvent);
                });
                break;

            default:
                // TODO: убрать это
                view = inflater.inflate(R.layout.text, container, false);

                ((TextView) view.findViewById(R.id.text)).setText(barcode.getDisplayValue());
                view.findViewById(R.id.copy).setOnClickListener((View v) -> {

                    copyToClipboard(barcode.getDisplayValue());
                    showCopiedMessage();
                });

        }
        return view;
    }

    private void showCopiedMessage() {
        Toast.makeText(requireContext(), R.string.copied, Toast.LENGTH_SHORT).show();
    }

    private void copyToClipboard(CharSequence charSequence) {
        ClipboardManager clipboard = (ClipboardManager) requireContext().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("", charSequence);
        clipboard.setPrimaryClip(clip);
    }

    private void openUrl(String url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    private void connectToWifi(Barcode.WiFi wifi) {
        // TODO: Возможно неожиданное поведение на Android 8.1, 9, 10 + ?

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            final WifiManager wifiManager = (WifiManager)
                    requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);

            if (!wifiManager.isWifiEnabled()) {
                Intent panelIntent = new Intent(Settings.Panel.ACTION_WIFI);
                startActivity(panelIntent);
            }

            WifiNetworkSuggestion.Builder suggestionBuilder = new WifiNetworkSuggestion.Builder()
                    .setSsid(wifi.ssid);

            if (wifi.encryptionType == Barcode.WiFi.WPA) {
                suggestionBuilder.setWpa2Passphrase(wifi.password);
            }

            WifiNetworkSuggestion suggestion = suggestionBuilder.build();

            List<WifiNetworkSuggestion> suggestions = new ArrayList<>();
            suggestions.add(suggestion);

            int result = wifiManager.addNetworkSuggestions(suggestions);

            if (result == WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_DUPLICATE) {
                wifiManager.removeNetworkSuggestions(suggestions);

                wifiManager.addNetworkSuggestions(suggestions);

//            } else if (result == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
//                System.out.println("success suggestion!");
//
//            } else {
//                System.out.println("Failure suggestion");
            }

        } else {
            WifiConfiguration wifiConfiguration = new WifiConfiguration();
            wifiConfiguration.SSID = "\"" + wifi.ssid + "\"";

            switch (wifi.encryptionType) {
                case Barcode.WiFi.WEP:
                    wifiConfiguration.wepKeys[0] = "\"" + wifi.password + "\"";
                    wifiConfiguration.wepTxKeyIndex = 0;
                    wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                    wifiConfiguration.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                    break;
                case Barcode.WiFi.WPA:
                    wifiConfiguration.preSharedKey = "\"" + wifi.password + "\"";
                    break;
                case Barcode.WiFi.OPEN:
                    wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            }

            WifiManager wifiManager = (WifiManager) requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            wifiManager.addNetwork(wifiConfiguration);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE_PERMISSION);
                return;
            }


            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }

            int netID = wifiManager.addNetwork(wifiConfiguration);
            wifiManager.disconnect();
            wifiManager.enableNetwork(netID, true);
            wifiManager.reconnect();

//                List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
//                for (WifiConfiguration i : list) {
//                    System.out.println("I'm here, ssid: " + i.SSID);
//                    if (i.SSID != null && i.SSID.equals("\"" + wifi.ssid + "\"")) {
//                        wifiManager.disconnect();
//                        wifiManager.setWifiEnabled(true);
//                        System.out.println("connect");
//                        wifiManager.enableNetwork(i.networkId, true);
//                        wifiManager.reconnect();
//                        break;
//                    }
//                }
        }
    }

    private void sendSms(Barcode.Sms sms) {

        Intent smsIntent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + sms.phoneNumber));
        smsIntent.putExtra("sms_body", sms.message);
        startActivity(smsIntent);
    }

    private void sendEmail(Barcode.Email email) {
        final Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", email.address, null));
//        emailIntent.setType("plain/text");

        // address
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{email.address});

        // subject
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, email.subject);

        // message
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, email.body);

        startActivity(Intent.createChooser(emailIntent,
                getString(R.string.send_email)));

    }

    private void addContact(Barcode.ContactInfo contact) {
        Intent intent = new Intent(ContactsContract.Intents.Insert.ACTION);
        intent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, contact.emails[0].address)
                .putExtra(ContactsContract.Intents.Insert.PHONE, contact.phones[0].number)
                .putExtra(ContactsContract.Intents.Insert.NAME, contact.name.formattedName)
                .putExtra(ContactsContract.Intents.Insert.COMPANY, contact.organization)
                .putExtra(ContactsContract.Intents.Insert.JOB_TITLE, contact.title)
                .putExtra(ContactsContract.CommonDataKinds.Website.URL, contact.urls[0]);

        startActivity(intent);

    }

    private void openLocation(Barcode.GeoPoint geoPoint) {
        String uri = String.format(Locale.ENGLISH, "geo:%f,%f", geoPoint.lat, geoPoint.lng);
        Intent intentLocation = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        startActivity(intentLocation);
    }

    private void addToCalendar(Barcode.CalendarEvent calendarEvent) {
//        ContentValues event = new ContentValues();
//
//        event.put("calendar_id", 1);
//        event.put("title", calendarEvent.summary);
//        event.put("description", calendarEvent.description);
//        event.put("eventLocation", calendarEvent.location);


        GregorianCalendar startDate = new GregorianCalendar(
                calendarEvent.start.year,
                calendarEvent.start.month,
                calendarEvent.start.day,
                calendarEvent.start.hours,
                calendarEvent.start.minutes,
                calendarEvent.start.seconds);

        GregorianCalendar endDate = new GregorianCalendar(
                calendarEvent.end.year,
                calendarEvent.end.month,
                calendarEvent.end.day,
                calendarEvent.end.hours,
                calendarEvent.end.minutes,
                calendarEvent.end.seconds);

//        event.put("dtstart", startDate.getTimeInMillis());
//        event.put("dtend", endDate.getTimeInMillis());
//        event.put("eventStatus", calendarEvent.status);


//        Calendar calendar = Calendar.getInstance();
        Intent i = new Intent(Intent.ACTION_EDIT);
        i.setType("vnd.android.cursor.item/event");
        i.putExtra("beginTime", startDate.getTimeInMillis());
        i.putExtra("endTime", endDate.getTimeInMillis());
        i.putExtra("title", calendarEvent.summary);
        i.putExtra("description", calendarEvent.description);
        i.putExtra("status", calendarEvent.status);
        i.putExtra("location", calendarEvent.location);
        startActivity(i);
    }


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
}