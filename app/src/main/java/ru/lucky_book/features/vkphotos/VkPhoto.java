package ru.lucky_book.features.vkphotos;

import android.content.Context;
import android.util.Log;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;
import com.vk.sdk.api.model.VKApiPhoto;
import com.vk.sdk.api.model.VKApiPhotoSize;
import com.vk.sdk.api.model.VKPhotoArray;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.bean.Image;
import ru.lucky_book.R;

public class VkPhoto {

    public static void getPhotos(Context context, ImagesLoadListener imagesLoadListener, int offset) {
        VKRequest request = new VKRequest("photos.getAll", VKParameters.from(VKApiConst.OFFSET, offset));

        request.executeWithListener(new VkPhotosLoadRequestListener(context, imagesLoadListener));
    }

    public interface ImagesLoadListener {

        void onImagesLoadSuccess(List<Image> images);

        void onImagesLoadError(String error);
    }

    private static class VkPhotosLoadRequestListener extends VKRequest.VKRequestListener {
        private Context mContext;
        private ImagesLoadListener mImagesLoadListener;

        public VkPhotosLoadRequestListener(Context context, ImagesLoadListener imagesLoadListener) {
            this.mContext = context.getApplicationContext();
            this.mImagesLoadListener = imagesLoadListener;
        }

        @Override
        public void onComplete(VKResponse response) {

            try {
                VKPhotoArray photos = (VKPhotoArray) new VKPhotoArray().parse(response.json);

                //List<VKPhotoItemData> photoItemDatas = VKJSONParser.populatePhotoList(response);
                List<Image> images = new ArrayList<>();
                for (VKApiPhoto photo : photos) {
                    //cause srcs are sorted by width
                    VKApiPhotoSize maxSize = photo.src.get(photo.src.size() - 1);

                    int imageDimen = mContext.getResources().getDimensionPixelOffset(R.dimen.image_size);
                    String thumbnailPath = photo.src.getImageForDimension(imageDimen, imageDimen);
                    if (thumbnailPath == null) {
                        thumbnailPath = maxSize.src;
                    }
                    Image image = new Image(
                            String.format("vk_%s.jpg", photo.id),
                            maxSize.src,
                            thumbnailPath,
                            photo.date,
                            photo.width != 0 ? photo.width : maxSize.width,
                            photo.height != 0 ? photo.height : maxSize.height,
                            false);
                    images.add(image);
                }
                if (mImagesLoadListener != null) {
                    mImagesLoadListener.onImagesLoadSuccess(images);
                }
            } catch (JSONException e) {
                Log.e(VkPhoto.class.getSimpleName(), e.getMessage());
                if (mImagesLoadListener != null) {
                    mImagesLoadListener.onImagesLoadError(e.getMessage());
                }
            }
        }

        @Override
        public void onError(VKError error) {
            if (mImagesLoadListener != null) {
                mImagesLoadListener.onImagesLoadError(error.errorMessage);
            }
        }
    }
}
