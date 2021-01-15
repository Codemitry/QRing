package com.codemitry.scanme.barcode;

import java.io.Serializable;
import java.util.List;

public class Barcode implements Serializable {
    public static final int ALL_FORMATS = 0;
    public static final int CODE_128 = 1;
    public static final int CODE_39 = 2;
    public static final int CODE_93 = 4;
    public static final int CODABAR = 8;
    public static final int DATA_MATRIX = 16;
    public static final int EAN_13 = 32;
    public static final int EAN_8 = 64;
    public static final int ITF = 128;
    public static final int QR_CODE = 256;
    public static final int UPC_A = 512;
    public static final int UPC_E = 1024;
    public static final int PDF417 = 2048;
    public static final int AZTEC = 4096;
    public static final int CONTACT_INFO = 1;
    public static final int EMAIL = 2;
    public static final int ISBN = 3;
    public static final int PHONE = 4;
    public static final int PRODUCT = 5;
    public static final int SMS = 6;
    public static final int TEXT = 7;
    public static final int URL = 8;
    public static final int WIFI = 9;
    public static final int GEO = 10;
    public static final int CALENDAR_EVENT = 11;
    public static final int DRIVER_LICENSE = 12;

    public int format;
    public String rawValue;
    public String displayValue;
    public int valueFormat;
    public byte[] rawBytes;

    public Email email;
    public Phone phone;
    public Sms sms;
    public WiFi wifi;
    public Url url;
    public GeoPoint geoPoint;
    public CalendarEvent calendarEvent;
    public ContactInfo contactInfo;
    public DriverLicense driverLicense;

    public int getValueType() {
        return valueFormat;
    }

    public String getDisplayValue() {
        return displayValue;
    }

    public static class Email implements Serializable {
        public static final int UNKNOWN = 0;
        public static final int WORK = 1;
        public static final int HOME = 2;

        public int type;
        public String address;
        public String subject;
        public String body;

        public Email(int type, String address, String subject, String body) {
            this.type = type;
            this.address = address;
            this.subject = subject;
            this.body = body;
        }
    }

    public static class PersonName implements Serializable {
        public String formattedName;
        public String pronunciation;
        public String prefix;
        public String first;
        public String middle;
        public String last;
        public String suffix;

        public PersonName(String formattedName, String pronunciation, String prefix, String first,
                          String middle, String last, String suffix) {
            this.formattedName = formattedName;
            this.pronunciation = pronunciation;
            this.prefix = prefix;
            this.first = first;
            this.middle = middle;
            this.last = last;
            this.suffix = suffix;
        }
    }

    public static class Address implements Serializable {
        public static final int UNKNOWN = 0;
        public static final int WORK = 1;
        public static final int HOME = 2;

        public int type;
        public String[] addressLines;

        public Address(int type, String[] addressLines) {
            this.type = type;
            this.addressLines = addressLines;
        }
    }

    public static class Phone implements Serializable {
        public static final int UNKNOWN = 0;
        public static final int WORK = 1;
        public static final int HOME = 2;
        public static final int FAX = 3;
        public static final int MOBILE = 4;

        public int type;
        public String number;

        public Phone(int type, String number) {
            this.type = type;
            this.number = number;
        }

    }

    public static class Sms implements Serializable {
        public String message;
        public String phoneNumber;

        public Sms(String message, String phoneNumber) {
            this.message = message;
            this.phoneNumber = phoneNumber;
        }
    }

    public static class WiFi implements Serializable {
        public static final int OPEN = 1;
        public static final int WPA = 2;
        public static final int WEP = 3;

        public String ssid;
        public String password;
        public int encryptionType;

        public WiFi(String ssid, String password, int encryptionType) {
            this.ssid = ssid;
            this.password = password;
            this.encryptionType = encryptionType;
        }
    }

    public static class Url implements Serializable {
        public String title;
        public String url;

        public Url(String title, String url) {
            this.title = title;
            this.url = url;
        }
    }

    public static class GeoPoint implements Serializable {
        public double lat;
        public double lng;

        public GeoPoint(double lat, double lng) {
            this.lat = lat;
            this.lng = lng;
        }
    }

    public static class CalendarDateTime implements Serializable {
        public int year;
        public int month;
        public int day;
        public int hours;
        public int minutes;
        public int seconds;
        public boolean isUtc;
        public String rawValue;

        public CalendarDateTime(int year, int month, int day, int hours, int minutes, int seconds,
                                boolean isUtc, String rawValue) {
            this.year = year;
            this.month = month;
            this.day = day;
            this.hours = hours;
            this.minutes = minutes;
            this.seconds = seconds;
            this.isUtc = isUtc;
            this.rawValue = rawValue;
        }

    }

    private static Sms getSms(com.google.mlkit.vision.barcode.Barcode.Sms from) {
        return new Sms(from.getMessage(), from.getPhoneNumber());
    }

    private static Url getUrl(com.google.mlkit.vision.barcode.Barcode.UrlBookmark from) {
        return new Url(from.getTitle(), from.getUrl());
    }

    public static class DriverLicense implements Serializable {
        public String documentType;
        public String firstName;
        public String middleName;
        public String lastName;
        public String gender;
        public String addressStreet;
        public String addressCity;
        public String addressState;
        public String addressZip;
        public String licenseNumber;
        public String issueDate;
        public String expireDate;
        public String birthDate;
        public String issuingCountry;

        public DriverLicense(String documentType, String firstName, String middleName,
                             String lastName, String gender, String addressStreet,
                             String addressCity, String addressState, String addressZip,
                             String licenseNumber, String issueDate, String expireDate,
                             String birthDate, String issuingCountry) {
            this.documentType = documentType;
            this.firstName = firstName;
            this.middleName = middleName;
            this.lastName = lastName;
            this.gender = gender;
            this.addressStreet = addressStreet;
            this.addressCity = addressCity;
            this.addressState = addressState;
            this.addressZip = addressZip;
            this.licenseNumber = licenseNumber;
            this.issueDate = issueDate;
            this.expireDate = expireDate;
            this.birthDate = birthDate;
            this.issuingCountry = issuingCountry;
        }

    }


    public static Barcode getBarcode(com.google.mlkit.vision.barcode.Barcode from) {
        Barcode newBarcode = new Barcode();

        switch (from.getValueType()) {
            case com.google.mlkit.vision.barcode.Barcode.TYPE_CONTACT_INFO:
                newBarcode.valueFormat = com.google.android.gms.vision.barcode.Barcode.CONTACT_INFO;

                newBarcode.contactInfo = getContactInfo(from.getContactInfo());
                break;

            case com.google.mlkit.vision.barcode.Barcode.TYPE_EMAIL:
                newBarcode.valueFormat = com.google.android.gms.vision.barcode.Barcode.EMAIL;

                newBarcode.email = getEmail(from.getEmail());
                break;

            case com.google.mlkit.vision.barcode.Barcode.TYPE_ISBN:
                newBarcode.valueFormat = com.google.android.gms.vision.barcode.Barcode.ISBN;

                // ?????????????

                break;

            case com.google.mlkit.vision.barcode.Barcode.TYPE_PHONE:
                newBarcode.valueFormat = com.google.android.gms.vision.barcode.Barcode.PHONE;

                newBarcode.phone = getPhone(from.getPhone());

                break;

            case com.google.mlkit.vision.barcode.Barcode.TYPE_PRODUCT:
                newBarcode.valueFormat = com.google.android.gms.vision.barcode.Barcode.PRODUCT;

                // ????????????

                break;
            case com.google.mlkit.vision.barcode.Barcode.TYPE_SMS:
                newBarcode.valueFormat = com.google.android.gms.vision.barcode.Barcode.SMS;

                newBarcode.sms = getSms(from.getSms());

                break;
            case com.google.mlkit.vision.barcode.Barcode.TYPE_TEXT:
                newBarcode.valueFormat = com.google.android.gms.vision.barcode.Barcode.TEXT;

                // ????????????

                break;

            case com.google.mlkit.vision.barcode.Barcode.TYPE_URL:
                newBarcode.valueFormat = com.google.android.gms.vision.barcode.Barcode.URL;

                newBarcode.url = getUrl(from.getUrl());
                break;
            case com.google.mlkit.vision.barcode.Barcode.TYPE_WIFI:
                newBarcode.valueFormat = com.google.android.gms.vision.barcode.Barcode.WIFI;

                newBarcode.wifi = getWiFi(from.getWifi());
                break;

            case com.google.mlkit.vision.barcode.Barcode.TYPE_GEO:
                newBarcode.valueFormat = com.google.android.gms.vision.barcode.Barcode.GEO;

                newBarcode.geoPoint = getGeo(from.getGeoPoint());

                break;
            case com.google.mlkit.vision.barcode.Barcode.TYPE_CALENDAR_EVENT:
                newBarcode.valueFormat = com.google.android.gms.vision.barcode.Barcode.CALENDAR_EVENT;

                newBarcode.calendarEvent = getCalendarEvent(from.getCalendarEvent());
                break;

            case com.google.mlkit.vision.barcode.Barcode.TYPE_DRIVER_LICENSE:
                newBarcode.valueFormat = com.google.android.gms.vision.barcode.Barcode.DRIVER_LICENSE;

                newBarcode.driverLicense = getDriverLicence(from.getDriverLicense());
                break;
        }

        newBarcode.rawValue = from.getRawValue();
        newBarcode.rawBytes = from.getRawBytes();
        newBarcode.displayValue = from.getDisplayValue();
//        newBarcode.cornerPoints = from.getCornerPoints();
        // Опасное преобразование форматов. Не факт, что в дальнейшем значения форматов совпадут
        newBarcode.format = from.getFormat();

        return newBarcode;
    }

    private static WiFi getWiFi(com.google.mlkit.vision.barcode.Barcode.WiFi from) {
        return new WiFi(from.getSsid(), from.getPassword(), from.getEncryptionType());
    }

    private static GeoPoint getGeo(com.google.mlkit.vision.barcode.Barcode.GeoPoint from) {
        return new GeoPoint(from.getLat(), from.getLng());
    }

    private static CalendarDateTime getCalendarDateTime(com.google.mlkit.vision.barcode.Barcode.CalendarDateTime from) {
        return new CalendarDateTime(
                from.getYear(),
                from.getMonth(),
                from.getDay(),
                from.getHours(),
                from.getMinutes(),
                from.getSeconds(),
                from.isUtc(),
                from.getRawValue()
        );
    }

    private static CalendarEvent getCalendarEvent(com.google.mlkit.vision.barcode.Barcode.CalendarEvent from) {
        return new CalendarEvent(
                from.getSummary(),
                from.getDescription(),
                from.getLocation(),
                from.getOrganizer(),
                from.getStatus(),
                getCalendarDateTime(from.getStart()),
                getCalendarDateTime(from.getEnd())
        );
    }

    private static DriverLicense getDriverLicence(com.google.mlkit.vision.barcode.Barcode.DriverLicense from) {
        return new DriverLicense(
                from.getDocumentType(),
                from.getFirstName(),
                from.getMiddleName(),
                from.getLastName(),
                from.getGender(),
                from.getAddressStreet(),
                from.getAddressCity(),
                from.getAddressState(),
                from.getAddressZip(),
                from.getLicenseNumber(),
                from.getIssueDate(),
                from.getExpiryDate(),
                from.getBirthDate(),
                from.getIssuingCountry()
        );
    }

    private static Phone getPhone(com.google.mlkit.vision.barcode.Barcode.Phone from) {
        return new Phone(from.getType(), from.getNumber());
    }

    private static Email getEmail(com.google.mlkit.vision.barcode.Barcode.Email from) {
        return new Email(from.getType(), from.getAddress(), from.getSubject(), from.getBody());
    }

    private static PersonName getPersonName(com.google.mlkit.vision.barcode.Barcode.PersonName from) {
        return new PersonName(
                from.getFormattedName(),
                from.getPronunciation(),
                from.getPrefix(),
                from.getFirst(),
                from.getMiddle(),
                from.getLast(),
                from.getSuffix()
        );
    }

    private static Phone[] getPhones(List<com.google.mlkit.vision.barcode.Barcode.Phone> from) {
        Phone[] phones = new Phone[from.size()];
        for (int i = 0; i < phones.length; i++) {
            phones[i] = getPhone(from.get(i));
        }
        return phones;
    }

    private static Email[] getEmails(List<com.google.mlkit.vision.barcode.Barcode.Email> from) {
        Email[] emails = new Email[from.size()];
        for (int i = 0; i < emails.length; i++) {
            emails[i] = getEmail(from.get(i));
        }
        return emails;
    }

    private static Address getAddress(com.google.mlkit.vision.barcode.Barcode.Address from) {
        return new Address(from.getType(), from.getAddressLines());
    }

    private static Address[] getAddresses(List<com.google.mlkit.vision.barcode.Barcode.Address> from) {
        Address[] addresses = new Address[from.size()];
        for (int i = 0; i < addresses.length; i++) {
            addresses[i] = getAddress(from.get(i));
        }
        return addresses;
    }

    private static ContactInfo getContactInfo(com.google.mlkit.vision.barcode.Barcode.ContactInfo from) {
        return new ContactInfo(
                getPersonName(from.getName()),
                from.getOrganization(),
                from.getTitle(),
                getPhones(from.getPhones()),
                getEmails(from.getEmails()),
                getUrls(from.getUrls()),
                getAddresses(from.getAddresses())
        );
    }

    public static class CalendarEvent implements Serializable {
        public String summary;
        public String description;
        public String location;
        public String organizer;
        public String status;
        public CalendarDateTime start;
        public CalendarDateTime end;

        public CalendarEvent(String summary, String description, String location, String organizer,
                             String status, CalendarDateTime start,
                             CalendarDateTime end) {
            this.summary = summary;
            this.description = description;
            this.location = location;
            this.organizer = organizer;
            this.status = status;
            this.start = start;
            this.end = end;
        }
    }

    private static String[] getUrls(List<String> from) {
        String[] urls = new String[from.size()];
        for (int i = 0; i < urls.length; i++) {
            urls[i] = from.get(i);
        }
        return urls;
    }

    public static class ContactInfo implements Serializable {
        public PersonName name;
        public String organization;
        public String title;
        public Phone[] phones;
        public Email[] emails;
        public String[] urls;
        public Address[] addresses;

        public ContactInfo(PersonName name, String organization, String title,
                           Phone[] phones, Email[] emails, String[] urls,
                           Address[] addresses) {
            this.name = name;
            this.organization = organization;
            this.title = title;
            this.phones = phones;
            this.emails = emails;
            this.urls = urls;
            this.addresses = addresses;
        }
    }

}
