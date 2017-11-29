package ru.lucky_book.features.spreads;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.List;

import ru.lucky_book.R;
import ru.lucky_book.entities.spread.Spread;
import ru.lucky_book.features.base.BaseRecyclerAdapter;
import ru.lucky_book.utils.SpreadUtils;
import ru.lucky_book.utils.dragndrop.ItemTouchHelperAdapter;

/**
 * Created by histler
 * on 29.08.16 15:33.
 */
public class SpreadsAdapter extends BaseRecyclerAdapter<Spread,SpreadViewHolder> implements ItemTouchHelperAdapter<SpreadViewHolder> {
    private int mWidth;
    private int mHeight;
    public SpreadsAdapter(List<Spread> data,int originalWidth, int originalHeight) {
        super(data);
        mWidth=originalWidth/ SpreadUtils.PREVIEW_SCALE;
        mHeight=originalHeight/ SpreadUtils.PREVIEW_SCALE;
    }

    @Override
    public SpreadViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.spread_preview,parent,false);
        View sizedPreview=view.findViewById(R.id.page_holder);
        ViewGroup.MarginLayoutParams layoutParams=(ViewGroup.MarginLayoutParams)sizedPreview.getLayoutParams();
        layoutParams.width=mWidth;
        layoutParams.height=mHeight;
        sizedPreview.setLayoutParams(layoutParams);
        return new SpreadViewHolder(view,mOnItemClickListener);
    }

    @Override
    public void onBindViewHolder(SpreadViewHolder holder, int position) {
        Spread spread=getItem(position);
        SpreadUtils.bindPage(spread.getLeft(),holder.leftPage,true,null);
        SpreadUtils.bindPage(spread.getRight(),holder.rightPage,true,null);
        holder.setSelection(isSelected(position));
        updateHolderPageNum(holder,position);
    }

    private void updateHolderPageNum(SpreadViewHolder holder, int position) {
        Context context=holder.itemView.getContext();
        holder.pageNum.setText(context.getString(R.string.page_nums,position*2+1,position*2+2));
    }

    @Override
    protected int getSelectionMode() {
        return SELECTION_SINGLE;
    }

    @Override
    public boolean onItemMove(SpreadViewHolder from, SpreadViewHolder to) {
        int fromPosition=from.getAdapterPosition();
        int toPosition=to.getAdapterPosition();
        Collections.swap(mData, fromPosition, toPosition);
        updateHolderPageNum(from,toPosition);
        updateHolderPageNum(to,fromPosition);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {

    }
}
