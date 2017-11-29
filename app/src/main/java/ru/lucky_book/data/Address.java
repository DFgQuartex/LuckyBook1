
package ru.lucky_book.data;


public class Address {

    private String city;
    private String street;
    private String house;
    private String flat;
    private String zip;

    public Address(String city, String street, String house, String flat, String zip) {
        this.city = city;
        this.street = street;
        this.house = house;
        this.flat = flat;
        this.zip = zip;
    }

    /**
     * @return The city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city The city
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return The street
     */
    public String getStreet() {
        return street;
    }

    /**
     * @param street The street
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * @return The house
     */
    public String getHouse() {
        return house;
    }

    /**
     * @param house The house
     */
    public void setHouse(String house) {
        this.house = house;
    }

    /**
     * @return The flat
     */
    public String getFlat() {
        return flat;
    }

    /**
     * @param flat The flat
     */
    public void setFlat(String flat) {
        this.flat = flat;
    }

    /**
     * @return The zip
     */
    public String getZip() {
        return zip;
    }

    /**
     * @param zip The zip
     */
    public void setZip(String zip) {
        this.zip = zip;
    }

}
