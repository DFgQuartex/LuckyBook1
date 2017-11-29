package ru.lucky_book.utils.transformation;

import android.content.Context;
import android.graphics.Bitmap;

import com.squareup.picasso.Transformation;

import org.insta.InstaFilter;
import org.insta.utils.FilterUtils;

/**
 * Created by histler
 * on 14.09.16 13:24.
 */
public class FilterTransformation implements Transformation {
    private Class<? extends InstaFilter> mFilterClass;
    private Context mContext;

    public FilterTransformation(Context context, Class<? extends InstaFilter> filterClass) {
        this.mFilterClass = filterClass;
        mContext = context.getApplicationContext();
    }

    @Override
    public Bitmap transform(Bitmap source) {
        InstaFilter filter = FilterUtils.filterForClass(mContext, mFilterClass);
        if (filter != null) {
            Bitmap result = FilterUtils.createFiltered(source, filter);
            if (result != source) {
                source.recycle();
            }
            return result;
        }
        return source;
    }

    @Override
    public String key() {
        return mFilterClass.getSimpleName() + " filter transformation";
    }
}
