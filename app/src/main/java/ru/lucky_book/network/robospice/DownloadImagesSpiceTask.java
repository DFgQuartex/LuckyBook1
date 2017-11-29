package ru.lucky_book.network.robospice;

import com.octo.android.robospice.request.SpiceRequest;

import java.util.List;

import me.nereo.multi_image_selector.bean.Image;
import ru.lucky_book.network.utils.DownloadUtil;

public class DownloadImagesSpiceTask extends SpiceRequest<List> implements DownloadUtil.ProgressListener {

    private List<Image> mImages;

    public DownloadImagesSpiceTask(List<Image> images) {
        super(List.class);
        mImages = images;
    }

    @Override
    public List<Image> loadDataFromNetwork() throws Exception {
        return DownloadUtil.downloadImages(mImages, this);
    }

    @Override
    public void onProgress(int count) {
        publishProgress((float) count);
    }
}
