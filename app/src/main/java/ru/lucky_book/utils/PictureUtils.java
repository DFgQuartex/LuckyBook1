package ru.lucky_book.utils;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import ru.lucky_book.entities.spread.Picture;
import ru.lucky_book.entities.spread.Spread;

/**
 * Created by DemaWork on 21.02.2017.
 */

public class PictureUtils {


    public static List<String> spreadToPaths(List<Spread> spreads) {
        List<String> pictureNames = new ArrayList<>();
        for (Spread spread : spreads) {
            for (Picture picture : spread.getAllPictures()) {
                if (picture!=null&&!TextUtils.isEmpty(picture.getPath())) {
                    String[] strings = picture.getPath().split("/");
                    pictureNames.add(strings[strings.length - 1]);
                }
            }
        }
        return pictureNames;
    }
}
