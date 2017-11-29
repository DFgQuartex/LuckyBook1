package ru.lucky_book.features.base;

import android.support.v4.app.Fragment;

/**
 * Created by demafayz on 25.08.16.
 */
public abstract class BaseFragment extends Fragment {

    @Override
    public void onStart() {
        super.onStart();
        setTitle();
    }

    public void setTitle() {
        String title = getFragmentTitle();
        if (title != null) {
            getActivity().setTitle(title);
        }
    }

    public abstract String getFragmentTitle();


}
