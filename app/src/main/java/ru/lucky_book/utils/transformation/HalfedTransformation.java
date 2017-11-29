package ru.lucky_book.utils.transformation;

import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

/**
 * Created by histler
 * on 26.08.16 18:11.
 */
public class HalfedTransformation implements Transformation {

    @Override
    public Bitmap transform(Bitmap source) {
        int width=source.getHeight();
        Bitmap result = Bitmap.createBitmap(source, source.getWidth()-width,0,width, source.getHeight());
        if (result != source) {
            source.recycle();
        }
        return result;
    }

    @Override
    public String key() {
        return "transformation right half of source";
    }
}
