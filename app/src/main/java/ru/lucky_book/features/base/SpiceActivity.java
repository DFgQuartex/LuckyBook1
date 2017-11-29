package ru.lucky_book.features.base;

import android.support.v7.app.AppCompatActivity;

import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.SpiceService;

import ru.lucky_book.spice.LocalSpiceService;
import ru.lucky_book.spice.LuckySpiceManager;

/**
 * Created by Badr
 * on 03.09.2016 19:21.
 */
public class SpiceActivity extends AppCompatActivity {
    private SpiceManager mSpiceManager=new LuckySpiceManager(getSpiceService());
    protected Class<? extends SpiceService> getSpiceService(){
        return LocalSpiceService.class;
    }

    protected SpiceManager getSpiceManager(){
        return mSpiceManager;
    }


    @Override
    protected void onStart() {
        super.onStart();
        if(!getSpiceManager().isStarted()){
            getSpiceManager().start(this);
            initialLoad();
        }
    }

    @Override
    protected void onDestroy() {
        if(getSpiceManager().isStarted()){
            getSpiceManager().shouldStop();
        }
        super.onDestroy();
    }

    protected void initialLoad(){

    }
}
