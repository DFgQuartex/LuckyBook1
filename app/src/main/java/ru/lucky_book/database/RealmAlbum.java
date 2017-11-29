package ru.lucky_book.database;

import java.util.Date;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import ru.lucky_book.data.evenbus.UploadEvent;

/**
 * Created by demafayz on 29.08.16.
 */
public class RealmAlbum extends RealmObject {
    public static final String STATUS_PAYMENT_DONE = "done_pay";
    public static final String STATUS_PAYMENT_SEND_SERVER = "send_done";
    @PrimaryKey
    private String id;
    private String title;
    private String cover;
    private Date updateTime;
    private String thumbnailPath;
    private String fullSizePath;
    private String fullSizePathLocal;
    private RealmList<RealmSpread> spreads;
    private String statusUpload = UploadEvent.STATUS_UPLOAD_NONE;
    private String statusPayment;

    public boolean isCoverPromo() {
        return coverPromo;
    }

    public void setCoverPromo(boolean coverPromo) {
        this.coverPromo = coverPromo;
    }

    private boolean coverPromo;

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
    }

    private int maxSize;

    public String getPayTransaction() {
        return payTransaction;
    }

    public void setPayTransaction(String payTransaction) {
        this.payTransaction = payTransaction;
    }

    public String getStatusPayment() {
        return statusPayment;
    }

    public void setStatusPayment(String statusPayment) {
        this.statusPayment = statusPayment;
    }

    private String payTransaction;
    private String promoCode;
    private int coverId;


    public String getStatusUpload() {
        return statusUpload;
    }

    public void setStatusUpload(String statusUpload) {
        this.statusUpload = statusUpload;
    }


    public String getFullSizePathLocal() {
        return fullSizePathLocal;
    }

    public void setFullSizePathLocal(String fullSizePathLocal) {
        this.fullSizePathLocal = fullSizePathLocal;
    }


    public String getPromoCode() {
        return promoCode;
    }

    public void setPromoCode(String promoCode) {
        this.promoCode = promoCode;
    }

    public int getCoverId() {
        return coverId;
    }

    public void setCoverId(int coverId) {
        this.coverId = coverId;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public RealmList<RealmSpread> getSpreads() {
        return spreads;
    }

    public void setSpreads(RealmList<RealmSpread> spreads) {
        this.spreads = spreads;
    }

    public String getThumbnailPath() {
        return thumbnailPath;
    }

    public void setThumbnailPath(String thumbnailPath) {
        this.thumbnailPath = thumbnailPath;
    }

    public String getFullSizePath() {
        return fullSizePath;
    }

    public void setFullSizePath(String fullSizePath) {
        this.fullSizePath = fullSizePath;
    }
}
