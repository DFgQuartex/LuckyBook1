package ru.lucky_book.task;

import android.content.Context;
import android.util.Log;

import java.util.List;

import ru.lucky_book.database.DBHelper;
import ru.lucky_book.database.RealmAlbum;
import ru.lucky_book.entities.spread.Spread;
import ru.lucky_book.pdf.PdfConverter;
import ru.lucky_book.utils.PageUtils;

/**
 * Created by histler
 * on 02.09.16 13:11.
 */
public abstract class PdfGenerationSpiceTask extends LocalSpiceRequest<String> implements PageUtils.CropStateListener, PdfConverter.PdfGenerationListener {
    public static final int PICTURES_CREATING_MAX = 80;
    public static final int PDF_CREATING_MAX = 100;
    private static final String TAG = "pdf generate";
    private RealmAlbum mRealmAlbum;
    private Context mContext;
    private int mProgress;
    private int mPdfStartProgress = PICTURES_CREATING_MAX;

    public PdfGenerationSpiceTask(Context context, RealmAlbum realmAlbum) {
        super(String.class);
        mContext = context.getApplicationContext();
        mRealmAlbum = realmAlbum;
    }

    protected abstract boolean isThumbNails();

    @Override
    public String loadData() throws Exception {
        System.gc();
        List<Spread> spreads = null;
        if (true) {
            mPdfStartProgress = PICTURES_CREATING_MAX;
            spreads = DBHelper.getRealmAlbumSpreads(mRealmAlbum);
            PageUtils.cropSpreadsAndSave(mContext, mRealmAlbum.getId(), spreads, mRealmAlbum.getCover(), this);
        } else {
            mPdfStartProgress = 0;
        }

        if (isCancelled()) {
            PageUtils.removeTempAlbumFolder(mRealmAlbum.getId());
            return null;
        }

        if (PdfConverter.isPdfExists(mRealmAlbum.getId(), isThumbNails()) && PdfConverter.isCorrectPdf(mRealmAlbum, isThumbNails())) {
            return PdfConverter.getPdfFile(mRealmAlbum.getId(), isThumbNails()).getAbsolutePath();
        }
        if (spreads == null) {
            spreads = DBHelper.getRealmAlbumSpreads(mRealmAlbum);
        }
        return PdfConverter.generatePdf(mRealmAlbum.getId(), spreads, isThumbNails(), this);
    }

    @Override
    public void onCroppingStarted() {
        publishProgress(0);
    }

    @Override
    public void onPictureCropping(int position, int totalCount) {
        int progress = position * PICTURES_CREATING_MAX / totalCount;
        Log.d(TAG, "onPictureCropping: " + progress);
        publishProgress(progress);
    }

    @Override
    public void onPictureCropped(int position, int totalCount) {
        int progress = position * PICTURES_CREATING_MAX / totalCount;
        Log.d(TAG, "onPictureCropped: " + position);
        publishProgress(progress);
    }

    @Override
    public void onCroppingFinished() {
        publishProgress(PICTURES_CREATING_MAX);
    }

    @Override
    public void onPdfGenerationStarted() {
        publishProgress(mPdfStartProgress);
    }

    @Override
    public void onPdfPageGenerating(int position, int totalCount) {
        int progress = mPdfStartProgress + position * (PDF_CREATING_MAX - mPdfStartProgress) / totalCount;
       /* if (mProgress <= progress)
            mProgress = progress;
        else
            mProgress++;
        Log.d(TAG, "onPdfPageGenerating: "+ progress);*/
        publishProgress(progress);
    }

    @Override
    public void onPdfGenerationFinished() {
        publishProgress(PDF_CREATING_MAX);
    }
}
