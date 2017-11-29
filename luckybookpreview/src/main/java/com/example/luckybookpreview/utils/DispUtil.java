package com.example.luckybookpreview.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.Size;
import android.view.Display;

/**
 * Created by DemaFayz on 30.06.2016.
 */
public class DispUtil {

    public static Point getDispSize(Activity activity) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static float getMarginPercent(Activity activity, int width, int height, boolean fullScreen) {
        Point sizeDisp = new Point();
        Point size = getNewSize(activity, width, height, fullScreen, sizeDisp);
        float percent = (float) size.y / (float) sizeDisp.y;
        return percent;
    }

    public static Point getNewSize(Activity activity, int width, int height, boolean fullScreen, Point sizeDisp) {
        Display display = activity.getWindowManager().getDefaultDisplay();
        display.getSize(sizeDisp);
        boolean widthIsMax = sizeDisp.x > sizeDisp.y;

        // определяем размеры превью камеры
        Point size = new Point();
        size.set(width, height);

        RectF rectDisplay = new RectF();
        RectF rectPreview = new RectF();

        // RectF экрана, соотвествует размерам экрана
        rectDisplay.set(0, 0, sizeDisp.x, sizeDisp.y);

        // RectF первью
        if (widthIsMax) {
            // превью в горизонтальной ориентации
            rectPreview.set(0, 0, size.x, size.y);
        } else {
            // превью в вертикальной ориентации
            rectPreview.set(0, 0, size.x, size.y);
        }

        Matrix matrix = new Matrix();
        // подготовка матрицы преобразования
        if (!fullScreen) {
            // если превью будет "втиснут" в экран
            matrix.setRectToRect(rectPreview, rectDisplay,
                    Matrix.ScaleToFit.START);
        } else {
            // если экран будет "втиснут" в превью
            matrix.setRectToRect(rectDisplay, rectPreview,
                    Matrix.ScaleToFit.START);
            matrix.invert(matrix);
        }
        // преобразование
        matrix.mapRect(rectPreview);

        int heightRes = (int) (rectPreview.bottom);
        int widthRes = (int) (rectPreview.right);
        Point result = new Point();
        result.y = heightRes;
        result.x = widthRes;
        return result;
    }
}
