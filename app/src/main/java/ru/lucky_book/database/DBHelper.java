package ru.lucky_book.database;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.insta.InstaFilter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.Sort;
import ru.lucky_book.app.Preferences;
import ru.lucky_book.data.evenbus.UploadEvent;
import ru.lucky_book.entities.spread.Page;
import ru.lucky_book.entities.spread.PageTemplate;
import ru.lucky_book.entities.spread.Picture;
import ru.lucky_book.entities.spread.PictureMatrixState;
import ru.lucky_book.entities.spread.PictureViewState;
import ru.lucky_book.entities.spread.Spread;
import ru.lucky_book.features.spreads.OrderClearedListener;
import ru.lucky_book.features.spreads.UpdateAlbumListener;
import ru.lucky_book.utils.IDUtils;

/**
 * Created by demafayz on 24.08.16.
 */
public class DBHelper {
    public static final int VERSION = 12;
    public static final String TAG = DBHelper.class.getSimpleName();

    public static RealmAppConfigs saveAppConfig(String googleLogin) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(RealmAppConfigs.class).findAll().deleteAllFromRealm();
        RealmAppConfigs appConfigs = realm.createObject(RealmAppConfigs.class);
        appConfigs.setGoogleLogin(googleLogin);
        realm.commitTransaction();
        return appConfigs;
    }

    /*public static RealmAppConfigs updateAppConfig(String googleLogin) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmAppConfigs appConfigs = realm.where(RealmAppConfigs.class).findFirst();
        realm.commitTransaction();
    }*/

    public static RealmAppConfigs getAppConfig() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(RealmAppConfigs.class).findFirst();
    }

    public static void saveAlbum(String mAlbumTitle, String mAlbumCover, List<Spread> spreads, String promoCode, int coverId, int maxCount, boolean coverPromo,
                                 UpdateAlbumListener onSuccess, Realm.Transaction.OnError onError) {
        Realm realm = Realm.getDefaultInstance();
        final RealmAlbum[] realmAlbum = {null};
        realm.executeTransactionAsync(realm1 -> {
            realmAlbum[0] = populateRealmObject(null, mAlbumTitle, mAlbumCover, spreads, realm1, coverId);
            realmAlbum[0].setPromoCode(promoCode);
            realmAlbum[0].setCoverId(coverId);
            realmAlbum[0].setUpdateTime(new Date());
            realmAlbum[0].setMaxSize(maxCount);
            realmAlbum[0].setCoverPromo(coverPromo);
            realmAlbum[0] = realm1.copyFromRealm(realmAlbum[0]);
        }, () -> onSuccess.updateAlbum(realmAlbum[0]), onError);
    }

    public static RealmLogin getRealmLogin() {
        Realm realm = Realm.getDefaultInstance();
        return realm.where(RealmLogin.class).findFirst();
    }

    public static RealmLogin saveLogin(String sid) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmLogin realmLogin = realm.where(RealmLogin.class).findFirst();
        if (realmLogin == null) {
            realmLogin = realm.createObject(RealmLogin.class);
        }
        realmLogin.setSid(sid);
        realmLogin.setLoginDate(new Date());
        realm.commitTransaction();
        return realmLogin;
    }

    private static RealmAlbum populateRealmObject(String id, String mAlbumTitle, String mAlbumCover, List<Spread> spreads, Realm realm, int coverId) {
        RealmAlbum realmAlbum;
        if (id == null) {
            //если id null, значит разраб пытается создать новый альбом и id нужно сгинерить
            id = IDUtils.idGenerate();
            realmAlbum = realm.createObject(RealmAlbum.class, id);
        } else {
            //если id не null, значит разраб пытвется обновить уже существующий альбом
            realmAlbum = realm.where(RealmAlbum.class).equalTo("id", id).findFirst();
        }

        realmAlbum.setCover(mAlbumCover);
        realmAlbum.setTitle(mAlbumTitle);
        realmAlbum.setCoverId(coverId);
        realmAlbum.setSpreads(populateRealmSpreads(spreads, realm));
        return realmAlbum;
    }

    public static RealmAlbum updateAlbum(String id, String mAlbumTitle, String mAlbumCover, List<Spread> spreads, int idCover) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmAlbum realmAlbum = populateRealmObject(id, mAlbumTitle, mAlbumCover, spreads, realm, idCover);
        realmAlbum.setUpdateTime(new Date());
        realmAlbum = realm.copyFromRealm(realmAlbum);
        realm.commitTransaction();
        realm.close();
        return realmAlbum;
    }

    public static RealmAlbum updateAlbumThumbnail(String id, String thumbnailPath) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmAlbum realmAlbum = findAlbumById(realm, id);
        realmAlbum.setThumbnailPath(thumbnailPath);
        realm.copyToRealmOrUpdate(realmAlbum);
        realm.commitTransaction();
        realm.close();
        return realmAlbum;
    }

    public static RealmAlbum updateAlbumFullSize(String id, String fullSizePath) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmAlbum realmAlbum = findAlbumById(realm, id);
        realmAlbum.setFullSizePath(fullSizePath);
        realm.copyToRealmOrUpdate(realmAlbum);
        realm.commitTransaction();
        realm.close();
        return realmAlbum;
    }

    public static void updateAlbumFullSizeLocal(String id, String fullSizePath, UpdateAlbumListener listener) {
        Realm realm = Realm.getDefaultInstance();
        realm.executeTransactionAsync(realm1 -> {
            RealmAlbum realmAlbum = findAlbumById(realm1, id);
            realmAlbum.setFullSizePathLocal(fullSizePath);
            realm1.copyToRealmOrUpdate(realmAlbum);
        }, () -> {
            listener.updateAlbum(null);
        }, error -> {
            error.printStackTrace();
        });
    }

    private static RealmAlbum updateAlbum(RealmAlbum updatedRealm) {
        removeAlbumById(updatedRealm.getId());
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        updatedRealm = realm.copyToRealm(updatedRealm);
        updatedRealm = realm.copyFromRealm(updatedRealm);
        realm.commitTransaction();
        realm.close();
        return updatedRealm;
    }

    public static void removeAlbumById(String id) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.where(RealmAlbum.class).equalTo("id", id).findAll().deleteAllFromRealm();
        realm.commitTransaction();
        realm.close();
    }

    public static RealmAlbum findAlbumById(String id) {
        RealmAlbum realmAlbum;
        Realm realm = Realm.getDefaultInstance();
        realmAlbum = realm.copyFromRealm(realm.where(RealmAlbum.class).equalTo("id", id).findFirst());
        realm.close();
        return realmAlbum;
    }

    public static RealmAlbum findAlbumById(Realm realm, String id) {
        RealmAlbum realmAlbum;
        realmAlbum = realm.copyFromRealm(realm.where(RealmAlbum.class).equalTo("id", id).findFirst());
        return realmAlbum;
    }

    public static List<Spread> getRealmAlbumSpreads(RealmAlbum album) {
        List<Spread> spreads = new ArrayList<>();
        if (album != null && album.getSpreads() != null) {
            for (RealmSpread realmSpread : album.getSpreads()) {
                Spread spread = new Spread();
                spread.setLeft(getRealmPagesPage(realmSpread.getLeft()));
                spread.setRight(getRealmPagesPage(realmSpread.getRight()));
                spreads.add(spread);
            }
        }
        return spreads;
    }

    public static Page getRealmPagesPage(RealmPage realmPage) {
        if (realmPage != null) {
            Page page = new Page();
            page.setPictures(getRealmPicturesPictures(realmPage.getPictures()));
            page.setTemplate(PageTemplate.valueOf(realmPage.getTemplateValue()));
            return page;
        }
        return null;
    }

    public static Picture[] getRealmPicturesPictures(RealmList<RealmPicture> realmPictures) {
        Picture[] pictures = null;
        if (realmPictures != null) {
            pictures = new Picture[realmPictures.size()];
            for (int i = 0; i < pictures.length; i++) {

                RealmPicture realmPicture = realmPictures.get(i);
                if (!TextUtils.isEmpty(realmPicture.getPath())) {
                    Picture picture = new Picture();

                    picture.setPath(realmPicture.getPath());
                    picture.setOrigWidth(realmPicture.getOrigWidth());
                    picture.setOrigHeight(realmPicture.getOrigHeight());

                    PictureMatrixState matrixState = new PictureMatrixState();
                    matrixState.set(realmPicture.getX(), realmPicture.getY(), realmPicture.getZoom(), realmPicture.getRotation());
                    picture.setMatrixState(matrixState);
                    if (!TextUtils.isEmpty(realmPicture.getFilter())) {
                        try {
                            picture.setFilter((Class<? extends InstaFilter>) Class.forName(realmPicture.getFilter()));
                        } catch (ClassNotFoundException e) {
                            Log.e(DBHelper.class.getSimpleName(), e.getMessage());
                        }
                    }

                    PictureViewState viewState = new PictureViewState();
                    viewState.setImageW(realmPicture.getImageW());
                    viewState.setImageH(realmPicture.getImageH());
                    viewState.setViewportW(realmPicture.getViewportW());
                    viewState.setViewportH(realmPicture.getViewportH());
                    picture.setViewState(viewState);

                    pictures[i] = picture;
                } else
                    pictures[i] = null;
            }
        }
        return pictures;
    }

    public static boolean hasAlbums() {
        Realm realm = Realm.getDefaultInstance();
        try {
            return realm.where(RealmAlbum.class).count() > 0;
        } finally {
            realm.close();
        }
    }

    public static List<RealmAlbum> getAllAlbums() {
        List<RealmAlbum> realmAlbums;
        Realm realm = Realm.getDefaultInstance();
        realmAlbums = realm.copyFromRealm(realm.where(RealmAlbum.class).findAll().sort("updateTime", Sort.DESCENDING));
        realm.close();
        return realmAlbums;
    }

    private static RealmList<RealmSpread> populateRealmSpreads(List<Spread> spreads, Realm realm) {
        RealmList<RealmSpread> realmSpreads = new RealmList<>();
        for (Spread spread : spreads) {
            RealmSpread realmSpread = realm.createObject(RealmSpread.class);
            realmSpread.setLeft(populateRealmPage(spread.getLeft(), realm));
            realmSpread.setRight(populateRealmPage(spread.getRight(), realm));
            realmSpreads.add(realmSpread);
        }
        return realmSpreads;
    }

    private static RealmPage populateRealmPage(Page left, Realm realm) {
        if (left != null) {
            RealmPage realmPage = realm.createObject(RealmPage.class);
            realmPage.setTemplateValue(left.getTemplate().name());
            realmPage.setPictures(populateRealmPictures(left.getPictures(), realm));
            return realmPage;
        }
        return null;
    }

    private static RealmList<RealmPicture> populateRealmPictures(Picture[] pictures, Realm realm) {
        RealmList<RealmPicture> realmPictures = new RealmList<>();
        if (pictures != null) {
            for (Picture picture : pictures) {
                RealmPicture realmPicture = realm.createObject(RealmPicture.class);
                if (picture != null) {
                    realmPicture.setPath(picture.getPath());
                    realmPicture.setOrigWidth(picture.getOrigWidth());
                    realmPicture.setOrigHeight(picture.getOrigHeight());
                    if (picture.getFilter() != null) {
                        realmPicture.setFilter(picture.getFilter().getName());
                    }
                    PictureMatrixState matrixState = picture.getMatrixState();
                    if (matrixState != null) {
                        realmPicture.setX(matrixState.getX());
                        realmPicture.setY(matrixState.getY());
                        realmPicture.setZoom(matrixState.getZoom());
                        realmPicture.setRotation(matrixState.getRotation());
                    }
                    PictureViewState viewState = picture.getViewState();
                    if (viewState != null) {
                        realmPicture.setViewportW(viewState.getViewportW());
                        realmPicture.setViewportH(viewState.getViewportH());
                        realmPicture.setImageW(viewState.getImageW());
                        realmPicture.setImageH(viewState.getImageH());
                    }
                }
                realmPictures.add(realmPicture);
            }
        }
        return realmPictures;
    }

    public static void updateUploadStatus(String id, String status, Context context, OrderClearedListener listener) {
        Realm realm = Realm.getDefaultInstance();
        final boolean[] isClear = {false};
        realm.executeTransactionAsync(realm1 -> {
            RealmAlbum realmAlbum = findAlbumById(realm1, id);
            realmAlbum.setStatusUpload(status);
            isClear[0] = clearInfoAboutOrder(id, context, realmAlbum);
            realm1.copyToRealmOrUpdate(realmAlbum);
        }, () -> {
            listener.onClear(isClear[0]);
        }, error -> {
            error.printStackTrace();
        });
    }

    /*public static RealmAlbum updatePaymentStatus(String id, String statusPaymentDone) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmAlbum realmAlbum = findAlbumById(realm, id);
        realmAlbum.setStatusPayment(statusPaymentDone);
        realm.copyToRealmOrUpdate(realmAlbum);
        realm.commitTransaction();
        realm.close();
        return realmAlbum;
    }*/

    public static void updatePaymentStatus(String id, String statusPaymentDone, OrderClearedListener listener, Context context) {
        Realm realm = Realm.getDefaultInstance();
        final RealmAlbum[] realmAlbum = {null};
        final boolean[] isClear = {false};
        realm.executeTransactionAsync(realm1 -> {
                    realmAlbum[0] = findAlbumById(realm1, id);
                    realmAlbum[0].setStatusPayment(statusPaymentDone);
                    isClear[0] = clearInfoAboutOrder(id, context, realmAlbum[0]);
                    realm1.copyToRealmOrUpdate(realmAlbum[0]);
                }, () -> {
                    Log.d(TAG, "updatePaymentStatus: success");
                    listener.onClear(isClear[0]);
                },
                error -> {
                    Log.d(TAG, "updatePaymentStatus: error");
                    error.printStackTrace();
                });

    }

    public static RealmAlbum updateTransaction(String id, String id_invoice) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmAlbum realmAlbum = findAlbumById(realm, id);
        realmAlbum.setPayTransaction(id_invoice);
        realm.copyToRealmOrUpdate(realmAlbum);
        realm.commitTransaction();
        realm.close();
        return realmAlbum;
    }

    public static boolean clearInfoAboutOrder(String id, Context context, RealmAlbum realmAlbum) {
        boolean flag = false;
        if (realmAlbum.getStatusPayment() != null
                && realmAlbum.getStatusUpload() != null
                && realmAlbum.getStatusPayment().contains(RealmAlbum.STATUS_PAYMENT_SEND_SERVER)
                && realmAlbum.getStatusUpload().contains(UploadEvent.STATUS_SEND_LINK_DONE)) {
            flag = true;
            realmAlbum.setStatusPayment(null);
            realmAlbum.setStatusUpload(UploadEvent.STATUS_UPLOAD_NONE);
            realmAlbum.setFullSizePath(null);
            realmAlbum.setCoverPromo(false);
            realmAlbum.setFullSizePathLocal(null);
            realmAlbum.setPromoCode(null);
        }
        if (flag) {
            Preferences.Payment.removePaid(context, id);
            Preferences.OrderData.setOrderId(context, id, Preferences.OrderData.DEFAULT_ORDER_ID);
        }
        return flag;
    }
}
