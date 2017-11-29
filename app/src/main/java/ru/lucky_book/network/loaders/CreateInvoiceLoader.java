package ru.lucky_book.network.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import ru.lucky_book.R;
import ru.lucky_book.entities.OrderData;
import ru.lucky_book.entities.spsr.InvoiceResponseBody;
import ru.lucky_book.network.repository.SpsrRepository;

public class CreateInvoiceLoader extends AsyncTaskLoader<String> {

    private OrderData mOrderData;

    public CreateInvoiceLoader(Context context, OrderData orderData) {
        super(context);
        mOrderData = orderData;
    }

    @Override
    public String loadInBackground() {
        SpsrRepository repository = SpsrRepository.getInstance(getContext(), getContext().getString(R.string.spsr_base_url));
        InvoiceResponseBody.Invoice invoice = repository.createInvoice(mOrderData);
        return String.format("%s : %s", invoice.getInvoiceNumber(), invoice.getGCNumber());
    }
}
