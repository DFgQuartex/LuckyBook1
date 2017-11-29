package ru.lucky_book.data;

/**
 * Created by Загит Талипов on 09.11.2016.
 */

public class Price {

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public double getSkin() {
        return skin;
    }

    public void setSkin(double skin) {
        this.skin = skin;
    }

    public double getPage() {
        return page;
    }

    public void setPage(double page) {
        this.page = page;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    private double amount;
    private double skin;
    private double page;
    private Integer discount;

}
