package ru.lucky_book.app;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.luckybookpreview.utils.FileUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Random;

import ru.lucky_book.BuildConfig;
import ru.lucky_book.R;
import ru.lucky_book.data.PromoCode;
import ru.lucky_book.dataapi.DataManager;
import ru.lucky_book.database.DBHelper;
import ru.lucky_book.features.albumcreate.albumlist.AlbumListActivity;
import ru.lucky_book.features.spreads.SpreadsActivity;
import ru.lucky_book.instruction.InstructionActivity;
import ru.lucky_book.utils.UiUtils;

public class GreetingActivity extends AppCompatActivity implements View.OnClickListener, GreetingView {

    private static final String TAG = "GreetingActivity";

    private static final int PERMISSION_REQUEST_ON_CREATE_WRITE = 3;
    private static final int REQUEST_PERMISSION_SETTING = 4;

    public static final String EXTRA_GO_TO = TAG + ".go_to";

    private static final int[] SPLASHES = {
            R.drawable.splash_1,
            R.drawable.splash_2,
            R.drawable.splash_3,
            R.drawable.splash_4,
            R.drawable.splash_5,
            R.drawable.splash_6,
            R.drawable.splash_7
    };

    private View mCreateAlbum;
    private View mMyAlbums;
    private Button mCheckPromo;
    private GreetingPresenter mPresenter;
    private MaterialDialog mDialog;
    private String mOriginalCoverPromo;
    private MaterialDialog materialDialog;
    private String promo = "";
    private int coverId;
    DataManager dataManager;
    private PromoCode mPromoCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        dataManager = DataManager.getInstance();
        if (!dataManager.isFirstStartDone()) {
            InstructionActivity.createActivity(this);
            finish();
        }
        //applyTranslucency();
        if (getIntent() != null && getIntent().hasExtra(EXTRA_GO_TO)) {
            try {
                goTo((Class<? extends Activity>) Class.forName(getIntent().getExtras().getString(EXTRA_GO_TO)));
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        mPresenter = new GreetingPresenter();
        mPresenter.attachView(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_greeting);
        int splashId = SPLASHES[new Random().nextInt(SPLASHES.length)];
        ((ImageView) findViewById(R.id.splash)).setImageResource(splashId);

        mCreateAlbum = findViewById(R.id.create_album);
        mMyAlbums = findViewById(R.id.my_albums);
        mCheckPromo = (Button) findViewById(R.id.search_by_code);

        mCreateAlbum.setOnClickListener(this);
        mMyAlbums.setOnClickListener(this);

        requestIfNoWritePermission();
    }

    @Override
    protected void onStart() {
        super.onStart();
        MainApplication.currentScreen = MainApplication.Screen.Главная;
        ((MainApplication)getApplication()).info();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        if (DBHelper.hasAlbums()) {
            p.weight = 1;
            mMyAlbums.setVisibility(View.VISIBLE);
            mCheckPromo.setCompoundDrawablePadding(0);

        } else {
            p.weight = 0;
            mMyAlbums.setVisibility(View.GONE);
            mCheckPromo.setCompoundDrawablePadding(10);
        }
        mCheckPromo.setLayoutParams(p);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void applyTranslucency() {
        if (UiUtils.isTranslucencyAvailable(getResources())) {
            UiUtils.setTranslucent(getWindow());
        }
    }

    private void goTo(Class<? extends Activity> activityClass) {
        Intent intent = new Intent(this, activityClass);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_PERMISSION_SETTING:
                requestIfNoWritePermission();
                break;
        }
    }

    void requestIfNoWritePermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ifPermissionDenied();
        } else {
            ifPermissionGranted();
        }

    }

    private void ifPermissionDenied() {
        mMyAlbums.setEnabled(false);
        mCreateAlbum.setEnabled(false);
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Snackbar.make(findViewById(R.id.container), R.string.write_permission_rationale, Snackbar.LENGTH_INDEFINITE).setAction(R.string.allow, v -> requestWritePermission()).show();
        } else {
            requestWritePermission();
        }
    }

    private void requestWritePermission() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                PERMISSION_REQUEST_ON_CREATE_WRITE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_ON_CREATE_WRITE:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    ifPermissionDenied();
                } else {
                    ifPermissionGranted();
                }
        }
    }

    private void ifPermissionGranted() {
        mMyAlbums.setEnabled(true);
        mCreateAlbum.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.create_album:
                choosePhotos();
                break;
            case R.id.my_albums:
                goTo(AlbumListActivity.class);
                break;
            case R.id.search_by_code:
                checkPromoCode();
                break;
            case R.id.button_tutorial: {
                InstructionActivity.createActivity(this);
            }
        }
    }

    private void checkPromoCode() {
        UiUtils.showEditTextDialogCheckPromo(this, getString(R.string.dialog_title_check_promo_code), null, getString(R.string.dialog_hint_check_promo_code), null
                , getString(R.string.dialog_check_promo_code_positive_button), getString(R.string.dialog__check_promo_code_negative_button),
                (dialog, input) -> {
                    promo = input.toString();
                    mPresenter.checkPromoCode(input.toString());
                });
    }

    protected void choosePhotos() {
        Intent intent = new Intent(this, SpreadsActivity.class);
        intent.putExtra(SpreadsActivity.TITLE_TAG, "");
        intent.putExtra(SpreadsActivity.COVER_TAG, mOriginalCoverPromo);
        intent.putExtra(SpreadsActivity.COVER_ID_TAG, coverId);
        intent.putExtra(SpreadsActivity.PROMO_CODE_TAG, promo);
        if (mPromoCode != null)
            intent.putExtra(SpreadsActivity.MAX_PAGE_COUNT_TAG, mPromoCode.getMaxPage());
        startActivity(intent);
    }

    protected void showTitleDialog() {
        dismissDialog();
        mDialog = UiUtils.showEditTextDialog(this, null, getString(R.string.set_title_dialog_title), getString(R.string.enter_album_title), null, getString(android.R.string.ok),
                (dialog, input) -> choosePhotos());
    }

    public void loadAndSaveCover(int idImage, String path) {
        materialDialog = UiUtils.showProgress(this, R.string.titleLoadImage);
        ImageView imageView = new ImageView(this);
        Picasso.with(this)
                .load(BuildConfig.ENDPOINT + path)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        Bitmap innerBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                        mOriginalCoverPromo = FileUtil.saveJpeg100(innerBitmap, "cover" + idImage);
                        if (materialDialog != null)
                            materialDialog.dismiss();
                        choosePhotos();
                    }

                    @Override
                    public void onError() {
                        UiUtils.showErrorDialog(GreetingActivity.this, R.string.error_loading_file);
                        if (materialDialog != null)
                            materialDialog.dismiss();
                    }
                });
    }


    protected void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    public void showCheckPromoCodeResult(PromoCode promoCode) {
        mPromoCode = promoCode;
        if (promoCode.getStatus() == null) {
            dismissDialog();
            mDialog = UiUtils.showInfoDialog(this, null, getString(R.string.text_info), getString(R.string.album_name_found, promoCode.getName()), null, getString(android.R.string.ok),
                    (dialog, input) -> {
                        if (promoCode.getSkin() != null)
                            loadAndSaveCover(promoCode.getSkinId(), promoCode.getSkin());
                        else {
                           choosePhotos();
                        }
                        coverId = promoCode.getSkinId();
                    });

        } else {
            notFoundCodeToast();
        }
    }

    public void notFoundCodeToast() {
        Toast.makeText(this, R.string.text_promo_code_not_found, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(Throwable throwable) {
        promo = "";
        throwable.printStackTrace();
    }

    public static void createActivity(Activity activity) {
        Intent intent = new Intent(activity, GreetingActivity.class);
        activity.startActivity(intent);
    }

}
