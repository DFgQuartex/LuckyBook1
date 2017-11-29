package ru.lucky_book.data;

/**
 * Created by Загит Талипов on 11.11.2016.
 */

public class SuccessOrderResponse {
    public boolean isOrder() {
        return order;
    }

    public void setOrder(boolean order) {
        this.order = order;
    }

    boolean order;
}
