package ru.lucky_book.features.order_screen;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import com.github.pinball83.maskededittext.MaskedEditText;

import ru.lucky_book.R;
import ru.lucky_book.app.Preferences;

public class PreferencesSaveListener implements View.OnFocusChangeListener {

    private String mKey;
    private Context mContext;

    public PreferencesSaveListener(Context context, String key) {
        mContext = context;
        mKey = key;
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            if (v instanceof MaskedEditText) {
                MaskedEditText editText = (MaskedEditText) v;
                Preferences.OrderData.setProperty(mContext, mKey, TextUtils.isEmpty(editText.getUnmaskedText()) ? null : editText.getUnmaskedText().toString());
            } else if (v instanceof EditText) {
                EditText editText = (EditText) v;
                Preferences.OrderData.setProperty(mContext, mKey, TextUtils.isEmpty(editText.getText()) ? null : editText.getText().toString());
            }
        } else {
            v.getBackground().setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        }
    }
}
