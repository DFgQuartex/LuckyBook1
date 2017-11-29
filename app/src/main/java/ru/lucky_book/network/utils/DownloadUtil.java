package ru.lucky_book.network.utils;

import com.example.luckybookpreview.utils.FileUtil;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import me.nereo.multi_image_selector.bean.Image;

public class DownloadUtil {

    public interface ProgressListener {
        void onProgress(int count);
    }

    public static List<Image> downloadImages(List<Image> images, ProgressListener listener) {
        createCacheFolder();
        if (images != null && !images.isEmpty()) {
            Iterator<Image> imageIterator = images.iterator();
            int imagesCount = 0;
            while (imageIterator.hasNext()) {
                Image image = imageIterator.next();
                boolean downloaded = downloadImage(image);
                if (downloaded) {
                    imageIterator.remove();
                }
                imagesCount++;
                listener.onProgress(imagesCount);
            }
        }
        return images;
    }

    public static File getLocal(Image image){
        return new File(FileUtil.getCacheFolder(), image.name);
    }

    public static File getLocalSocial(Image image){
        return new File(FileUtil.getCacheFolder(), image.name+"1");
    }

    public static String getLocalPath(Image image) {
        return getLocal(image).getAbsolutePath();
    }

    public static void createCacheFolder() {
        File folder = FileUtil.getCacheFolder();
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    public static boolean downloadImage(Image image) {
        File imageFile = getLocal(image);
        if (imageFile.exists()) {
            return true;
        }
        try {
            imageFile.createNewFile();
            URL url = new URL(image.path);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            OutputStream os = new FileOutputStream(imageFile);
            InputStream is = connection.getInputStream();

            byte[] buffer = new byte[1024];
            int bufferLength = 0;

            while ((bufferLength = is.read(buffer)) > 0) {
                os.write(buffer, 0, bufferLength);
            }

            os.close();
            return true;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
