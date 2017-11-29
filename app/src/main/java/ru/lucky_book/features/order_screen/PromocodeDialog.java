package ru.lucky_book.features.order_screen;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.InputType;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import ru.lucky_book.R;

public class PromocodeDialog extends DialogFragment {

    private static final int RANGE = 6;

    private OnSubmitClickListener mCallback;

    public interface OnSubmitClickListener {
        void onSubmitClick(String code);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSubmitClickListener) {
            mCallback = (OnSubmitClickListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(getContext());
        builder.title(R.string.promo_code_fragment_title);
        builder.inputType(InputType.TYPE_CLASS_NUMBER);
        builder.inputRange(RANGE, RANGE);
        builder.input("", "", new MaterialDialog.InputCallback() {
            @Override
            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                if (mCallback != null) {
                    mCallback.onSubmitClick(dialog.getInputEditText().getText().toString());
                    dialog.dismiss();
                }
            }
        });
        builder.positiveText(R.string.promo_code_fragment_submit);
        builder.negativeText(R.string.promo_code_fragment_cancel);
        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });
        return builder.build();
    }
}
