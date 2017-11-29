package ru.lucky_book.features.order_screen;

import ru.lucky_book.data.OrderId;
import ru.lucky_book.data.SuccessOrderResponse;
import ru.lucky_book.features.base.MvpView;
import ru.luckybook.data.DeliveryPriceSpsr;

/**
 * Created by Загит Талипов on 11.11.2016.
 */

public interface OrderView extends MvpView {

    void resultSendOrder(SuccessOrderResponse successOrder);
    void resultGenerateOrderId(OrderId orderId);
    void showDeliveryPrice(DeliveryPriceSpsr deliveryPriceSpsr);
    void resultSendPreOrder(SuccessOrderResponse successOrderResponse);
}
