package ru.luckybook.data;

/**
 * Created by Загит Талипов on 12.12.2016.
 */

public class DeliveryPriceSpsr {
    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    int price;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    String error;
}
