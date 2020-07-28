package com.codemitry.scanme.history;

import com.codemitry.scanme.R;
import com.codemitry.scanme.barcode.Barcode;

import java.io.Serializable;

//import com.google.android.gms.vision.barcode.Barcode;

public class HistoryAction implements Serializable {
    private Actions action;
    private Barcode barcode;

    public HistoryAction(Actions action, Barcode barcode) {
        this.action = action;
        this.barcode = barcode;
    }

    public HistoryAction(Actions action, com.google.mlkit.vision.barcode.Barcode barcode) {
        this(action, Barcode.getBarcode(barcode));
    }

    public enum Actions {
        SCAN, CREATE;

        public static int getString(Actions action) {
            switch (action) {
                case SCAN:
                    return R.string.qr_scanning;
                case CREATE:
                    return R.string.qr_creation;
                default:
                    return -1;
            }
        }
    }

    public Actions getAction() {
        return action;
    }

    public Barcode getBarcode() {
        return barcode;
    }
//
//    private static Barcode getBarcode(com.google.mlkit.vision.barcode.Barcode from) {
//        Barcode newBarcode = new Barcode();
//
//        switch (from.getValueType()) {
//            case com.google.mlkit.vision.barcode.Barcode.TYPE_CONTACT_INFO:
//                newBarcode.valueFormat = Barcode.CONTACT_INFO;
//
//                newBarcode.contactInfo = getContactInfo(from.getContactInfo());
//                break;
//
//            case com.google.mlkit.vision.barcode.Barcode.TYPE_EMAIL:
//                newBarcode.valueFormat = Barcode.EMAIL;
//
//                newBarcode.email = getEmail(from.getEmail());
//                break;
//
//            case com.google.mlkit.vision.barcode.Barcode.TYPE_ISBN:
//                newBarcode.valueFormat = Barcode.ISBN;
//
//                // ?????????????
//
//                break;
//
//            case com.google.mlkit.vision.barcode.Barcode.TYPE_PHONE:
//                newBarcode.valueFormat = Barcode.PHONE;
//
//                newBarcode.phone = getPhone(from.getPhone());
//
//                break;
//
//            case com.google.mlkit.vision.barcode.Barcode.TYPE_PRODUCT:
//                newBarcode.valueFormat = Barcode.PRODUCT;
//
//                // ????????????
//
//                break;
//            case com.google.mlkit.vision.barcode.Barcode.TYPE_SMS:
//                newBarcode.valueFormat = Barcode.SMS;
//
//                newBarcode.sms = getSms(from.getSms());
//
//                break;
//            case com.google.mlkit.vision.barcode.Barcode.TYPE_TEXT:
//                newBarcode.valueFormat = Barcode.TEXT;
//
//                // ????????????
//
//                break;
//
//            case com.google.mlkit.vision.barcode.Barcode.TYPE_URL:
//                newBarcode.valueFormat = Barcode.URL;
//
//                newBarcode.url = getUrl(from.getUrl());
//                break;
//            case com.google.mlkit.vision.barcode.Barcode.TYPE_WIFI:
//                newBarcode.valueFormat = Barcode.WIFI;
//
//                newBarcode.wifi = getWiFi(from.getWifi());
//                break;
//
//            case com.google.mlkit.vision.barcode.Barcode.TYPE_GEO:
//                newBarcode.valueFormat = Barcode.GEO;
//
//                newBarcode.geoPoint = getGeo(from.getGeoPoint());
//
//                break;
//            case com.google.mlkit.vision.barcode.Barcode.TYPE_CALENDAR_EVENT:
//                newBarcode.valueFormat = Barcode.CALENDAR_EVENT;
//
//                newBarcode.calendarEvent = getCalendarEvent(from.getCalendarEvent());
//                break;
//
//            case com.google.mlkit.vision.barcode.Barcode.TYPE_DRIVER_LICENSE:
//                newBarcode.valueFormat = Barcode.DRIVER_LICENSE;
//
//                newBarcode.driverLicense = getDriverLicence(from.getDriverLicense());
//                break;
//        }
//
//        newBarcode.rawValue = from.getRawValue();
//        newBarcode.rawBytes = from.getRawBytes();
//        newBarcode.displayValue = from.getDisplayValue();
//        newBarcode.cornerPoints = from.getCornerPoints();
//        // Опасное преобразование форматов. Не факт, что в дальнейшем значения форматов совпадут
//        newBarcode.format = from.getFormat();
//
//        return newBarcode;
//    }
//
//    private static Barcode.Sms getSms(com.google.mlkit.vision.barcode.Barcode.Sms from) {
//        return new Barcode.Sms(from.getMessage(), from.getPhoneNumber());
//    }
//
//    private static Barcode.UrlBookmark getUrl(com.google.mlkit.vision.barcode.Barcode.UrlBookmark from) {
//        return new Barcode.UrlBookmark(from.getTitle(), from.getUrl());
//    }
//
//    private static Barcode.WiFi getWiFi(com.google.mlkit.vision.barcode.Barcode.WiFi from) {
//        return new Barcode.WiFi(from.getSsid(), from.getPassword(), from.getEncryptionType());
//    }
//
//    private static Barcode.GeoPoint getGeo(com.google.mlkit.vision.barcode.Barcode.GeoPoint from) {
//        return new Barcode.GeoPoint(from.getLat(), from.getLng());
//    }
//
//    private static Barcode.CalendarDateTime getCalendarDateTime(com.google.mlkit.vision.barcode.Barcode.CalendarDateTime from) {
//        return new Barcode.CalendarDateTime(
//                from.getYear(),
//                from.getMonth(),
//                from.getDay(),
//                from.getHours(),
//                from.getMinutes(),
//                from.getSeconds(),
//                from.isUtc(),
//                from.getRawValue()
//        );
//    }
//
//    private static Barcode.CalendarEvent getCalendarEvent(com.google.mlkit.vision.barcode.Barcode.CalendarEvent from) {
//        return new Barcode.CalendarEvent(
//                from.getSummary(),
//                from.getDescription(),
//                from.getLocation(),
//                from.getOrganizer(),
//                from.getStatus(),
//                getCalendarDateTime(from.getStart()),
//                getCalendarDateTime(from.getEnd())
//        );
//    }
//
//    private static Barcode.DriverLicense getDriverLicence(com.google.mlkit.vision.barcode.Barcode.DriverLicense from) {
//        return new Barcode.DriverLicense(
//                from.getDocumentType(),
//                from.getFirstName(),
//                from.getMiddleName(),
//                from.getLastName(),
//                from.getGender(),
//                from.getAddressStreet(),
//                from.getAddressCity(),
//                from.getAddressState(),
//                from.getAddressZip(),
//                from.getLicenseNumber(),
//                from.getIssueDate(),
//                from.getExpiryDate(),
//                from.getBirthDate(),
//                from.getIssuingCountry()
//        );
//    }
//
//    private static Barcode.Phone getPhone(com.google.mlkit.vision.barcode.Barcode.Phone from) {
//        return new Barcode.Phone(from.getType(), from.getNumber());
//    }
//
//    private static Barcode.Email getEmail(com.google.mlkit.vision.barcode.Barcode.Email from) {
//        return new Barcode.Email(from.getType(), from.getAddress(), from.getSubject(), from.getBody());
//    }
//
//    private static Barcode.PersonName getPersonName(com.google.mlkit.vision.barcode.Barcode.PersonName from) {
//        return new Barcode.PersonName(
//                from.getFormattedName(),
//                from.getPronunciation(),
//                from.getPrefix(),
//                from.getFirst(),
//                from.getMiddle(),
//                from.getLast(),
//                from.getSuffix()
//        );
//    }
//
//    private static Barcode.Phone[] getPhones(List<com.google.mlkit.vision.barcode.Barcode.Phone> from) {
//        Barcode.Phone[] phones = new Barcode.Phone[from.size()];
//        for (int i = 0; i < phones.length; i++) {
//            phones[i] = getPhone(from.get(i));
//        }
//        return phones;
//    }
//
//    private static Barcode.Email[] getEmails(List<com.google.mlkit.vision.barcode.Barcode.Email> from) {
//        Barcode.Email[] emails = new Barcode.Email[from.size()];
//        for (int i = 0; i < emails.length; i++) {
//            emails[i] = getEmail(from.get(i));
//        }
//        return emails;
//    }
//
//    private static Barcode.Address getAddress(com.google.mlkit.vision.barcode.Barcode.Address from) {
//        return new Barcode.Address(from.getType(), from.getAddressLines());
//    }
//
//    private static Barcode.Address[] getAddresses(List<com.google.mlkit.vision.barcode.Barcode.Address> from) {
//        Barcode.Address[] addresses = new Barcode.Address[from.size()];
//        for (int i = 0; i < addresses.length; i++) {
//            addresses[i] = getAddress(from.get(i));
//        }
//        return addresses;
//    }
//
//    private static String[] getUrls(List<String> from) {
//        String[] urls = new String[from.size()];
//        for (int i = 0; i < urls.length; i++) {
//            urls[i] = from.get(i);
//        }
//        return urls;
//    }
//
//
//    private static Barcode.ContactInfo getContactInfo(com.google.mlkit.vision.barcode.Barcode.ContactInfo from) {
//        return new Barcode.ContactInfo(
//                getPersonName(from.getName()),
//                from.getOrganization(),
//                from.getTitle(),
//                getPhones(from.getPhones()),
//                getEmails(from.getEmails()),
//                getUrls(from.getUrls()),
//                getAddresses(from.getAddresses())
//        );
//    }
}
