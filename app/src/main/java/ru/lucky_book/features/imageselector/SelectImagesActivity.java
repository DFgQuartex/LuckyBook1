package ru.lucky_book.features.imageselector;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.UncachedSpiceService;
import com.octo.android.robospice.persistence.DurationInMillis;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.CachedSpiceRequest;
import com.octo.android.robospice.request.listener.RequestListener;
import com.octo.android.robospice.request.listener.RequestProgress;
import com.octo.android.robospice.request.listener.RequestProgressListener;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKCallback;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKError;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import me.nereo.multi_image_selector.bean.Image;
import ru.lucky_book.R;
import ru.lucky_book.app.MainApplication;
import ru.lucky_book.entities.spread.PageTemplate;
import ru.lucky_book.features.base.BaseSelectableRecyclerFragment;
import ru.lucky_book.features.facebook.FacebookPhotosFragment;
import ru.lucky_book.features.instagram.InstagramPhotosFragment;
import ru.lucky_book.features.localselector.LocalSelectorFragment;
import ru.lucky_book.features.vkphotos.VKPhotosFragment;
import ru.lucky_book.network.robospice.DownloadImagesSpiceTask;
import ru.lucky_book.network.utils.DownloadUtil;
import ru.lucky_book.spice.LuckySpiceManager;
import ru.lucky_book.utils.UiUtils;

public class SelectImagesActivity extends AppCompatActivity implements BaseSelectableRecyclerFragment.SelectionListener, View.OnClickListener, ImagesAdapter.SelectedImageChecker {
    public static final int MODE_SINGLE = 0;
    public static final int MODE_MULTIPLE = 1;

    public static final int DEFAULT_MIN_IMAGE_COUNT = 10;
    public static final int DEFAULT_MAX_IMAGE_COUNT = 20;
    public static int mSelectedId;
    public static String EXTRA_PATHS = "mPaths";
    protected MaterialDialog mLoadingDialog;
    /**
     * Page template, if needed. null by default
     */
    public static final String EXTRA_PAGE_TEMPLATE = "page_template";
    /**
     * Select mode，{@link #MODE_MULTIPLE} by default
     */
    public static final String EXTRA_SELECT_MODE = "select_mode";
    /**
     * Max image count，int，{@link #DEFAULT_MAX_IMAGE_COUNT} by default
     */
    public static final String EXTRA_MAX_SELECT_COUNT = "max_select_count";
    /**
     * Min image count, int, {@link #DEFAULT_MIN_IMAGE_COUNT} by default
     */
    public static final String EXTRA_MIN_SELECT_COUNT = "min_select_count";
    /**
     * Result data set，ArrayList&lt;Image&gt;
     */
    public static final String EXTRA_RESULT = "select_result";

    @SuppressWarnings("unchecked")
    private static final Class<? extends BaseSelectableRecyclerFragment>[] TAB_CLASSES = new Class[]{
            LocalSelectorFragment.class,
            VKPhotosFragment.class,
            FacebookPhotosFragment.class,
            InstagramPhotosFragment.class
    };

    private static final int[] icons = new int[]{
            R.drawable.ic_image,
            R.drawable.ic_vk,
            R.drawable.ic_facebook,
            R.drawable.ic_instagram
    };

    private int mMaxImageCount;
    private int mMinImageCount;
    private MaterialDialog mDownloadProgressDialog;
    SpiceManager mSpiceManager = new LuckySpiceManager(UncachedSpiceService.class);

    private int mMode;

    private ViewHolder mHolder;

    protected List<BaseSelectableRecyclerFragment> mFragments;

    private List<Image> mSelected = new ArrayList<>();
    private HashMap<String, String> mPaths;

    protected SpiceManager getSpiceManger() {
        return mSpiceManager;
    }

    public void initFragments() {
        PageTemplate template = null;
        if (getIntent() != null && getIntent().hasExtra(EXTRA_PAGE_TEMPLATE)) {
            template = (PageTemplate) getIntent().getSerializableExtra(EXTRA_PAGE_TEMPLATE);
        }
        mFragments = new ArrayList<>();
        for (Class<? extends BaseSelectableRecyclerFragment> fragmentClass : TAB_CLASSES) {
            mFragments.add(BaseSelectableRecyclerFragment.newInstance(fragmentClass, template, mMode));
        }
    }

    protected int getLayoutId() {
        return R.layout.activity_select_images;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        initDataFromIntent(getIntent());
        createViewHolder();
        fillViewHolder();
    }

    protected void initDataFromIntent(Intent intent) {
        mMaxImageCount = intent.getIntExtra(EXTRA_MAX_SELECT_COUNT, DEFAULT_MAX_IMAGE_COUNT);
        mMinImageCount = intent.getIntExtra(EXTRA_MIN_SELECT_COUNT, DEFAULT_MIN_IMAGE_COUNT);
        mMode = intent.getIntExtra(EXTRA_SELECT_MODE, MODE_MULTIPLE);
        ArrayList<String> listExtra = intent.getStringArrayListExtra(EXTRA_PATHS);
        if (listExtra != null && !listExtra.isEmpty()) {
            mPaths = new HashMap<>();
            for (String s : listExtra) {
                mPaths.put(s, s);
            }
        }

    }

    @Override
    protected void onStart() {
        if (!mSpiceManager.isStarted()) {
            mSpiceManager.start(this);
        }
        super.onStart();
        MainApplication.currentScreen = MainApplication.Screen.ВыборФото;
        ((MainApplication)getApplication()).info();
    }

    @Override
    protected void onDestroy() {
        if (mSpiceManager.isStarted()) {
            mSpiceManager.shouldStop();
        }
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (!VKSdk.onActivityResult(requestCode, resultCode, data, new VKCallback<VKAccessToken>() {
            @Override
            public void onResult(VKAccessToken res) {
                Log.d("tag", "success");
                int currentItem = mHolder.pager.getCurrentItem();
                BaseSelectableRecyclerFragment fragment = mFragments.get(currentItem);
                if (fragment instanceof VKPhotosFragment) {
                    ((VKPhotosFragment) fragment).updateUI();
                }
            }

            @Override
            public void onError(VKError error) {
                Log.d("tag", "error");
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
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

    private void fillViewHolder() {
        setSupportActionBar(mHolder.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        initFragments();
        mHolder.pager.setAdapter(new ImageSelectorPagerAdapter(getSupportFragmentManager()));
        mHolder.tabLayout.setupWithViewPager(mHolder.pager);
        for (int i = 0; i < TAB_CLASSES.length; i++) {
            mHolder.tabLayout.getTabAt(i).setIcon(icons[i]);
        }
        mHolder.tabLayout.getTabAt(mSelectedId).select();
        mHolder.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mSelectedId = tab.getPosition();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        if (mMode == MODE_MULTIPLE) {
            mHolder.submit.setVisibility(View.VISIBLE);
            mHolder.submit.setOnClickListener(this);
            updateSubmitButton();
        } else {
            mHolder.submit.setVisibility(View.GONE);
        }
    }

    private void createViewHolder() {
        mHolder = new ViewHolder();
        mHolder.toolbar = (Toolbar) findViewById(R.id.toolbar);
        mHolder.tabLayout = (TabLayout) findViewById(R.id.tabs);
        mHolder.pager = (ViewPager) findViewById(R.id.pager);
        mHolder.submit = (Button) findViewById(R.id.submit);
    }

    protected boolean isValidCount() {
        int size = mSelected.size();
        return size > 0;
    }

    protected String getSubmitButtonText() {
        int size = mSelected.size();
        return getString(R.string.mis_action_button_string,
                getString(R.string.mis_action_done), size, mMaxImageCount, mMinImageCount);
    }

    protected void updateSubmitButton() {
        mHolder.submit.setText(R.string.mis_action_done);
        mHolder.submit.setEnabled(mSelected.size() > 0);
    }


    @Override
    public void onImageSelected(Image image) {
        //since we use the same list, we don't need to add it into mSelected
        if (mMode == MODE_SINGLE) {
            List<Image> networkImages = getNetworkImages(mSelected);
            if (!networkImages.isEmpty()) {
                loadImages(networkImages);
            } else {
                sendResult();
            }
        } else {
            updateSubmitButton();
        }
    }

    @Override
    public void onImageUnselected(Image image) {
        //notify All
        for (BaseSelectableRecyclerFragment fragment : mFragments) {
            if (fragment.isAdded()) {
                fragment.notifySelectionChanged();
            }
        }
        updateSubmitButton();
    }

    @Override
    public List<Image> getSelected() {
        return mSelected;
    }

    @Override
    public boolean canSelectMore(Image image) {
        if (checkImage(image)) {
            UiUtils.showErrorDialogNoTitle(this, getString(R.string.text_this_photo_selected));
            return false;
        }
        return true;
    }

    @Override
    public void checkLastSelected() {

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                submitResult();
                break;
        }
    }

    public void submitResult() {
        if (mSelected != null && !mSelected.isEmpty()) {
            List<Image> networkImages = getNetworkImages(mSelected);
            if (!networkImages.isEmpty()) {
                loadImages(networkImages);
            } else {
                sendResult();
            }
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    public void loadImages(List<Image> networkImages) {
        showProgressDialog(networkImages);
        mSpiceManager.execute(new CachedSpiceRequest<>(new DownloadImagesSpiceTask(networkImages), null, DurationInMillis.ALWAYS_EXPIRED), new DownloadImagesListener());
    }

    private void showProgressDialog(List<Image> networkImages) {
        mDownloadProgressDialog = new MaterialDialog.Builder(this)
                .title(R.string.downloading)
                .progress(false, networkImages.size(), true)
                .cancelable(false)
                .show();
    }

    public List<Image> getNetworkImages(List<Image> selected) {
        List<Image> networkImages = new ArrayList<>();
        for (Image image : selected) {
            if (!image.isLocal) {
                networkImages.add(image);
            }
        }
        return networkImages;
    }

    protected void initResult(Intent data) {
        List<String> paths = getPaths(mSelected);
        data.putExtra(EXTRA_RESULT, (ArrayList<String>) paths);
    }

    public void sendResult() {
        if (mLoadingDialog != null)
            mLoadingDialog.dismiss();
        Intent data = new Intent();
        initResult(data);
        setResult(RESULT_OK, data);
        finish();
    }

    private List<String> getPaths(List<Image> selected) {
        List<String> paths = new ArrayList<>();
        for (Image image : selected) {
            if (image.isLocal) {
                paths.add(image.path);
            } else {
                File file = DownloadUtil.getLocal(image);
                if (file != null && file.exists()) {
                    paths.add(file.getAbsolutePath());
                }
            }
        }
        return paths;
    }

    @Override
    public boolean checkImage(Image image) {
        if (mPaths != null) {
            return mPaths.containsKey(image.name);
        }
        return false;
    }

    private class ViewHolder {
        Toolbar toolbar;
        TabLayout tabLayout;
        ViewPager pager;
        Button submit;
    }

    protected Button getSubmitButton() {
        return mHolder.submit;
    }

    private class DownloadImagesListener implements RequestListener<List>, RequestProgressListener {

        @Override
        public void onRequestFailure(SpiceException spiceException) {
            if (mDownloadProgressDialog != null && mDownloadProgressDialog.isShowing()) {
                mDownloadProgressDialog.dismiss();
                mDownloadProgressDialog = null;
            }
            Toast.makeText(SelectImagesActivity.this, R.string.download_images_error, Toast.LENGTH_LONG).show();
        }

        @Override
        public void onRequestSuccess(List images) {
            if (mDownloadProgressDialog != null && mDownloadProgressDialog.isShowing()) {
                mDownloadProgressDialog.dismiss();
                mDownloadProgressDialog = null;
            }
            if (!images.isEmpty()) {
                Toast.makeText(SelectImagesActivity.this, getString(R.string.download_some_images_error, images.size()), Toast.LENGTH_LONG).show();
            } else {
                sendResult();
            }
        }

        @Override
        public void onRequestProgressUpdate(RequestProgress progress) {
            if (mDownloadProgressDialog != null && mDownloadProgressDialog.isShowing()) {
                mDownloadProgressDialog.incrementProgress(1);
            }
        }
    }

    private class ImageSelectorPagerAdapter extends FragmentStatePagerAdapter {

        public ImageSelectorPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments != null ? mFragments.size() : 0;
        }

        @Override
        public CharSequence getPageTitle(int position) {
           /* if (position == 0)
                return getString(mFragments.get(position).getPageTitle());*/
            return "";
        }
    }

    @Override
    public void onBackPressed() {
        if (mSelected.isEmpty()) {
            super.onBackPressed();
        } else {
            onImageUnselected(mSelected.remove(mSelected.size() - 1));
        }
    }
}
