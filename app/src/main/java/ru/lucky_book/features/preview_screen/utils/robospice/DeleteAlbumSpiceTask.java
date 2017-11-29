package ru.lucky_book.features.preview_screen.utils.robospice;

import com.octo.android.robospice.request.SpiceRequest;

import ru.lucky_book.database.DBHelper;
import ru.lucky_book.utils.PageUtils;

public class DeleteAlbumSpiceTask extends SpiceRequest<Boolean> {

    private String mAlbumId;

    public DeleteAlbumSpiceTask(String albumId) {
        super(Boolean.class);
        mAlbumId = albumId;
    }

    @Override
    public Boolean loadDataFromNetwork() throws Exception {
        PageUtils.removePreviewAlbumFolder(mAlbumId);
        PageUtils.removeTempAlbumFolder(mAlbumId);
        DBHelper.removeAlbumById(mAlbumId);
        return DBHelper.hasAlbums();
    }
}
