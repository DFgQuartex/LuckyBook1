package ru.lucky_book.features.imageselector;

import com.octo.android.robospice.request.SpiceRequest;

import me.nereo.multi_image_selector.bean.Image;
import ru.lucky_book.entities.spread.Page;
import ru.lucky_book.entities.spread.Picture;
import ru.lucky_book.network.utils.DownloadUtil;
import ru.lucky_book.utils.SpreadUtils;

/**
 * Created by histler
 * on 19.09.16 16:11.
 */
public class PictureFromImageSpiceTask extends SpiceRequest<Picture>{
    private Image mImage;
    private int mPageWidth;
    private int mPageHeight;
    private Page mPage;

    public PictureFromImageSpiceTask(Image image, Page page, int pageWidth, int pageHeight) {
        super(Picture.class);
        mImage=image;
        mPageWidth=pageWidth;
        mPageHeight=pageHeight;
        mPage=page;
    }

    @Override
    public Picture loadDataFromNetwork() throws Exception {
        DownloadUtil.createCacheFolder();
        if(mImage.isLocal||DownloadUtil.downloadImage(mImage)) {
            Picture picture=SpreadUtils.initPicture(mImage);
            //we can add picture to page before, but... whatever
            SpreadUtils.initPage(mPage,mPageWidth,mPageHeight);
            SpreadUtils.centerCropPicture(picture,mPage.getTemplate(),mPageWidth,mPageHeight);
            return picture;
        }
        throw new FileLoadException();
    }
}
