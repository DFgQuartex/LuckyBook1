package ru.lucky_book.utils.pay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import ru.yandex.money.android.PaymentActivity;

/**
 * Created by demafayz on 31.08.16.
 */
public class YandexMoney {

    public static final int REQUEST_CODE = 101;

    private static final String SHOP_ID = "71848";
    private static final String PATTERN_ID = "67979";

    private static final String SHOP_ID_KEY = "shopId";
    private static final String SUM_KEY = "sum";
    private static final String NAME_KEY = "custName";
    private static final String ADDRESS_KEY = "custAddr";
    private static final String EMAIL_KEY = "custEmail";

    private final WeakReference<Activity> activity;
    private final Map<String, String> params;

    public YandexMoney(Activity activity, double amount, String name, String address, String email) {
        this.activity = new WeakReference<>(activity);
        params = new HashMap<>();
        params.put(SHOP_ID_KEY, SHOP_ID);
        params.put(SUM_KEY, Double.toString(amount));
        params.put(NAME_KEY, name);
        params.put(ADDRESS_KEY, address);
        params.put(EMAIL_KEY, email);
    }

    public void pay() {
        if (isValid()) {
            Activity activity = this.activity.get();
            ApiData apiData = ApiData.getFromProperties(activity);

            Intent intent = PaymentActivity.getBuilder(activity)
                    .setPaymentParams(PATTERN_ID, params)
                    .setClientId(apiData.clientId)
                    .setHost(apiData.host)
                    .build();
            activity.startActivityForResult(intent, REQUEST_CODE);
        }
    }

    public boolean isValid() {
        return Double.parseDouble(params.get(SUM_KEY)) > 0;
    }

    private static class ApiData {

        public final String clientId;
        public final String host;

        private ApiData(String clientId, String host) {
            this.clientId = clientId;
            this.host = host;
        }

        public static ApiData getFromProperties(Context context)  {
            Properties prop = loadProperties(context);
            return new ApiData(prop.getProperty("client_id"), prop.getProperty("host"));
        }

        private static Properties loadProperties(Context context) {
            InputStream is = null;
            try {
                is = context.getAssets().open("yandex_money.properties");
                Properties prop = new Properties();
                prop.load(is);
                return prop;
            } catch (IOException e) {
                throw new IllegalStateException("no properties file found", e);
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
