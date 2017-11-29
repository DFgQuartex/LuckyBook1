package ru.lucky_book.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import ru.lucky_book.database.DBHelper;
import ru.lucky_book.utils.PageUtils;

/**
 * @deprecated Use {@link ru.lucky_book.features.preview_screen.utils.robospice.DeleteAlbumSpiceTask} instead
 */
@Deprecated
public class DeleteAlbumLoader extends AsyncTaskLoader<Boolean> {

    private String mAlbumId;

    public DeleteAlbumLoader(Context context, String albumId) {
        super(context);
        mAlbumId = albumId;
    }

    @Override
    public Boolean loadInBackground() {
        PageUtils.removePreviewAlbumFolder(mAlbumId);
        PageUtils.removeTempAlbumFolder(mAlbumId);
        DBHelper.removeAlbumById(mAlbumId);
        return DBHelper.hasAlbums();
    }
}
