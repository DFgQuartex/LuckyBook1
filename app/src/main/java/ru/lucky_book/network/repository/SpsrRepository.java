package ru.lucky_book.network.repository;

import android.content.Context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.simplexml.SimpleXmlConverterFactory;
import ru.lucky_book.R;
import ru.lucky_book.dataapi.remote.OkHttp;
import ru.lucky_book.database.DBHelper;
import ru.lucky_book.database.RealmLogin;
import ru.lucky_book.entities.OrderData;
import ru.lucky_book.entities.spsr.CostResponseBody;
import ru.lucky_book.entities.spsr.GetCitiesRequestBody;
import ru.lucky_book.entities.spsr.GetCitiesResponseBody;
import ru.lucky_book.entities.spsr.InvoiceRequestBody;
import ru.lucky_book.entities.spsr.InvoiceResponseBody;
import ru.lucky_book.entities.spsr.LoginRequestBody;
import ru.lucky_book.entities.spsr.LoginResponseBody;
import ru.lucky_book.entities.spsr.Shipper;
import ru.lucky_book.network.SpsrApi;

public class SpsrRepository {

    private static SpsrRepository sInstance;
    private String mBaseUrl;
    private Context mContext;

    private SpsrApi mApi;

    private SpsrRepository(Context context, String baseUrl) {
        mContext = context;
        mBaseUrl = baseUrl;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .client(OkHttp.getClientSpsr())
                .build();
        mApi = retrofit.create(SpsrApi.class);
    }

    public static SpsrRepository getInstance(Context context, String baseUrl) {
        if (sInstance == null || !sInstance.mBaseUrl.equals(baseUrl)) {
            sInstance = new SpsrRepository(context, baseUrl);
        }
        return sInstance;
    }

    public LoginResponseBody.Login login() {
        if (mApi != null) {
            RealmLogin realmLogin = DBHelper.getRealmLogin();
            if (realmLogin != null) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(new Date());
                int currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                calendar.setTime(realmLogin.getLoginDate());
                int loginDay = calendar.get(Calendar.DAY_OF_MONTH);
                if (loginDay == currentDay) {
                    LoginResponseBody.Login login = new LoginResponseBody.Login();
                    login.setSid(realmLogin.getSid());
                    login.setAdmin(false);
                    return login;
                }
            }
            Call<LoginResponseBody> request = mApi.login(new LoginRequestBody());
            try {
                Response<LoginResponseBody> response = request.execute();
                LoginResponseBody responseBody = response.body();
                if (responseBody != null) {
                    return responseBody.getLogin();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public GetCitiesResponseBody.City getCity(String part) {
        GetCitiesRequestBody requestBody = new GetCitiesRequestBody();
        GetCitiesRequestBody.GetCities getCities = new GetCitiesRequestBody.GetCities();
        getCities.setCityName(part);
        getCities.setCountryName(mContext.getString(R.string.default_country));
        requestBody.setGetCities(getCities);
        if (mApi != null) {
            Call<GetCitiesResponseBody> request = mApi.getCities(requestBody);
            try {
                Response<GetCitiesResponseBody> response = request.execute();
                GetCitiesResponseBody responseBody = response.body();
                if (responseBody != null) {
                    List<GetCitiesResponseBody.City> cities = responseBody.getCities();
                    if (cities != null && !cities.isEmpty()) {
                        return cities.get(0);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public CostResponseBody.Tariff calculateCost(GetCitiesResponseBody.City sourceCity, GetCitiesResponseBody.City destinationCity, LoginResponseBody.Login login) {
        if (mApi != null) {
            String toCity = destinationCity.getCityId() + "|" + destinationCity.getCityOwnerId();
            String fromCity = sourceCity.getCityId() + "|" + sourceCity.getCityOwnerId();
            int defaultWeight = 1;
            Call<CostResponseBody> request = mApi.calculateCost(toCity, fromCity, defaultWeight, login.getSid(), mContext.getString(R.string.spsr_icn));
            try {
                Response<CostResponseBody> response = request.execute();
                if (response != null) {
                    CostResponseBody costResponseBody = response.body();
                    if (costResponseBody != null) {
                        List<CostResponseBody.Tariff> tariffs = costResponseBody.getTariffs();
                        if (tariffs != null && !tariffs.isEmpty()) {
                            return findZebraOnline(tariffs);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public InvoiceResponseBody.Invoice createInvoice(OrderData orderData) {

        LoginResponseBody.Login login = login();
        if (login != null && mApi != null) {
            InvoiceRequestBody requestBody = createInvoiceRequestBody(login.getSid(), orderData);
            Call<InvoiceResponseBody> request = mApi.createInvoice(requestBody);
            try {
                Response<InvoiceResponseBody> response = request.execute();
                if (response != null) {
                    InvoiceResponseBody responseBody = response.body();
                    if (responseBody != null) {
                        return responseBody.getInvoice();
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private InvoiceRequestBody createInvoiceRequestBody(String sid, OrderData orderData) {
        InvoiceRequestBody requestBody = new InvoiceRequestBody();
        GetCitiesResponseBody.City sourceCity = getCity(mContext.getString(R.string.default_city_source));
        GetCitiesResponseBody.City destinationCity = getCity(orderData.getAddress().getCity());

        // create invoicesLogin
        InvoiceRequestBody.Login invoicesLogin = new InvoiceRequestBody.Login();
        invoicesLogin.setSid(sid);

        // create xml converter
        InvoiceRequestBody.XmlConverter xmlConverter = new InvoiceRequestBody.XmlConverter();

        // create general info
        InvoiceRequestBody.XmlConverter.GeneralInfo generalInfo = new InvoiceRequestBody.XmlConverter.GeneralInfo();

        // create invoices
        List<InvoiceRequestBody.XmlConverter.GeneralInfo.Invoice> invoices = new ArrayList<>();
        InvoiceRequestBody.XmlConverter.GeneralInfo.Invoice invoice = new InvoiceRequestBody.XmlConverter.GeneralInfo.Invoice();

        // create shipper
        Shipper shipper = new Shipper();
        shipper.setAddress(String.format("%s, %s", mContext.getString(R.string.default_street_source), mContext.getString(R.string.default_house_source)));
        shipper.setRegion(sourceCity.getRegionName());
        shipper.setCity(sourceCity.getName());
        shipper.setContactName(mContext.getString(R.string.default_shipper_name));


        // create receiver
        InvoiceRequestBody.XmlConverter.GeneralInfo.Invoice.Receiver receiver = new InvoiceRequestBody.XmlConverter.GeneralInfo.Invoice.Receiver();
        receiver.setAddress(String.format("%s, %s, %s", orderData.getAddress().getStreet(), orderData.getAddress().getHouse(), orderData.getAddress().getFlat()));
        receiver.setCity(destinationCity.getName());
        receiver.setRegion(destinationCity.getRegionName());
        receiver.setContactName(String.format("%s %s %s", orderData.getContact().getSurname(), orderData.getContact().getName(), orderData.getContact().getPatronymic()));
        receiver.setEmail(orderData.getContact().getEmail());
        receiver.setPhone(orderData.getContact().getPhone());

        // create pieces
        List<InvoiceRequestBody.XmlConverter.GeneralInfo.Invoice.Piece> pieces = new ArrayList<>();
        InvoiceRequestBody.XmlConverter.GeneralInfo.Invoice.Piece piece = new InvoiceRequestBody.XmlConverter.GeneralInfo.Invoice.Piece();
        pieces.add(piece);

        //create Sms

        InvoiceRequestBody.XmlConverter.GeneralInfo.Invoice.Sms sms = new InvoiceRequestBody.XmlConverter.GeneralInfo.Invoice.Sms();
        sms.setSmsToReceiver(1);
        sms.setPhoneNumber(orderData.getContact().getPhone());

        //create additional

        InvoiceRequestBody.XmlConverter.GeneralInfo.Invoice.AdditionalServices additionalServices = new InvoiceRequestBody.XmlConverter.GeneralInfo.Invoice.AdditionalServices();
        additionalServices.setCheckContents(1);


        invoice.setSms(sms);
        invoice.setAdditionalServices(additionalServices);
        invoice.setShipper(shipper);
        invoice.setReceiver(receiver);
        invoice.setPieces(pieces);

        invoices.add(invoice);

        generalInfo.setInvoices(invoices);
        xmlConverter.setGeneralInfo(generalInfo);

        requestBody.setLogin(invoicesLogin);
        requestBody.setXmlConverter(xmlConverter);

        return requestBody;
    }

    private CostResponseBody.Tariff findZebraOnline(List<CostResponseBody.Tariff> tariffs) {
        for (CostResponseBody.Tariff tariff : tariffs) {
            if (tariff.getTariffType().contains(mContext.getString(R.string.zebra_online))) {
                return tariff;
            }
        }
        return null;
    }
}
