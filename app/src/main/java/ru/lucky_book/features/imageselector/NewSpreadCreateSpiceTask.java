package ru.lucky_book.features.imageselector;

import android.util.Log;

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
import rx.Observable;

/**
 * Created by Загит Талипов on 21.12.2016.
 */

public class NewSpreadCreateSpiceTask{
    private static final String TAG = NewSpreadCreateSpiceTask.class.getSimpleName();
    private List<Spread> mSpreads;
    private List<Page> mPages;
    private List<Image> mImages;
    private Page mCurrentPage;
    int mPageWidth;
    int mPageHeight;
    int mMaxSize;
    public static final int MIN_SIZE_PAGE=20;

    private List<Image> mImagesSingleTemp = new ArrayList<>();
    private List<Image> mImagesVerticalTemp = new ArrayList<>();
    private List<Image> mImagesHorizontalTemp = new ArrayList<>();
    private List<Image> mImagesFourTemp = new ArrayList<>();
    private List<List<Image>> mLists = new ArrayList<>();

    public NewSpreadCreateSpiceTask(List<Image> images, int pageWidth, int pageHeight, int maxSize) {
        mImages = images;
        mPageWidth = pageWidth;
        mPageHeight = pageHeight;
        mSpreads = new ArrayList<>();
        mPages = new ArrayList<>();
        mMaxSize = maxSize;
        mLists.add(mImagesFourTemp);
        mLists.add(mImagesVerticalTemp);
        mLists.add(mImagesHorizontalTemp);
        mLists.add(mImagesSingleTemp);

    }


    public List<Spread> loadDataFromNetwork(){
        createPages();
        createSpreads();
        return mSpreads;
    }

    private void shiftImages() {
        if (!mImagesVerticalTemp.isEmpty() || mImagesHorizontalTemp.isEmpty()) {
            for (int i = 0; i < 4; i++) {
                if (!mImagesVerticalTemp.isEmpty()) {
                    mImagesFourTemp.add(mImagesVerticalTemp.get(0));
                    mImagesVerticalTemp.remove(0);
                    continue;
                }
                if (!mImagesHorizontalTemp.isEmpty()) {
                    mImagesFourTemp.add(mImagesHorizontalTemp.get(0));
                    mImagesHorizontalTemp.remove(0);
                    continue;
                }
                if (!mImagesSingleTemp.isEmpty()) {
                    mImagesFourTemp.add(mImagesSingleTemp.get(0));
                    mImagesSingleTemp.remove(0);
                }
            }
        } else if (!mImagesSingleTemp.isEmpty()) {
            for (int i = 0; i < 2; i++) {
                if (!mImagesSingleTemp.isEmpty()) {
                    mImagesVerticalTemp.add(mImagesSingleTemp.get(0));
                    mImagesSingleTemp.remove(0);
                }
            }
        }
    }

    private void normalization() {
        int shortageSize = 4 - mImagesFourTemp.size() % 4;
        Log.d(TAG, "shortageSize: " + shortageSize);
        if (shortageSize != 0 && shortageSize != 4) {
            for (int i = 0; i < shortageSize; i++) {
                if (!mImagesVerticalTemp.isEmpty()) {
                    mImagesFourTemp.add(mImagesVerticalTemp.get(0));
                    mImagesVerticalTemp.remove(0);
                    continue;
                }
                if (!mImagesHorizontalTemp.isEmpty()) {
                    mImagesFourTemp.add(mImagesHorizontalTemp.get(0));
                    mImagesHorizontalTemp.remove(0);
                    continue;
                }
                if (!mImagesSingleTemp.isEmpty()) {
                    mImagesFourTemp.add(mImagesSingleTemp.get(0));
                    mImagesSingleTemp.remove(0);
                }
            }
        }
        shortageSize = mImagesVerticalTemp.size() % 2;
        Log.d(TAG, "shortageSize: " + shortageSize);
        if (shortageSize != 0) {
            for (int i = 0; i < shortageSize; i++) {
                if (!mImagesSingleTemp.isEmpty()) {
                    mImagesVerticalTemp.add(mImagesSingleTemp.get(0));
                    mImagesSingleTemp.remove(0);
                }
            }
        }

        shortageSize = mImagesHorizontalTemp.size() % 2;
        Log.d(TAG, "shortageSize: " + shortageSize);
        if (shortageSize != 0) {
            for (int i = 0; i < shortageSize; i++) {
                if (!mImagesSingleTemp.isEmpty()) {
                    mImagesHorizontalTemp.add(mImagesSingleTemp.get(0));
                    mImagesSingleTemp.remove(0);
                }
            }
        }
    }

    private boolean isCorrectSizePages() {
        return (mImagesVerticalTemp.size() + 1) / 2
                + (mImagesHorizontalTemp.size() + 1) / 2
                + mImagesSingleTemp.size()
                + (mImagesFourTemp.size() + 3) / 4 <= mMaxSize * 2;
    }

    private void createSpreads() {
        for (int i = 0; i < mPages.size(); i = i + 2) {
            Spread spread = new Spread();
            spread.setLeft(mPages.get(i));
            spread.setRight(mPages.get(i + 1));
            mSpreads.add(spread);
        }
    }

    private void createPages() {
        for (Image image : mImages) {
            PageTemplate pageTemplate = SizeUtils.getPerfectTemplate(image);
            /*if (pageTemplate == null) {
                addImageInPage(image, PageTemplate.FOUR);
                continue;
            }*/
            addImageInPage(image, pageTemplate);
        }

        while ((mPages.size() % 2 != 0)||mPages.size()<10) {
            Page page = new Page();
            page.setTemplateEmpty(PageTemplate.SINGLE);
            mPages.add(page);
        }
    }

    private void distributionImages() {
        for (Image image : mImages) {
            PageTemplate pageTemplate = SizeUtils.getPerfectTemplate(image);
            if (pageTemplate == null) {
                mImagesFourTemp.add(image);
                continue;
            }

            switch (pageTemplate) {
                case SINGLE:
                    mImagesSingleTemp.add(image);
                    break;
                /*case TWO_HORIZONTAL:
                    mImagesHorizontalTemp.add(image);
                    break;
                case TWO_VERTICAL:
                    mImagesVerticalTemp.add(image);
                    break;
                case FOUR:
                    mImagesFourTemp.add(image);
                    break;*/
            }
        }
    }

    public void addImageInPage(Image image, PageTemplate pageTemplate) {
            mCurrentPage = null;

        if (mCurrentPage == null || mCurrentPage.getTemplate() != pageTemplate) {
            mCurrentPage = new Page();
            mPages.add(mCurrentPage);
        }
        mCurrentPage.setTemplate(pageTemplate);

        DownloadUtil.createCacheFolder();
        if (image.isLocal || DownloadUtil.downloadImage(image)) {
            Picture picture = SpreadUtils.initPicture(image);
            //we can add picture to page before, but... whatever
            SpreadUtils.initPage(mCurrentPage, mPageWidth, mPageHeight);
            SpreadUtils.centerCropPicture(picture, mCurrentPage.getTemplate(), mPageWidth, mPageHeight);
            if (mCurrentPage == null) {
                return;
            }
            mCurrentPage.getPictures()[0] = picture;
        }
    }

}
