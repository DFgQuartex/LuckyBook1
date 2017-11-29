package ru.lucky_book.entities;

import com.google.gson.annotations.SerializedName;

public class OrderData {

    @SerializedName("address")
    private Address mAddress;

    @SerializedName("contact")
    private Contact mContact;

    @SerializedName("link")
    private String mLink;

    @SerializedName("promo_code")
    private String mPromoCode;

    @SerializedName("amount")
    private double mAmount;

    public String getPromoCode() {
        return mPromoCode;
    }

    public void setPromoCode(String promoCode) {
        mPromoCode = promoCode;
    }

    public double getAmount() {
        return mAmount;
    }

    public void setAmount(double amount) {
        mAmount = amount;
    }

    public Address getAddress() {
        return mAddress;
    }

    public Contact getContact() {
        return mContact;
    }

    public String getLink() {
        return mLink;
    }

    public void setAddress(Address address) {
        mAddress = address;
    }

    public void setContact(Contact contact) {
        mContact = contact;
    }

    public void setLink(String link) {
        mLink = link;
    }

    public static class Address {

        @SerializedName("city")
        private String mCity;

        @SerializedName("street")
        private String mStreet;

        @SerializedName("house")
        private String mHouse;

        @SerializedName("flat")
        private String mFlat;

        @SerializedName("zip")
        private int mZip;

        public String getCity() {
            return mCity;
        }

        public String getStreet() {
            return mStreet;
        }

        public String getHouse() {
            return mHouse;
        }

        public String getFlat() {
            return mFlat;
        }

        public int getZip() {
            return mZip;
        }

        public void setCity(String city) {
            mCity = city;
        }

        public void setStreet(String street) {
            mStreet = street;
        }

        public void setHouse(String house) {
            mHouse = house;
        }

        public void setFlat(String flat) {
            mFlat = flat;
        }

        public void setZip(int zip) {
            mZip = zip;
        }
    }

    public static class Contact {

        @SerializedName("name")
        private String mName;

        @SerializedName("surname")
        private String mSurname;

        @SerializedName("patronymic")
        private String mPatronymic;

        @SerializedName("email")
        private String mEmail;

        @SerializedName("phone")
        private String mPhone;

        @SerializedName("birthday_at_millis")
        private Long mBirthday;

        public String getName() {
            return mName;
        }

        public String getSurname() {
            return mSurname;
        }

        public String getPatronymic() {
            return mPatronymic;
        }

        public String getEmail() {
            return mEmail;
        }

        public String getPhone() {
            return mPhone;
        }

        public void setName(String name) {
            mName = name;
        }

        public void setSurname(String surname) {
            mSurname = surname;
        }

        public void setPatronymic(String patronymic) {
            mPatronymic = patronymic;
        }

        public void setEmail(String email) {
            mEmail = email;
        }

        public void setPhone(String phone) {
            mPhone = phone;
        }

        public Long getBirthday() {
            return mBirthday;
        }

        public void setBirthday(Long birthday) {
            mBirthday = birthday;
        }
    }
}
