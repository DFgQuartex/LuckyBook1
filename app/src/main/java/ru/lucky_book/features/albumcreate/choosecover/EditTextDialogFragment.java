package ru.lucky_book.features.albumcreate.choosecover;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ru.lucky_book.R;

/**
 * Created by demafayz on 25.08.16.
 */

@Deprecated
public class EditTextDialogFragment extends DialogFragment implements View.OnClickListener {

    private static final String TAG = EditTextDialogFragment.class.getSimpleName();
    private static final String TITLE_TAG = TAG + "_title";
    private String title;

    private ViewHolder vh;
    private OnDialogClickListener onDialogClickListener;

    private class ViewHolder {
        public TextView tvTitle;
        public EditText etTitle;
        public Button btnPositive;
        public Button btnNegative;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        View rootView = inflater.inflate(R.layout.fragment_dialog_edit_text, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        populateViewHolder(view);
        showData();
    }

    private void showData() {
        vh.tvTitle.setText(title);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(TITLE_TAG, title);
    }

    private void initData(Bundle savedInstanceState) {
        Bundle args = getArguments();
        if (args == null) {
            args = savedInstanceState;
        }
        title = args.getString(TITLE_TAG, "");
    }

    private void populateViewHolder(View itemView) {
        vh = new ViewHolder();
        vh.etTitle = (EditText) itemView.findViewById(R.id.etTitle);
        vh.tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);

        vh.btnNegative = (Button) itemView.findViewById(R.id.btnNegative);
        vh.btnPositive = (Button) itemView.findViewById(R.id.btnPositive);

        vh.btnPositive.setOnClickListener(this);
        vh.btnNegative.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.btnNegative:
                getDialog().dismiss();
                if (onDialogClickListener != null)
                    onDialogClickListener.onNegativeClickListener();
                break;
            case R.id.btnPositive:
                getDialog().dismiss();
                if (onDialogClickListener != null)
                    onDialogClickListener.onPositiveClickListener(vh.etTitle.getText().toString());
                break;
        }
    }

    public static EditTextDialogFragment newInstance(String title) {

        Bundle args = new Bundle();
        args.putString(TITLE_TAG, title);
        EditTextDialogFragment fragment = new EditTextDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void setOnDialogClickListener(OnDialogClickListener onDialogClickListener) {
        this.onDialogClickListener = onDialogClickListener;
    }

    public interface OnDialogClickListener {

        public void onPositiveClickListener(String result);
        public void onNegativeClickListener();
    }
}
