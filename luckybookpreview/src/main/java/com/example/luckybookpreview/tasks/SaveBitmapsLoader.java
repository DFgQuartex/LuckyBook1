package com.example.luckybookpreview.tasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.AsyncTaskLoader;

import com.example.luckybookpreview.utils.FileUtil;

import java.util.List;

/**
 * Created by DemaFayz on 01.07.2016.
 */
public class SaveBitmapsLoader extends AsyncTaskLoader<List<String>> {

    private List<Bitmap> bitmaps;

    public SaveBitmapsLoader(Activity activity, List<Bitmap> bitmaps) {
        super(activity);
        this.bitmaps = bitmaps;
    }

    @Override
    public List<String> loadInBackground() {
        List<String> paths = FileUtil.saveBitmapList(bitmaps);
        return paths;
    }
}
