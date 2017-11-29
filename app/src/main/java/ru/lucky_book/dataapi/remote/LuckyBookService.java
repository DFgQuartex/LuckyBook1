package ru.lucky_book.dataapi.remote;


import java.util.List;
import java.util.Map;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import ru.lucky_book.data.Cover;
import ru.lucky_book.data.Order;
import ru.lucky_book.data.OrderId;
import ru.lucky_book.data.OrderLink;
import ru.lucky_book.data.Price;
import ru.lucky_book.data.PromoCode;
import ru.lucky_book.data.SubCover;
import ru.lucky_book.data.SuccessOrderResponse;
import ru.luckybook.data.DeliveryPriceSpsr;
import rx.Observable;

/**
 * Created by Zahit Talipov on 08.07.2016.
 */
public interface    LuckyBookService {

    @GET("{path}")
    Observable<List<SubCover>> getSubCoverList(@Path(value = "path", encoded = true) String path);

    @GET("/jsn/rubric")
    Observable<List<Cover>> getCoverList();

    @GET("jsn/check/{promo}")
    Observable<PromoCode> checkPromoCode(@Path("promo") String promo);

    @GET("jsn/price")
    Observable<Price> getPrice(@QueryMap Map<String, String> stringStringMap);

    @POST("jsn/order{promo}")
    Observable<SuccessOrderResponse> sendSuccessfulOrder(@Path(value = "promo", encoded = true) String promo, @Body Order order);

    @POST("jsn/preorder{promo}")
    Observable<SuccessOrderResponse> sendPreOrder(@Path(value = "promo", encoded = true) String promo, @Body Order order);

    @POST("jsn/order")
    Observable<SuccessOrderResponse> sendOrderLink(@Body OrderLink order);

    @GET("jsn/id")
    Observable<OrderId> getOrderId();

    @GET("jsn/delivery")
    Observable<DeliveryPriceSpsr> findCity(@Query("city") String city);
}
