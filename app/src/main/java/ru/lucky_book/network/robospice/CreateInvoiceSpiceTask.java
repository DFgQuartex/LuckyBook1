package ru.lucky_book.network.robospice;

import android.content.Context;
import android.text.TextUtils;

import com.octo.android.robospice.request.SpiceRequest;

import ru.lucky_book.R;
import ru.lucky_book.entities.OrderData;
import ru.lucky_book.entities.spsr.InvoiceResponseBody;
import ru.lucky_book.network.repository.SpsrRepository;

public class CreateInvoiceSpiceTask extends SpiceRequest<String> {

    private OrderData mOrderData;
    private Context mContext;

    public CreateInvoiceSpiceTask(Context context, OrderData orderData) {
        super(String.class);
        mOrderData = orderData;
        mContext = context;
    }

    @Override
    public String loadDataFromNetwork() throws Exception {
        SpsrRepository repository = SpsrRepository.getInstance(mContext, mContext.getString(R.string.spsr_base_url));
        InvoiceResponseBody.Invoice invoice = repository.createInvoice(mOrderData);
        if (TextUtils.isEmpty(invoice.getInvoiceNumber()) || TextUtils.isEmpty(invoice.getGCNumber())) {
            return null;
        }
        return String.format("%s : %s", invoice.getInvoiceNumber(), invoice.getGCNumber());
    }
}
