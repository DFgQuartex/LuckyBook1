package ru.lucky_book.features.albumcreate.choosecover.choosesubcover;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import ru.lucky_book.R;
import ru.lucky_book.features.albumcreate.choosecover.ChooseCoverItem;
import ru.lucky_book.features.albumcreate.choosecover.choosecover.ChooseCoverFragment;
import ru.lucky_book.features.base.BaseRecyclerAdapter;

/**
 * Created by demafayz on 25.08.16.
 */
public class SubChooseCoverFragment extends ChooseCoverFragment {
    private static final String TAG = SubChooseCoverFragment.class.getSimpleName();
    private static final String COVER_TAG = TAG + "_cover";
    private static final String COVER_ID = "cover_id";
    private String mCoverPath;
    private ChooseSubCoverPresenter mPresenter;
    private int mId;
    Snackbar mSnackbar;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mPresenter = new ChooseSubCoverPresenter();
        mPresenter.attachView(this);
        readBundle(savedInstanceState);
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    protected BaseRecyclerAdapter<ChooseCoverItem, ?> getNewAdapter() {
        return new SubChooseCoverAdapter(new ArrayList<>());
    }

    @Override
    protected GridLayoutManager getLayoutManager() {
        return new GridLayoutManager(getContext(), 2);
    }

    private void readBundle(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args == null) {
            args = savedInstanceState;
        }
        mCoverPath = args.getString(COVER_TAG);
        mId = args.getInt(COVER_ID);
    }

    @Override
    public void onItemClick(View view, int position) {
        this.mAssetsCover = adapter.getItem(position).getOriginalImage();
        int idImage = adapter.getItem(position).getId();
        loadAndSaveCover(idImage);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(COVER_TAG, mCoverPath);
        outState.putInt(COVER_ID, mId);
    }

    @Override
    public void showListCover(List<ChooseCoverItem> chooseCoverItems) {
        mList.addAll(chooseCoverItems);
        adapter.addAll(mList);
    }

    @Override
    public void showError(Throwable throwable) {
        adapter.addAll(mList);
        showGetErrorListCover();
    }

    private void showGetErrorListCover() {
        mSnackbar = Snackbar.make(mRecyclerView, R.string.text_error_request_list_cover, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.text_repeat, view -> mPresenter.loadListCover(mCoverPath,mId));
        mSnackbar.show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(mSnackbar!=null){
            mSnackbar.dismiss();
        }
    }

    @Override
    protected void initData(Context context) {
        mList = new ArrayList<>();
        mPresenter.loadListCover(mCoverPath, mId);
    }

    public static SubChooseCoverFragment newInstance(String path, int id) {
        Bundle args = new Bundle();
        args.putString(COVER_TAG, path);
        args.putInt(COVER_ID, id);
        SubChooseCoverFragment fragment = new SubChooseCoverFragment();
        fragment.setArguments(args);
        return fragment;
    }
}