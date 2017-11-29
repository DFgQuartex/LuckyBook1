package ru.lucky_book.spice;


import com.octo.android.robospice.UncachedSpiceService;

public class LuckyUncachedSpiceService extends UncachedSpiceService {

    private static final int LUCKY_THREAD_COUNT = 2;

    @Override
    public int getThreadCount() {
        return LUCKY_THREAD_COUNT;
    }
}
