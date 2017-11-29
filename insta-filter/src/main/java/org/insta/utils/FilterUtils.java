package org.insta.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;

import org.insta.IF1977Filter;
import org.insta.IFAmaroFilter;
import org.insta.IFBrannanFilter;
import org.insta.IFEarlybirdFilter;
import org.insta.IFHefeFilter;
import org.insta.IFHudsonFilter;
import org.insta.IFInkwellFilter;
import org.insta.IFLomofiFilter;
import org.insta.IFLordKelvinFilter;
import org.insta.IFNashvilleFilter;
import org.insta.IFNormalFilter;
import org.insta.IFRiseFilter;
import org.insta.IFSierraFilter;
import org.insta.IFSutroFilter;
import org.insta.IFToasterFilter;
import org.insta.IFValenciaFilter;
import org.insta.IFWaldenFilter;
import org.insta.IFXproIIFilter;
import org.insta.InstaFilter;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageRenderer;
import jp.co.cyberagent.android.gpuimage.PixelBuffer;
import jp.co.cyberagent.android.gpuimage.Rotation;

/**
 * Created by histler
 * on 13.09.16 17:01.
 */
public final class FilterUtils {
    public final static List<Class<? extends InstaFilter>> FILTERS=new ArrayList<>();
    static {
        FILTERS.add(IFNormalFilter.class);
        FILTERS.add(IF1977Filter.class);
        FILTERS.add(IFAmaroFilter.class);
        FILTERS.add(IFBrannanFilter.class);
        FILTERS.add(IFEarlybirdFilter.class);
        FILTERS.add(IFHefeFilter.class);
        FILTERS.add(IFHudsonFilter.class);
        FILTERS.add(IFInkwellFilter.class);
        FILTERS.add(IFLomofiFilter.class);
        FILTERS.add(IFLordKelvinFilter.class);
        FILTERS.add(IFNashvilleFilter.class);
        FILTERS.add(IFRiseFilter.class);
        FILTERS.add(IFSierraFilter.class);
        FILTERS.add(IFSutroFilter.class);
        FILTERS.add(IFToasterFilter.class);
        FILTERS.add(IFValenciaFilter.class);
        FILTERS.add(IFWaldenFilter.class);
        FILTERS.add(IFXproIIFilter.class);
    }


    public static String getFilterName(Context context, Class<? extends InstaFilter> filterClass) {
        String lowerName="filter_"+filterClass.getSimpleName().toLowerCase();
        String packageName = context.getPackageName();
        int resId = context.getResources().getIdentifier(lowerName, "string", packageName);
        return context.getString(resId);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static InstaFilter filterForClass(Context context, Class<? extends InstaFilter> filterClass){
        try {
            Constructor<? extends InstaFilter> constructor = filterClass.getConstructor(Context.class);
            return constructor.newInstance(context);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Bitmap createFiltered(Bitmap bitmap, InstaFilter filter){
        GPUImageRenderer renderer = new GPUImageRenderer(filter);

        renderer.setRotation(Rotation.NORMAL,false, false);
        renderer.setScaleType(GPUImage.ScaleType.CENTER_CROP);
        PixelBuffer buffer = new PixelBuffer(bitmap.getWidth(), bitmap.getHeight());
        buffer.setRenderer(renderer);
        renderer.setImageBitmap(bitmap, false);//maybe true?
        try {
            Bitmap result = buffer.getBitmap();
            bitmap.recycle();
            return result;
        }catch (OutOfMemoryError e){
            e.printStackTrace();
            return bitmap;
        }
    }
}
