package ru.lucky_book.features.spreads;

import java.util.List;

import ru.lucky_book.entities.spread.Spread;
import ru.lucky_book.task.LocalSpiceRequest;
import ru.lucky_book.utils.SpreadUtils;

/**
 * Created by Badr
 * on 04.09.2016 3:05.
 */
public class GenerateSpreadsSpiceTask extends LocalSpiceRequest<List> {
    private List<Spread> mSpreads;
    private int mPageWidth;
    private int mPageHeight;

    public GenerateSpreadsSpiceTask(List<Spread> spreads, int pageWidth, int pageHeight) {
        super(List.class);
        mSpreads =spreads;
        mPageWidth=pageWidth;
        mPageHeight=pageHeight;
    }

    @Override
    public List loadData() throws Exception {
        SpreadUtils.initSpreads(mSpreads,mPageWidth,mPageHeight);
        return mSpreads;
    }
}
