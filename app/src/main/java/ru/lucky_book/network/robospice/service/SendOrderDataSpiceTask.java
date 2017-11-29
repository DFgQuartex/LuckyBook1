package ru.lucky_book.network.robospice.service;

import android.content.Context;

import com.octo.android.robospice.request.SpiceRequest;

import ru.lucky_book.entities.OrderData;
import ru.lucky_book.network.repository.ServiceRepository;

public class SendOrderDataSpiceTask extends SpiceRequest<String> {

    public static final int ID = 300;

    private Context mContext;
    private OrderData mOrderData;

    public SendOrderDataSpiceTask(Context context, OrderData orderData) {
        super(String.class);
        mContext = context;
        mOrderData = orderData;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        return ServiceRepository.getInstance(mContext).sendOrderData(mOrderData);

    }
}
