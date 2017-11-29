package ru.lucky_book.features.albumcreate.albumlist;

import java.util.List;

import ru.lucky_book.database.DBHelper;
import ru.lucky_book.task.LocalSpiceRequest;

/**
 * Created by Badr
 * on 03.09.2016 19:38.
 */
public class AlbumsListSpiceTask extends LocalSpiceRequest<List> {
    public AlbumsListSpiceTask() {
        super(List.class);
    }

    @Override
    public List loadData() throws Exception {
        return DBHelper.getAllAlbums();
    }
}
