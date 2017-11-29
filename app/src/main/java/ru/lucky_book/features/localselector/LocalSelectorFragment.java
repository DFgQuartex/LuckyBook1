package ru.lucky_book.features.localselector;

import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.ListPopupWindow;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.adapter.FolderAdapter;
import me.nereo.multi_image_selector.bean.Folder;
import me.nereo.multi_image_selector.bean.Image;
import me.nereo.multi_image_selector.utils.ScreenUtils;
import ru.lucky_book.R;
import ru.lucky_book.features.base.BaseSelectableRecyclerFragment;
import ru.lucky_book.utils.SizeUtils;

/**
 * Created by demafayz on 14.09.16.
 */
public class LocalSelectorFragment extends BaseSelectableRecyclerFragment implements View.OnClickListener {

    private static final int LOADER_ALL = 0;
    private static final int LOADER_CATEGORY = 1;

    private ViewHolder mViewHolder;
    private ListPopupWindow mFolderPopupWindow;
    private FolderAdapter mFolderAdapter;
    private boolean hasFolderGened = false;

    private ArrayList<Folder> mResultFolder = new ArrayList<>();

    private class ViewHolder {
        public View popupAnchorView;
        public Button categoryButton;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getSupportLoaderManager().initLoader(LOADER_ALL, null, loaderCallback);
    }

    @Override
    public int getIcon() {
        return 0;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_local_picture_selector, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateViewHolder(view);
        populateFooter();
    }

    private void populateFooter() {
        mViewHolder.categoryButton.setText(R.string.mis_folder_all);
        mViewHolder.categoryButton.setOnClickListener(this);
        mFolderAdapter = new FolderAdapter(getActivity());
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.category_btn:
                actionFoldersPopup();
                break;
        }
    }

    private void actionFoldersPopup() {
        if (mFolderPopupWindow == null) {
            createPopupFolderList();
        }

        if (mFolderPopupWindow == null) {
            mFolderPopupWindow.dismiss();
        } else {
            mFolderPopupWindow.show();
            int index = mFolderAdapter.getSelectIndex();
            index = index == 0 ? index : index - 1;
            mFolderPopupWindow.getListView().setSelection(index);
        }
    }

    private void createPopupFolderList() {
        Point point = ScreenUtils.getScreenSize(getActivity());
        int width = point.x;
        int height = (int) (point.y * (4.5f / 8.0f));
        mFolderPopupWindow = new ListPopupWindow(getActivity());
        mFolderPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        mFolderPopupWindow.setAdapter(mFolderAdapter);
        mFolderPopupWindow.setContentWidth(width);
        mFolderPopupWindow.setWidth(width);
        mFolderPopupWindow.setHeight(height);
        mFolderPopupWindow.setAnchorView(mViewHolder.popupAnchorView);
        mFolderPopupWindow.setModal(true);
        mFolderPopupWindow.setOnItemClickListener((adapterView, view, i, l) -> {

            mFolderAdapter.setSelectIndex(i);

            final int index = i;
            final AdapterView v = adapterView;
            if (hasFolderGened) {
                Folder folder = (Folder) v.getAdapter().getItem(index);

                if (null != folder) {
                    setImages(folder.images);
                    mViewHolder.categoryButton.setText(folder.name);
                    getRecyclerView().smoothScrollToPosition(0);
                    mFolderPopupWindow.dismiss();
                }
            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mFolderPopupWindow.dismiss();

                        if (index == 0) {
                            getActivity().getSupportLoaderManager().restartLoader(LOADER_ALL, null, loaderCallback);
                            mViewHolder.categoryButton.setText(R.string.mis_folder_all);
                        } else {
                            Folder folder = (Folder) v.getAdapter().getItem(index);

                            if (null != folder) {
                                mViewHolder.categoryButton.setText(folder.name);
                                setImages(folder.images);
                            }
                        }

                        getRecyclerView().smoothScrollToPosition(0);
                    }
                }, 100);
            }
        });
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (mFolderPopupWindow != null) {
            if (mFolderPopupWindow.isShowing()) {
                mFolderPopupWindow.dismiss();
            }
        }
        super.onConfigurationChanged(newConfig);
    }

    private LoaderManager.LoaderCallbacks<Cursor> loaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

        private final String[] IMAGE_PROJECTION = {
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.MIME_TYPE,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.WIDTH,
                MediaStore.Images.Media.HEIGHT
        };

        @Override
        public Loader<Cursor> onCreateLoader(int id, Bundle args) {
            CursorLoader cursorLoader = null;
            if (id == LOADER_ALL) {
                cursorLoader = new CursorLoader(getActivity(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        IMAGE_PROJECTION[4] + ">0" +
                                " AND (" + IMAGE_PROJECTION[3] + "=?" +
                                " OR " + IMAGE_PROJECTION[3] + "=?" +
                                " OR " + IMAGE_PROJECTION[3] + "=? )",
                        new String[]{"image/jpeg", "image/png", "image/jpg"}, IMAGE_PROJECTION[2] + " DESC");
            } else if (id == LOADER_CATEGORY) {
                cursorLoader = new CursorLoader(getActivity(),
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_PROJECTION,
                        IMAGE_PROJECTION[4] + ">0 AND " + IMAGE_PROJECTION[0] + " like '%" + args.getString("path") + "%'",
                        null, IMAGE_PROJECTION[2] + " DESC");
            }
            return cursorLoader;
        }

        private boolean fileExist(String path) {
            if (!TextUtils.isEmpty(path)) {
                return new File(path).exists();
            }
            return false;
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
            if (data != null) {
                if (data.getCount() > 0) {
                    List<Image> images = new ArrayList<>();
                    data.moveToFirst();
                    do {
                        String path = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
                        String name = data.getString(data.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
                        long dateTime = data.getLong(data.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
                        int width = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[6]));
                        int height = data.getInt(data.getColumnIndexOrThrow(IMAGE_PROJECTION[7]));
                        if (!fileExist(path)) {
                            continue;
                        }
                        Image image = null;
                        if (!TextUtils.isEmpty(name)) {
                            image = new Image(name, path, null, dateTime, width, height, true);
                            /*if we have template, and image is valid
                            or if we don't have any template, and image valid for any template,
                            then we add it to Valid images*/
                            if ((getPageTemplate() != null
                                    && SizeUtils.imageSizeValid(image, getPageTemplate()))) {
                                images.add(image);
                            }
                        }
                        if (!hasFolderGened) {
                            // get all folder data
                            File folderFile = new File(path).getParentFile();
                            if (folderFile != null && folderFile.exists()) {
                                String fp = folderFile.getAbsolutePath();
                                Folder f = getFolderByPath(fp);
                                if (f == null) {
                                    Folder folder = new Folder();
                                    folder.name = folderFile.getName();
                                    folder.path = fp;
                                    folder.cover = image;
                                    List<Image> imageList = new ArrayList<>();
                                    imageList.add(image);
                                    folder.images = imageList;
                                    mResultFolder.add(folder);
                                } else {
                                    f.images.add(image);
                                }
                            }
                        }

                    } while (data.moveToNext());
                    if (!hasFolderGened) {
                        Folder allFolders = new Folder();
                        allFolders.name = getString(R.string.mis_folder_all);
                        allFolders.cover = images.isEmpty() ? null : images.get(0);
                        mResultFolder.add(0, allFolders);
                        allFolders.images = images;
                    }
                    setImages(images);
                    if (!hasFolderGened) {
                        mFolderAdapter.setData(mResultFolder);
                        hasFolderGened = true;
                    }
                }
            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }
    };

    private Folder getFolderByPath(String path) {
        if (mResultFolder != null) {
            for (Folder folder : mResultFolder) {
                if (TextUtils.equals(folder.path, path)) {
                    return folder;
                }
            }
        }
        return null;
    }

    private void populateViewHolder(View view) {
        mViewHolder = new ViewHolder();
        mViewHolder.popupAnchorView = view.findViewById(R.id.footer);
        mViewHolder.categoryButton = (Button) view.findViewById(R.id.category_btn);
    }

    @Override
    protected void onLoadMore(int currentPage) {

    }

    @Override
    public int getPageTitle() {
        return R.string.local_selector_fragment__title;
    }
}
