package ru.lucky_book.features.filters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alexvasilkov.gestures.Settings;
import com.alexvasilkov.gestures.State;
import com.example.luckybookpreview.utils.PictureUtils;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.insta.InstaFilter;
import org.insta.utils.FilterUtils;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ru.lucky_book.R;
import ru.lucky_book.entities.spread.Picture;
import ru.lucky_book.features.base.BaseRecyclerAdapter;
import ru.lucky_book.utils.transformation.CropTransformation;
import ru.lucky_book.utils.transformation.FilterTransformation;

/**
 * Created by histler
 * on 13.09.16 17:20.
 */
/*public class FiltersAdapter extends BaseRecyclerAdapter<Class<? extends InstaFilter>, FilterViewHolder> {
    public static final String TAG = FiltersAdapter.class.getSimpleName();
    private Picture mPicture;
    private int mResultWidth, mResultHeight;
    private float mScale;
    private Map<FilterViewHolder, Target> mTargets = new HashMap<>();

    public FiltersAdapter(Context context, Picture picture) {
        super(FilterUtils.FILTERS);
        mPicture = picture;

        int rowSize = context.getResources().getDimensionPixelSize(R.dimen.preview_height2);
        mScale = (float) mPicture.getViewState().getViewportW() / rowSize;
        mResultWidth = (int) (mPicture.getViewState().getImageW() / mScale);
        mResultHeight = (int) (mPicture.getViewState().getImageH() / mScale);
        int orientation = PictureUtils.getBitmapOrientation(mPicture.getPath());
        boolean isRotated = orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270;
        if (isRotated) {
            int temp = mResultHeight;
            mResultHeight = mResultWidth;
            mResultWidth = temp;
        }
    }

    @Override
    protected int getSelectionMode() {
        return SELECTION_SINGLE;
    }

    @Override
    public FilterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.filter_row, parent, false);
        return new FilterViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(final FilterViewHolder holder, int position) {
        Class<? extends InstaFilter> filterClass = getItem(position);
        Context context = holder.itemView.getContext();
        holder.name.setText(FilterUtils.getFilterName(context, filterClass));
        holder.setSelection(isSelected(position));

        Picasso.with(context).cancelRequest(mTargets.get(holder));

        final ImageView view = holder.image;
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                mTargets.remove(holder);
                /*BitmapDrawable drawable = (BitmapDrawable) view.getDrawable();
                if (drawable != null) {
                    drawable.getBitmap().recycle();
                }*/
/*
                view.setImageBitmap(bitmap);
                State copy = mPicture.getMatrixState().toState();
                float translateX = copy.getX();
                float translateY = copy.getY();
                copy.translateTo(translateX / mScale, translateY / mScale);

            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                mTargets.remove(holder);
                view.setImageDrawable(errorDrawable);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                view.setImageDrawable(placeHolderDrawable);
            }
        };
        mTargets.put(holder, target);
        State copy = mPicture.getMatrixState().toState();
        float translateX = copy.getX();
        float translateY = copy.getY();
        copy.translateTo(translateX / mScale, translateY / mScale);

        Settings settingsCopy=mPicture.getViewState().toSettings();
        settingsCopy.setViewport(settingsCopy.getViewportW(),settingsCopy.getViewportH());
        Picasso.with(context)
                .load(new File(mPicture.getPath()))
                .resize(mResultWidth, mResultHeight)
                .transform(Arrays.asList(new CropTransformation(copy, settingsCopy), new FilterTransformation(context, filterClass)))
                .into(target);
        //Picasso.with(context).load();
        //holder.image.setImageBitmap(FilterUtils.createFiltered(mBitmap,filter));
        /*
        holder.image.setImage(mBitmap);
        holder.image.setFilter(filter);
        holder.image.requestRender();*/
    /*}*/
/*
    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        Context context = recyclerView.getContext();
        Set<FilterViewHolder> keys = mTargets.keySet();
        for (FilterViewHolder key : keys) {
            Picasso.with(context).cancelRequest(mTargets.get(key));
        }
        mTargets.clear();
    }
    /*
        //todo
        LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int from = layoutManager.findFirstVisibleItemPosition();
        int to = layoutManager.findLastVisibleItemPosition();
        for (int i = from; i <= to; i++) {
            FilterViewHolder viewHolder = (FilterViewHolder) recyclerView.findViewHolderForAdapterPosition(i);
            if (viewHolder != null) {
                BitmapDrawable drawable = (BitmapDrawable) viewHolder.image.getDrawable();
                if (drawable != null) {
                    drawable.getBitmap().recycle();
                }
            }
        }
    }*/

/*}*/
