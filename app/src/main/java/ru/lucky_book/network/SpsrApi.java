package ru.lucky_book.network;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import ru.lucky_book.entities.spsr.CostResponseBody;
import ru.lucky_book.entities.spsr.GetCitiesRequestBody;
import ru.lucky_book.entities.spsr.GetCitiesResponseBody;
import ru.lucky_book.entities.spsr.InvoiceRequestBody;
import ru.lucky_book.entities.spsr.InvoiceResponseBody;
import ru.lucky_book.entities.spsr.LoginRequestBody;
import ru.lucky_book.entities.spsr.LoginResponseBody;

public interface SpsrApi {

    @POST("/")
    Call<LoginResponseBody> login(@Body LoginRequestBody body);

    @POST("/")
    Call<GetCitiesResponseBody> getCities(@Body GetCitiesRequestBody body);

    @POST("/")
    Call<InvoiceResponseBody> createInvoice(@Body InvoiceRequestBody body);

    @GET("/cgi-bin/postxml.pl?TARIFFCOMPUTE_2")
    Call<CostResponseBody> calculateCost(@Query(value = "ToCity") String toCity, @Query("FromCity") String fromCity, @Query("Weight") int weight, @Query("SID") String sid, @Query("ICN") String icn);
}
