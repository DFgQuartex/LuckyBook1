package ru.lucky_book.utils;

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.lucky_book.BuildConfig;
import ru.lucky_book.features.albumcreate.choosecover.ChooseCoverItem;

/**
 * Created by demafayz on 25.08.16.
 */
public class CoverUtils {
    private static final String DEFAULT_COVERS_FOLDER = "covers";
    private static final String PREVIEW_FILE_NAME = "preview.png";
    private static final String ASSETS_URL_PREFIX = "file:///android_asset/";

    public static List<ChooseCoverItem> getTags(Context context) {
        List<ChooseCoverItem> list = new ArrayList<>();
        try {
            String[] coverTags = context.getAssets().list(DEFAULT_COVERS_FOLDER);
            for (String coverTag : coverTags) {
                if (coverTag.contains("promo")) {
                    list.add(0, getPromoItem(coverTag));
                    break;
                }/*else { //todo если надо будет использовать локальные шаблоны
                    list.add(getTagItem(context,coverTag));
                }*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static ChooseCoverItem getPromoItem(String assetsPath) {
        ChooseCoverItem item = new ChooseCoverItem();
        item.setIcon(DEFAULT_COVERS_FOLDER + File.separator + assetsPath);
        return item;
    }

    private static ChooseCoverItem getTagItem(Context context, String tag) {
        String pathName = DEFAULT_COVERS_FOLDER + File.separator + tag;
        ChooseCoverItem item = new ChooseCoverItem();
        item.setTitle(ContextUtils.getStringResourceByName(context, DEFAULT_COVERS_FOLDER + "_" + tag));
        item.setIcon(pathName + File.separator + PREVIEW_FILE_NAME);
        return item;
    }

    public static List<ChooseCoverItem> getTagItems(Context context, String tag) {
        try {
            String pathName = DEFAULT_COVERS_FOLDER + File.separator + tag;
            String[] subItems = context.getAssets().list(pathName);
            List<ChooseCoverItem> list = new ArrayList<>();
            for (String itemName : subItems) {
                if (!itemName.endsWith(PREVIEW_FILE_NAME)) {
                    ChooseCoverItem subItem = new ChooseCoverItem();
                    subItem.setIcon(pathName + File.separator + itemName);
                    list.add(subItem);
                }
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    public static String toAssetsUrl(String cover) {
        return ASSETS_URL_PREFIX + cover;
    }

    public static String toUrl(String cover) {
        return BuildConfig.ENDPOINT + cover;
    }
}
