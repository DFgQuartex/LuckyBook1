package ru.lucky_book.features.disposition_screen;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.lucky_book.R;
import ru.lucky_book.database.DBHelper;
import ru.lucky_book.database.RealmAlbum;
import ru.lucky_book.entities.spread.Page;
import ru.lucky_book.entities.spread.Spread;
import ru.lucky_book.features.base.SpiceActivity;
import ru.lucky_book.features.preview_screen.PreviewActivity;
import ru.lucky_book.utils.dragndrop.SimpleItemTouchHelperCallback;

/**
 * Created by Загит Талипов on 10.11.2016.
 */

public class DispositionActivity extends SpiceActivity implements RequestListener<Boolean> {
    public static final String ALBUM_ID_DATA = "album_id";
    public static final String PAGE_HEIGHT_DATA = "height";
    public static final int REQUEST_CODE = 22;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    String mAlbumId;
    List<Spread> mSpreads;
    List<Page> mPages;
    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;
    private ItemTouchHelper mItemTouchHelper;
    private RealmAlbum mRealmAlbum;
    private MaterialDialog mProgressDialog;
    private int mPageHeight;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disposition);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_pages);
        mAlbumId = getIntent().getStringExtra(ALBUM_ID_DATA);
        mPageHeight = getIntent().getIntExtra(PAGE_HEIGHT_DATA, 0);
        loadPages();
        initAdapter();
    }

    @Override
    public void onBackPressed() {
        saveAlbum();
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public static void createActivity(Activity activity, String id, int height) {
        Intent intent = new Intent(activity, DispositionActivity.class);
        intent.putExtra(ALBUM_ID_DATA, id);
        intent.putExtra(PAGE_HEIGHT_DATA, height);
        activity.startActivityForResult(intent,REQUEST_CODE);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    protected void dismissDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    private void initAdapter() {
        mRecyclerView.addItemDecoration(new PageSpaceDecoration());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        DispositionAdapter adapter = new DispositionAdapter(mPages,mPageHeight,mPageHeight);
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mRecyclerView.setAdapter(adapter);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

    }

    private void loadPages() {
        mRealmAlbum = DBHelper.findAlbumById(mAlbumId);
        if (mRealmAlbum != null) {
            mSpreads = DBHelper.getRealmAlbumSpreads(mRealmAlbum);
            mPages = new ArrayList<>();
            for (Spread spread : mSpreads) {
                mPages.add(spread.getLeft());
                mPages.add(spread.getRight());
            }
        }
    }

    private void saveAlbum() {
        for (int i = 0; i < mSpreads.size(); i++) {
            Spread spread = mSpreads.get(i);
            spread.setLeft(mPages.get(i * 2));
            spread.setRight(mPages.get(i * 2 + 1));
        }
        DBHelper.updateAlbum(mAlbumId, mRealmAlbum.getTitle(), mRealmAlbum.getCover(), mSpreads,mRealmAlbum.getCoverId());
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        dismissDialog();
        Toast.makeText(this, getString(R.string.some_error), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onRequestSuccess(Boolean aBoolean) {
        dismissDialog();
        moveNext();
    }
}
