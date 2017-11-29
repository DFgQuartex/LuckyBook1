package ru.lucky_book.instruction;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.itsronald.widget.ViewPagerIndicator;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.lucky_book.R;
import ru.lucky_book.app.GreetingActivity;
import ru.lucky_book.data.Instruction;
import ru.lucky_book.dataapi.DataManager;
import ru.lucky_book.features.base.BaseActivity;
import ru.lucky_book.utils.ResourcesUtil;

/**
 * Created by Загит Талипов on 09.11.2016.
 */

public class InstructionActivity extends BaseActivity {

    @BindView(R.id.indicator_view_pager)
    ViewPagerIndicator indicatorViewPager;
    @BindView(R.id.view_pager)
    ViewPager viewPager;
    @BindView(R.id.start_button)
    Button startButton;
    @BindView(R.id.root_start_button)
    RelativeLayout rootStartButton;
    DataManager dataManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction);
        ButterKnife.bind(this);
        dataManager = DataManager.getInstance();
        List<Instruction> instructions = ResourcesUtil.getInstrutionImages(this);
        viewPager.setAdapter(new InstrutionPagerAdapter(getSupportFragmentManager(), instructions));
        viewPager.addOnPageChangeListener(new ViewPagerChangeListener(rootStartButton, instructions.size(), this));
    }

    @OnClick(R.id.start_button)
    public void onClick() {
        dataManager.firstStartDone();
        GreetingActivity.createActivity(this);
        finish();
    }

    public static void createActivity(Activity activity) {
        Intent intent = new Intent(activity, InstructionActivity.class);
        activity.startActivity(intent);
    }
}
