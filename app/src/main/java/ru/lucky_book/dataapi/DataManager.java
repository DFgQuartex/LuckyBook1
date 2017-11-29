package ru.lucky_book.dataapi;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.lucky_book.app.Preferences;
import ru.lucky_book.data.Cover;
import ru.lucky_book.data.Order;
import ru.lucky_book.data.OrderId;
import ru.lucky_book.data.OrderLink;
import ru.lucky_book.data.Price;
import ru.lucky_book.data.PromoCode;
import ru.lucky_book.data.SubCover;
import ru.lucky_book.data.SuccessOrderResponse;
import ru.lucky_book.data.insta.MediasInsta;
import ru.lucky_book.dataapi.remote.ApiProvider;
import ru.lucky_book.dataapi.remote.InstagramService;
import ru.lucky_book.dataapi.remote.LuckyBookService;
import ru.lucky_book.database.RealmCoverAlbum;
import ru.lucky_book.database.RealmSubCoverAlbum;
import ru.lucky_book.utils.ConnectionUtils;
import ru.luckybook.data.DeliveryPriceSpsr;
import rx.Observable;


public class DataManager {

    private static DataManager mDataManager;
    private LuckyBookService mLuckyBookService;
    private InstagramService mInstagramService;
    private static Context mContext;

    public static DataManager getInstance() {
        if (mDataManager == null) {
            mDataManager = new DataManager();
        }
        return mDataManager;
    }

    private DataManager() {
        mLuckyBookService = ApiProvider.getLuckyBookService();
        mInstagramService = InstagramService.Creator.getInstaService();
    }

    public Observable<List<Cover>> listObservableCover() {
        if (ConnectionUtils.connectedToNetwork(mContext))
            return mLuckyBookService.getCoverList();
        return getLocalCovers();
    }

    public static void initContext(Context context) {
        mContext = context;
    }

    public Observable<List<SubCover>> listObservableSubCover(@NonNull String path, int id) {
        if (ConnectionUtils.connectedToNetwork(mContext))
            return mLuckyBookService.getSubCoverList(path);
        return getLocalSubCovers(id);
    }

    public Observable<PromoCode> checkPromoCode(@NonNull String s) {
        return mLuckyBookService.checkPromoCode(s);
    }

    public Observable<MediasInsta> getMedias(@NonNull String token, @Nullable String maxId, String limit) {
        Map<String, String> map = new HashMap<>();
        map.put("access_token", token);
        map.put("count", limit);
        if (maxId != null)
            map.put("max_id", maxId);
        return mInstagramService.getMedias(map);
    }

    public Observable<Price> getPrice(@NonNull int count, String promo) {
        Map<String, String> map = new HashMap<>();
        map.put("count", String.valueOf(count));
        if (promo != null && !promo.isEmpty()) {
            map.put("promo", promo);
        }
        return mLuckyBookService.getPrice(map);
    }

    public Observable<SuccessOrderResponse> sendSuccessfulOrder(@NonNull Order order, @Nullable String promo) {
        if (promo == null)
            promo = "";
        else {
            promo = "/" + promo;
        }
        return mLuckyBookService.sendSuccessfulOrder(promo, order);
    }

    public Observable<SuccessOrderResponse> sendPreOrder(@NonNull Order order, @Nullable String promo) {
        if (promo == null)
            promo = "";
        else {
            promo = "/" + promo;
        }
        return mLuckyBookService.sendPreOrder(promo, order);
    }

    public Observable<OrderId> getNewIdForOrder() {
        return mLuckyBookService.getOrderId();
    }

    public Observable<SuccessOrderResponse> sendOrderLink(@NonNull OrderLink orderLink) {
        return mLuckyBookService.sendOrderLink(orderLink);
    }


    public void firstStartDone() {
        Preferences.Instructions.firstStartDone();
    }

    public boolean isFirstStartDone() {
        return Preferences.Instructions.isFirstStartDone();
    }

    public Observable<DeliveryPriceSpsr> findCity(String city) {
        return mLuckyBookService.findCity(city);
    }

    public void saveCoversLocal(List<Cover> covers) {
        Realm.getDefaultInstance().executeTransactionAsync(realm -> {
            RealmResults<RealmCoverAlbum> coverAlbums = realm.where(RealmCoverAlbum.class).findAll();
            List<RealmCoverAlbum> realmCovers = new ArrayList<>();
            for (Cover cover : covers) {
                if (coverAlbums.where().equalTo("id", cover.getId()).findFirst() == null) {
                    RealmCoverAlbum album = realm.createObject(RealmCoverAlbum.class, cover.getId());
                    album.init(cover);
                    realmCovers.add(album);
                }
            }
        });
    }

    public Observable<List<Cover>> getLocalCovers() {
        RealmResults<RealmCoverAlbum> realmCovers = Realm.getDefaultInstance().where(RealmCoverAlbum.class).findAll();
        if (realmCovers != null && !realmCovers.isEmpty()) {
            List<Cover> covers = new ArrayList<>();
            for (RealmCoverAlbum realmCover : realmCovers) {
                covers.add(realmCover.getCover());
            }
            if (!covers.isEmpty())
                return Observable.just(covers);
        }
        return Observable.error(new Exception());
    }

    public Observable<List<SubCover>> getLocalSubCovers(int id) {
        RealmCoverAlbum realmCovers = Realm.getDefaultInstance().where(RealmCoverAlbum.class).equalTo("id", id).findFirst();
        if (realmCovers != null) {
            List<SubCover> covers = new ArrayList<>();
            for (RealmSubCoverAlbum realmCover : realmCovers.getSubcoverAlbumList()) {
                covers.add(realmCover.getCover());
            }
            if (!covers.isEmpty())
            return Observable.just(covers);
        }
        return Observable.error(new Exception());
    }

    public void saveSubCoversLocal(int id, List<SubCover> covers) {
        Realm.getDefaultInstance().executeTransactionAsync(realm -> {
            RealmCoverAlbum albumCover = realm.where(RealmCoverAlbum.class).equalTo("id", id).findFirst();
            if (albumCover == null)
                return;
            for (SubCover cover : covers) {
                RealmSubCoverAlbum subcoverAlbum = albumCover.getSubcoverAlbumList().where().equalTo("id", cover.getId()).findFirst();
                if (subcoverAlbum == null) {
                    subcoverAlbum = realm.createObject(RealmSubCoverAlbum.class, cover.getId());
                    subcoverAlbum.init(cover);
                    albumCover.getSubcoverAlbumList().add(subcoverAlbum);
                }
            }
        });
    }
}
