package ru.lucky_book.network.repository;

import android.content.Context;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.lucky_book.R;
import ru.lucky_book.dataapi.remote.OkHttp;
import ru.lucky_book.entities.instagram.InstagramLoginResponse;
import ru.lucky_book.entities.instagram.InstagramMediaResponseBody;
import ru.lucky_book.network.InstagramApi;

public class InstagramRepository {

    private static InstagramRepository sInstance;

    private InstagramApi mApi;

    private InstagramRepository(Context context) {

//        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder();
//        clientBuilder.addInterceptor(new Interceptor() {
//            @Override
//            public okhttp3.Response intercept(Chain chain) throws IOException {
//                Request originalRequest = chain.request();
//
//                Request request = originalRequest.newBuilder()
//                        .header("cookie", cookies)
//                        .method(originalRequest.method(), originalRequest.body())
//                        .build();
//
//                return chain.proceed(request);
//            }
//        });

//        OkHttpClient client = clientBuilder.build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.instagram_base_url))
                .client(OkHttp.getClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        mApi = retrofit.create(InstagramApi.class);
    }

    public interface OnLoadListener<T> {
        void onLoad(T data);

        void onFail();
    }

    public static InstagramRepository getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new InstagramRepository(context);
        }
        return sInstance;
    }

    public void getUsername(String id, final OnLoadListener listener) {
        if (mApi != null) {
            String queryParam = String.format("ig_user(%s){username}", id);
            Call<InstagramLoginResponse> request = mApi.getLogin(queryParam);
            request.enqueue(new Callback<InstagramLoginResponse>() {
                @Override
                public void onResponse(Call<InstagramLoginResponse> call, Response<InstagramLoginResponse> response) {
                    if (listener != null) {
                        InstagramLoginResponse responseBody = response.body();
                        if (responseBody != null) {
                            listener.onLoad(responseBody.getUsername());
                        } else {
                            listener.onFail();
                        }
                    }
                }

                @Override
                public void onFailure(Call<InstagramLoginResponse> call, Throwable t) {
                    if (listener != null) {
                        listener.onFail();
                    }
                }
            });
        }
    }

    public void getMedia(String cookie, String username, final OnLoadListener listener, String endCursor) {
        if (mApi != null) {
            Call<InstagramMediaResponseBody> request = mApi.getMedias(cookie, username, 1, endCursor);
            request.enqueue(new Callback<InstagramMediaResponseBody>() {
                @Override
                public void onResponse(Call<InstagramMediaResponseBody> call, Response<InstagramMediaResponseBody> response) {
                    if (listener != null) {
                        InstagramMediaResponseBody responseBody = response.body();
                        if (responseBody != null && responseBody.getUser() != null) {
                            listener.onLoad(responseBody.getUser().getMedia());
                        } else {
                            listener.onFail();
                        }
                    }
                }

                @Override
                public void onFailure(Call<InstagramMediaResponseBody> call, Throwable t) {
                    if (listener != null) {
                        listener.onFail();
                    }
                }
            });
        }
    }
}
