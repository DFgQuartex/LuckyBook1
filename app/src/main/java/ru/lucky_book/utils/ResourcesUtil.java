package ru.lucky_book.utils;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import ru.lucky_book.R;
import ru.lucky_book.data.Instruction;

/**
 * Created by Загит Талипов on 10.11.2016.
 */

public class ResourcesUtil {


    public static List<Instruction> getInstrutionImages(Context context) {
        List<Instruction> instructions = new ArrayList<>();
        int[] ints = new int[]{R.drawable.image_1
                , R.drawable.image_3
                , R.drawable.image_4
                , R.drawable.image_5
                , R.drawable.image_2
                , R.drawable.image_6
                , R.drawable.image_7
                , R.drawable.image_8};
        String[] labels = context.getResources().getStringArray(R.array.instruction_labels);
        for (int i = 0; i < labels.length; i++) {
            instructions.add(new Instruction(labels[i], ints[i]));
        }
        instructions.get(instructions.size() - 1).setLabel2(context.getString(R.string.instruction_last_page));
        return instructions;
    }

}
