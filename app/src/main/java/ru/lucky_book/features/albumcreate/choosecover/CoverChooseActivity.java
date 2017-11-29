package ru.lucky_book.features.albumcreate.choosecover;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.example.luckybookpreview.utils.Navigate;

import ru.lucky_book.R;
import ru.lucky_book.app.MainApplication;
import ru.lucky_book.features.albumcreate.choosecover.choosecover.ChooseCoverFragment;
import ru.lucky_book.features.base.BaseActivity;
import ru.lucky_book.utils.ContextUtils;

/**
 * Created by demafayz on 25.08.16.
 */
public class CoverChooseActivity extends BaseActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_album);
        initToolbar();
        Navigate.showFragment(getSupportFragmentManager(), ChooseCoverFragment.newInstance(), false);
    }

    private void initToolbar() {
        ContextUtils.setToolBar(this, (Toolbar) findViewById(R.id.toolbar));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return false;
    }
    @Override
    protected void onStart() {
        super.onStart();
        MainApplication.currentScreen = MainApplication.Screen.ВыборОбложки;
    }
}
