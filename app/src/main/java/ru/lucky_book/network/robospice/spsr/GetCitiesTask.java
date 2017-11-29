package ru.lucky_book.network.robospice.spsr;

import android.content.Context;

import com.octo.android.robospice.request.SpiceRequest;

import ru.lucky_book.R;
import ru.lucky_book.entities.spsr.GetCitiesResponseBody;
import ru.lucky_book.network.repository.SpsrRepository;

public class GetCitiesTask extends SpiceRequest<GetCitiesResponseBody.City> {

    private Context mContext;
    private String mPart;

    public GetCitiesTask(Context context, String part) {
        super(GetCitiesResponseBody.City.class);
        mContext = context;
        mPart = part;
    }

    @Override
    public GetCitiesResponseBody.City loadDataFromNetwork() throws Exception {
        return SpsrRepository.getInstance(mContext, mContext.getString(R.string.spsr_base_url)).getCity(mPart);
    }
}
