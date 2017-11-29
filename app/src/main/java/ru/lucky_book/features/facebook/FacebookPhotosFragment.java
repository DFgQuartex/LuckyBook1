package ru.lucky_book.features.facebook;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.persistence.exception.SpiceException;
import com.octo.android.robospice.request.listener.RequestListener;

import java.util.Collections;
import java.util.List;

import me.nereo.multi_image_selector.bean.Image;
import ru.lucky_book.R;
import ru.lucky_book.entities.facebook.Photo;
import ru.lucky_book.features.base.BaseSelectableRecyclerFragment;
import ru.lucky_book.network.repository.FacebookRepository;
import ru.lucky_book.spice.LocalSpiceService;
import ru.lucky_book.spice.LuckySpiceManager;
import ru.lucky_book.task.GenerateImagesSpiceTask;
import ru.lucky_book.utils.ConnectionUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class FacebookPhotosFragment extends BaseSelectableRecyclerFragment implements FacebookRepository.OnLoadListener<List<Photo>>, RequestListener<Pair>, View.OnClickListener {

    private ViewHolder mViewHolder;
    private CallbackManager mCallbackManager;
    private String mAfter;
    private SpiceManager mSpiceManager = new LuckySpiceManager(LocalSpiceService.class);

    @Override
    public int getPageTitle() {
        return R.string.facebook_photos_fragment__title;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAfter = null;
        FacebookSdk.sdkInitialize(getContext().getApplicationContext());
        mCallbackManager = CallbackManager.Factory.create();
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
        super.onDestroy();
        if (mSpiceManager.isStarted()) {
            mSpiceManager.shouldStop();
        }
    }

    @Override
    public int getIcon() {
        return R.drawable.ic_facebook;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_facebook_photos, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        createViewHolder(view);
//        populateViewHolder();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void populateViewHolder() {
        super.populateViewHolder();
        //mViewHolder.mLoginButton.setFragment(this);
        //mViewHolder.mLoginButton.setReadPermissions(Arrays.asList("user_photos"));
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                updateUI();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        mViewHolder.mLoginButton.setOnClickListener(this);
        updateUI();
    }

    @Override
    protected void createViewHolder(View view) {
        super.createViewHolder(view);
        mViewHolder = new ViewHolder();
        mViewHolder.mLoginButton = view.findViewById(R.id.login_button);
    }

    private void updateUI() {
        boolean buttonVisible = AccessToken.getCurrentAccessToken() == null;
        mViewHolder.mLoginButton.setVisibility(buttonVisible ? View.VISIBLE : View.GONE);
//        mViewHolder.mPhotosRecyclerView.setVisibility(buttonVisible ? View.GONE : View.VISIBLE);
        if (!buttonVisible && ConnectionUtils.connectedToNetwork(getContext())) {
            FacebookRepository.newInstance(FacebookPhotosFragment.this, mAfter).getPhotos();
        }
    }

    @Override
    public void onLoad(List<Photo> data, String after) {
        if (!data.isEmpty()) {
            mSpiceManager.execute(new GenerateImagesSpiceTask<String>(data, after, "fb"), this);
        }

        Log.d("tag", "photos size is: " + data.size());
    }


    @Override
    public void onFail() {
        Log.d("tag", "onFail");
    }

    @Override
    protected void onLoadMore(int currentPage) {
        FacebookRepository.newInstance(this, mAfter).getPhotos();
    }

    @Override
    public void onRequestFailure(SpiceException spiceException) {
        Toast.makeText(getContext(), R.string.social_photo_load_error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestSuccess(Pair pair) {
        List<Image> images = getValidImage((List<Image>) pair.second);
        if (images.size() == 0 && getAdapter().getItemCount() == 0)
            images.add(new Image(Image.TYPE_EMPTY));
        if (mAfter == null) {
            setImages(images);
        } else {
            addImages(images);
        }
        if (getAdapter().getItemCount() > 0)
            mAfter = pair.first == null ? null : pair.first.toString();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.login_button) {
            LoginManager.getInstance().logInWithReadPermissions(this, Collections.singletonList("user_photos"));
        }
    }


    private class ViewHolder {
        View mLoginButton;
    }

}
