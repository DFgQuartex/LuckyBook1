package com.example.luckybookpreview.ui.provider;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.example.luckybookpreview.datas.CurlPage;
import com.example.luckybookpreview.ui.views.CurlView;
import com.example.luckybookpreview.utils.PictureUtils;

import java.util.List;

/**
 * Created by DemaFayz on 30.06.2016.
 */
public class AlbumPageProvider implements CurlView.PageProvider {

    private Context context;

    // Bitmap resources.
    private Bitmap[] slides;

    public AlbumPageProvider(Context context, List<Bitmap> slides) {
        if (slides == null) return;


        for (int i = 0; i < slides.size(); i++) {
            if (i % 2 != 0) {
                Bitmap slide = slides.get(i);
                slide = PictureUtils.makeImageMirror(slide);
                slides.set(i, slide);
            }
        }

        this.context = context;
        this.slides = new Bitmap[slides.size()];
        int i = 0;
        for (Bitmap slide : slides) {
            this.slides[i] = slide;
            i++;
        }
    }

    @Override
    public int getPageCount() {
        if (slides == null) return 0;
        return slides.length / 2;
    }

    private Bitmap loadBitmap(int width, int height, int index) {
        Bitmap b = Bitmap.createBitmap(width, height,
                Bitmap.Config.RGB_565);
        b.eraseColor(0x00000000);
        Canvas c = new Canvas(b);
        Drawable d = new BitmapDrawable(context.getResources(), slides[index]);

        int margin = 0;
        int border = 0;
        Rect r = new Rect(margin, margin, width+ - margin, height - margin);

        int imageWidth = r.width() - (border * 2);
        int imageHeight = imageWidth * d.getIntrinsicHeight()
                / d.getIntrinsicWidth();
        if (imageHeight > r.height() - (border * 2)) {
            imageHeight = r.height() - (border * 2);
            imageWidth = imageHeight * d.getIntrinsicWidth()
                    / d.getIntrinsicHeight();
        }

        /*r.left += ((r.width() - imageWidth) / 2) - border;
        r.right = r.left + imageWidth + border + border;
        r.top += ((r.height() - imageHeight) / 2) - border;
        r.bottom = r.top + imageHeight + border + border;*/

        Paint p = new Paint();
        p.setColor(0xFFC0C0C0);
        c.drawRect(r, p);
        r.left += border;
        r.right -= border;
        r.top += border;
        r.bottom -= border;

        d.setBounds(r);
        d.draw(c);

        return b;
    }

    @Override
    public void updatePage(CurlPage page, int width, int height, int index) {
        Bitmap front = loadBitmap(width, height, index * 2);
        Bitmap back = loadBitmap(width, height, index * 2 + 1);
        page.setTexture(front, CurlPage.SIDE_FRONT);
        page.setTexture(back, CurlPage.SIDE_BACK);
        /*Bitmap front = loadBitmap(width, height, index);
        page.setTexture(front, CurlPage.SIDE_FRONT);
        page.setColor(Color.rgb(180, 180, 180), CurlPage.SIDE_BACK);*/
    }
}