package ru.lucky_book.features.albumcreate.choosecover.choosecover;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.luckybookpreview.utils.FileUtil;
import com.example.luckybookpreview.utils.Navigate;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ru.lucky_book.BuildConfig;
import ru.lucky_book.R;
import ru.lucky_book.app.MainApplication;
import ru.lucky_book.features.albumcreate.choosecover.ChooseCoverItem;
import ru.lucky_book.features.albumcreate.choosecover.choosesubcover.SubChooseCoverFragment;
import ru.lucky_book.features.base.BaseFragment;
import ru.lucky_book.features.base.BaseRecyclerAdapter;
import ru.lucky_book.features.base.listener.OnItemClickListener;
import ru.lucky_book.features.spreads.SpreadsActivity;
import ru.lucky_book.utils.UiUtils;
import ru.lucky_book.widget.EmptyRecyclerView;

import static com.example.luckybookpreview.utils.FileUtil.getCoverCashFolder;

/**
 * Created by demafayz on 25.08.16.
 */
public class ChooseCoverFragment extends BaseFragment implements OnItemClickListener, ChooseCoverView {

    protected List<ChooseCoverItem> mList;
    protected BaseRecyclerAdapter<ChooseCoverItem, ?> adapter;
    protected String mAssetsCover;
    protected EmptyRecyclerView mRecyclerView;
    protected MaterialDialog mDialog;
    protected View mEmptyView;
    protected ChooseCoverPresenter mPresenter;
    MaterialDialog materialDialog;
    private int mIdCover;

    @Override
    public String getFragmentTitle() {
        return getString(R.string.choose_cover);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recycler_view_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mPresenter = new ChooseCoverPresenter();
        mPresenter.attachView(this);
        initData(view.getContext());
        initView(view);
        createNewAdapter();
    }

    public void loadAndSaveCover(int idImage) {
        mIdCover = idImage;
        materialDialog = UiUtils.showProgress(getContext(), R.string.titleLoadImage);
        ImageView imageView = new ImageView(getContext());
        File file = new File(getCoverCashFolder(), "cover" + idImage);
        if (file.exists()) {
            materialDialog.dismiss();
            mAssetsCover = file.getAbsolutePath();
            choosePhotos();
        } else
            Picasso.with(getContext())
                    .load(BuildConfig.ENDPOINT + mAssetsCover)
                    //  .networkPolicy(NetworkPolicy.OFFLINE)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            Bitmap innerBitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                            mAssetsCover = FileUtil.saveJpeg100(innerBitmap, "cover" + idImage);
                            materialDialog.dismiss();
                            choosePhotos();
                        }

                        @Override
                        public void onError() {
                            UiUtils.showErrorDialog(getContext(), R.string.error_loading_file);
                            materialDialog.dismiss();
                        }
                    });
    }

    protected void initData(Context context) {
        mList = new ArrayList<>();
        mPresenter.loadListCover();
    }

    protected BaseRecyclerAdapter<ChooseCoverItem, ?> getNewAdapter() {
        return new ChooseCoverAdapter(new ArrayList<>());
    }

    protected GridLayoutManager getLayoutManager() {
        GridLayoutManager manager = new GridLayoutManager(getContext(), 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (adapter.getItemViewType(position) == ChooseCoverAdapter.PROMO) {
                    return 2;
                }
                return 1;
            }
        });
        return manager;
    }

    protected void createNewAdapter() {
        adapter = getNewAdapter();
        adapter.setOnItemClickListener(this);
        mRecyclerView.setLayoutManager(getLayoutManager());
        mRecyclerView.setViewLoad(mEmptyView);
        adapter.attachToRecyclerView(mRecyclerView);
        mRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void onItemClick(View view, int position) {
        ChooseCoverItem item = adapter.getItem(position);
        String coverIcon = item.getIcon();
        String tag = item.getUrlSubitems();
        Navigate.showFragment(getActivity().getSupportFragmentManager(), SubChooseCoverFragment.newInstance(tag, item.getId()), true);

    }


    protected void choosePhotos() {
        Intent intent = new Intent(getActivity(), SpreadsActivity.class);
        intent.putExtra(SpreadsActivity.COVER_TAG, mAssetsCover);
        intent.putExtra(SpreadsActivity.COVER_ID_TAG, mIdCover);
        getActivity().setResult(Activity.RESULT_OK,intent);
        getActivity().finish();
    }


    protected void dismissDialog() {
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
            mDialog = null;
        }
    }

    @Override
    public void onDestroy() {
        dismissDialog();
        super.onDestroy();
    }

    private void initView(View view) {
        mRecyclerView = (EmptyRecyclerView) view.findViewById(R.id.rvList);
        mEmptyView = view.findViewById(R.id.empty_view);
    }

    public static ChooseCoverFragment newInstance() {
        ChooseCoverFragment fragment = new ChooseCoverFragment();
        return fragment;
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
        Snackbar.make(mRecyclerView, R.string.text_error_request_list_cover, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.text_repeat, view -> mPresenter.loadListCover()).show();
    }
    @Override
    public void onStart(){
        super.onStart();
        MainApplication.currentScreen = MainApplication.Screen.ВыборОбложки;
        ((MainApplication)getActivity().getApplicationContext()).info();
    }
}
