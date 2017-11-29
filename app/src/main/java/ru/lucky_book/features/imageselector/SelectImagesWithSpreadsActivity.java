package ru.lucky_book.features.imageselector;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Display;
import android.widget.ImageView;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.bean.Image;
import ru.lucky_book.R;
import ru.lucky_book.entities.spread.Page;
import ru.lucky_book.entities.spread.PageTemplate;
import ru.lucky_book.entities.spread.Picture;
import ru.lucky_book.entities.spread.Spread;
import ru.lucky_book.features.base.BaseSelectableRecyclerFragment;
import ru.lucky_book.utils.UiUtils;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by histler
 * on 19.09.16 11:20.
 */
public class SelectImagesWithSpreadsActivity extends SelectImagesActivity implements RequestListener<Picture> {
    public static final String EXTRA_PAGE_WIDTH = "page_width";
    public static final String MAX_SPREADS_COUNT = "max_spreads_count";
    public static final String MIN_SPREADS_COUNT = "min_spreads_count";
    public static final int MIN_SPREADS_COUNT_DEFAULT = 5;//было 5
    public static final String SPREADS_RESULT = "spreads_result";
    public static final int DEFAULT_MAX_SPREADS_COUNT = 15;// было 10, пока тест
    public static final int DEFAULT_MAX_IMAGE_COUNT = 20;
    public static final int DEFAULT_MIN_SPREADS_COUNT = 5;
    //  private RecyclerView mSpreadsRecycler;
    private ArrayList<Spread> mSpreads = new ArrayList<>();
    // private SpreadsAdapter mSpreadsAdapter;
    private Spread mCurrentSpread;
    private Page mCurrentPage;
    private int mPageWidth;
    private int mMaxSpreads;
    private int mMinSpreads;
    private List<Image> mSelectedImage = new ArrayList<>();
    private ImageView mViewMarker;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewMarker = (ImageView) findViewById(R.id.marker);
        mViewMarker.setColorFilter(ContextCompat.getColor(this, R.color.yellow));
    }


    @Override
    protected void initDataFromIntent(Intent intent) {
        super.initDataFromIntent(intent);
        mMaxSpreads = getIntent().getIntExtra(MAX_SPREADS_COUNT, DEFAULT_MAX_SPREADS_COUNT);
        mMinSpreads = getIntent().getIntExtra(MIN_SPREADS_COUNT, DEFAULT_MIN_SPREADS_COUNT);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        mPageWidth = getIntent().getIntExtra(EXTRA_PAGE_WIDTH, (int) (width * 0.45f));
    }

    @Override
    public boolean canSelectMore(Image image) {
        if (mSelectedImage.size() == mMaxSpreads * 2) {
            return false;
        }
        return true;
    }

   /* private void initSpreadsRecycler() {
        mSpreadsRecycler = (RecyclerView) findViewById(R.id.bottom_recycler_view);
        mSpreadsRecycler.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mSpreadsAdapter = new SpreadsAdapter(mSpreads, mPageWidth * 2, mPageWidth);
        mSpreadsRecycler.setAdapter(mSpreadsAdapter);
    }*/

    @Override
    protected int getLayoutId() {
        return R.layout.activity_select_images_with_spreads;
    }

    @Override
    public void onImageSelected(Image image) {
        super.onImageSelected(image);
        if (mSelectedImage != null) {
            mSelectedImage.add(image);
        }
        updateSubmitButton();
    }

    protected void dismissDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    @Override
    public void checkLastSelected() {
        if (mSelectedImage.size() == mMaxSpreads * 2)
            createSpread();

    }

    private void createSpread() {
        mLoadingDialog = UiUtils.showLoadingDialog(this, R.string.text_please_wait, R.string.text_progress_download_images);
        Observable.create(new Observable.OnSubscribe<List<Spread>>() {
            @Override
            public void call(Subscriber<? super List<Spread>> subscriber) {
                NewSpreadCreateSpiceTask listSpreadCreateSpiceTask = new NewSpreadCreateSpiceTask(mSelectedImage, mPageWidth, mPageWidth, mMaxSpreads);
                List<Spread> spreads = listSpreadCreateSpiceTask.loadDataFromNetwork();
                subscriber.onNext(spreads);
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(spreads -> {
                    if (spreads.size() < MIN_SPREADS_COUNT_DEFAULT) {
                        UiUtils.showMessageDialog(SelectImagesWithSpreadsActivity.this,
                                getString(R.string.text_min_spread_count_error, mSpreads.size(), spreads.size() == 1 ? "" : "a", MIN_SPREADS_COUNT_DEFAULT));//mSpreads.size()
                        if (mLoadingDialog != null)
                            mLoadingDialog.dismiss();
                        return;
                    }
                    mSpreads = new ArrayList<>();
                    mSpreads.addAll(spreads);
                    if (mSelectedImage != null && !mSelectedImage.isEmpty()) {
                        List<Image> networkImages = getNetworkImages(mSelectedImage);
                        if (!networkImages.isEmpty()) {
                            loadImages(networkImages);
                        } else {
                            sendResult();
                        }
                    } else {
                        setResult(RESULT_CANCELED);
                        finish();
                    }
                });
    }

    private int getValidSpreadsCount() {
        int size = mSpreads.size();
        if (!mSpreads.isEmpty()) {
            if (!isLastSpreadFilled()) {
                size--;
            }
        }
        return size;
    }

    private boolean isLastSpreadFilled() {
        if (mSpreads.isEmpty()) {
            return false;
        }
        Spread last = mSpreads.get(mSpreads.size() - 1);
        if (last.getRight() != null) {
            Page right = last.getRight();
            if (right.getPictures() != null && right.getPictures()[right.getPictures().length - 1] != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void initResult(Intent data) {
        super.initResult(data);
        data.putExtra(SPREADS_RESULT, mSpreads);
    }

    @Override
    public void submitResult() {
        createSpread();
    }

    @Override
    protected String getSubmitButtonText() {
        if (canSelectMore(null)) {
            return getString(R.string.mis_action_button_string,
                    getString(R.string.mis_action_done), getValidSpreadsCount(), mMaxSpreads, mMinSpreads);
        } else {
            return getString(R.string.ready);
        }
    }

    @Override
    protected boolean isValidCount() {
        if (mSpreads.size() >= mMinSpreads && mSpreads.size() <= mMaxSpreads) {
            return isLastSpreadFilled();
        }
        return false;
    }

    protected void afterLoading(Picture picture) {
        if (mCurrentPage == null) {
            return;
        }
        int position = 0;
        while (position < mCurrentPage.getPictures().length && mCurrentPage.getPictures()[position] != null) {
            position++;
        }

        mCurrentPage.getPictures()[position] = picture;

/*        updateSpreadsAdapter();*/

        if (mCurrentPage.getPictures()[mCurrentPage.getPictures().length - 1] != null) {
            mCurrentPage = null;
        }

        updatePageTemplate();
        updateSubmitButton();
    }

   /* private void updateSpreadsAdapter() {
        mSpreadsAdapter.notifyDataSetChanged();
        mSpreadsRecycler.scrollToPosition(mSpreadsAdapter.getItemCount() - 1);
    }*/

    public void addNewSpread() {
        mCurrentSpread = new Spread();
        mSpreads.add(mCurrentSpread);
    }

    @Override
    public void onImageUnselected(Image image) {
        super.onImageUnselected(image);
        if (mSelectedImage != null) {
            mSelectedImage.remove(image);
        }
        updateSubmitButton();
    }

    public void removeLastPagePicture() {
        int position = 0;
        while (position < mCurrentPage.getPictures().length && mCurrentPage.getPictures()[position] != null) {
            position++;
        }
        if (position != 0) {
            mCurrentPage.getPictures()[position - 1] = null;
        }
        if (position <= 1) {
            if (mCurrentSpread.getRight() == mCurrentPage) {
                mCurrentSpread.setRight(null);
                mCurrentPage = null;
            } else if (mCurrentSpread.getLeft() == mCurrentPage) {
                mCurrentSpread.setLeft(null);
                mCurrentPage = null;
                mSpreads.remove(mCurrentSpread);
                mCurrentSpread = null;
            }
        }
    }

    protected void updatePageTemplate() {
        PageTemplate pageTemplate = mCurrentPage != null ? mCurrentPage.getTemplate() : null;
        for (BaseSelectableRecyclerFragment fragment : mFragments) {
            fragment.setSoftPageTemplate(pageTemplate);
        }
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        dismissDialog();
        if (spiceException.getCause() != null) {
            if (spiceException.getCause() instanceof FileLoadException) {
                Toast.makeText(this, R.string.error_loading_file, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, spiceException.getCause().getMessage(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, spiceException.getMessage(), Toast.LENGTH_LONG).show();
        }
        onBackPressed();
    }

    @Override
    public void onRequestSuccess(Picture picture) {
        dismissDialog();
        afterLoading(picture);
    }
}