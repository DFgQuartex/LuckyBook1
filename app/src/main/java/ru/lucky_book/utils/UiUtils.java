package ru.lucky_book.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.victor.loading.book.BookLoading;

import ru.lucky_book.R;

/**
 * Created by histler
 * on 23.08.16 16:24.
 */
public final class UiUtils {
    private static final String CONFIG_ENABLE_TRANSLUCENT_DECOR = "config_enableTranslucentDecor";

    public interface OnDeleteAlbumDialogButtonClickListener {
        void onYesButtonClick();
    }

    /**
     * Checks if translucency is available on the device.
     *
     * @param resources The Resources
     * @return True, if translucency is available.
     */
    public static boolean isTranslucencyAvailable(@NonNull Resources resources) {
        int id = resources.getIdentifier(CONFIG_ENABLE_TRANSLUCENT_DECOR, "bool", "android");

        return id != 0 && resources.getBoolean(id);
    }

    public static void setTranslucent(Window window) {
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    public static MaterialDialog showProgress(Context context) {
        return showProgress(context, R.string.progress_content);
    }

    public static MaterialDialog showProgress(Context context, @StringRes int message, DialogInterface.OnKeyListener onCancelListener) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        View customView = LayoutInflater.from(context).inflate(R.layout.loading_dialog, null);
        TextView messageView = (TextView) customView.findViewById(android.R.id.text1);
        final BookLoading bookLoading = (BookLoading) customView.findViewById(R.id.book_loading);
        messageView.setText(message);
        builder.customView(customView, false);
        //builder.title(R.string.progress_title);
        //builder.content(message);
        //builder.progress(true, 0);
        builder.cancelable(false);
        builder.keyListener(onCancelListener);
        MaterialDialog dialog = builder.build();
        dialog.setOnShowListener(dialog1 -> {
            if (!bookLoading.isStart()) {
                bookLoading.start();
            }
        });
        dialog.setOnDismissListener(dialog12 -> {
            if (bookLoading.isStart()) {
                bookLoading.stop();
            }
        });
        dialog.show();
        return dialog;
    }

    public static MaterialDialog showProgress(Context context, @StringRes int message) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        View customView = LayoutInflater.from(context).inflate(R.layout.loading_dialog, null);
        TextView messageView = (TextView) customView.findViewById(android.R.id.text1);
        final BookLoading bookLoading = (BookLoading) customView.findViewById(R.id.book_loading);
        messageView.setText(message);
        builder.customView(customView, false);
        //builder.title(R.string.progress_title);
        //builder.content(message);
        //builder.progress(true, 0);
        builder.cancelable(false);
        MaterialDialog dialog = builder.build();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                if (!bookLoading.isStart()) {
                    bookLoading.start();
                }
            }
        });
        dialog.setOnDismissListener(dialog1 -> {
            if (bookLoading.isStart()) {
                bookLoading.stop();
            }
        });
        dialog.show();
        return dialog;
    }

    public static MaterialDialog showLoadingDialog(Context context, @StringRes int title, @StringRes int message) {
        MaterialDialog materialDialog = new MaterialDialog.
                Builder(context)
                .title(title)
                .content(message)
                .cancelable(false)
                .build();
        materialDialog.show();
        return materialDialog;
    }

    public static void showErrorDialog(Context context, String message) {
        showMessageDialog(context, R.string.dialog_error_title, message);
    }
    public static void showErrorDialogNoTitle(Context context, String message) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.content(message);
        builder.negativeText(android.R.string.ok);
        builder.onNegative((dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public static void showErrorDialog(Context context, @StringRes int message) {
        showErrorDialog(context, context.getString(message));
    }

    public static void showMessageDialog(Context context, @StringRes int title, @StringRes int message) {
        showMessageDialog(context, title, context.getString(message));
    }

    public static void showMessageDialog(Context context, @StringRes int title, String message) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(title);
        builder.content(message);
        builder.negativeText(android.R.string.ok);
        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public static void showMessageDialog(Context context, String message) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.content(message);
        builder.negativeText(android.R.string.ok);
        builder.onNegative((dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public static void showInfoDialog(final Context context, @StringRes int title, View view) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(title);
        builder.customView(view, true);
        builder.negativeText(android.R.string.ok);
        builder.neutralText(R.string.dialog_neutral_mail);
        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });
        builder.onNeutral(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                showEmail(context);
            }
        });
        builder.show();
    }

    public static void showDeleteDialog(Context context, @StringRes int title, @StringRes int content, final OnDeleteAlbumDialogButtonClickListener listener) {
        showDeleteDialog(context, title, context.getString(content), listener);
    }

    public static void showDeleteDialog(Context context, @StringRes int title, String content, final OnDeleteAlbumDialogButtonClickListener listener) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context);
        builder.title(title);
        builder.content(content);
        builder.positiveText(android.R.string.yes);
        builder.negativeText(android.R.string.no);
        builder.onPositive(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                if (listener != null) {
                    listener.onYesButtonClick();
                }
            }
        });
        builder.onNegative(new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    public static MaterialDialog showEditTextDialog(Context context, String title, String content, String hint, String preText, String positive, MaterialDialog.InputCallback callback) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .inputRange(2, 16)
                .positiveText(positive)
                .input(hint, preText, false, callback).show();
        return dialog;
    }

    public static MaterialDialog showInfoDialog(Context context, String title, String content, String hint, String preText, String positive, MaterialDialog.SingleButtonCallback callback) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(title)
                .content(hint)
                .positiveText(positive)
                .onPositive(callback).show();
        return dialog;
    }

    public static MaterialDialog showEditTextDialogCheckPromo(Context context, String title, String content, String hint, String preText, String positive, String negative, MaterialDialog.InputCallback callback) {
        MaterialDialog dialog = new MaterialDialog.Builder(context)
                .title(title)
                .content(content)
                .inputType(InputType.TYPE_CLASS_TEXT |
                        InputType.TYPE_TEXT_VARIATION_PERSON_NAME |
                        InputType.TYPE_TEXT_FLAG_CAP_WORDS)
                .positiveText(positive)
                .negativeText(negative)
                .input(hint, preText, false, callback).show();
        return dialog;
    }

    public static MaterialDialog showNoNegativeMessageDialog(Context context, @StringRes int title, @StringRes int message, @StringRes int ok, final MaterialDialog.SingleButtonCallback onOkClick) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(title)
                .content(message)
                .positiveText(ok)
                .onPositive(onOkClick);

        return builder.show();
    }

    public static MaterialDialog showMessageDialog(Context context, @StringRes int title, @StringRes int message, @StringRes int ok, final MaterialDialog.SingleButtonCallback onOkClick) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(title)
                .content(message)
                .positiveText(ok)
                .onPositive(onOkClick)
                .negativeText(android.R.string.cancel)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                });

        return builder.show();
    }

    public static MaterialDialog showDeleteImageOrPageDialog(Context context, @StringRes int title, @NonNull final View.OnClickListener onOkClick) {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_delete_image_or_page, null);
        View viewBtnDeletePage = view.findViewById(R.id.btn_delete_page);
        viewBtnDeletePage.setOnClickListener(onOkClick);
        viewBtnDeletePage = view.findViewById(R.id.btn_delete_image);
        viewBtnDeletePage.setOnClickListener(onOkClick);
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(title)
                .customView(view, true)
                .negativeText(android.R.string.cancel)
                .onNegative((dialog, which) -> dialog.dismiss());
        return builder.show();
    }

    public static void showEmail(Context context) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.customer_email)});
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.email_subject));
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public static void showOrderAlertDialog(Context context, MaterialDialog.SingleButtonCallback callback, View view) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(R.string.order_alert_title)
                .customView(view, true)
                .positiveText(R.string.go_to_order)
                .negativeText(R.string.order_later)
                .onPositive(callback)
                .onNegative((dialog, which) -> dialog.dismiss());
        builder.show();
    }

    public static void showOrderAlertDialog(Context context, @StringRes int message, MaterialDialog.SingleButtonCallback positiveCallback, MaterialDialog.SingleButtonCallback negativeCallback) {
        MaterialDialog.Builder builder = new MaterialDialog.Builder(context)
                .title(R.string.order_alert_error_title)
                .content(message)
                .positiveText(R.string.retry)
                .negativeText(R.string.ok)
                .onPositive(positiveCallback)
                .onNegative(negativeCallback);
        builder.show();
    }
}
