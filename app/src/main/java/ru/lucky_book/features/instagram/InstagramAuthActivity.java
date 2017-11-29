package ru.lucky_book.features.instagram;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import im.delight.android.webview.AdvancedWebView;
import ru.lucky_book.R;
import ru.lucky_book.features.base.BaseActivity;
import ru.lucky_book.network.repository.InstagramRepository;

public class InstagramAuthActivity extends BaseActivity implements InstagramRepository.OnLoadListener<String>, AdvancedWebView.Listener {

    public static final String CLIENT_ID = "666982f87f9548418de1c09220bae4c8";
    public static final String CLIENT_SECRET = "88b1e26e1d7c415ebf403848db70c923";
    public static final String REDIRECT_URI = "http://callback.lucky";

    public static final String EXTRA_COOKIES = "cookies_instagram";
    public static final String EXTRA_USERNAME = "username_instagram";
    public static final String EXTRA_TOKEN = "insta_token";
    String mToken;


    private static final String URL_AUTH = String.format("https://www.instagram.com/oauth/authorize/?client_id=%s&redirect_uri=%s&response_type=token", CLIENT_ID, REDIRECT_URI);

    private ViewHolder mViewHolder;
    private String mCookies;
    private String TAG = InstagramAuthActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram_auth);

        createViewHolder();
        populateViewHolder();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mViewHolder.mAuthWebView.onResume();
    }

    @Override
    protected void onPause() {
        mViewHolder.mAuthWebView.onPause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mViewHolder.mAuthWebView.onDestroy();
        super.onDestroy();
    }

    private void createViewHolder() {
        mViewHolder = new ViewHolder();
        mViewHolder.mAuthWebView = (AdvancedWebView) findViewById(R.id.auth);
    }

    private void populateViewHolder() {
        mViewHolder.mAuthWebView.getSettings().setJavaScriptEnabled(true);
        mViewHolder.mAuthWebView.setListener(this, this);
        mViewHolder.mAuthWebView.loadUrl(URL_AUTH);

    }

    @Override
    public void onLoad(String data) {
        if (data != null) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_COOKIES, mCookies);
            intent.putExtra(EXTRA_USERNAME, data);
            setResult(RESULT_OK, intent);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    @Override
    public void onFail() {
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void onPageStarted(String url, Bitmap favicon) {
        if (checkCode(url)) {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_TOKEN, mToken);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void onPageFinished(String url) {

    }

    public boolean checkCode(String url) {
        String[] strings = url.split("#access_token=");
        if (strings != null && strings.length == 2) {
            mToken = strings[1];
            Log.d(TAG, "onPageStarted: " + strings[1]);
            return true;
        }
        return false;
    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
    }

    @Override
    public void onDownloadRequested(String url, String suggestedFilename, String mimeType, long contentLength, String contentDisposition, String userAgent) {
    }

    @Override
    public void onExternalPageRequest(String url) {
    }

    private class ViewHolder {
        AdvancedWebView mAuthWebView;
    }
}
