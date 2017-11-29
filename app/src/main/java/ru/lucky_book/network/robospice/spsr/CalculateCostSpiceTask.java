package ru.lucky_book.network.robospice.spsr;

import android.content.Context;

import com.octo.android.robospice.request.SpiceRequest;

import ru.lucky_book.R;
import ru.lucky_book.database.DBHelper;
import ru.lucky_book.entities.spsr.CostResponseBody;
import ru.lucky_book.entities.spsr.GetCitiesResponseBody;
import ru.lucky_book.entities.spsr.LoginResponseBody;
import ru.lucky_book.network.repository.SpsrRepository;

public class CalculateCostSpiceTask extends SpiceRequest<CostResponseBody.Tariff> {

    public static final int ID = 100;

    private String mDestinationCity;
    private Context mContext;

    public CalculateCostSpiceTask(Context context, String destinationCity) {
        super(CostResponseBody.Tariff.class);
        mContext = context;
        mDestinationCity = destinationCity;
    }

    @Override
    public CostResponseBody.Tariff loadDataFromNetwork() throws Exception {
        SpsrRepository repository = SpsrRepository.getInstance(mContext, mContext.getString(R.string.spsr_base_url));
        LoginResponseBody.Login login = repository.login();
        if (login != null) {
            DBHelper.saveLogin(login.getSid());
            GetCitiesResponseBody.City destinationCity = repository.getCity(mDestinationCity);
            GetCitiesResponseBody.City sourceCity = repository.getCity(mContext.getString(R.string.default_city_source));
            if (sourceCity != null && destinationCity != null&&destinationCity.getName().replace(" ","").matches(mDestinationCity.replace(" ",""))) {
                repository = SpsrRepository.getInstance(mContext, mContext.getString(R.string.spsr_base_url2));
                CostResponseBody.Tariff tariff = repository.calculateCost(sourceCity, destinationCity, login);
                return tariff;
            }
        }
        return null;
    }
}
