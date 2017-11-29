package ru.lucky_book.features.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.bean.Image;
import ru.lucky_book.R;
import ru.lucky_book.entities.spread.PageTemplate;
import ru.lucky_book.features.base.listener.OnItemClickListener;
import ru.lucky_book.features.imageselector.ImagesAdapter;
import ru.lucky_book.features.imageselector.SelectImagesActivity;
import ru.lucky_book.features.vkphotos.EndlessRecyclerOnScrollListener;
import ru.lucky_book.utils.SizeUtils;

/**
 * Created by demafayz on 07.09.16.
 */
public abstract class BaseSelectableRecyclerFragment extends BaseFragment implements OnItemClickListener {

    public static final String TAG = BaseSelectableRecyclerFragment.class.getSimpleName();
    public RecyclerView mRecyclerView;
    private ImagesAdapter mAdapter;
    private PageTemplate mTemplate;
    private PageTemplate mSoftTemplate;

    public int getMode() {
        return mMode;
    }

    private int mMode;

    public abstract int getIcon();

    private SelectionListener mSelectionListener;

    public List<Image> getValidImage(List<Image> images) {
        if (getMode() == SelectImagesActivity.MODE_MULTIPLE)
            return images;
        else {
            List<Image> imageList = new ArrayList<>();
            for (Image image : images) {
                if (SizeUtils.imageSizeValid(image, getPageTemplate()))
                    imageList.add(image);
            }
            return imageList;
        }

    }

    public static <T extends BaseSelectableRecyclerFragment> T newInstance(Class<T> fragmentClass, PageTemplate template, int mode) {
        T fragment = null;
        try {
            fragment = fragmentClass.newInstance();
            Bundle bundle = new Bundle();
            bundle.putSerializable(SelectImagesActivity.EXTRA_PAGE_TEMPLATE, template);
            bundle.putInt(SelectImagesActivity.EXTRA_SELECT_MODE, mode);
            fragment.setArguments(bundle);
        } catch (java.lang.InstantiationException | IllegalAccessException e) {
            Log.e(TAG, e.getMessage());
        }
        return fragment;
    }

    protected ImagesAdapter getAdapter() {
        return mAdapter;
    }

    protected RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    protected PageTemplate getPageTemplate() {
        return mTemplate;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mSelectionListener = (SelectionListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException("The Activity must implement interface " + SelectionListener.class.getSimpleName());
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_picture_selector, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mTemplate = (PageTemplate) getArguments().get(SelectImagesActivity.EXTRA_PAGE_TEMPLATE);
        mMode = getArguments().getInt(SelectImagesActivity.EXTRA_SELECT_MODE);
        createViewHolder(view);
        populateViewHolder();
        initRecyclerView();
    }

    private void setNewAdapter() {
        mAdapter = new ImagesAdapter(getContext(), null, mSelectionListener.getSelected(), mSoftTemplate, true/*mode=MULTI*/,(ImagesAdapter.SelectedImageChecker) getActivity());
        mAdapter.setOnItemClickListener(this);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemClick(View view, int position) {
        Image image = mAdapter.getItem(position);
        if (mAdapter.isSelected(image)) {
            //we can unselect only the last one
            List<Image> selected = mAdapter.getSelected();
            mAdapter.setSelection(position, false);
            mSelectionListener.onImageUnselected(image);

        } else {
            if (!mSelectionListener.canSelectMore(image)) {
                return;
            }
            //check template
            /*if ((mTemplate != null
                    && SizeUtils.imageSizeValid(image, mTemplate))
                    || (SizeUtils.getPerfectTemplate(image) != null)) {*/
            mAdapter.setSelection(position, true);
            mSelectionListener.onImageSelected(image);
            mSelectionListener.checkLastSelected();
            /*} else {
                ContextUtils.showNotification(getContext(), getString(R.string.multi_image_selector_fragment__short_image));
            }*/
        }
    }

    public void setSoftPageTemplate(PageTemplate pageTemplate) {
        if (mSoftTemplate != pageTemplate) {
            mSoftTemplate = pageTemplate;
            if (isAdded()) {
                if (mAdapter == null) {
                    setNewAdapter();
                } else {
                    mAdapter.setPageTemplate(mSoftTemplate);
                }
            }
        }
    }

    protected abstract void onLoadMore(int currentPage);

    protected void addImages(List<Image> images) {
        if (mAdapter == null) {
            setNewAdapter();
        }
        mAdapter.addAll(images);
    }

    protected void setImages(List<Image> images) {
        if (mAdapter == null) {
            setNewAdapter();
        }
        mAdapter.setData(images);
    }

    public void setSelected(List<Image> selectedImages) {
        if (mAdapter == null) {
            setNewAdapter();
        }
        mAdapter.setSelection(selectedImages);
    }

    public void notifySelectionChanged() {
        if (mAdapter == null) {
            setNewAdapter();
        }
        mAdapter.notifyDataSetChanged();
    }

    protected void createViewHolder(View view) {
    }

    @Override
    public String getFragmentTitle() {
        return null;
    }

    @StringRes
    public abstract int getPageTitle();

    protected void populateViewHolder() {

    }

    private void initRecyclerView() {
        GridLayoutManager manager = new GridLayoutManager(getContext(), 3);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mAdapter.getItem(position).getType() == Image.TYPE_EMPTY)
                    return 3;
                else
                    return 1;
            }
        });
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(manager) {
            @Override
            public void onLoadMore(int current_page) {
                BaseSelectableRecyclerFragment.this.onLoadMore(current_page);
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    Picasso.with(recyclerView.getContext()).pauseTag(TAG);
                } else {
                    Picasso.with(recyclerView.getContext()).resumeTag(TAG);
                }
            }
        });
        setNewAdapter();
    }

    public interface SelectionListener {
        void onImageSelected(Image image);

        void onImageUnselected(Image image);

        List<Image> getSelected();

        boolean canSelectMore(Image image);

        void checkLastSelected();
    }
}
