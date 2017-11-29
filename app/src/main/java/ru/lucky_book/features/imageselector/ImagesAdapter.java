package ru.lucky_book.features.imageselector;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.bean.Image;
import ru.lucky_book.R;
import ru.lucky_book.entities.spread.PageTemplate;
import ru.lucky_book.features.base.BaseRecyclerAdapter;
import ru.lucky_book.features.base.BaseSelectableRecyclerFragment;
import ru.lucky_book.features.base.BaseViewHolder;
import ru.lucky_book.utils.SizeUtils;

/**
 * Created by demafayz on 07.09.16.
 */
public class ImagesAdapter extends BaseRecyclerAdapter<Image, BaseViewHolder> {
    private PageTemplate mPageTemplate;
    private boolean mShowSelectIndicator = true;
    private int mWarningColor;
    private int mErrorColor;
    private SelectedImageChecker selectedImageChecker;

    public ImagesAdapter(Context context, List<Image> images, List<Image> selected, PageTemplate pageTemplate, boolean showSelectIndicator,SelectedImageChecker selectedImageChecker) {
        super(images, selected);
        mPageTemplate = pageTemplate;
        mShowSelectIndicator = showSelectIndicator;
        mWarningColor = ContextCompat.getColor(context, R.color.yellow);
        mErrorColor = ContextCompat.getColor(context, R.color.red);
        this.selectedImageChecker = selectedImageChecker;
    }

    public void setPageTemplate(PageTemplate pageTemplate) {
        this.mPageTemplate = pageTemplate;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == Image.TYPE_IMAGE) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mis_list_item_image, parent, false);
            ImageViewHolder vh = new ImageViewHolder(itemView, mOnItemClickListener, mOnItemLongClickListener);
            return vh;
        } else {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.no_mathching_photos_view, parent, false);
            return new EmptyViewHolder(itemView);
        }
    }

    @Override
    public void setData(List<Image> data) {
        //    clearInvalidImage(data);
        super.setData(data);
    }


    private void clearInvalidImage(List<Image> data) {
        List<Image> images = new ArrayList<>();
        for (Image image : data) {
            PageTemplate perfect = mPageTemplate;
            if (perfect == null) {
                perfect = SizeUtils.getPerfectTemplate(image);
            }
            if (perfect == null) {
                images.add(image);
            } else if (!SizeUtils.imageSizeValid(image, perfect)) {
                images.add(image);
            }
        }
        for (Image image : images) {
            data.remove(image);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holderBase, int position) {
        if (holderBase instanceof ImageViewHolder) {
            ImageViewHolder holder = (ImageViewHolder) holderBase;
            Image item = getItem(position);
            Context context = holder.itemView.getContext();
            RequestCreator creator;
            if (selectedImageChecker.checkImage(item)) {
                holder.shadeView.setVisibility(View.VISIBLE);
            } else {
                holder.shadeView.setVisibility(View.GONE);
            }
            if (item.isLocal) {
                creator = Picasso
                        .with(context)
                        .load(new File(item.path));
            } else {
                creator = Picasso
                        .with(context)
                        .load(item.thumbnailPath != null ? item.thumbnailPath : item.path);
            }
            creator
                    .placeholder(R.drawable.mis_default_error)
                    .tag(BaseSelectableRecyclerFragment.TAG)
                    .resizeDimen(R.dimen.image_size, R.dimen.image_size)
                    .centerCrop()
                    .into(holder.picture);
            if (mShowSelectIndicator) {
                int selectedPosition = getSelected().indexOf(item);
                if (selectedPosition >= 0) {
                    holder.mask.setVisibility(View.VISIBLE);
                    holder.selectIndex.setVisibility(View.VISIBLE);
                    holder.selectIndex.setText(String.valueOf(selectedPosition + 1));
                } else {
                    holder.selectIndex.setVisibility(View.GONE);
                    holder.mask.setVisibility(View.GONE);
                }
            } else {
                holder.mask.setVisibility(View.GONE);
            }
            if (mPageTemplate != null) {
                if (SizeUtils.imageSizeValid(item, mPageTemplate)) {
                    holder.sizeTypeMarker.setVisibility(View.GONE);
                } else {
                    holder.sizeTypeMarker.setVisibility(View.VISIBLE);
                    if (SizeUtils.getPerfectTemplate(item) != null) {
                        holder.sizeTypeMarker.setImageResource(R.drawable.ic_warning_white_24dp);
                        holder.sizeTypeMarker.setColorFilter(mWarningColor);
                    } else {
                   /* holder.sizeTypeMarker.setImageResource(R.drawable.ic_error_white_24dp);
                    holder.sizeTypeMarker.setColorFilter(mErrorColor);*/
                        holder.sizeTypeMarker.setImageResource(R.drawable.ic_warning_white_24dp);
                        holder.sizeTypeMarker.setColorFilter(mWarningColor);
                    }
                }
            } else {
                PageTemplate perfect = SizeUtils.getPerfectTemplate(item);
                if (perfect == PageTemplate.SINGLE) {
                    holder.sizeTypeMarker.setVisibility(View.GONE);
                } else {
                    holder.sizeTypeMarker.setVisibility(View.VISIBLE);
                    if (perfect != null) {
                        holder.sizeTypeMarker.setImageResource(R.drawable.ic_warning_white_24dp);
                        holder.sizeTypeMarker.setColorFilter(mWarningColor);
                    } else {
                 /*   holder.sizeTypeMarker.setImageResource(R.drawable.ic_error_white_24dp);
                    holder.sizeTypeMarker.setColorFilter(mErrorColor);*/
                        holder.sizeTypeMarker.setImageResource(R.drawable.ic_warning_white_24dp);
                        holder.sizeTypeMarker.setColorFilter(mWarningColor);
                    }
                }
            }
        }
    }

    public interface SelectedImageChecker {
        boolean checkImage(Image image);
    }
}
