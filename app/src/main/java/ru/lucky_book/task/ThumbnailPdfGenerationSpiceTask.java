package ru.lucky_book.task;

import android.content.Context;

import ru.lucky_book.database.RealmAlbum;

/**
 * Created by histler
 * on 02.09.16 13:18.
 */
public class ThumbnailPdfGenerationSpiceTask extends PdfGenerationSpiceTask {
    public ThumbnailPdfGenerationSpiceTask(Context context, RealmAlbum realmAlbum) {
        super(context, realmAlbum);
    }

    @Override
    protected boolean isThumbNails() {
        return true;
    }
}
