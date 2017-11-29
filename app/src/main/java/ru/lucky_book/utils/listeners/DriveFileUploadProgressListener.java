package ru.lucky_book.utils.listeners;

import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;

import java.io.InputStream;

public interface DriveFileUploadProgressListener extends MediaHttpUploaderProgressListener {
    void onStreamSet(InputStream inputStream);
}
