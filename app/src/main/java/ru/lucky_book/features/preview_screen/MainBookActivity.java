package ru.lucky_book.features.preview_screen;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.example.luckybookpreview.R;
import com.example.luckybookpreview.tasks.SaveBitmapsLoader;
import com.example.luckybookpreview.ui.fragments.BookFragment;
import com.example.luckybookpreview.utils.Navigate;

import java.util.ArrayList;
import java.util.List;

import ru.lucky_book.app.MainApplication;
import ru.lucky_book.utils.PageUtils;

public class MainBookActivity extends AppCompatActivity {

    private List<Bitmap> slides;

    public static final String BITMAP_TAG_PREF = MainBookActivity.class.getSimpleName() + "_BITMAP";
    private static final int SAVE_BITMAP_TASK_ID = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        setContentView(R.layout.activity_main_book);
        Navigate.showFragment(getSupportFragmentManager(), BookFragment.newInstance(slides),false);
    }

    private void fullScreen() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fullScreen();
    }

    private void initData() {
        Intent intent = getIntent();
        //List<String> paths = intent.getStringArrayListExtra(BITMAP_TAG_PREF);
        slides = PageUtils.getPreviewBitmaps(intent.getStringExtra(PreviewActivity.EXTRA_ALBUM_ID)); //FileUtil.getBitmapsByStrings(paths);
    }

    public static void setUp(final AppCompatActivity activity, final List<Bitmap> bitmaps, boolean orienataion) {

        LoaderManager manager;
        Loader<List<String>> loader;

        manager = activity.getSupportLoaderManager();
        final ProgressDialog dialog = new ProgressDialog(activity);
        dialog.setMessage(activity.getString(R.string.main_book_activity__create_album_dialog_message));

        loader = manager.restartLoader(SAVE_BITMAP_TASK_ID, null, new LoaderManager.LoaderCallbacks<List<String>>() {
            @Override
            public Loader<List<String>> onCreateLoader(int id, Bundle args) {
                dialog.show();
                return new SaveBitmapsLoader(activity, bitmaps);
            }

            @Override
            public void onLoadFinished(Loader<List<String>> loader, List<String> paths) {
                if (dialog != null) {
                    dialog.dismiss();
                }
                Intent intent;
                intent = new Intent(activity, MainBookActivity.class);
                intent.putStringArrayListExtra(MainBookActivity.BITMAP_TAG_PREF, new ArrayList<String>(paths));
                activity.startActivity(intent);
            }

            @Override
            public void onLoaderReset(Loader<List<String>> loader) {

            }
        });
        loader.forceLoad();
    }
    @Override
    protected void onStart(){
        super.onStart();
        MainApplication.currentScreen = MainApplication.Screen.ЭмуляцияАльбома;
        ((MainApplication)getApplication()).info();
    }
}
