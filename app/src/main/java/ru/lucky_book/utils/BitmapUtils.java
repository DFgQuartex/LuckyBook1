package ru.lucky_book.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.example.luckybookpreview.utils.PictureUtils;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by histler
 * on 24.08.16 17:00.
 */
public final class BitmapUtils {

    public static int[] getBitmapSizes(String path){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        //Returns null, sizes are in the options variable
        BitmapFactory.decodeFile(path, options);
        int orientation=PictureUtils.getBitmapOrientation(path);
        if(orientation== ExifInterface.ORIENTATION_ROTATE_90||orientation==ExifInterface.ORIENTATION_ROTATE_270){
            return new int[]{options.outHeight,options.outWidth};
        }else {
            return new int[]{options.outWidth,options.outHeight};
        }
    }

    public static Bitmap getScaledBitmap(Bitmap original, Matrix matrix,int imageWidth,int imageHeight){
        /*float[] sizes=new float[]{original.getWidth(),original.getHeight()};
        matrix.mapPoints(sizes);*/
        float[] values=new float[9];
        matrix.getValues(values);
        //тут вытаскиваем сдвиг.
        float transX=Math.max(values[Matrix.MTRANS_X],imageWidth-original.getWidth());
        float transY=Math.max(values[Matrix.MTRANS_Y],imageHeight-original.getHeight());
        values[Matrix.MTRANS_X]=0;
        values[Matrix.MTRANS_Y]=0;
        matrix.setValues(values);

        int resultTransX=-(int)transX;
        int resultTransY=-(int)transY;
        if(resultTransX<0){
            resultTransX=0;
        }
        if(resultTransY<0){
            resultTransY=0;
        }

        return Bitmap.createBitmap(original, resultTransX, resultTransY,Math.min(imageWidth,original.getWidth()), Math.min(imageHeight,original.getHeight()), matrix, true);
    }

    public static Bitmap getFromAsset(Context context,String filePath, int width, int height){
        AssetManager assetManager = context.getAssets();
        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(istr,null, options);
            int inSampleSize=PictureUtils.calculateInSampleSize(options,width,height);
            options=new BitmapFactory.Options();
            options.inSampleSize=inSampleSize;
            istr = assetManager.open(filePath);
            bitmap = BitmapFactory.decodeStream(istr,null, options);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap getFromAsset(Context context,String filePath){
        AssetManager assetManager = context.getAssets();

        InputStream istr;
        Bitmap bitmap = null;
        try {
            istr = assetManager.open(filePath);
            bitmap=BitmapFactory.decodeStream(istr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }
}
