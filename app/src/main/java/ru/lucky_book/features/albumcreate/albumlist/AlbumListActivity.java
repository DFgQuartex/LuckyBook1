package ru.lucky_book.features.albumcreate.albumlist;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.List;

import ru.lucky_book.R;
import ru.lucky_book.database.RealmAlbum;
import ru.lucky_book.features.base.SpiceActivity;
import ru.lucky_book.features.base.listener.OnItemClickListener;
import ru.lucky_book.features.spreads.SpreadsActivity;
import ru.lucky_book.utils.ContextUtils;

/**
 * Created by Badr
 * on 03.09.2016 18:52.
 */
public class AlbumListActivity extends SpiceActivity implements View.OnClickListener, OnItemClickListener, RequestListener<List> {

    private AlbumListAdapter mAdapter;
    int selectPosition = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_albums);
        initToolbar();
        initList();
        findViewById(R.id.add).setOnClickListener(this);
    }

    private void initToolbar() {
        ContextUtils.setToolBar(this, (Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initList() {
        mAdapter = new AlbumListAdapter(null);
        mAdapter.setOnItemClickListener(this);
        CarouselLayoutManager manager = new CarouselLayoutManager(CarouselLayoutManager.VERTICAL);
        manager.setPostLayoutListener(new CarouselZoomPostLayoutListener());
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(manager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        recyclerView.addOnScrollListener(new CenterScrollListener());
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAdapter != null) {
            initialLoad();
        }
    }

    @Override
    protected void initialLoad() {
        if (getSpiceManager().getRequestToLaunchCount() == 0)
            getSpiceManager().execute(new AlbumsListSpiceTask(), this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add) {
            Intent intent = new Intent(this, SpreadsActivity.class);
            intent.putExtra(SpreadsActivity.TITLE_TAG, "");
            startActivity(intent);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        RealmAlbum item = mAdapter.getItem(position);
        String id = item.getId();
        selectPosition = position;

        Intent intent = new Intent(this, SpreadsActivity.class);
        intent.putExtra(SpreadsActivity.ID_TAG, id);
        startActivity(intent);

    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Toast.makeText(this, R.string.some_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestSuccess(List albums) {
        mAdapter.setData(albums);
    }
}
