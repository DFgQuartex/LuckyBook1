package com.example.luckybookpreview.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.ExifInterface;
import android.net.Uri;
import android.support.annotation.UiThread;
import android.util.Log;

import java.io.IOException;

/**
 * Created by ilgiz on 6/29/16.
 */
public class PictureUtils {

    public static final int MINIMAL_SATISFACTORY_PPI = 100;
    public static final int IMAGE_SIZE_MAGIC_NUMBER = 400;
    private static final String TAG = "PictureUtils";

    /**
     * Computes the minimal dimensions of a picture to be of good PPI at a given
     * image place (we want to print it at the given dimensions in inches)
     *
     * @param placeWidthInches
     * @param placeHeightInches
     * @return
     */
    public static Dimensions getMinSatisfactoryDimensionsInPx(double placeWidthInches,
                                                              double placeHeightInches) {
        int minWidth = (int) Math.ceil(MINIMAL_SATISFACTORY_PPI * placeWidthInches);
        int minHeight = (int) Math.ceil(MINIMAL_SATISFACTORY_PPI * placeHeightInches);
        return new Dimensions(minWidth, minHeight);
    }

    @UiThread
    public static Dimensions getPictureDimensionsPx(Context context, Uri uri) {
        String path = FileUtil.getRealPathFromURI(context, uri);
        Log.d(TAG, "Real path: " + path);
        return getPictureDimensionsPx(path);
    }

    public static Dimensions getPictureDimensionsPx(String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);
        return new Dimensions(options.outWidth, options.outHeight);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight){
        return calculateInSampleSize(options,reqWidth,reqHeight,ExifInterface.ORIENTATION_UNDEFINED);
    }
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight,int orientation) {
        // Raw height and width of image
        boolean rotated=orientation==ExifInterface.ORIENTATION_ROTATE_90||orientation==ExifInterface.ORIENTATION_ROTATE_270;
        final int height = rotated?options.outWidth:options.outHeight;
        final int width = rotated?options.outHeight:options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * Right now uses some strange constants for width and height. See code.
     *
     * @param path
     * @return
     * @deprecated Use {@link #decodeSampledBitmapFromFile(String, int, int)}
     */
    @Deprecated
    public static Bitmap decodeSampledBitmapFromFile(String path) {

        int reqWidth = IMAGE_SIZE_MAGIC_NUMBER;
        int reqHeight = IMAGE_SIZE_MAGIC_NUMBER;

        return decodeSampledBitmapFromFile(path, reqWidth, reqHeight);
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
        int sampleSize = calculateInSampleSize(path, reqWidth, reqHeight);
        return decodeSampledBitmapFromFile(path, sampleSize);
    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        return getOrientedBitmap(path, options);
    }

    public static int calculateInSampleSize(String path, int reqWidth, int reqHeight) {
        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        int orientation=getBitmapOrientation(path);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight,orientation);
        return options.inSampleSize;
    }

    public static Bitmap getOrientedBitmap(String path, BitmapFactory.Options options) {
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        int orientation = getBitmapOrientation(path);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                bitmap=rotateImage(bitmap, 90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                bitmap=rotateImage(bitmap, 180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                bitmap=rotateImage(bitmap, 270);
                break;
            case ExifInterface.ORIENTATION_NORMAL:
            default:
                break;
        }
        return bitmap;
    }

    public static int getBitmapOrientation(String path) {
        try {
            ExifInterface ei = new ExifInterface(path);
            return ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return ExifInterface.ORIENTATION_UNDEFINED;
        }
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        try {
            Bitmap rotated = Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix,
                    true);
            source.recycle();
            return rotated;
        }catch (OutOfMemoryError e){
            Log.e(TAG,e.getMessage());
            e.printStackTrace();
            return source;
        }
    }
    public final static Bitmap makeImageMirror(final Bitmap bmp) {
        final int width = bmp.getWidth();
        final int height = bmp.getHeight();

        // This will not scale but will flip on the X axis.
        final Matrix mtx = new Matrix();
        mtx.preScale(-1, 1);

        // Create a Bitmap with the flip matrix applied to it.
        final Bitmap reflection = Bitmap.createBitmap(bmp, 0, 0, width, height, mtx, false);

        // Create a new Canvas with the bitmap.
        final Canvas cnv = new Canvas(reflection);

        // Draw the reflection Image.
        cnv.drawBitmap(reflection, 0, 0, null);

        //
        final Paint pnt = new Paint(Paint.ANTI_ALIAS_FLAG);
        // Set the Transfer mode to be porter duff and destination in.
        pnt.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

        // Draw a rectangle using the paint.
        cnv.drawRect(0, 0, width, height, pnt);

        return reflection;
    }
}
