package ru.lucky_book.features.order_screen;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import ru.lucky_book.app.GreetingActivity;
import ru.lucky_book.R;
import ru.lucky_book.app.MainApplication;
import ru.lucky_book.features.albumcreate.albumlist.AlbumListActivity;
import ru.lucky_book.features.albumcreate.choosecover.CoverChooseActivity;
import ru.lucky_book.utils.AlbumUtils;
import ru.lucky_book.utils.UiUtils;

public class OrderReadyActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String EXTRA_ALBUM_ID = "ru.luckybook.features.order_screen.OrderReadyActivity.extra.EXTRA_ALBUM_ID";

    private ViewHolder mViewHolder;
    private String mAlbumId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        applyTranslucency();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_ready);
        collectData();
        createViewHolder();
        fillViewHolder();
    }

    private void collectData() {
        Intent intent = getIntent();
        if (intent != null) {
            mAlbumId = intent.getStringExtra(EXTRA_ALBUM_ID);
        }
    }

    private void fillViewHolder() {
        if (mAlbumId != null) {
            Picasso.with(this)
                    .load(AlbumUtils.getAlbumFirstCover(mAlbumId))
                    .fit()
                    .centerInside()
                    .into(mViewHolder.mCoverImageView);
        }
        mViewHolder.mFeedbackTextView.setText(Html.fromHtml(getString(R.string.feedback_info)));

        mViewHolder.mFeedbackTextView.setOnClickListener(this);
        findViewById(R.id.new_album).setOnClickListener(this);
        findViewById(R.id.to_album_list).setOnClickListener(this);
    }

    private void createViewHolder() {
        mViewHolder = new ViewHolder();
        mViewHolder.mCoverImageView = (ImageView) findViewById(R.id.ivCover);
        mViewHolder.mFeedbackTextView = (TextView) findViewById(R.id.feedback);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.new_album:
                goTo(CoverChooseActivity.class);
                break;
            case R.id.to_album_list:
                goTo(AlbumListActivity.class);
                break;
            case R.id.feedback:
                UiUtils.showEmail(this);
                break;
        }
    }

    private void goTo(Class<? extends Activity> clazz) {
        Intent intent = new Intent(this, GreetingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(GreetingActivity.EXTRA_GO_TO,clazz.getName());
        startActivity(intent);
        finish();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private void applyTranslucency() {
        if (UiUtils.isTranslucencyAvailable(getResources())) {
            UiUtils.setTranslucent(getWindow());
        }
    }

    private class ViewHolder {
        ImageView mCoverImageView;
        TextView mFeedbackTextView;
    }
    @Override
    protected void onStart(){
        super.onStart();
        MainApplication.currentScreen = MainApplication.Screen.ЗаказСделан;
        ((MainApplication)getApplication()).info();
    }
}
