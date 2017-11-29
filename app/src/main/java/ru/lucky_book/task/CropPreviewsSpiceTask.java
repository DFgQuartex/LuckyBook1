package ru.lucky_book.task;

import android.content.Context;

import java.lang.ref.WeakReference;
import java.util.List;

import ru.lucky_book.entities.spread.Spread;
import ru.lucky_book.utils.PageUtils;

/**
 * Created by histler
 * on 01.09.16 17:21.
 */
public class CropPreviewsSpiceTask extends LocalSpiceRequest<Boolean> implements PageUtils.CropStateListener {
    private WeakReference<List<Spread>> mSpreads;
    private String mAlbumCover;
    private String mAlbumId;
    private Context mContext;
    private int mPageSize;
    public CropPreviewsSpiceTask(Context context, String albumId, List<Spread> spreads, String albumCover,int pageSize) {
        super(Boolean.class);
        mContext=context.getApplicationContext();
        mAlbumId=albumId;
        mSpreads=new WeakReference<>(spreads);
        mAlbumCover=albumCover;
        mPageSize=pageSize;
    }

    @Override
    public Boolean loadData() throws Exception {
        System.gc();
        return PageUtils.cropSpreadPreviews(mContext,mAlbumId,mSpreads.get(),mAlbumCover,mPageSize,this);
    }

    @Override
    public void onCroppingStarted() {
        publishProgress(0);
    }

    @Override
    public void onPictureCropping(int position, int totalCount) {
        int progress=position*100/totalCount;
        publishProgress(progress);
    }

    @Override
    public void onPictureCropped(int position, int totalCount) {
        int progress=position*100/totalCount;
        publishProgress(progress);
    }

    @Override
    public void onCroppingFinished() {
        publishProgress(100);
    }
}
