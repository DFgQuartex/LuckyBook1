package ru.lucky_book.instruction;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import ru.lucky_book.data.Instruction;

/**
 * Created by Загит Талипов on 10.11.2016.
 */

public class InstrutionPagerAdapter extends FragmentPagerAdapter {

    public InstrutionPagerAdapter(FragmentManager fm, List<Instruction> instructions) {
        super(fm);
        this.instructions = instructions;
    }

    List<Instruction> instructions;

    @Override
    public Fragment getItem(int position) {
        int type;
        if (position == 0) {
            type = Instruction.TYPE_FIRST;
        } else if (position == instructions.size() - 1) {
            type = Instruction.TYPE_LAST;
        } else
            type = Instruction.TYPE_MEDIUM;
        return new ItemInstructionFragment().create(instructions.get(position).setType(type));
    }


    @Override
    public int getCount() {
        return instructions.size();
    }
}
