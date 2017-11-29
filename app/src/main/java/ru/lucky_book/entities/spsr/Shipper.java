package ru.lucky_book.entities.spsr;

import org.simpleframework.xml.Attribute;

public class Shipper {

    @Attribute(name = "Region")
    private String mRegion;

    @Attribute(name = "City")
    private String mCity;

    @Attribute(name = "Address")
    private String mAddress;

    @Attribute(name = "ContactName")
    private String mContactName;

    public String getRegion() {
        return mRegion;
    }

    public String getCity() {
        return mCity;
    }

    public String getAddress() {
        return mAddress;
    }

    public String getContactName() {
        return mContactName;
    }

    public void setRegion(String region) {
        mRegion = region;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public void setAddress(String address) {
        mAddress = address;
    }

    public void setContactName(String contactName) {
        mContactName = contactName;
    }
}
