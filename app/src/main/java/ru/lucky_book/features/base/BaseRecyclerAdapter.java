package ru.lucky_book.features.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.lucky_book.features.base.listener.OnItemClickListener;
import ru.lucky_book.features.base.listener.OnItemLongClickListener;
import ru.lucky_book.widget.EmptyRecyclerView;

/**
 * Created by histler
 * on 29.08.16 15:42.
 */
public abstract class BaseRecyclerAdapter<T, R extends BaseViewHolder> extends RecyclerView.Adapter<R> {
    public static final int SELECTION_SINGLE = 0;
    public static final int SELECTION_MULTIPLE = 1;
    @Nullable
    EmptyRecyclerView mEmptyRecyclerView;
    protected List<T> mData;
    protected List<T> mSelected;
    protected OnItemClickListener mOnItemClickListener;
    protected OnItemLongClickListener mOnItemLongClickListener;

    protected int getSelectionMode() {
        return SELECTION_MULTIPLE;
    }

    public BaseRecyclerAdapter(List<T> data) {
        this(data, null);
    }

    public BaseRecyclerAdapter(List<T> data, List<T> selected) {
        initData(data, selected);
    }

    private void initData(List<T> data, List<T> selected) {
        mData = data != null ? data : new ArrayList<T>();
        initSelection(selected);
    }

    public void refreshRecycler() {
        notifyDataSetChanged();
        if (mEmptyRecyclerView != null)
            mEmptyRecyclerView.checkIfEmpty();
    }

    public void attachToRecyclerView(@NonNull EmptyRecyclerView recyclerView) {
        mEmptyRecyclerView = recyclerView;
        mEmptyRecyclerView.setAdapter(this);
        refreshRecycler();
    }

    public void setData(List<T> data) {
        initData(data, mSelected);
        notifyDataSetChanged();
    }

    public void addItem(T item) {
        int position = getItemCount();
        addItem(position, item);
        refreshRecycler();
    }

    public void addItem(int position, T item) {
        mData.add(position, item);
        notifyItemInserted(position);
        refreshRecycler();
    }

    public void addAll(List<T> items) {
        int maxPosition = mData.size();
        mData.addAll(items);
        notifyItemRangeInserted(maxPosition, items.size());
        refreshRecycler();
    }

    public void removeItem(int position) {
        T deletedItem = mData.remove(position);
        if (deletedItem != null) {
            mSelected.remove(deletedItem);
        }
        notifyItemRemoved(position);
        refreshRecycler();
    }

    public void removeItem(T item) {
        int position = mData.indexOf(item);
        if (position >= 0) {
            removeItem(position);
        }
        refreshRecycler();
    }

    public void removeItems(List<Integer> positions) {
        Collections.sort(positions, new Comparator<Integer>() {
            @Override
            public int compare(Integer lhs, Integer rhs) {
                return rhs - lhs;
            }
        });
        while (!positions.isEmpty()) {
            if (positions.size() == 1) {
                removeItem(positions.get(0));
                positions.remove(0);
            } else {
                int count = 1;
                while (positions.size() > count && positions.get(count).equals(positions.get(count - 1) - 1)) {
                    ++count;
                }

                if (count == 1) {
                    removeItem(positions.get(0));
                } else {
                    removeItemRange(positions.get(count - 1), count);
                }

                for (int i = 0; i < count; ++i) {
                    positions.remove(0);
                }
            }
        }

        refreshRecycler();
    }

    public void removeItemRange(int positionStart, int itemCount) {
        for (int i = 0; i < itemCount; i++) {
            T deletedItem = mData.remove(positionStart);
            if (deletedItem != null) {
                mSelected.remove(deletedItem);
            }
        }
        notifyItemRangeRemoved(positionStart, itemCount);
        refreshRecycler();
    }

    public void setOnItemClickListener(OnItemClickListener mListener) {
        this.mOnItemClickListener = mListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.mOnItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public T getItem(int position) {
        return mData.get(position);
    }

    public List<T> getItems() {
        return mData;
    }

    public boolean isSelected(int position) {
        T item = getItem(position);
        return mSelected.contains(item);
    }

    public boolean isSelected(T item) {
        return mSelected.contains(item);
    }

    public void initSelection(List<T> items) {
        mSelected = items != null ? items : new ArrayList<T>();
    }

    public void setSelection(List<T> items) {
        mSelected.clear();
        if (items != null && !items.isEmpty()) {
            for (T item : items) {
                mSelected.add(item);
            }
        }
        notifyDataSetChanged();
    }

    public void setSelection(T item, boolean isSelected) {
        if (isSelected) {
            if (!mSelected.contains(item)) {
                if (getSelectionMode() == SELECTION_SINGLE) {
                    for (Object selected : mSelected) {
                        mSelected.remove(selected);
                        notifyItemChanged(mData.indexOf(selected));
                    }
                }
                mSelected.add(item);
                notifyItemChanged(mData.indexOf(item));
            }
        } else {
            if (mSelected.contains(item)) {
                mSelected.remove(item);
                notifyItemChanged(mData.indexOf(item));
            }
        }
    }

    public void setSelection(RecyclerView recyclerView, T item, boolean isSelected) {
        if (isSelected) {
            if (!mSelected.contains(item)) {
                if (getSelectionMode() == SELECTION_SINGLE) {
                    for (Object selected : mSelected) {
                        mSelected.remove(selected);
                        int index = mData.indexOf(selected);
                        if (recyclerView != null) {
                            R viewHolder = (R) recyclerView.findViewHolderForAdapterPosition(index);
                            if (viewHolder != null) {
                                viewHolder.setSelection(false);
                            } else {
                                notifyItemChanged(index);
                            }
                        } else {
                            notifyItemChanged(index);
                        }
                    }
                }
                mSelected.add(item);
            }
        } else {
            mSelected.remove(item);
        }
        int position = mData.indexOf(item);
        if (recyclerView != null) {
            R viewHolder = (R) recyclerView.findViewHolderForAdapterPosition(position);
            if (viewHolder != null) {
                viewHolder.setSelection(isSelected);
                return;
            }
        }
        notifyItemChanged(position);
    }

    public void setSelection(RecyclerView recyclerView, int position, boolean isSelected) {
        T item = getItem(position);
        if (isSelected) {
            if (!mSelected.contains(item)) {
                if (getSelectionMode() == SELECTION_SINGLE) {
                    for (Object selected : mSelected) {
                        mSelected.remove(selected);
                        int index = mData.indexOf(selected);
                        if (recyclerView != null) {
                            R viewHolder = (R) recyclerView.findViewHolderForAdapterPosition(index);
                            if (viewHolder != null) {
                                viewHolder.setSelection(false);
                            } else {
                                notifyItemChanged(index);
                            }
                        } else {
                            notifyItemChanged(index);
                        }
                    }
                }
                mSelected.add(item);
            }
        } else {
            mSelected.remove(item);
        }
        if (recyclerView != null) {
            R viewHolder = (R) recyclerView.findViewHolderForAdapterPosition(position);
            if (viewHolder != null) {
                viewHolder.setSelection(isSelected);
                return;
            }
        }
        notifyItemChanged(position);
    }

    public void setSelection(int position, boolean isSelected) {
        setSelection(null, position, isSelected);
    }

    public void setAllSelection(boolean isSelected) {
        if (!isSelected) {
            if (!mSelected.isEmpty()) {
                mSelected.clear();
                notifyDataSetChanged();
            }
        } else {
            for (int i = 0, size = getItemCount(); i < size; i++) {
                T item = getItem(i);
                if (!mSelected.contains(item)) {
                    mSelected.add(item);
                    notifyItemChanged(i);
                }
            }
        }
    }

    public List<T> getSelected() {
        /*List<T> selected=new ArrayList<>();
        for(int i=0,size=getItemCount();i<size;i++){
            T item=getItem(i);
            if(mSelected.contains(item)){
                selected.add(item);
            }
        }
        return selected;*/
        return mSelected;
    }

    public List<Integer> getSelectedPositions() {
        List<Integer> selectedPositions = new ArrayList<>();
        for (int i = 0, size = getItemCount(); i < size; i++) {
            T item = getItem(i);
            if (mSelected.contains(item)) {
                selectedPositions.add(i);
            }
        }
        return selectedPositions;
    }

    public int getSelectedCount() {
        return mSelected.size();
    }

}
