package ru.lucky_book.features.preview_screen;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.lucky_book.R;

public class ViewHolderInfo {
    @BindView(R.id.about_payment_and_delivery)
    TextView mAboutPaymentAndDelivery;
    Activity mActivity;

    ViewHolderInfo(@NonNull View view, @NonNull Activity activity) {
        ButterKnife.bind(this, view);
        mActivity = activity;
    }

    @OnClick({R.id.about_payment_and_delivery})
    public void onClick(View view) {
        String link = null;
        switch (view.getId()) {
            case R.id.about_payment_and_delivery:
                link = "http://Luckybook.ru/offer";
                break;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(link));
        mActivity.startActivity(i);
    }
}
