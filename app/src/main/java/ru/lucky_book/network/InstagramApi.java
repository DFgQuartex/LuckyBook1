package ru.lucky_book.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import ru.lucky_book.entities.instagram.InstagramLoginResponse;
import ru.lucky_book.entities.instagram.InstagramMediaResponseBody;

public interface InstagramApi {

    @GET("/{username}/")
    Call<InstagramMediaResponseBody> getMedias(@Header("cookie") String cookie, @Path(value = "username") String username, @Query(value = "__a", encoded= true) int value, @Query(value = "max_id") String maxId);

    @FormUrlEncoded
    @POST("/query/")
    Call<InstagramLoginResponse> getLogin(@Field(value = "q") String query);

}
