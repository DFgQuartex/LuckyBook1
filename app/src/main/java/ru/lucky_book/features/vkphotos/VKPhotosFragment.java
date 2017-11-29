package ru.lucky_book.features.vkphotos;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vk.sdk.VKScope;
import com.vk.sdk.VKSdk;

import java.util.List;

import me.nereo.multi_image_selector.bean.Image;
import ru.lucky_book.R;
import ru.lucky_book.features.base.BaseSelectableRecyclerFragment;

/**
 * Created by demafayz on 07.09.16.
 */
public class VKPhotosFragment extends BaseSelectableRecyclerFragment implements VkPhoto.ImagesLoadListener {

    private String[] vkScope = {VKScope.PHOTOS};

    View mAuthorizeButton;

    int itemCount = 0;

    @Override
    public int getIcon() {
        return R.drawable.ic_vk;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_vk_photos, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        updateUI();
    }

    @Override
    protected void onLoadMore(int currentPage) {
        VkPhoto.getPhotos(getContext(), this, itemCount);
    }

    @Override
    protected void createViewHolder(View view) {
        super.createViewHolder(view);
        mAuthorizeButton = view.findViewById(R.id.login_button);
    }


    @Override
    protected void populateViewHolder() {
        super.populateViewHolder();
        mAuthorizeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VKSdk.login(getActivity(), vkScope);
            }
        });
    }

    public void updateUI() {
        boolean buttonVisible = !VKSdk.wakeUpSession(getContext().getApplicationContext());

        mAuthorizeButton.setVisibility(buttonVisible ? View.VISIBLE : View.GONE);
        if (!buttonVisible) {
            VkPhoto.getPhotos(getContext(), this, getAdapter().getItemCount());
        }
    }

    @Override
    public int getPageTitle() {
        return R.string.vk_photos_fragment__title;
    }

    @Override
    public void onImagesLoadSuccess(List<Image> images) {
        itemCount += images.size();
        images = getValidImage(images);
        if (images.size() == 0&&getAdapter().getItemCount()==0)
            images.add(new Image(Image.TYPE_EMPTY));
        if (getAdapter() != null && getAdapter().getItemCount() > 0) {
            addImages(images);
        } else {
            setImages(images);
        }
    }


    @Override
    public void onImagesLoadError(String error) {
        Snackbar.make(getView(), error, Snackbar.LENGTH_LONG).show();
    }
}
