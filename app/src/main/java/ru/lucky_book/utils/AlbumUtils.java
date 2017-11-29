package ru.lucky_book.utils;

import com.example.luckybookpreview.utils.FileUtil;

import java.io.File;

/**
 * Created by Badr
 * on 03.09.2016 17:06.
 */
public final class AlbumUtils {

    public static File getAlbumFolder(String albumId){
        return new File(FileUtil.getAlbumsFolder(),File.separator+albumId);
    }
    public static File getAlbumFirstCover(String albumId){
        return new File(FileUtil.getAlbumsFolder(),PageUtils.previewFileNameForPageImage(albumId,0));
    }
}
