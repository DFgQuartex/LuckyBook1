package ru.lucky_book.network.repository;

import android.content.Context;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.lucky_book.R;
import ru.lucky_book.dataapi.remote.OkHttp;
import ru.lucky_book.entities.OrderData;
import ru.lucky_book.network.ServiceApi;

public class ServiceRepository {

    public static final int STATE_INCORRECT = 100;
    public static final int STATE_CONNECTION = 200;

    private ServiceApi mApi;
    private OnLoadListener mListener;

    private static ServiceRepository sInstance;

    private ServiceRepository(Context context) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.service_base_url))
                .addConverterFactory(GsonConverterFactory.create())
                .client(OkHttp.getClientSpsr())
                .build();
        mApi = retrofit.create(ServiceApi.class);
    }

    public interface OnLoadListener {
        void onLoad(String code);
        void onFail(int state);
    }

    public static ServiceRepository getInstance(Context context) {
        return sInstance == null ? new ServiceRepository(context) : sInstance;
    }

    public void setListener(OnLoadListener listener) {
        mListener = listener;
    }

    public void sendPromoCode(final String promoCode) {
        if (mApi != null) {
            Call<ResponseBody> request = mApi.sendPromoCode(promoCode);
            request.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.code() == HttpURLConnection.HTTP_OK) {
                        if (mListener != null) {
                            mListener.onLoad(promoCode);
                        }
                    } else {
                        if (mListener != null) {

                            mListener.onFail(STATE_INCORRECT);

                        }
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    if (mListener != null) {
                        mListener.onFail(STATE_CONNECTION);
                    }
                }
            });
        }
    }

    public String sendOrderData(OrderData orderData) {
        if (mApi != null) {
            Call<ResponseBody> request = mApi.sendOrderData(orderData);
            try {
                Response<ResponseBody> response = request.execute();
                if (response.code() == HttpURLConnection.HTTP_OK) {
                    ResponseBody responseBody = response.body();
                    return responseBody.string();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
