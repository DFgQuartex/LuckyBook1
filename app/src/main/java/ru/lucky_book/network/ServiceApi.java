package ru.lucky_book.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.lucky_book.entities.OrderData;

public interface ServiceApi {

    @POST("/api/v2/orders")
    Call<ResponseBody> sendOrderData(@Body OrderData orderData);

    @GET("/api/v2/check_promo_code")
    Call<ResponseBody> sendPromoCode(@Query(value = "code", encoded = true) String promoCode);

}
