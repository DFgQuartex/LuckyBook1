package ru.lucky_book.dataapi.remote;

import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;
import ru.lucky_book.data.insta.MediasInsta;
import rx.Observable;

/**
 * Created by Загит Талипов on 29.12.2016.
 */

public interface InstagramService {


    String API_ENDPOINT = "https://api.instagram.com";


    @GET("v1/users/self/media/recent")
    Observable<MediasInsta> getMedias(@QueryMap Map<String, String> map);

    public static class Creator {
        public static InstagramService getInstaService() {
            return new Retrofit.Builder()
                    .baseUrl(API_ENDPOINT)
                    .client(OkHttp.getClient())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(InstagramService.class);
        }
    }
}
