package com.alexvasilkov.gestures.internal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

import com.alexvasilkov.gestures.Settings;
import com.alexvasilkov.gestures.State;

import ru.lucky_book.entities.spread.Picture;
import ru.lucky_book.utils.BitmapUtils;


public class CropUtils {

    private CropUtils() {
    }

    /**
     * Crops image drawable into bitmap according to current image position.
     */
    public static Bitmap crop(Drawable drawable, State state, Settings settings) {
        if (drawable == null) {
            return null;
        }

        float zoom = state.getZoom();

        // Computing crop size for base zoom level (zoom == 1)
        int width = Math.round(settings.getMovementAreaW() / zoom);
        int height = Math.round(settings.getMovementAreaH() / zoom);

        // Crop area coordinates within viewport
        Rect pos = MovementBounds.getMovementAreaWithGravity(settings);

        Matrix matrix = new Matrix();
        state.get(matrix);
        // Scaling to base zoom level (zoom == 1)
        matrix.postScale(1f / zoom, 1f / zoom, pos.left, pos.top);
        // Positioning crop area
        matrix.postTranslate(-pos.left, -pos.top);

        try {
            // Draw drawable into bitmap
            Bitmap dst = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            Canvas canvas = new Canvas(dst);
            canvas.concat(matrix);
            drawable.draw(canvas);

            return dst;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return null; // Not enough memory for cropped bitmap
        }
    }

    public static Bitmap cropOrig(Bitmap bitmap, State state, Settings settings) {
        if (bitmap == null) {
            return null;
        }

        float zoom = state.getZoom();

        // Computing crop size for base zoom level (zoom == 1)
        int width = Math.round(settings.getMovementAreaW() / zoom);
        int height = Math.round(settings.getMovementAreaH() / zoom);

        // Crop area coordinates within viewport
        Rect pos = MovementBounds.getMovementAreaWithGravity(settings);

        Matrix matrix = new Matrix();
        state.get(matrix);
        // Scaling to base zoom level (zoom == 1)
        matrix.postScale(1f / zoom, 1f / zoom, pos.left, pos.top);
        // Positioning crop area
        matrix.postTranslate(-pos.left, -pos.top);

        try {
            // Draw drawable into bitmap
            Bitmap dst = BitmapUtils.getScaledBitmap(bitmap, matrix, width, height);
            if (dst != bitmap)
                bitmap.recycle();
            return dst;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            return bitmap; // Not enough memory for cropped bitmap
        }
    }

    public static RectF getCropRect(State state, Settings settings) {
        float zoom = state.getZoom();

        // Computing crop size for base zoom level (zoom == 1)
        int width = Math.round(settings.getMovementAreaW() / zoom);
        int height = Math.round(settings.getMovementAreaH() / zoom);

        // Crop area coordinates within viewport
        Rect pos = MovementBounds.getMovementAreaWithGravity(settings);

        Matrix matrix = new Matrix();
        state.get(matrix);
        // Scaling to base zoom level (zoom == 1)
        matrix.postScale(1f / zoom, 1f / zoom, pos.left, pos.top);
        // Positioning crop area
        matrix.postTranslate(-pos.left, -pos.top);

        float[] values = new float[9];
        matrix.getValues(values);
        float transX = values[Matrix.MTRANS_X], transY = values[Matrix.MTRANS_Y];
        return new RectF(-transX, -transY, width - transX, height - transY);
    }

    public static State updateFromCropRect(Picture picture, Settings settings, RectF rectF) {
        float transX = rectF.left, transY = rectF.top;
        float width = rectF.width(), height = rectF.height();
        int thumbnailW = settings.getImageW(), thumbnailH = settings.getImageH();
        float thumbnailZoomW = (float) picture.getOrigWidth() / (float) thumbnailW, thumbnailZoomH = (float) picture.getOrigHeight() / (float) thumbnailH;
        transX /= thumbnailZoomW;
        transY /= thumbnailZoomH;
        width /= thumbnailZoomW;
        height /= thumbnailZoomH;
        float zoomX = (float) settings.getMovementAreaW() / width, zoomY = (float) settings.getMovementAreaH() / height;
        Matrix matrix = new Matrix();
        matrix.postTranslate(-transX, -transY);
        matrix.postScale(zoomX, zoomY);
        State state = new State();
        state.set(matrix);
        return state;
    }
}
