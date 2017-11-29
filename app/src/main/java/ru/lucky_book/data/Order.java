
package ru.lucky_book.data;


import com.google.gson.annotations.SerializedName;

public class Order {

    private Address address;
    private Contact contact;
    @SerializedName("app_id")
    private String appId;
    private String os;
    @SerializedName("common_id")
    private int commonId;
    @SerializedName("phone_model")
    private String phoneModel;
    @SerializedName("skin_id")
    private Integer skinId;
    @SerializedName("delivery_price")
    private Double deliveryPrice;
    @SerializedName("delivery_type")
    private Integer deliveryType;
    @SerializedName("pay_transaction")
    private String payTransaction;

    public String getDeliveryId() {
        return mDeliveryId;
    }

    public void setDeliveryId(String deliveryId) {
        mDeliveryId = deliveryId;
    }

    @SerializedName("delivery_id")
    String mDeliveryId;
    private Double price;

    public int getCommonId() {
        return commonId;
    }

    public void setCommonId(int commonId) {
        this.commonId = commonId;
    }


    public Order(Address address, Contact contact) {
        this.address = address;
        this.contact = contact;
    }


    /**
     * @return The address
     */
    public Address getAddress() {
        return address;
    }

    /**
     * @param address The address
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * @return The contact
     */
    public Contact getContact() {
        return contact;
    }

    /**
     * @param contact The contact
     */
    public void setContact(Contact contact) {
        this.contact = contact;
    }

    /**
     * @return The link
     */

    /**
     * @return The appId
     */
    public String getAppId() {
        return appId;
    }

    /**
     * @param appId The app_id
     */
    public void setAppId(String appId) {
        this.appId = appId;
    }

    /**
     * @return The os
     */
    public String getOs() {
        return os;
    }

    /**
     * @param os The os
     */
    public void setOs(String os) {
        this.os = os;
    }

    /**
     * @return The phoneModel
     */
    public String getPhoneModel() {
        return phoneModel;
    }

    /**
     * @param phoneModel The phone_model
     */
    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    /**
     * @return The skinId
     */
    public Integer getSkinId() {
        return skinId;
    }

    /**
     * @param skinId The skin_id
     */
    public void setSkinId(Integer skinId) {
        this.skinId = skinId;
    }

    /**
     * @return The deliveryPrice
     */
    public Double getDeliveryPrice() {
        return deliveryPrice;
    }

    /**
     * @param deliveryPrice The delivery_price
     */
    public void setDeliveryPrice(Double deliveryPrice) {
        this.deliveryPrice = deliveryPrice;
    }

    /**
     * @return The deliveryType
     */
    public Integer getDeliveryType() {
        return deliveryType;
    }

    /**
     * @param deliveryType The delivery_type
     */
    public void setDeliveryType(Integer deliveryType) {
        this.deliveryType = deliveryType;
    }

    /**
     * @return The payTransaction
     */
    public String getPayTransaction() {
        return payTransaction;
    }

    /**
     * @param payTransaction The pay_transaction
     */
    public void setPayTransaction(String payTransaction) {
        this.payTransaction = payTransaction;
    }

    /**
     * @return The price
     */
    public Double getPrice() {
        return price;
    }

    /**
     * @param price The price
     */
    public void setPrice(Double price) {
        this.price = price;
    }

}
