package me.nereo.multi_image_selector;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;

import ru.lucky_book.R;
import ru.lucky_book.entities.spread.PageTemplate;

/**
 * Multi image selector
 * Created by Nereo on 2015/4/7.
 * Updated by nereo on 2016/1/19.
 * Updated by nereo on 2016/5/18.
 */
@Deprecated
public class MultiImageSelectorActivity extends AppCompatActivity
        implements MultiImageSelectorFragment.Callback {

    // Single choice
    public static final int MODE_SINGLE = 0;
    // Multi choice
    public static final int MODE_MULTI = 1;

    public static final int MAX_COUNT = 10;
    public static final int MIN_COUNT = 4;

    //    /**
//     * Max image count，int，{@link #DEFAULT_MAX_IMAGE_COUNT} by default
//     */
    public static final String EXTRA_MAX_SELECT_COUNT = "max_select_count";
    /**
     * Min image count, int, 0 by default
     */
    public static final String EXTRA_MIN_SELECT_COUNT = "min_select_count";
    /**
     * Select mode，{@link #MODE_MULTI} by default
     */
    public static final String EXTRA_SELECT_MODE = "select_count_mode";


    public static final String EXTRA_PAGE_TEMPLATE="page_template";
    /**
     * Whether show camera，true by default
     */
    public static final String EXTRA_SHOW_CAMERA = "show_camera";
    /**
     * Result data set，ArrayList&lt;String&gt;
     */
    public static final String EXTRA_RESULT = "select_result";
    /**
     * Original data set
     */
    public static final String EXTRA_DEFAULT_SELECTED_LIST = "default_list";
    // Default image size

    protected static final int DEFAULT_MIN_IMAGE_COUNT = 10;
    protected static final int DEFAULT_MAX_IMAGE_COUNT = 20;

    private ArrayList<String> resultList = new ArrayList<>();
    private Button mSubmitButton;
    private int mMaxImageCount;
    private int mMinImageCount;

    private int mMode;

    private PageTemplate mPageTemplate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mis_activity_default);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }

        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        final Intent intent = getIntent();
        mMaxImageCount = intent.getIntExtra(EXTRA_MAX_SELECT_COUNT, DEFAULT_MAX_IMAGE_COUNT);
        mMinImageCount = intent.getIntExtra(EXTRA_MIN_SELECT_COUNT, DEFAULT_MIN_IMAGE_COUNT);

        mMode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_MULTI);

        mPageTemplate = (PageTemplate) intent.getSerializableExtra(EXTRA_PAGE_TEMPLATE);
        if(mPageTemplate==null){
            mPageTemplate=PageTemplate.SINGLE;
        }

        final boolean isShow = intent.getBooleanExtra(EXTRA_SHOW_CAMERA, true);
        if (mMode == MODE_MULTI && intent.hasExtra(EXTRA_DEFAULT_SELECTED_LIST)) {
            resultList = intent.getStringArrayListExtra(EXTRA_DEFAULT_SELECTED_LIST);
        }

        mSubmitButton = (Button) findViewById(R.id.commit);
        if (mMode == MODE_MULTI) {
            updateDoneText(resultList);
            mSubmitButton.setVisibility(View.VISIBLE);
            mSubmitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (resultList != null && resultList.size() > 0) {
                        // Notify success
                        Intent data = new Intent();
                        data.putStringArrayListExtra(EXTRA_RESULT, resultList);
                        setResult(RESULT_OK, data);
                    } else {
                        setResult(RESULT_CANCELED);
                    }
                    finish();
                }
            });
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putInt(MultiImageSelectorFragment.EXTRA_MAX_SELECT_COUNT, mMaxImageCount);
            bundle.putInt(MultiImageSelectorFragment.EXTRA_MIN_SELECT_COUNT, mMinImageCount);
            bundle.putInt(MultiImageSelectorFragment.EXTRA_SELECT_MODE, mMode);
            bundle.putBoolean(MultiImageSelectorFragment.EXTRA_SHOW_CAMERA, isShow);
            bundle.putStringArrayList(MultiImageSelectorFragment.EXTRA_DEFAULT_SELECTED_LIST, resultList);
            bundle.putSerializable(MultiImageSelectorFragment.EXTRA_PAGE_TEMPLATE,mPageTemplate);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.image_grid, Fragment.instantiate(this, MultiImageSelectorFragment.class.getName(), bundle))
                    .commit();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_CANCELED);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Update done button by select image data
     *
     * @param mResultList selected image data
     */
    private void updateDoneText(ArrayList<String> mResultList) {
        int size = 0;
        if (resultList == null || resultList.size() == 0) {
            mSubmitButton.setText(R.string.mis_action_done);
            mSubmitButton.setEnabled(false);
        } else {
            size = resultList.size();
            if (resultList.size() >= mMinImageCount) {
                mSubmitButton.setEnabled(true);
            }
        }
        mSubmitButton.setText(getString(R.string.mis_action_button_string,
                getString(R.string.mis_action_done), size, mMaxImageCount, mMinImageCount));

        boolean valid = isValidSelection(this,mMode, mResultList, mMinImageCount);
        mSubmitButton.setEnabled(valid);
    }

    static boolean isValidSelection(Context context,int mode, ArrayList<String> resultList, int minCount) {
//        return computeLeafCount(context, resultList) == DESIRED_LEAF_COUNT;
        return resultList.size() >= minCount&&(mode==MODE_SINGLE||resultList.size()%2==0);
    }

    @Override
    public void onSingleImageSelected(String path) {
        Intent data = new Intent();
        resultList.add(path);
        data.putStringArrayListExtra(EXTRA_RESULT, resultList);
        setResult(RESULT_OK, data);
        finish();
    }

    @Override
    public void onImageSelected(String path) {
        if (!resultList.contains(path)) {
            resultList.add(path);
        }
        updateDoneText(resultList);
    }

    @Override
    public void onImageUnselected(String path) {
        if (resultList.contains(path)) {
            resultList.remove(path);
        }
        updateDoneText(resultList);
    }

    @Override
    public void onCameraShot(File imageFile) {
        if (imageFile != null) {
            // notify system the image has change
            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));

            Intent data = new Intent();
            resultList.add(imageFile.getAbsolutePath());
            data.putStringArrayListExtra(EXTRA_RESULT, resultList);
            setResult(RESULT_OK, data);
            finish();
        }
    }

    private class SelectImagesPagerAdapter extends FragmentStatePagerAdapter {

        public SelectImagesPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return 0;
        }
    }
}
