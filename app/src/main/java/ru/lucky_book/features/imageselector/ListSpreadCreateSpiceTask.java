package ru.lucky_book.features.imageselector;

import com.octo.android.robospice.request.SpiceRequest;

import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.bean.Image;
import ru.lucky_book.entities.spread.Page;
import ru.lucky_book.entities.spread.PageTemplate;
import ru.lucky_book.entities.spread.Picture;
import ru.lucky_book.entities.spread.Spread;
import ru.lucky_book.network.utils.DownloadUtil;
import ru.lucky_book.utils.SizeUtils;
import ru.lucky_book.utils.SpreadUtils;

/**
 * Created by Загит Талипов on 20.12.2016.
 */

public class ListSpreadCreateSpiceTask extends SpiceRequest<List> {
    private Spread mCurrentSpread;
    private List<Spread> mSpreads;
    private List<Image> mImages;
    private Page mCurrentPage;
    int mPageWidth;
    int mPageHeight;

    public ListSpreadCreateSpiceTask(List<Image> images, int pageWidth, int pageHeight) {
        super(List.class);
        mImages = images;
        mPageWidth = pageWidth;
        mPageHeight = pageHeight;
        mSpreads = new ArrayList<>();
    }

    @Override
    public List<Spread> loadDataFromNetwork() throws Exception {
        for (Image image : mImages) {
            addImageInSpread(image);
        }
        mCurrentSpread = mSpreads.get(mSpreads.size() - 1);
        mCurrentPage = mCurrentSpread.getRight();
        if (mCurrentPage == null) {
            Page page = new Page();
            page.setTemplateEmpty(PageTemplate.SINGLE);
            mCurrentSpread.setRight(page);
        }
        return mSpreads;
    }

    public void addImageInSpread(Image image) {
        if (mCurrentPage != null && (mCurrentPage.getPictures() == null || mCurrentPage.getPictures()[mCurrentPage.getPictures().length - 1] != null)) {
            mCurrentPage = null;
        }
        if (mCurrentPage == null) {
            do {
                if (mCurrentSpread == null) {
                    addNewSpread();
                }
                if (mCurrentSpread.getLeft() == null) {
                    mCurrentPage = new Page();
                    mCurrentSpread.setLeft(mCurrentPage);
                } else if (mCurrentSpread.getRight() == null) {
                    mCurrentPage = new Page();
                    mCurrentSpread.setRight(mCurrentPage);
                } else {
                    mCurrentSpread = null;
                }
            } while (mCurrentPage == null);
        }
        mCurrentPage.setTemplate(SizeUtils.getPerfectTemplate(image, mCurrentPage.getPictures()));

        DownloadUtil.createCacheFolder();
        if (image.isLocal || DownloadUtil.downloadImage(image)) {
            Picture picture = SpreadUtils.initPicture(image);
            //we can add picture to page before, but... whatever
            SpreadUtils.initPage(mCurrentPage, mPageWidth, mPageHeight);
            SpreadUtils.centerCropPicture(picture, mCurrentPage.getTemplate(), mPageWidth, mPageHeight);
            if (mCurrentPage == null) {
                return;
            }
            int position = 0;
            while (position < mCurrentPage.getPictures().length && mCurrentPage.getPictures()[position] != null) {
                position++;
            }

            mCurrentPage.getPictures()[position] = picture;
            if (mCurrentPage.getPictures()[mCurrentPage.getPictures().length - 1] != null) {
                mCurrentPage = null;
            }
        }
    }

    public void addNewSpread() {
        mCurrentSpread = new Spread();
        mSpreads.add(mCurrentSpread);
    }
}
