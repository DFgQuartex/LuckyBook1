package com.example.luckybookpreview.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.UiThread;
import android.support.v4.content.CursorLoader;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DemaFayz on 29.06.2016.
 */
public class FileUtil {

    private static final String SLIDE_NAME = "Slide_";

    public static File getAppFolder() {
        return new File(Environment.getExternalStorageDirectory(), "LuckyBook");
    }

    public static File getCacheFolder() {
        File cacheDir = new File(getAppFolder(), "cache");
        if (!cacheDir.exists()) {
            if (cacheDir.mkdirs()) {
                try {
                    new File(cacheDir, ".nomedia").createNewFile();
                } catch (IOException ignored) {
                }
            }
        }
        return cacheDir;
    }

    public static File getAlbumsFolder() {
        File albumsDir = new File(getAppFolder(), "albums");
        if (!albumsDir.exists()) {
            if (albumsDir.mkdirs()) {
                try {
                    new File(albumsDir, ".nomedia").createNewFile();
                } catch (IOException ignored) {
                }
            }
        }
        return albumsDir;
    }

    public static File getCoverCashFolder() {
        File albumsDir = new File(getAppFolder(), "covers");
        if (!albumsDir.exists()) {
            if (albumsDir.mkdirs()) {
                try {
                    new File(albumsDir, ".nomedia").createNewFile();
                } catch (IOException ignored) {
                }
            }
        }
        return albumsDir;
    }

    public static String saveJpeg90(Bitmap bitmap, String fileName) {
        bitmap = changeBitmapContrastBrightness(bitmap, 1.0f, 8f);
        return saveBitmap(bitmap, fileName, Bitmap.CompressFormat.JPEG, 90);
    }

    public static String saveJpeg50(Bitmap bitmap, String filename) {
        return saveBitmap(bitmap, filename, Bitmap.CompressFormat.JPEG, 50);
    }

    public static String saveJpeg100Preview(Bitmap bitmap, String filename) {
        return saveBitmap(bitmap, filename, Bitmap.CompressFormat.JPEG, 100);
    }
    public static String saveJpeg100(Bitmap bitmap, String filename) {
        return saveBitmapRewrite(bitmap, filename, Bitmap.CompressFormat.JPEG, 100);
    }

    public static boolean deleteAlbumFile(String fileName) {
        File file = new File(getAlbumsFolder(), fileName);
        if (file.exists()) {
            return file.delete();
        }
        return true;
    }

    private static String saveBitmap(Bitmap bitmap, String fileName, Bitmap.CompressFormat compressFormat, int quality) {
        OutputStream outStream;
        File file = new File(getAlbumsFolder(), fileName);
        try {
            file.getParentFile().mkdirs();
            outStream = new FileOutputStream(file);
            bitmap.compress(compressFormat, quality, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {

        }
        return file.getAbsolutePath();
    }

    private static String saveBitmapRewrite(Bitmap bitmap, String fileName, Bitmap.CompressFormat compressFormat, int quality) {
        OutputStream outStream;
        File file = new File(getCoverCashFolder(), fileName);
        if (file.exists()) {
            return file.getAbsolutePath();
        }
        try {
            file.getParentFile().createNewFile();
            outStream = new FileOutputStream(file);
            bitmap.compress(compressFormat, quality, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.fillInStackTrace();
        }
        return file.getAbsolutePath();
    }

    public static Bitmap changeBitmapContrastBrightness(Bitmap bmp, float contrast, float brightness) {
        ColorMatrix cm = new ColorMatrix(new float[]
                {
                        contrast, 0, 0, 0, brightness,
                        0, contrast, 0, 0, brightness,
                        0, 0, contrast, 0, brightness,
                        0, 0, 0, 1, 0
                });

        Bitmap ret = Bitmap.createBitmap(bmp.getWidth(), bmp.getHeight(), bmp.getConfig());

        Canvas canvas = new Canvas(ret);

        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cm));
        canvas.drawBitmap(bmp, 0, 0, paint);
        return ret;
    }

    public static String renameAlbumFile(String sourceName, String destinationName) {
        File file = new File(getAlbumsFolder(), sourceName);
        file.renameTo(new File(getAlbumsFolder(), destinationName));
        return file.getAbsolutePath();
    }

    public static Bitmap getBitmapByFile(String path) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        bmOptions.inDither = true;

        Bitmap bitmap = PictureUtils.getOrientedBitmap(path, bmOptions);
        return bitmap;
    }

    public static List<String> saveBitmapList(List<Bitmap> bitmaps) {
        List<String> paths = new ArrayList<>();
        for (int i = 0; i < bitmaps.size(); i++) {
            paths.add(saveJpeg90(bitmaps.get(i), SLIDE_NAME + i + ".jpg"));
        }
        return paths;
    }

    public static List<Bitmap> getBitmapsByStrings(List<String> paths) {
        List<Bitmap> photos = new ArrayList<>();
        for (int i = 0; i < paths.size(); i++) {
            Bitmap bmp = getBitmapByFile(paths.get(i));
            photos.add(bmp);
        }
        return photos;
    }

    @UiThread
    public static String getRealPathFromURI(Context context, Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};

        CursorLoader cursorLoader = new CursorLoader(
                context,
                contentUri, proj, null, null, null);
        Cursor cursor = cursorLoader.loadInBackground();

        int column_index =
                cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String ans = cursor.getString(column_index);
        cursor.close();
        return ans;
    }

    public static void clearFolder(File folder) {
        File[] files = folder.listFiles();
        for (File f : files) {
            f.delete();
        }
    }

    public static void bitmapToPng(File file, Bitmap bitmap) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        out.close();
    }
}