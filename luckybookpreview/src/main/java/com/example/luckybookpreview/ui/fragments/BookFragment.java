package com.example.luckybookpreview.ui.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.luckybookpreview.R;
import com.example.luckybookpreview.ui.fragments.base.BaseFragment;
import com.example.luckybookpreview.ui.provider.AlbumPageProvider;
import com.example.luckybookpreview.ui.views.CurlView;
import com.example.luckybookpreview.utils.SizeChangedObserver;

import java.util.List;

/**
 * Created by DemaFayz on 29.06.2016.
 */
public class BookFragment extends BaseFragment {

    private ViewHolder vh;
    private List<Bitmap> slides;

    private class ViewHolder {
        public CurlView cvBook;
    }

    @Override
    protected String initTag() {
        return BookFragment.class.getSimpleName();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.fragment_curl, container, false);
        return layout;
    }

    @Override
    public void onViewCreated(View layout, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(layout, savedInstanceState);
        populateViewHolder(layout);
        populateCurl();

    }

    private void populateViewHolder(View layout) {
        vh = new ViewHolder();
        vh.cvBook = (CurlView) layout.findViewById(R.id.cvBook);
    }

    private void populateCurl() {
        /*int index = 0;
        if (getActivity().getLastNonConfigurationInstance() != null) {
            index = (Integer) getActivity().getLastNonConfigurationInstance();
        }*/

        vh.cvBook.setPageProvider(new AlbumPageProvider(getContext(), slides));
        vh.cvBook.setSizeChangedObserver(new SizeChangedObserver(getActivity(), 491, 551));
       // vh.cvBook.setSizeChangedObserver(new SizeChangedObserver(getActivity(), 500, 500));
        vh.cvBook.setCurrentIndex(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        vh.cvBook.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        vh.cvBook.onResume();
    }

    public void setSlides(List<Bitmap> slides) {
        this.slides = slides;
    }

    public static BookFragment newInstance(List<Bitmap> slides) {
        BookFragment fragment = new BookFragment();
        fragment.setSlides(slides);
        return fragment;
    }
}

