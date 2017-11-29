package ru.lucky_book.features.upload;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.exception.RequestCancelledException;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.listener.RequestProgress;
import com.octo.android.robospice.request.listener.RequestProgressListener;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.DecimalFormat;

import ru.lucky_book.R;
import ru.lucky_book.app.Preferences;
import ru.lucky_book.data.OrderLink;
import ru.lucky_book.data.SuccessOrderResponse;
import ru.lucky_book.data.evenbus.UploadEvent;
import ru.lucky_book.database.DBHelper;
import ru.lucky_book.database.RealmAlbum;
import ru.lucky_book.network.robospice.dropbox.DropboxSpiceTask;
import ru.lucky_book.spice.LuckySpiceManager;
import ru.lucky_book.spice.LuckyUncachedSpiceService;
import ru.lucky_book.task.FullSizePdfGenerationSpiceTask;
import ru.lucky_book.utils.ConnectionUtils;
import ru.lucky_book.utils.NotificationUtils;
import ru.lucky_book.utils.NumberUtils;
import ru.lucky_book.utils.UiUtils;

/**
 * Created by Загит Талипов on 20.11.2016.
 */

public class UploadService extends Service implements UploadView {


    private static final String TAG = "upload_service";
    private NotificationManager mManager;
    private NotificationCompat.Builder mBuilder;
    private SpiceManager mSpiceManager = new LuckySpiceManager(LuckyUncachedSpiceService.class);
    private DecimalFormat mFormat = new DecimalFormat("#.##");
    private File mOriginalBook;
    private RealmAlbum mRealmAlbum;
    private float mProgress;


    UploadPresenter mPresenter;
    private DropboxUploadListener mDropboxUploadListener;
    private PdfGenerationListener mPdfGenerationListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: service");
        Log.d(TAG, "onCreate: ");
    }

    public void updateStatus(String status) {
        DBHelper.updateUploadStatus(mRealmAlbum.getId(), status, getApplicationContext(), isClear -> {
            mRealmAlbum = DBHelper.findAlbumById(mRealmAlbum.getId());
            if (TextUtils.equals(status, UploadEvent.STATUS_SEND_LINK_DONE)) {
                checkStack();
                EventBus.getDefault().postSticky(new UploadEvent(
                        mRealmAlbum.getId(),
                        UploadEvent.STATUS_UPLOAD_DONE,
                        isClear ? 1 : 0,
                        UploadEvent.TYPE_ORDER_DONE,
                        mOriginalBook.length()
                ));
            }
        });
    }

    public void checkStack() {
        for (RealmAlbum realmAlbum : DBHelper.getAllAlbums()) {
            if (realmAlbum.getStatusUpload() != null && (TextUtils.equals(realmAlbum.getStatusUpload(), UploadEvent.STATUS_UPLOAD_DONE) ||
                    TextUtils.equals(realmAlbum.getStatusUpload(), UploadEvent.STATUS_SEND_LINK_PROCESSING))) {
                mRealmAlbum = realmAlbum;
                sendLink();
                return;
            }
        }

        for (RealmAlbum realmAlbum : DBHelper.getAllAlbums()) {
            if (realmAlbum.getStatusUpload() != null && TextUtils.equals(realmAlbum.getStatusUpload(), UploadEvent.STATUS_UPLOAD_STACK)) {
                mRealmAlbum = realmAlbum;
                startAlbumUploading();
                return;
            }
        }
        stopForeground(true);
        stopSelf();
    }


    private void sendLink() {
        updateStatus(UploadEvent.STATUS_SEND_LINK_PROCESSING);
        mPresenter.sendPdfLink(new OrderLink(Preferences.OrderData.getOrderId(getApplicationContext(), mRealmAlbum.getId()), mRealmAlbum.getFullSizePath()));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (!mSpiceManager.isStarted()) {
            mSpiceManager.start(this);
        }
        mPresenter = new UploadPresenter();
        mPresenter.attachView(this);
        mManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        checkStack();
        return START_NOT_STICKY;
    }

    public void startUpload(String path) {
        Log.d("PDF_UPLOAD", path);
        String userFullName = String.format("%s", Preferences.OrderData.getOrderId(getApplicationContext(), mRealmAlbum.getId()));
        mOriginalBook = new File(path);
        mBuilder = NotificationUtils.getNotificationBuilder(R.string.app_name);
        mBuilder.setProgress(100, 0, false);
        mBuilder.setContentText(getString(R.string.uploading_progress, mFormat.format(0), mFormat.format(mOriginalBook.length() / (double) NumberUtils.SizeInBytes.MiB)));
        EventBus.getDefault().postSticky(new UploadEvent(
                mRealmAlbum.getId(),
                UploadEvent.STATUS_UPLOAD_PROCESSING,
                0,
                UploadEvent.TYPE_UPLOAD,
                mOriginalBook.length()
        ));
        mSpiceManager.execute(new DropboxSpiceTask(getApplicationContext(), mOriginalBook, userFullName),
                mDropboxUploadListener);
    }

    private void startAlbumUploading() {
        mPdfGenerationListener = new PdfGenerationListener();
        mDropboxUploadListener = new DropboxUploadListener();
        if (ConnectionUtils.connectedToNetwork(this) && TextUtils.isEmpty(mRealmAlbum.getFullSizePath())) {
            if (mRealmAlbum.getFullSizePathLocal() != null && !mRealmAlbum.getFullSizePathLocal().isEmpty()) {
                startUpload(mRealmAlbum.getFullSizePathLocal());
            } else if (mRealmAlbum.getFullSizePath() == null) {
                mBuilder = NotificationUtils.getNotificationBuilder(R.string.app_name);
                mBuilder.setProgress(100, 0, false);
                mBuilder.setContentText(getString(R.string.progress_album_creating, mFormat.format(0)));
                startForeground(NotificationUtils.PROGRESS_ID, mBuilder.build());
                EventBus.getDefault().postSticky(new UploadEvent(
                        mRealmAlbum.getId(),
                        UploadEvent.STATUS_UPLOAD_PROCESSING,
                        0,
                        UploadEvent.TYPE_PDF_CREATE
                ));

                getSpiceManager().execute(new FullSizePdfGenerationSpiceTask(getApplicationContext(), mRealmAlbum),
                        mPdfGenerationListener);
            }
        } else if (TextUtils.isEmpty(mRealmAlbum.getFullSizePath())) {
            UiUtils.showOrderAlertDialog(this, R.string.order_alert_error_network_content, (dialog, which) -> startAlbumUploading(), (dialog, which) -> {
                stopForeground(true);
                dialog.dismiss();
            });
        }
    }


    @Override
    public void resultSendOrderLink(SuccessOrderResponse successOrder) {
        updateStatus(UploadEvent.STATUS_SEND_LINK_DONE);
    }


    @Override
    public void showError(Throwable throwable) {

    }

    public SpiceManager getSpiceManager() {
        return mSpiceManager;
    }

    private class PdfGenerationListener implements RequestListener<String>, RequestProgressListener {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            stopForeground(true);
            Log.d(TAG, "onRequestFailure: " + mRealmAlbum.getId());
            //checkStack();
        }


        @Override
        public void onRequestSuccess(String path) {
            if (path != null) {
                Log.d(TAG, "onRequestSuccess: createPdf: " + path);
                mRealmAlbum.setFullSizePathLocal(path);
                DBHelper.updateAlbumFullSizeLocal(mRealmAlbum.getId(), path,
                        realmAlbum -> {
                            mRealmAlbum = DBHelper.findAlbumById(mRealmAlbum.getId());
                            EventBus.getDefault().postSticky(new UploadEvent(
                                    mRealmAlbum.getId(),
                                    UploadEvent.STATUS_UPLOAD_PROCESSING,
                                    100,
                                    UploadEvent.TYPE_PDF_CREATE
                            ));
                            startUpload(path);
                        });

            } else {
                stopForeground(true);
            }
        }

        @Override
        public void onRequestProgressUpdate(RequestProgress progress) {
            if (mProgress <= progress.getProgress()) {
                mProgress = progress.getProgress();
            } else {
                if (mProgress <= 99f) {
                    mProgress++;
                }
                progress.setProgress(mProgress);
            }
            Log.d(TAG, "onRequestProgressUpdate: " + progress.getProgress());
            EventBus.getDefault().postSticky(new UploadEvent(
                    mRealmAlbum.getId(),
                    UploadEvent.STATUS_UPLOAD_PROCESSING,
                    progress.getProgress(),
                    UploadEvent.TYPE_PDF_CREATE
            ));

            mBuilder.setProgress(100, (int) progress.getProgress(), false);
            mBuilder.setContentText(getString(R.string.progress_album_creating, mFormat.format(progress.getProgress())));
            startForeground(NotificationUtils.PROGRESS_ID, mBuilder.build());
        }
    }

    private class DropboxUploadListener implements RequestListener<String>, RequestProgressListener {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            stopForeground(true);
            Log.d(TAG, "onRequestFailure: upload " + mRealmAlbum.getId());
            if (!(spiceException instanceof RequestCancelledException)) {
                Toast.makeText(getApplicationContext(), spiceException.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onRequestSuccess(String link) {
            mRealmAlbum = DBHelper.updateAlbumFullSize(mRealmAlbum.getId(), link);
            if (Preferences.OrderData.getOrderId(getApplicationContext(), mRealmAlbum.getId()) != Preferences.OrderData.DEFAULT_ORDER_ID)
                sendLink();
            stopForeground(true);
            Log.d(TAG, "onRequestSuccess: upload link " + link);
            if (!TextUtils.isEmpty(link)) {
                updateStatus(UploadEvent.STATUS_UPLOAD_DONE);
                EventBus.getDefault().postSticky(new UploadEvent(
                        mRealmAlbum.getId(),
                        UploadEvent.STATUS_UPLOAD_DONE,
                        100,
                        UploadEvent.TYPE_UPLOAD
                ));
            } else {
                UiUtils.showOrderAlertDialog(getApplicationContext(), R.string.some_error, (dialog, which) -> startAlbumUploading(), (dialog, which) -> dialog.dismiss());
            }
        }


        @Override
        public void onRequestProgressUpdate(RequestProgress progress) {
            Log.d(TAG, "onRequestProgressUpdate: upload progress" + progress.getProgress());
            EventBus.getDefault().postSticky(new UploadEvent(
                    mRealmAlbum.getId(),
                    UploadEvent.STATUS_UPLOAD_PROCESSING,
                    progress.getProgress(),
                    UploadEvent.TYPE_UPLOAD,
                    mOriginalBook.length()
            ));
            String message = getString(R.string.uploading_progress,
                    mFormat.format(progress.getProgress() / NumberUtils.SizeInBytes.MiB),
                    mFormat.format(mOriginalBook.length() / (double) NumberUtils.SizeInBytes.MiB));
            mBuilder.setProgress(100, (int) (100 * (progress.getProgress() / mOriginalBook.length())), false);
            mBuilder.setContentText(
                    getString(R.string.uploading_progress,
                            mFormat.format(progress.getProgress() / (double) NumberUtils.SizeInBytes.MiB),
                            mFormat.format(mOriginalBook.length() / (double) NumberUtils.SizeInBytes.MiB)));
            startForeground(NotificationUtils.PROGRESS_ID, mBuilder.build());
        }
    }

}
