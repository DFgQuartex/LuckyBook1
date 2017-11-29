package ru.lucky_book.network.repository;

import android.os.Bundle;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.google.gson.Gson;

import ru.lucky_book.entities.facebook.PhotoResponse;

public class FacebookRepository {

    private static final int DEFAULT_LIMIT = 20;

    private final Bundle mGetIdsParams;
    private OnLoadListener mListener;

    public interface OnLoadListener<T> {
        void onLoad(T data, String after);
        void onFail();
    }

    private FacebookRepository(OnLoadListener listener, String after) {
        mGetIdsParams = new Bundle();
        mGetIdsParams.putString("type", "uploaded");
        mGetIdsParams.putString("fields", "id,created_time,picture,images,source,width,height");
        mGetIdsParams.putInt("limit", DEFAULT_LIMIT);
        if (after != null) {
            mGetIdsParams.putString("after", after);
        }
        mListener = listener;
    }

    public static FacebookRepository newInstance(OnLoadListener listener, String after) {
        return new FacebookRepository(listener, after);
    }

    public void getPhotos() {
        new GraphRequest(AccessToken.getCurrentAccessToken(), "me/photos", mGetIdsParams, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse response) {
                Gson gson = new Gson();
                PhotoResponse photoResponse = gson.fromJson(response.getJSONObject().toString(), PhotoResponse.class);
                if (mListener != null) {
                    if (photoResponse != null) {
                        if (photoResponse.getPaging() != null && photoResponse.getPaging().getCursors() != null) {
                            mListener.onLoad(photoResponse.getPhotos(), photoResponse.getPaging().getCursors().getAfter());
                        }

                    } else {
                        mListener.onFail();
                    }
                }
            }
        }).executeAsync();
    }
}
