package me.nereo.multi_image_selector;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import ru.lucky_book.R;
import ru.lucky_book.entities.spread.PageTemplate;
import ru.lucky_book.features.imageselector.SelectImagesActivity;
import ru.lucky_book.features.imageselector.SelectImagesWithSpreadsActivity;

/**
 * 图片选择器
 * Created by nereo on 16/3/17.
 */
public class MultiImageSelector {

    public static final String EXTRA_RESULT = SelectImagesActivity.EXTRA_RESULT;

    private int mMaxCount = SelectImagesActivity.DEFAULT_MAX_IMAGE_COUNT;
    private int mMinCount = SelectImagesActivity.DEFAULT_MIN_IMAGE_COUNT;
    private int mMode = SelectImagesActivity.MODE_MULTIPLE;
    private PageTemplate mPageTemplate = PageTemplate.SINGLE;

    private Integer mPageWidth;

    private static MultiImageSelector sSelector;
    private ArrayList<String> mPictures;

    @Deprecated
    private MultiImageSelector(Context context) {

    }

    private MultiImageSelector() {
    }

    @Deprecated
    public static MultiImageSelector create(Context context) {
        if (sSelector == null) {
            sSelector = new MultiImageSelector(context);
        }
        return sSelector;
    }

    public static MultiImageSelector create() {
        if (sSelector == null) {
            sSelector = new MultiImageSelector();
        }

        return sSelector;
    }

    public MultiImageSelector pageWith(int pageWidth) {
        mPageWidth = pageWidth;
        return sSelector;
    }

    public MultiImageSelector single() {
        mMode = SelectImagesActivity.MODE_SINGLE;
        return sSelector;
    }

    public MultiImageSelector multiple() {
        mMode = SelectImagesActivity.MODE_MULTIPLE;
        //mPageTemplate = PageTemplate.FOUR;
        mPictures = null;
        return sSelector;
    }

    public MultiImageSelector maxCount(int count) {
        mMaxCount = count;
        return sSelector;
    }

    public MultiImageSelector pageTemplate(PageTemplate template) {
        mPageTemplate = template;
        return sSelector;
    }

    public void start(Activity activity, int requestCode) {
        final Context context = activity;
        if (hasPermission(context)) {
            activity.startActivityForResult(createIntent(context), requestCode);
        } else {
            Toast.makeText(context, R.string.mis_error_no_permission, Toast.LENGTH_SHORT).show();
        }
    }

    public void start(Fragment fragment, int requestCode) {
        final Context context = fragment.getContext();
        if (hasPermission(context)) {
            fragment.startActivityForResult(createIntent(context), requestCode);
        } else {
            Toast.makeText(context, R.string.mis_error_no_permission, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean hasPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            // static Permission was added in API Level 16
            return ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private Intent createIntent(Context context) {
        Intent intent = new Intent(context, mMode == SelectImagesActivity.MODE_SINGLE ? SelectImagesActivity.class : SelectImagesWithSpreadsActivity.class);
        intent.putExtra(SelectImagesActivity.EXTRA_MAX_SELECT_COUNT, mMaxCount);
        intent.putExtra(SelectImagesActivity.EXTRA_MIN_SELECT_COUNT, mMinCount);
        intent.putExtra(SelectImagesWithSpreadsActivity.MAX_SPREADS_COUNT, mMaxCount);
        intent.putExtra(SelectImagesActivity.EXTRA_SELECT_MODE, mMode);
        intent.putExtra(SelectImagesActivity.EXTRA_PATHS, mPictures);
        if (mPageWidth != null) {
            intent.putExtra(SelectImagesWithSpreadsActivity.EXTRA_PAGE_WIDTH, mPageWidth);
        }
        intent.putExtra(SelectImagesActivity.EXTRA_PAGE_TEMPLATE, mPageTemplate);
        return intent;
    }


    public MultiImageSelector pictureNames(List<String> pictures) {
        this.mPictures = (ArrayList<String>) pictures;
        return this;
    }
}
