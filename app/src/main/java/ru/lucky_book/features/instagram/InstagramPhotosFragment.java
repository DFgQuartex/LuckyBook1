package ru.lucky_book.features.instagram;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.bean.Image;
import ru.lucky_book.R;
import ru.lucky_book.app.Preferences;
import ru.lucky_book.data.insta.Datum;
import ru.lucky_book.data.insta.MediasInsta;
import ru.lucky_book.entities.instagram.InstagramMediaResponseBody;
import ru.lucky_book.features.base.BaseSelectableRecyclerFragment;
import ru.lucky_book.network.repository.InstagramRepository;
import ru.lucky_book.spice.LocalSpiceService;
import ru.lucky_book.spice.LuckySpiceManager;
import ru.lucky_book.task.GenerateImagesSpiceTask;

public class InstagramPhotosFragment extends BaseSelectableRecyclerFragment implements InstagramRepository.OnLoadListener<InstagramMediaResponseBody.User.Media>, RequestListener<Pair>, InstagramView {

    private static final int INSTAGRAM_AUTH_REQUEST_CODE = 100;

    private ViewHolder mViewHolder;
    private String mEndCursor;
    private String mCookies;
    private String mUsername;
    private boolean mNextPage;
    private SpiceManager mSpiceManager = new LuckySpiceManager(LocalSpiceService.class);
    private String mToken;
    private InstagramPresenter mPresenter;

    @Override
    public int getPageTitle() {
        return R.string.instagram_photos_fragment__title;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mToken = Preferences.Instagram.getAccessToken(getContext());
        initPresenter();
    }

    private void initPresenter() {
        mPresenter = new InstagramPresenter();
        mPresenter.attachView(this);
    }

    @Override
    public void onStart() {
        if (!mSpiceManager.isStarted()) {
            mSpiceManager.start(getContext());
        }
        super.onStart();
    }

    @Override
    public void onDestroy() {
        if (mSpiceManager.isStarted()) {
            mSpiceManager.shouldStop();
        }
        super.onDestroy();
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_instagram;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_instagram_photos, container, false);
    }

    @Override
    protected void onLoadMore(int currentPage) {
        if (mNextPage) {
            mPresenter.getMedias(mToken, mEndCursor);
        }
    }

    @Override
    protected void createViewHolder(View view) {
        super.createViewHolder(view);
        mViewHolder = new ViewHolder();
        mViewHolder.mAuthorizeButton = view.findViewById(R.id.login_button);
    }

    @Override
    protected void populateViewHolder() {
        super.populateViewHolder();
        mViewHolder.mAuthorizeButton.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), InstagramAuthActivity.class);
            startActivityForResult(intent, INSTAGRAM_AUTH_REQUEST_CODE);
        });
        updateUI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case INSTAGRAM_AUTH_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    mViewHolder.mAuthorizeButton.setVisibility(View.GONE);
                    mToken = data.getStringExtra(InstagramAuthActivity.EXTRA_TOKEN);
                    Preferences.Instagram.setAccessToken(getContext(), mToken);
//                    InstagramRepository.getInstance(getContext()).getMedia(mCookies, mUsername, this, mEndCursor);
                    updateUI();
                    break;
                } else {
                    Toast.makeText(getContext(), R.string.instagram_auth_error, Toast.LENGTH_LONG).show();
                }
        }
    }

    private void updateUI() {
        mEndCursor = null;
        boolean buttonVisible = mToken == null;
        mViewHolder.mAuthorizeButton.setVisibility(buttonVisible ? View.VISIBLE : View.GONE);
        if (!buttonVisible) {
            mPresenter.getMedias(mToken, mEndCursor);
        }
    }

    @Override
    public void onLoad(InstagramMediaResponseBody.User.Media data) {

    }

    @Override
    public void onFail() {
        Toast.makeText(getContext(), R.string.social_photo_load_error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Toast.makeText(getContext(), R.string.social_photo_load_error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestSuccess(Pair pair) {
        if (pair.first != null) {
            Boolean hasPreviousPage = (Boolean) pair.first;
            List<Image> images = getValidImage((List<Image>) pair.second);
            if (images.size() == 0 && getAdapter().getItemCount() == 0)
                images.add(new Image(Image.TYPE_EMPTY));
            if (!hasPreviousPage) {
                setImages(images);
            } else {
                addImages(images);
            }
        }
    }


    @Override
    public void showMedias(MediasInsta mediasInsta) {
        if (mediasInsta != null) {
            List<Datum> data = mediasInsta.getData();
            List<Datum> list = new ArrayList<>();

            for (Datum datum : data) {
                if (datum.getHeight() >= 400 && datum.getWidth() >= 400) {
                    list.add(datum);
                }
            }
            data = list;
            if (data != null) {
                mNextPage = mediasInsta.getPagination().getNextMaxId() != null;
                if (mNextPage) {
                    mEndCursor = mediasInsta.getPagination().getNextMaxId();
                } else {
                    mEndCursor = null;
                }
                mSpiceManager.execute(new GenerateImagesSpiceTask<>(data, getAdapter().getItemCount() > 0, "inst"), this);
            }
        }
    }

    @Override
    public void showError(Throwable throwable) {

    }

    private class ViewHolder {
        View mAuthorizeButton;
    }
}
