package ru.lucky_book.task;

import com.octo.android.robospice.request.SpiceRequest;

/**
 * Created by histler
 * on 01.09.16 17:23.
 */
public abstract class LocalSpiceRequest<T> extends SpiceRequest<T> {
    public LocalSpiceRequest(Class<T> clazz) {
        super(clazz);
    }

    public abstract T loadData() throws Exception;

    @Override
    public final T loadDataFromNetwork() throws Exception {
        return loadData();
    }
}