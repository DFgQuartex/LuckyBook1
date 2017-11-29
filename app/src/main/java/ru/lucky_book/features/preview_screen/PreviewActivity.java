package ru.lucky_book.features.preview_screen;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.exception.RequestCancelledException;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.CachedSpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.listener.RequestProgress;
import com.octo.android.robospice.request.listener.RequestProgressListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.xiaochen.progressroundbutton.AnimDownloadProgressButton;

import java.io.File;
import java.text.DecimalFormat;

import ru.lucky_book.R;
import ru.lucky_book.app.GreetingActivity;
import ru.lucky_book.app.MainApplication;
import ru.lucky_book.data.Price;
import ru.lucky_book.data.evenbus.UploadEvent;
import ru.lucky_book.database.DBHelper;
import ru.lucky_book.database.RealmAlbum;
import ru.lucky_book.database.RealmAppConfigs;
import ru.lucky_book.features.albumcreate.albumlist.AlbumListActivity;
import ru.lucky_book.features.albumcreate.choosecover.CoverChooseActivity;
import ru.lucky_book.features.order_screen.OrderActivity;
import ru.lucky_book.features.preview_screen.utils.GoogleDriveSpiceTask;
import ru.lucky_book.features.preview_screen.utils.robospice.DeleteAlbumSpiceTask;
import ru.lucky_book.features.spreads.SpreadsActivity;
import ru.lucky_book.spice.LocalSpiceService;
import ru.lucky_book.spice.LuckySpiceManager;
import ru.lucky_book.task.ThumbnailPdfGenerationSpiceTask;
import ru.lucky_book.utils.AlbumUtils;
import ru.lucky_book.utils.NumberUtils;
import ru.lucky_book.utils.UiUtils;

public class PreviewActivity extends AppCompatActivity implements PreviewView {
    private static final int PERMISSION_READ_CONTACTS = 101;
    private static final int REQUEST_ACCOUNT_PICKER = 1000;
    private static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    public static final int REQ_SIGN_IN_REQUIRED = 200;
    protected static final int REQUEST_EDIT = 202;
    public static final String EXTRA_ALBUM_ID = "album_id";
    public static final String EXTRA_FROM_LIST = "from_list";
    public static final String EXTRA_COST = "ru.luckybook.features.preview_screen.PreviewActivity.extra.COST";

    private SpiceManager mSpiceManager = new LuckySpiceManager(LocalSpiceService.class);

    private PdfGenerationListener mPdfGenerationListener = new PdfGenerationListener();
    private DeleteAlbumListener mDeleteAlbumListener = new DeleteAlbumListener();

    private ViewHolder mViewHolder;
    private MaterialDialog mProgressDialog;
    private File mSmallBook;
    private RealmAlbum mRealmAlbum;
    private DecimalFormat mFormat = new DecimalFormat("#.##");
    private PreviewPresenter mPreviewPresenter;
    @Nullable
    private Price mPrice;

    //private List<String> mPaths;

    protected SpiceManager getSpiceManager() {
        return mSpiceManager;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview);
        collectData();
        createViewHolder();
        fillViewHolder();
        mPreviewPresenter = new PreviewPresenter();
        mPreviewPresenter.attachView(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!getSpiceManager().isStarted()) {
            getSpiceManager().start(this);
        }
        MainApplication.currentScreen = MainApplication.Screen.Предпросмотр;
        ((MainApplication)getApplication()).info();
    }

    @Override
    protected void onDestroy() {
        if (getSpiceManager().isStarted()) {
            cancelRequests();
            getSpiceManager().shouldStop();
        }
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (isNotUploading()) {
            getMenuInflater().inflate(R.menu.preview_menu, menu);
        }
        return true;
    }

    public boolean isNotUploading() {
        return getIntent().hasExtra(EXTRA_FROM_LIST) && getIntent().getBooleanExtra(EXTRA_FROM_LIST, false) &&
                (mRealmAlbum.getStatusPayment() == null || mRealmAlbum.getStatusPayment().contains(UploadEvent.STATUS_UPLOAD_NONE)) &&
                (mRealmAlbum.getStatusUpload() == null || mRealmAlbum.getStatusUpload().contains(UploadEvent.STATUS_UPLOAD_NONE));
    }

    @Override
    protected void onResume() {
        super.onResume();
        mRealmAlbum = DBHelper.findAlbumById(mRealmAlbum.getId());
        calculateTotalCost();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.change:
                if (mIsSomethingInBackground) {
                    showCancelBackgroundDialog((dialog, which) -> {
                        dialog.dismiss();
                        cancelRequests();
                        openChangeScreen();
                    });
                } else {
                    if (isNotUploading())
                        openChangeScreen();
                    else {
                        Toast.makeText(this, R.string.text_editing_not_available, Toast.LENGTH_LONG)
                                .show();
                    }
                }

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void openChangeScreen() {
        Intent intent = new Intent(this, SpreadsActivity.class);
        intent.putExtra(SpreadsActivity.ID_TAG, mRealmAlbum.getId());
        startActivityForResult(intent, REQUEST_EDIT);
    }

    protected void cancelRequests() {
        getSpiceManager().cancel(String.class, mRealmAlbum.getId());
    }

    private boolean mIsSomethingInBackground = false;

    private void collectData() {
        Intent intent = getIntent();
        if (intent != null) {
            String realmAlbumId = intent.getStringExtra(EXTRA_ALBUM_ID);
            mRealmAlbum = DBHelper.findAlbumById(realmAlbumId);
        }
    }

    private void createViewHolder() {
        mViewHolder = new ViewHolder();
        mViewHolder.mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mViewHolder.mAlbumPreviewImageView = (ImageView) findViewById(R.id.album_preview);
        mViewHolder.mSumTextView = (TextView) findViewById(R.id.sum);
        mViewHolder.mDeleteImageView = findViewById(R.id.delete);
        mViewHolder.mInfoImageView = findViewById(R.id.info);
        mViewHolder.mMakeOrderButton = (AnimDownloadProgressButton) findViewById(R.id.make_order);
        mViewHolder.mShareButton = (AnimDownloadProgressButton) findViewById(R.id.share);

    }

    private void fillViewHolder() {
        setSupportActionBar(mViewHolder.mToolbar);
        View.OnClickListener onPreviewClickListener = v -> {
            if (mIsSomethingInBackground) {
                showCancelBackgroundDialog((dialog, which) -> {
                    dialog.dismiss();
                    cancelRequests();
                    openBookPreview();
                });
            } else {
                openBookPreview();
            }

        };
        mViewHolder.mToolbar.setOnClickListener(onPreviewClickListener);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(R.string.preview_screen);
        }
        Picasso.with(this)
                .load(AlbumUtils.getAlbumFirstCover(mRealmAlbum.getId()))
                .memoryPolicy(MemoryPolicy.NO_CACHE)
                .into(mViewHolder.mAlbumPreviewImageView);

        mViewHolder.mAlbumPreviewImageView.setOnClickListener(onPreviewClickListener);

        mViewHolder.mMakeOrderButton.setOnClickListener(v -> {
            if (mPrice == null)
                return;
            if (mIsSomethingInBackground) {
                showCancelBackgroundDialog((dialog, which) -> {
                    dialog.dismiss();
                    cancelRequests();
                    showOrderAlertDialog();
                });
            } else {
                showOrderAlertDialog();
            }

        });

        mViewHolder.mInfoImageView.setOnClickListener(v -> showInfoDialog());
        mViewHolder.mShareButton.setOnClickListener(v -> {
            if (!mIsSomethingInBackground) {
                if (mRealmAlbum.getThumbnailPath() == null) {
                    setIsSomethingInBackground(true);
                    mViewHolder.mShareButton.setState(AnimDownloadProgressButton.DOWNLOADING);
                    mViewHolder.mShareButton.setProgressText(getString(R.string.progress_album_creating), 0);
                    getSpiceManager().execute(new CachedSpiceRequest<>(new ThumbnailPdfGenerationSpiceTask(PreviewActivity.this, mRealmAlbum), mRealmAlbum.getId(), DurationInMillis.ALWAYS_EXPIRED), mPdfGenerationListener);
                } else {
                    shareLink();
                }
            }
        });

        mViewHolder.mDeleteImageView.setOnClickListener(v -> {
            if (mIsSomethingInBackground) {
                showCancelBackgroundDialog((dialog, which) -> {
                    dialog.dismiss();
                    cancelRequests();
                    showDeleteAlertDialog();
                });
            } else {
                showDeleteAlertDialog();
            }
        });
    }

    private void showOrderAlertDialog() {
        View view = getLayoutInflater().inflate(R.layout.dialog_info_content, null);
        new ViewHolderInfo(view, this);
        UiUtils.showOrderAlertDialog(this, (dialog, which) -> openOrder(), view);
    }

    public void openBookPreview() {
        Intent intent = new Intent(PreviewActivity.this, MainBookActivity.class);
        intent.putExtra(EXTRA_ALBUM_ID, mRealmAlbum.getId());
        startActivity(intent);
    }

    private void showDeleteAlertDialog() {
        UiUtils.showDeleteDialog(this, R.string.delete_album_title, getString(R.string.delete_album_content, mRealmAlbum.getTitle()), () -> {
            showDialog();
            getSpiceManager().execute(new DeleteAlbumSpiceTask(mRealmAlbum.getId()), mDeleteAlbumListener);
        });
    }

    private void openOrder() {
        Intent intent = new Intent(PreviewActivity.this, OrderActivity.class);
        intent.putExtra(EXTRA_ALBUM_ID, mRealmAlbum.getId());
        intent.putExtra(EXTRA_COST, getTotalCoast());
        startActivity(intent);
    }

    private int getTotalCoast() {
        if (mPrice != null) {
            return (int) mPrice.getAmount();
        }
        return 0;
    }

    private void showCancelBackgroundDialog(MaterialDialog.SingleButtonCallback onOkClick) {
        UiUtils.showMessageDialog(this, R.string.attention, R.string.need_to_cancel_background_task_before, R.string.stop, onOkClick);
    }

    private synchronized void setIsSomethingInBackground(boolean isEnabled) {
        mIsSomethingInBackground = isEnabled;
    }


    private void calculateTotalCost() {
        showDialog();
        mPreviewPresenter.calculateTotalCoast(mRealmAlbum.getSpreads().size(), mRealmAlbum.getPromoCode());
    }

    private void showInfoDialog() {
        if (mPrice == null)
            return;
        int spreadsCount = mRealmAlbum.getSpreads().size();
        TextView textView = new TextView(this);
        textView.setLayoutParams(new AppBarLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setText(getString(R.string.cost_info_dialog_content, (int)mPrice.getSkin(), (int)mPrice.getPage(), spreadsCount, getTotalCoast() == 0 ? 0 : (int)(getTotalCoast() - mPrice.getSkin()), getTotalCoast()));
        UiUtils.showInfoDialog(this, R.string.cost_info_dialog_title, textView);
    }

    private void showCostError() {
        Snackbar.make(mViewHolder.mSumTextView, R.string.text_error_request_price, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.text_repeat, view -> calculateTotalCost()).show();
    }

    private void uploadFileToGDrive() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_READ_CONTACTS);
        } else if (isGooglePlayServicesAvailable()) {
            refreshResults();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_READ_CONTACTS:
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    if (isGooglePlayServicesAvailable()) {
                        refreshResults();
                    }
                } else {
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.root), R.string.read_contacts_permission_desc
                            , Snackbar.LENGTH_INDEFINITE)
                            .setAction(R.string.give_access_read_contacts, v -> uploadFileToGDrive());
                    View view = snackbar.getView();
                    TextView text = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    text.setMaxLines(Integer.MAX_VALUE);
                    text.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                    snackbar.show();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {
                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        DBHelper.saveAppConfig(accountName);
                        refreshResults();
                    }
                }
                break;
            case REQUEST_EDIT:
                fillViewHolder();
                break;
            case REQ_SIGN_IN_REQUIRED:
                if (resultCode == RESULT_OK) {
                    refreshResults();
                } else {
                    Toast.makeText(this, R.string.share_error, Toast.LENGTH_LONG).show();
                }
                break;
        }

    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(apiAvailability, connectionStatusCode);
            return false;
        } else if (connectionStatusCode != ConnectionResult.SUCCESS) {
            return false;
        }
        return true;
    }

    private void showGooglePlayServicesAvailabilityErrorDialog(GoogleApiAvailability apiAvailability, final int connectionStatusCode) {
        Dialog dialog = apiAvailability.getErrorDialog(this, connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private void refreshResults() {
        if (mRealmAlbum.getThumbnailPath() == null) {
            RealmAppConfigs config = DBHelper.getAppConfig();
            if (config != null && !TextUtils.isEmpty(config.getGoogleLogin())) {
                if (mSmallBook != null && mSmallBook.exists()) {
                    setIsSomethingInBackground(true);
                    mViewHolder.mShareButton.setState(AnimDownloadProgressButton.DOWNLOADING);
                    String fullSize = mFormat.format(mSmallBook.length() / (double) NumberUtils.SizeInBytes.MiB);
                    mViewHolder.mShareButton.setProgressText(getString(R.string.uploading_progress, mFormat.format(0), fullSize), 0);
                    getSpiceManager().execute(new CachedSpiceRequest<>(new GoogleDriveSpiceTask(this, config.getGoogleLogin(), mSmallBook), mRealmAlbum.getId(), DurationInMillis.ALWAYS_EXPIRED), new GoogleUploadListener(fullSize));
                } else {
                    Toast.makeText(this, R.string.file_not_found, Toast.LENGTH_SHORT).show();
                }
            } else {
                chooseAccount();
            }
        } else {
            shareLink();
        }
    }

    private void chooseAccount() {
        startActivityForResult(AccountPicker.newChooseAccountIntent(null, null, new String[]{"com.google"}, false, null, null, null, null), REQUEST_ACCOUNT_PICKER);
    }

    private void showDialog() {
        mProgressDialog = UiUtils.showProgress(this);
    }

    private void goToStart(Class<? extends Activity> clazz) {
        Intent intent = new Intent(this, GreetingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(GreetingActivity.EXTRA_GO_TO, clazz.getName());
        startActivity(intent);
        finish();
    }

    private void shareLink() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_message, mRealmAlbum.getThumbnailPath()));
        intent.setType("text/plain");
        startActivity(intent);
    }

    @Override
    public void showPrice(Price price) {
        mPrice = price;
        hideLoading();
        if (getTotalCoast() != 0) {
            mViewHolder.mSumTextView.setTextSize(23);
            mViewHolder.mSumTextView.setText(getString(R.string.album_cost_total, getTotalCoast()));
        } else {
            mViewHolder.mSumTextView.setTextSize(20);
            mViewHolder.mSumTextView.setText(getString(R.string.cost_free));
        }

    }


    public void hideLoading() {
        if (mProgressDialog != null)
            mProgressDialog.dismiss();
    }

    @Override
    public void showError(Throwable throwable) {
        hideLoading();
        throwable.printStackTrace();
        showCostError();
    }

    static

    private class ViewHolder {
        Toolbar mToolbar;
        ImageView mAlbumPreviewImageView;
        TextView mSumTextView;
        View mDeleteImageView;
        View mInfoImageView;
        AnimDownloadProgressButton mMakeOrderButton;
        AnimDownloadProgressButton mShareButton;
    }

    private class PdfGenerationListener implements RequestListener<String>, RequestProgressListener {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            mViewHolder.mShareButton.setState(AnimDownloadProgressButton.NORMAL);
            mViewHolder.mShareButton.setProgress(0);
            setIsSomethingInBackground(false);
            if (!(spiceException instanceof RequestCancelledException)) {
                UiUtils.showErrorDialog(PreviewActivity.this, spiceException.getMessage());
            }
        }

        @Override
        public void onRequestSuccess(String path) {
            setIsSomethingInBackground(false);
            if (path != null) {
                mViewHolder.mShareButton.toProgress(100);
                mViewHolder.mShareButton.setState(AnimDownloadProgressButton.NORMAL);
                mSmallBook = new File(path);
                uploadFileToGDrive();
            }
        }

        @Override
        public void onRequestProgressUpdate(RequestProgress progress) {
            mViewHolder.mShareButton.toProgress(progress.getProgress());
        }
    }

    private class GoogleUploadListener implements RequestListener<String>, RequestProgressListener {

        private String mFullSize;

        GoogleUploadListener(String fullSize) {
            mFullSize = fullSize;
        }

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            mViewHolder.mShareButton.toProgress(0);
            mViewHolder.mShareButton.setState(AnimDownloadProgressButton.NORMAL);
            setIsSomethingInBackground(false);
            if (!(spiceException instanceof RequestCancelledException)) {
                Throwable throwable = spiceException.getCause();
                if (throwable != null) {
                    if (throwable instanceof UserRecoverableAuthException) {
                        startActivityForResult(((UserRecoverableAuthException) throwable).getIntent(), REQ_SIGN_IN_REQUIRED);
                    } else {
                        UiUtils.showErrorDialog(PreviewActivity.this, throwable.getMessage());
                    }
                } else {
                    UiUtils.showErrorDialog(PreviewActivity.this, spiceException.getMessage());
                }
            }
        }

        @Override
        public void onRequestSuccess(String path) {
            setIsSomethingInBackground(false);
            if (!TextUtils.isEmpty(path)) {
                mViewHolder.mShareButton.toProgress(100);
                mViewHolder.mShareButton.setState(AnimDownloadProgressButton.NORMAL);
                mRealmAlbum.setThumbnailPath(path);
                mRealmAlbum = DBHelper.updateAlbumThumbnail(mRealmAlbum.getId(), path);
                shareLink();
            } else if (path == null) {
                mViewHolder.mShareButton.toProgress(0);
                mViewHolder.mShareButton.setState(AnimDownloadProgressButton.NORMAL);
                Toast.makeText(PreviewActivity.this, R.string.share_error, Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onRequestProgressUpdate(RequestProgress progress) {
//            mViewHolder.mShareButton.toProgress(progress.getProgress());
            float percent = (progress.getProgress() / mSmallBook.length()) * 100;
            String progressText = getString(R.string.uploading_progress, mFormat.format(progress.getProgress() / NumberUtils.SizeInBytes.MiB), mFullSize);
            mViewHolder.mShareButton.setProgressText(progressText, percent);
        }
    }

    private class DeleteAlbumListener implements RequestListener<Boolean> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            Toast.makeText(PreviewActivity.this, R.string.delete_album_error, Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onRequestSuccess(Boolean aBoolean) {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
                mProgressDialog = null;
            }
            if (aBoolean != null) {
                goToStart(aBoolean ? AlbumListActivity.class : CoverChooseActivity.class);
            } else {
                Toast.makeText(PreviewActivity.this, R.string.delete_album_error, Toast.LENGTH_SHORT).show();
            }
        }
    }

}

