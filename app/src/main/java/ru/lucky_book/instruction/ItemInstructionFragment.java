package ru.lucky_book.instruction;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.lucky_book.R;
import ru.lucky_book.data.Instruction;
import ru.lucky_book.features.base.BaseFragment;

/**
 * Created by Загит Талипов on 09.11.2016.
 */

public class ItemInstructionFragment extends BaseFragment {
    @BindView(R.id.label_tv)
    TextView labelTv;
    @BindView(R.id.image_view)
    ImageView imageView;
    @Nullable
    @BindView(R.id.label_tv2)
    TextView labelTv2;

    public Instruction getInstruction() {
        return mInstruction;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        int res = 0;
        switch (mInstruction.getType()) {
            case Instruction.TYPE_FIRST: {
                res = R.layout.fragment_instruction_item_first;
                break;
            }
            case Instruction.TYPE_LAST: {
                res = R.layout.fragment_instruction_item_last;
                break;
            }
            case Instruction.TYPE_MEDIUM: {
                res = R.layout.fragment_instruction_item;
                break;
            }
        }
        View view = inflater.inflate(res, null, false);
        ButterKnife.bind(this, view);
        labelTv.setText(mInstruction.getLabel());
        imageView.setImageResource(mInstruction.getImage());
        if (mInstruction.getType() == Instruction.TYPE_LAST) {
            labelTv2.setText(mInstruction.getLabel2());
        }
        return view;
    }

    public void setInstruction(Instruction instruction) {
        this.mInstruction = instruction;
    }

    Instruction mInstruction;

    @Override
    public String getFragmentTitle() {
        return mInstruction.getLabel();
    }

    public ItemInstructionFragment create(Instruction instruction) {
        ItemInstructionFragment itemInstructionFragment = new ItemInstructionFragment();
        itemInstructionFragment.setInstruction(instruction);
        return itemInstructionFragment;
    }
}
