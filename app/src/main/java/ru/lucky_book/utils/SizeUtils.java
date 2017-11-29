package ru.lucky_book.utils;

import android.content.Context;
import android.graphics.BitmapFactory;

import java.io.File;

import me.nereo.multi_image_selector.bean.Image;
import ru.lucky_book.data.Size;
import ru.lucky_book.entities.spread.PageTemplate;
import ru.lucky_book.entities.spread.Picture;
import ru.lucky_book.pdf.PdfConverter;

/**
 * Created by demafayz on 23.08.16.
 */
public final class SizeUtils {

    public static final int VALID_HEIGHT = 800;
    public static final int VALID_WIDTH = 800;

    public static Size getImageSizeByFile(File photoFile) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(photoFile.getAbsolutePath(), options);
        int width = options.outWidth;
        int height = options.outHeight;
        Size size = new Size();
        size.setHeight(height);
        size.setWidth(width);
        return size;
    }

    public static boolean sizeValid(Size size) {
        return size.getHeight() >= VALID_HEIGHT && size.getWidth() >= VALID_WIDTH;
    }

    public static boolean imageSizeValid(Image image, PageTemplate template) {
        return image.height>= VALID_HEIGHT/template.getHeightCount() && image.width >= VALID_WIDTH/template.getWidthCount();
    }

    public static boolean pictureSizeValid(Picture picture,PageTemplate template){
        return picture.getOrigHeight()>=VALID_HEIGHT/template.getHeightCount()&&picture.getOrigWidth()>=VALID_WIDTH/template.getWidthCount();
    }

    public static float getSizeFromResource(Context context, int dimenResource) {
        return context.getResources().getDimension(dimenResource);
    }

    public static PageTemplate getPerfectTemplate(Image image,Picture... pictures){
        if(pictures!=null) {
            for (PageTemplate template : PageTemplate.values()) {
                boolean isValid = imageSizeValid(image,template);
                for (int i = 0; isValid && i < pictures.length; i++) {
                    if(pictures[i]!=null) {
                        isValid = pictureSizeValid(pictures[i], template);
                    }
                }
                if (isValid) {
                    return template;
                }
            }
        }else {
            return getPerfectTemplate(image);
        }
        return null;
    }

    public static float getPictureMaxScale(String path){
        Size size=getImageSizeByFile(new File(path));
        float maxScaleX=size.getWidth()/VALID_WIDTH;
        float maxScaleY=size.getHeight()/VALID_HEIGHT;
        return Math.min(maxScaleX,maxScaleY);
    }

    public static PageTemplate getPerfectTemplate(Image image){
        for (PageTemplate template:PageTemplate.values()){
            if(imageSizeValid(image,template)){
                return template;
            }
        }
        return null;
    }
}
