package ru.lucky_book.data.evenbus;

/**
 * Created by Загит Талипов on 20.11.2016.
 */

public class UploadEvent {
    public static final String STATUS_UPLOAD_NONE = "none";
    public static final String STATUS_UPLOAD_STACK = "stack";
    public static final String STATUS_UPLOAD_PROCESSING = "processing";
    public static final String STATUS_UPLOAD_DONE = "done";
    public static final String STATUS_SEND_LINK_PROCESSING = "link_processing";
    public static final String STATUS_SEND_LINK_DONE = "link_send";
    public static final int TYPE_UPLOAD = 1;
    public static final int TYPE_PDF_CREATE = 2;
    public static final int TYPE_ORDER_DONE = 3;
    String id;
    String status;
    float progress;

    public UploadEvent(String id, String status, float progress, int type, long fileSize) {
        this.id = id;
        this.status = status;
        this.progress = progress;
        this.type = type;
        this.fileSize = fileSize;
    }

    public long getFileSize() {

        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    long fileSize;

    public UploadEvent(String id, String status, float progress, int type) {
        this.id = id;
        this.status = status;
        this.progress = progress;
        this.type = type;
    }

    int type;

    public String getId() {

        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }
}
