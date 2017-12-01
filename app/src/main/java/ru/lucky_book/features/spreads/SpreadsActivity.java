package ru.lucky_book.features.spreads;

import android.content.Intent;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.support.percent.PercentRelativeLayout;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.alexvasilkov.gestures.Settings;
import com.alexvasilkov.gestures.State;

import com.example.luckybookpreview.utils.FileUtil;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.listener.RequestProgress;
import com.octo.android.robospice.request.listener.RequestProgressListener;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import org.insta.InstaFilter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.MultiImageSelector;
import ru.lucky_book.R;
import ru.lucky_book.app.MainApplication;
import ru.lucky_book.data.PromoCode;
import ru.lucky_book.database.DBHelper;
import ru.lucky_book.database.RealmAlbum;
import ru.lucky_book.entities.spread.Page;
import ru.lucky_book.entities.spread.PageTemplate;
import ru.lucky_book.entities.spread.Picture;
import ru.lucky_book.entities.spread.PictureMatrixState;
import ru.lucky_book.entities.spread.PictureViewState;
import ru.lucky_book.entities.spread.Spread;
import ru.lucky_book.features.albumcreate.choosecover.CoverChooseActivity;
import ru.lucky_book.features.base.SpiceActivity;
import ru.lucky_book.features.base.listener.OnItemClickListener;
import ru.lucky_book.features.disposition_screen.DispositionActivity;
import ru.lucky_book.features.imageselector.SelectImagesActivity;
import ru.lucky_book.features.imageselector.SelectImagesWithSpreadsActivity;
import ru.lucky_book.features.preview_screen.PreviewActivity;
import ru.lucky_book.pdf.PdfConverter;
import ru.lucky_book.task.CropPreviewsSpiceTask;
import ru.lucky_book.utils.BitmapUtils;
import ru.lucky_book.utils.ConnectionUtils;
import ru.lucky_book.utils.ContextUtils;
import ru.lucky_book.utils.PageUtils;
import ru.lucky_book.utils.PictureUtils;
import ru.lucky_book.utils.SpreadUtils;
import ru.lucky_book.utils.UiUtils;
import ru.lucky_book.utils.dragndrop.SimpleItemTouchHelperCallback;

/**
 * Created by histler
 * on 29.08.16 15:03.
 * вроде переписал, а щас опять все в одном классе...
 */
public class SpreadsActivity extends SpiceActivity implements OnItemClickListener, RequestListener<Boolean>, RequestProgressListener, PictureClickListener, View.OnClickListener {
    public static final int MODE_NONE = -1;
    public static final int MODE_SPREADS = 0;
    public static final int MODE_TEMPLATES = 1;
    public static final int MODE_FILTERS = 2;
    private static final String TAG = SpreadsActivity.class.getSimpleName();
    public static final String TITLE_TAG = TAG + "_title";
    public static final String COVER_TAG = TAG + "_cover";
    public static final String ID_TAG = TAG + "_id";
    private static final int REQUEST_PICK_IMAGES = 3;
    private static final int REQUEST_PICK_ONE_IMAGE = 2;
    public static final String COVER_ID_TAG = "idCover";
    public static final String PROMO_CODE_TAG = "promo";
    public static final String MAX_PAGE = "max_page";
    public static final String MAX_PAGE_COUNT_TAG = "promoObject";
    public static final int CHOOSE_COVER_REQUEST_CODE = 4;
    private PromoCode mPromoCode;
    private String mAlbumTitle;
    private String mAlbumCover;
    private String mImportId;
    private RecyclerView mPagesRecycler;
    private ItemTouchHelper mItemTouchHelper;
    private ViewGroup mLeftPage;
    private ViewGroup mRightPage;
    private View mActionsHolder;
    PercentRelativeLayout mLayoutRoot;
    //private RecyclerView mTemplatesRecycler;
    private View mAddPageButton;
    private View mDeleteButton;
    private View mSwapPagesButton;
    private RealmAlbum mRealmAlbum;
    private List<Spread> mSpreads;
    private SpreadsAdapter mSpreadsAdapter;
    private MaterialDialog mProgressDialog;
    private int mPageWidth;
    private int mPageHeight;
    private Page mClickedPage;
    private int mClickedPicturePosition;
    private ImageView mClickedView;
    private OnSpreadsGeneratedListener mOnSpreadsGeneratedListener = new OnSpreadsGeneratedListener();
    private int mMode = MODE_NONE;
    private int mIdCover;
    private String mPromo;
    private int mMaxCount;
    private int mCurrentPositionSpread = 0;
    private boolean mCoverPromo = false;
    private MaterialDialog materialDialogDelete;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData(savedInstanceState);
        setContentView(R.layout.activity_spreads);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorAccent));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Альбом");
        initView();
    }

    private void showInfoDialog() {
        new MaterialDialog.Builder(this)
                .content(R.string.text_info_text)
                .negativeText(R.string.text_sed_mail)
                .positiveText(R.string.ok)
                .onNegative((dialog, which) -> {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", "lucky.book@yandex.ru", null));
                    startActivity(Intent.createChooser(emailIntent, "Отправить письмо"));
                }).show();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void initData(Bundle savedInstanceState) {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle == null) {
            bundle = savedInstanceState;
        }

        if (bundle == null) {
            return;
        }

        mImportId = bundle.getString(ID_TAG, null);
        if (mImportId != null) {
            mRealmAlbum = DBHelper.findAlbumById(mImportId);
            mAlbumTitle = mRealmAlbum.getTitle();
            mAlbumCover = mRealmAlbum.getCover();
            mMaxCount = mRealmAlbum.getMaxSize();
            mCoverPromo = mRealmAlbum.isCoverPromo();
        } else {
            mAlbumTitle = bundle.getString(TITLE_TAG);
            mAlbumCover = bundle.getString(COVER_TAG);
            mIdCover = bundle.getInt(COVER_ID_TAG);
            mPromo = bundle.getString(PROMO_CODE_TAG);
            if (!TextUtils.isEmpty(mAlbumCover) && !TextUtils.isEmpty(mPromo)) {
                mCoverPromo = true;
            }
        }
        if (mMaxCount == 0)
            mMaxCount = bundle.getInt(MAX_PAGE_COUNT_TAG, SelectImagesActivity.DEFAULT_MAX_IMAGE_COUNT / 2);

    }

    private void initView() {
        mPagesRecycler = (RecyclerView) findViewById(R.id.bottom_recycler_view);
        mPagesRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mActionsHolder = findViewById(R.id.actions_holder);
        mLayoutRoot = (PercentRelativeLayout) findViewById(R.id.rootLayout);
        mDeleteButton = mActionsHolder.findViewById(R.id.delete);
        mSwapPagesButton = mActionsHolder.findViewById(R.id.swap_pages_button);
        mAddPageButton = mActionsHolder.findViewById(R.id.add_page_button);
        mLayoutRoot.setOnTouchListener(mOnTouchListener);
        mAddPageButton.setOnClickListener(this);
        mSwapPagesButton.setOnClickListener(this);
        mDeleteButton.setOnClickListener(this);
        mLeftPage = (ViewGroup) findViewById(R.id.left_page);
        mRightPage = (ViewGroup) findViewById(R.id.right_page);
        mLeftPage.setOnTouchListener(mOnTouchListener);
        mRightPage.setOnTouchListener(mOnTouchListener);

        mLeftPage.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mLeftPage.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mPageHeight = (mLeftPage.getWidth() - mLeftPage.getPaddingRight() - mLeftPage.getPaddingLeft());//yeah, this is right
                mPageWidth = mPageHeight;
                showSpreads();
                initSpreads();
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        MainApplication.currentScreen = MainApplication.Screen.ДобавлениеФото;
        ((MainApplication)getApplication()).info();
    }
    private void showSpreads() {
        if (mMode != MODE_SPREADS) {
            mMode = MODE_SPREADS;
            if (mSpreadsAdapter == null) {
                mSpreadsAdapter = new SpreadsAdapter(mSpreads, mPageWidth * 2, mPageHeight);
                mSpreadsAdapter.setOnItemClickListener(SpreadsActivity.this);
                ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(mSpreadsAdapter);
                mItemTouchHelper = new ItemTouchHelper(callback);
            }
            mPagesRecycler.setAdapter(mSpreadsAdapter);
            mItemTouchHelper.attachToRecyclerView(mPagesRecycler);
        }
    }

    private void initSpreads() {
        if (mRealmAlbum == null) {
            pickStartingImages();
        } else {
            onSpreadsLoaded(DBHelper.getRealmAlbumSpreads(DBHelper.findAlbumById(mImportId)));
        }
    }
    private void detachItemTouchHelper() {
        if (mItemTouchHelper != null) {
            mItemTouchHelper.attachToRecyclerView(null);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.book_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.move_next:
                if (!checkNotFilled()) {
                    if (mSpreads.size() <= mMaxCount && mSpreads.size() >= 5)
                        if (((mRealmAlbum != null && !TextUtils.isEmpty(mRealmAlbum.getPromoCode()) && !TextUtils.isEmpty(mRealmAlbum.getCover())) || (!TextUtils.isEmpty(mPromo) && !TextUtils.isEmpty(mAlbumCover))) && mCoverPromo)
                            saveAlbum(realmAlbum -> {
                                updateImportAlbum(realmAlbum);
                            });
                        else {
                            if (mAlbumCover == null) {
                                openChoiceCover();
                            } else {
                                new MaterialDialog.Builder(this)
                                        .content(R.string.text_question_want_change_cover)
                                        .positiveText(R.string.positive_text)
                                        .negativeText(R.string.negative_text)
                                        .onPositive((dialog, which) -> openChoiceCover())
                                        .onNegative((dialog, which) -> saveAlbum(realmAlbum -> {
                                            updateImportAlbum(realmAlbum);
                                        }))
                                        .build().show();
                            }

                        }
                    else
                        UiUtils.showMessageDialog(this, R.string.warning_title, getString(R.string.message_max_and_min_count, mMaxCount));
                }
                return true;
        }
        return super.onOptionsItemSelected(item);

    }


    private void openChoiceCover() {
        if (ConnectionUtils.connectedToNetwork(this)) {
            Intent intent = new Intent(this, CoverChooseActivity.class);
            startActivityForResult(intent, CHOOSE_COVER_REQUEST_CODE);
        } else {
            UiUtils.showMessageDialog(this, R.string.warning_title, R.string.text_not_connection);
            saveAlbum(realmAlbum -> {
            });
        }
    }


    @Override
    public void onBackPressed() {
        saveAlbum(realmAlbum -> {
            super.onBackPressed();
        });
    }

    private void showExitDialog() {
        UiUtils.showMessageDialog(this, R.string.warning_title, R.string.title_back_message, R.string.yes,
                (dialog, which) -> NavUtils.navigateUpFromSameTask(this));
    }

    private boolean checkNotFilled() {
        Spread notFilled = SpreadUtils.getFirstNotFilled(mSpreads);
        if (notFilled != null) {
            int position = mSpreads.indexOf(notFilled);
            mPagesRecycler.scrollToPosition(position);
            onItemClick(null, position);
            UiUtils.showMessageDialog(this, R.string.album_not_filled, R.string.album_not_filled_message);
            return true;
        } else {
            return false;
        }
    }

    private void disableActions() {

        // mDeleteButton.setEnabled(false);

    }

    @Override
    public void onItemClick(View view, int position) {
        disableActions();
        mCurrentPositionSpread = position;
        Spread spread = mSpreadsAdapter.getItem(position);
        mSpreadsAdapter.setSelection(mPagesRecycler, position, true);
        SpreadUtils.bindPage(spread.getLeft(), mLeftPage, false, this, mOnTouchListener);
        SpreadUtils.bindPage(spread.getRight(), mRightPage, false, this, mOnTouchListener);
    }

    void pickStartingImages() {
        MultiImageSelector.create()
                .multiple()
                .maxCount(mMaxCount)
                .pageWith(mPageWidth)
                .start(this, REQUEST_PICK_IMAGES);
    }

    void pickOneImage() {
        MultiImageSelector.create()
                .single()
                .pictureNames(PictureUtils.spreadToPaths(mSpreads))
                .pageTemplate(mClickedPage.getTemplate())
                .start(this, REQUEST_PICK_ONE_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult code " + requestCode);

        switch (requestCode) {
            case REQUEST_PICK_IMAGES:
                if (resultCode != RESULT_OK) {
                    ContextUtils.openMainScreen(this);
                } else {
                    onImagesSelected((ArrayList<Spread>) data.getSerializableExtra(SelectImagesWithSpreadsActivity.SPREADS_RESULT));
                }
                break;
            case REQUEST_PICK_ONE_IMAGE:
                if (resultCode == RESULT_OK) {
                    List<String> selected = data.getStringArrayListExtra(SelectImagesWithSpreadsActivity.EXTRA_RESULT);
                    if (selected != null && !selected.isEmpty()) {
                        changeImage(selected.get(0));
                    }
                }
                break;
            case DispositionActivity.REQUEST_CODE:
                initSpreads();
                showSpreads();
                break;
            case CHOOSE_COVER_REQUEST_CODE: {
                if (resultCode == RESULT_OK) {
                    mAlbumCover = data.getStringExtra(COVER_TAG);
                    mIdCover = data.getIntExtra(COVER_ID_TAG, 0);
                    saveAlbum(realmAlbum -> {
                        updateImportAlbum(realmAlbum);
                    });
                }
            }

        }
    }

    private void updateCurrentPicture(Picture current) {
        Picasso.with(mClickedView.getContext()).invalidate(new File(current.getPath()));
        onChanged(current, current.getMatrixState().toState(), current.getViewState().toSettings());
        SpreadUtils.bindPicture(mClickedView, mClickedPage, mClickedPicturePosition, false, SpreadsActivity.this);
    }

    @Override
    protected void onDestroy() {
        dismissDialog();
        super.onDestroy();
    }

    protected void dismissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    protected void onImagesSelected(List<Spread> spreads) {
        mProgressDialog = UiUtils.showProgress(this);
        getSpiceManager().execute(new GenerateSpreadsSpiceTask(spreads, mPageWidth, mPageHeight), mOnSpreadsGeneratedListener);
    }

    private void onSpreadsLoaded(List<Spread> data) {
        mSpreads = data;
        updateStateAddButton();
        mSpreadsAdapter.setData(mSpreads);
        onItemClick(null, 0);
        /*if(Preferences.Tutorial.getProperty(this,Preferences.Tutorial.FIRST_CHANGE_SCALE_RUN)){
            UiUtils.showNoNegativeMessageDialog(this, R.string.help, R.string.first_change_scale_run, R.string.got_it, new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    dialog.dismiss();
                    Preferences.Tutorial.setProperty(SpreadsActivity.this,Preferences.Tutorial.FIRST_CHANGE_SCALE_RUN,false);
                }
            });
        }*/
    }

    private void updateStateAddButton() {
        if (mSpreads.size() >= mMaxCount) {
            mAddPageButton.setEnabled(false);
        } else {
            mAddPageButton.setEnabled(true);
        }
    }

    private void saveAlbum(UpdateAlbumListener onSuccess) {
        if (mRealmAlbum == null) {
            DBHelper.saveAlbum(mAlbumTitle, mAlbumCover, mSpreads, mPromo, mIdCover, mMaxCount, mCoverPromo,
                    onSuccess, Throwable::printStackTrace);
        } else {
            mRealmAlbum = DBHelper.updateAlbum(mImportId, mAlbumTitle, mAlbumCover, mSpreads, mIdCover);
            onSuccess.updateAlbum(mRealmAlbum);
        }
    }


    private void moveNext() {
        if (getCallingActivity() != null) {
            setResult(RESULT_OK);
            finish();
        } else {
            Intent intent = new Intent(this, PreviewActivity.class);
            intent.putExtra(PreviewActivity.EXTRA_ALBUM_ID, mRealmAlbum.getId());
            startActivity(intent);
        }
    }

    private void swapPages() {
        DispositionActivity.createActivity(this, mRealmAlbum.getId(), mPageHeight);
    }

    private void updateImportAlbum(RealmAlbum realmAlbum) {
        mRealmAlbum = realmAlbum;
        mImportId = mRealmAlbum.getId();
    }


    public void onChanged(Picture picture, State state, Settings settings) {
        PictureMatrixState matrixState = new PictureMatrixState();
        matrixState.fromState(state);
        picture.setMatrixState(matrixState);
        PictureViewState viewState = new PictureViewState();
        viewState.fromSettings(settings);
        picture.setViewState(viewState);
        updateSelected();
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        dismissDialog();
        Toast.makeText(this, getString(R.string.some_error), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestSuccess(Boolean aBoolean) {
        dismissDialog();
        moveNext();
    }

    @Override
    public void onRequestProgressUpdate(RequestProgress progress) {
        Log.d(TAG, "current progress:" + progress.getProgress());
    }

    @Override
    public void onPictureClick(ImageView view, Page page, int picturePosition) {
        if (mClickedView != null) {
            ((View) mClickedView.getParent()).setSelected(false);
        }
        View viewRoot = (View) view.getParent().getParent().getParent();

        mClickedPicturePosition = picturePosition;
        mClickedView = view;
        mClickedPage = page;
        //mActionsHolder.setVisibility(View.VISIBLE);
        boolean isEmpty = true;
        for (Picture picture : page.getPictures()) {
            if (picture != null)
                isEmpty = false;
        }
        if (viewRoot != null && !viewRoot.isActivated() && isEmpty) {
            if (viewRoot == mLeftPage) {
                ((FrameLayout) mRightPage).setActivated(false);
                ((FrameLayout) mLeftPage).setActivated(true);
            }
            if (viewRoot == mRightPage) {
                ((FrameLayout) mLeftPage).setActivated(false);
                ((FrameLayout) mRightPage).setActivated(true);
            }
            return;
        }

        ((FrameLayout) mRightPage).setActivated(false);
        ((FrameLayout) mLeftPage).setActivated(false);


        if (mClickedPage.getPictures()[mClickedPicturePosition] == null) {
            pickOneImage();
        } else {

            if (view != null) {
                ((View) view.getParent()).setSelected(true);
            }
        }
        if (mMode != MODE_SPREADS) {
            showSpreads();
        }
    }

    private void changeImage(String path) {
        Picture picture = new Picture();
        picture.setPath(path);
        picture.setFilter(null);
        int[] sizes = BitmapUtils.getBitmapSizes(path);
        picture.setOrigWidth(sizes[0]);
        picture.setOrigHeight(sizes[1]);
        mClickedPage.getPictures()[mClickedPicturePosition] = picture;
        SpreadUtils.centerCropPicture(
                picture,
                mClickedPage.getTemplate(),
                mPageWidth,
                mPageHeight);
        Picasso.with(mClickedView.getContext()).invalidate(new File(path));
        SpreadUtils.bindPicture(mClickedView, mClickedPage, mClickedPicturePosition, false, this);
        List<Integer> selects = mSpreadsAdapter.getSelectedPositions();
        for (Integer selected : selects) {
            mSpreadsAdapter.notifyItemChanged(selected);
        }
    }

    private void deleteCurrentPicture() {
        mClickedPage.getPictures()[mClickedPicturePosition] = null;
        SpreadUtils.bindPicture(mClickedView, mClickedPage, mClickedPicturePosition, false, this);
        updateSelected();
    }

    public void changeTemplate(PageTemplate template) {
        Picture[] pictures = mClickedPage.getPictures();
       /* for (int i = 0, size = Math.min(template.getImagesCount(), pictureNames.length); i < size; i++) {
            Picture picture = pictureNames[i];
            if (picture != null && !SizeUtils.pictureSizeValid(picture, template)) {
                showPicturesNotValidForTemplate();
                return;
            }
        }*/

        mClickedPage.setTemplateEmpty(template);
        SpreadUtils.initPage(mClickedPage, mPageWidth, mPageHeight);
        List<Integer> currents = mSpreadsAdapter.getSelectedPositions();

        for (Integer currentPosition : currents) {
            Spread current = mSpreadsAdapter.getItem(currentPosition);
            /*ViewGroup viewGroup = current.getLeft().equals(mClickedPage) ? mLeftPage : mRightPage;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                ImageView imageView = (ImageView) viewGroup.findViewById(R.id.image);
                Picasso.with(imageView.getContext()).invalidate(new File(mClickedPage.getPictures()[i].getPath()));
            }*/
            SpreadUtils.bindPage(mClickedPage, current.getLeft().equals(mClickedPage) ? mLeftPage : mRightPage, false, this, mOnTouchListener);
        }

        updateSelected();
        disableActions();
    }

    /*public void changeTemplate(PageTemplate template){
        mClickedPage.setTemplateEmpty(template);
       // SpreadUtils.bindPage(mClickedPage, current.getLeft().equals(mClickedPage) ? mLeftPage : mRightPage, false, this, mOnTouchListener);
        updateSelected();
        disableActions();
    }*/

    private void showPicturesNotValidForTemplate() {
        UiUtils.showMessageDialog(this, R.string.invalid_picture_size, R.string.invalid_pictures_for_template);
    }

    public void updateSelected() {
        List<Integer> selects = mSpreadsAdapter.getSelectedPositions();
        for (Integer selected : selects) {
            //  mSpreadsAdapter.notifyItemChanged(selected);
        }
    }

    public void deleteImage() {
        deleteCurrentPicture();
        //  mDeleteButton.setEnabled(false);
        List<Integer> selects = mSpreadsAdapter.getSelectedPositions();
        for (Integer selected : selects) {
            mSpreadsAdapter.notifyItemChanged(selected);
        }
    }

    public void deletePage() {
        int size = mSpreads.size();
        mSpreads.remove(mCurrentPositionSpread);
        if (size == 1) {
            mSpreads.add(SpreadUtils.createEmptySpread());
            mSpreadsAdapter.notifyDataSetChanged();
        } else if (size - 1 > mCurrentPositionSpread) {
            mSpreadsAdapter.notifyItemRemoved(mCurrentPositionSpread);
        } else {
            mSpreadsAdapter.notifyItemRemoved(mCurrentPositionSpread);
            mCurrentPositionSpread--;
        }
        onItemClick(null, mCurrentPositionSpread);
        updateStateAddButton();
        mSpreadsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.delete:
                if (mClickedPage != null) {
                    materialDialogDelete = UiUtils.showDeleteImageOrPageDialog(this, R.string.delete_picture, view -> {
                        if (view.getId() == R.id.btn_delete_image) {
                            deleteImage();
                        } else {
                            deletePage();
                        }
                        materialDialogDelete.dismiss();
                    });
                } else {
                    UiUtils.showMessageDialog(this, R.string.delete_page_title, R.string.delete_page_message, R.string.ok,
                            (dialog, which) -> {
                                deletePage();
                                dialog.dismiss();
                            });
                }
                break;
            case R.id.swap_pages_button:
                /*if (!checkNotFilled()) */
            {
                saveAlbum(realmAlbum -> {
                    updateImportAlbum(realmAlbum);
                    swapPages();
                });
            }
            break;
            case R.id.add_page_button: {
                mSpreads.add(SpreadUtils.createEmptySpread());
                mSpreadsAdapter.notifyItemInserted(mSpreads.size() - 1);
                mPagesRecycler.smoothScrollToPosition(mSpreads.size() - 1);
                onItemClick(null, mSpreads.size() - 1);
                updateStateAddButton();
            }
        }
    }


    public void updateDate() {

    }

    private class OnSpreadsGeneratedListener implements RequestListener<List> {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            dismissDialog();
            UiUtils.showErrorDialog(SpreadsActivity.this, spiceException.getMessage());
        }

        @Override
        public void onRequestSuccess(List list) {
            dismissDialog();
            onSpreadsLoaded(list);
        }
    }


    View.OnTouchListener mOnTouchListener = new View.OnTouchListener() {
        int downX, upX;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                downX = (int) event.getRawX();
                Log.i("event.getX()", " downX " + downX);
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                upX = (int) event.getRawX();
                Log.i("event.getX()", " upX " + upX);
                int result = upX - downX;
                Log.d("result", "res = " + result);
                if (result < -100) {
                    if (mCurrentPositionSpread != mSpreads.size() - 1) {
                        onItemClick(null, ++mCurrentPositionSpread);
                        mPagesRecycler.smoothScrollToPosition(mCurrentPositionSpread);
                        showSpreads();
                    }
                } else if (result > 100) {
                    if (mCurrentPositionSpread != 0) {
                        onItemClick(null, --mCurrentPositionSpread);
                        mPagesRecycler.smoothScrollToPosition(mCurrentPositionSpread);
                        showSpreads();
                    }
                }
                return false;

            }
            return false;
        }
    };
}