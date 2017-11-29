package ru.lucky_book.dataapi.remote;


import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.lucky_book.BuildConfig;


public class ApiProvider {

    private static String API_ENDPOINT = BuildConfig.ENDPOINT;

    public static LuckyBookService getLuckyBookService() {
        return new Retrofit.Builder()
                .baseUrl(API_ENDPOINT)
                .client(OkHttp.getClient())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(LuckyBookService.class);
    }
}
