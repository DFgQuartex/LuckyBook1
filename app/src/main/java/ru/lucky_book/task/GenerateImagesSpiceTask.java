package ru.lucky_book.task;

import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.bean.Image;
import ru.lucky_book.entities.SocialImage;

public class GenerateImagesSpiceTask<T> extends LocalSpiceRequest<Pair> {

    private List<? extends SocialImage> mSocialImages;
    private T mCursor;
    private String mPrefix;

    public GenerateImagesSpiceTask(List<? extends SocialImage> socialImages, T cursor, String prefix) {
        super(Pair.class);
        mSocialImages = socialImages;
        mCursor = cursor;
        mPrefix = prefix;
    }

    @Override
    public Pair<T, List<Image>> loadData() throws Exception {
        List<Image> images = new ArrayList<>(mSocialImages.size());
        if (mSocialImages != null && !mSocialImages.isEmpty()) {
            for (SocialImage socialImage : mSocialImages) {
                if (!socialImage.isVideo()) {
                    Image image = new Image(String.format("%s_%s.jpg", mPrefix, socialImage.getId()),
                            socialImage.getSource(),
                            socialImage.getThumbnail(),
                            socialImage.getDate(),
                            socialImage.getWidth(),
                            socialImage.getHeight(),
                            false);
                    images.add(image);
                }
            }
        }
        return Pair.create(mCursor, images);
    }
}
