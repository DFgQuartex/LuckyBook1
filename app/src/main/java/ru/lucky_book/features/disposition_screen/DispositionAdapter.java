package ru.lucky_book.features.disposition_screen;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import ru.lucky_book.R;
import ru.lucky_book.entities.spread.Page;
import ru.lucky_book.features.base.BaseRecyclerAdapter;
import ru.lucky_book.utils.SpreadUtils;
import ru.lucky_book.utils.dragndrop.ItemTouchHelperAdapter;

/**
 * Created by Загит Талипов on 10.11.2016.
 */

public class DispositionAdapter extends BaseRecyclerAdapter<Page, PageViewHolder> implements ItemTouchHelperAdapter<PageViewHolder> {
    private int mHeight;
    private int mWidth;
    private PageViewHolder[] mArrayPage;

    public DispositionAdapter(List<Page> data, int originalWidth, int originalHeight) {
        super(data);
        mWidth = originalWidth / SpreadUtils.PREVIEW_SCALE;
        mHeight = originalHeight / SpreadUtils.PREVIEW_SCALE;
        mArrayPage = new PageViewHolder[data.size()];
    }

    public DispositionAdapter(List<Page> data, List<Page> selected) {
        super(data, selected);

    }

    @Override
    public PageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.page_preveiw, parent, false);
        View sizedPreview = view.findViewById(R.id.page);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) sizedPreview.getLayoutParams();
        layoutParams.width = mWidth;
        layoutParams.height = mHeight;
        sizedPreview.setLayoutParams(layoutParams);
        return new PageViewHolder(view, mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(PageViewHolder holder, int position) {
        mArrayPage[position] = holder;
        Page page = getItem(position);
        SpreadUtils.bindPage(page, holder.page, true, null,R.drawable.template_img2);
        updateHolderPageNum(holder, position);
    }

    @Override
    protected int getSelectionMode() {
        return SELECTION_SINGLE;
    }

    @Override
    public boolean onItemMove(PageViewHolder from, PageViewHolder to) {
        int fromPosition = from.getAdapterPosition();
        int toPosition = to.getAdapterPosition();
        swap(fromPosition, toPosition);
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i <= toPosition; i++) {
                if (i != toPosition)
                    Collections.swap(mData, i, i + 1);
                mArrayPage[i].pagesNum.setText(String.valueOf(i + 1));
            }
        } else {
            for (int i = fromPosition; i >= toPosition; i--) {
                if (i != toPosition)
                    Collections.swap(mData, i, i - 1);
                mArrayPage[i].pagesNum.setText(String.valueOf(i + 1));
            }
        }

        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    private void swap(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                PageViewHolder temp = mArrayPage[i];
                mArrayPage[i] = mArrayPage[i + 1];
                mArrayPage[i + 1] = temp;
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                PageViewHolder temp = mArrayPage[i];
                mArrayPage[i] = mArrayPage[i - 1];
                mArrayPage[i - 1] = temp;
            }
        }
    }

    private void updateHolderPageNum(PageViewHolder holder, int position) {
        Context context = holder.itemView.getContext();
        holder.pagesNum.setText(String.valueOf(position + 1));
    }

    @Override
    public void onItemDismiss(int position) {

    }
}
