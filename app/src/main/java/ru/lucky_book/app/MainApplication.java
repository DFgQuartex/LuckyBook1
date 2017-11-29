package ru.lucky_book.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.vk.sdk.VKSdk;
import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;

import java.util.HashMap;
import java.util.Map;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import ru.lucky_book.dataapi.DataManager;
import ru.lucky_book.database.DBHelper;
import ru.lucky_book.database.DbMigration;
import ru.lucky_book.features.upload.UploadService;
import ru.lucky_book.utils.NotificationUtils;

/**
 * Created by demafayz on 24.08.16.
 */
public class MainApplication extends Application {
    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    public void onCreate() {
        MultiDex.install(this);
        super.onCreate();
        Preferences.init(this);
        Fabric.with(this, new Crashlytics());
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        populateRealm(getApplicationContext());
        VKSdk.initialize(getApplicationContext());
        NotificationUtils.init(getApplicationContext());
        DataManager.initContext(getApplicationContext());
        startService(new Intent(this, UploadService.class));

        YandexMetricaConfig.Builder configBuilder = YandexMetricaConfig.newConfigBuilder("8b5d0a4e-3b77-458c-bb1f-0affc3da3519");

        boolean isFirstApplicationLaunch = false;
        if (!isFirstApplicationLaunch) {
            //Передайте значение true, если не хотите, чтобы данный пользователь засчитывался как новый
            configBuilder.handleFirstActivationAsUpdate(true);
        }
        //Создание объекта расширенной конфигурации
        YandexMetricaConfig extendedConfig = configBuilder.build();
        // Инициализация AppMetrica SDK
        YandexMetrica.activate(getApplicationContext(), extendedConfig);
        // Отслеживание активности пользователей
        YandexMetrica.enableActivityAutoTracking(this);
    }

    public enum Screen
    {
        Главная,// главный экран
        ВыборФото,//Выбор фото
        ДобавлениеФото,//Добавление фото
        ВыборОбложки,//Выбор обложки
        Предпросмотр,//до покупки
        ЗаполнениеДанных,//заполнение данных
        ЗаказСделан,
        ЭмуляцияАльбома

    }
    static public Screen currentScreen;
    public void info(){

        Map<String, Object> eventAttributes = new HashMap<String, Object>();
        String id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        eventAttributes.put(currentScreen.name(), " Открыл");
        YandexMetrica.reportEvent("User " + id, eventAttributes);
    }


    private void populateRealm(Context context) {
        Realm.init(context);
        RealmConfiguration configuration = new RealmConfiguration.Builder()
                .schemaVersion(DBHelper.VERSION)
                .migration(new DbMigration())
                .build();
        Realm.setDefaultConfiguration(configuration);
    }
}
