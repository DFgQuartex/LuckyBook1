package ru.lucky_book.utils.transformation;

import android.graphics.Bitmap;
import android.util.Log;

import com.alexvasilkov.gestures.Settings;
import com.alexvasilkov.gestures.State;
import com.alexvasilkov.gestures.internal.CropUtils;
import com.squareup.picasso.Transformation;

/**
 * Created by histler
 * on 30.09.16 14:48.
 */
public class CropTransformation implements Transformation {
    State mState;
    Settings mSettings;

    public CropTransformation(State matrixState, Settings viewState) {
        this.mState = matrixState;
        this.mSettings = viewState;
    }

    @Override
    public Bitmap transform(Bitmap source) {
        try {
            Bitmap result = CropUtils.cropOrig(source, mState, mSettings);
            if (result != source)
                source.recycle();
            return result;
        } catch (Exception e) {
            Log.e(key(), e.getMessage());
            return source;
        }
    }

    @Override
    public String key() {
        return "crop transformation";
    }
}
