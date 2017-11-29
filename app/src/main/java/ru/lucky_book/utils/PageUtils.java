package ru.lucky_book.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.util.Log;

import com.alexvasilkov.gestures.Settings;
import com.alexvasilkov.gestures.State;
import com.alexvasilkov.gestures.internal.CropUtils;
import com.example.luckybookpreview.utils.FileUtil;
import com.example.luckybookpreview.utils.PictureUtils;

import org.insta.utils.FilterUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.lucky_book.entities.spread.Page;
import ru.lucky_book.entities.spread.PageTemplate;
import ru.lucky_book.entities.spread.Picture;
import ru.lucky_book.entities.spread.PictureViewState;
import ru.lucky_book.entities.spread.Spread;
import ru.lucky_book.pdf.PdfConverter;

/**
 * Created by histler
 * on 25.08.16 10:24.
 * <p>
 * generating pages images for sharing, preview and order
 */
public final class PageUtils {
    public static final String COVER_FILE_NAME = "%1$s" + File.separator + "temp" + File.separator + "cover";
    public static final String THUMB_ENDING = "_thumb.jpg";
    public static final String FULL_SIZE_ENDING = ".jpg";
    public static final String PREVIEW_ENDING = FULL_SIZE_ENDING;
    public static int mSize = 2;

    private static final String TAG = "LeafUtils";

    public static String fileNameForPageImage(String albumId, int spreadPosition, int pagePosition, int imagePosition, boolean isThumbnail) {
        return fileNameForPageImage(albumId, spreadPosition, pagePosition, imagePosition) + (isThumbnail ? THUMB_ENDING : FULL_SIZE_ENDING);
    }

    public static String fileNameForPageImage(String albumId, int spreadPosition, int pagePosition, int imagePosition) {
        return albumId + File.separator + "temp" + File.separator + "spread_" + spreadPosition + "_page_" + pagePosition + "_img_" + imagePosition;
    }

    public static String previewFileNameForPageImage(String albumId, int position) {
        return albumId + File.separator + "preview" + File.separator + position + PREVIEW_ENDING;
    }

    public static List<Bitmap> getPreviewBitmaps(String albumId) {
        File folder = new File(FileUtil.getAlbumsFolder(), previewFileNameForPageImage(albumId, 0)).getParentFile();
        if (folder.isDirectory()) {
            int count = folder.list().length;
            List<Bitmap> photos = new ArrayList<>();
            for (int position = 0; position < count; position++) {
                Bitmap bmp = FileUtil.getBitmapByFile(new File(FileUtil.getAlbumsFolder(), previewFileNameForPageImage(albumId, position)).getAbsolutePath());
                photos.add(bmp);
            }
            return photos;
        }
        return null;
    }


    public static boolean cropSpreadPreviews(Context context, String albumId, List<Spread> spreads, String albumCover, int pageSize, CropStateListener cropStateListener) {
        if (cropStateListener != null) {
            cropStateListener.onCroppingStarted();
        }
        long currentTime = System.currentTimeMillis();
        long prevTime;
        File folder = new File(FileUtil.getAlbumsFolder(), albumId + File.separator + "preview" + File.separator);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        } else {
            folder.mkdirs();
        }
        //createNoMediaInFolder(folder);
        prevTime = currentTime;
        currentTime = System.currentTimeMillis();
        Log.d(TAG, "time for deleting: " + (currentTime - prevTime));
        if (spreads != null) {

            int totalPositions = 2;// first and last covers separated
            for (Spread spread : spreads) {
                totalPositions += spread.getTotalImages();
            }
            int position = 0;

            if (cropStateListener != null) {
                cropStateListener.onPictureCropping(position + 1, totalPositions);
            }
            Bitmap fromAssets = BitmapFactory.decodeFile(albumCover);
            int height = fromAssets.getHeight();
            int width = fromAssets.getWidth();
            prevTime = currentTime;
            currentTime = System.currentTimeMillis();
            Log.d(TAG, "time for loading bitmap from assets: " + (currentTime - prevTime));
            Bitmap temp = Bitmap.createBitmap(fromAssets, width - height, 0, height, height);
            prevTime = currentTime;
            currentTime = System.currentTimeMillis();
            Log.d(TAG, "time for creating 1st cover: " + (currentTime - prevTime));
            FileUtil.saveJpeg90(temp, previewFileNameForPageImage(albumId, position));
            prevTime = currentTime;
            currentTime = System.currentTimeMillis();
            Log.d(TAG, "time for saving cover: " + (currentTime - prevTime));
            if (temp != fromAssets)
                temp.recycle();
            if (cropStateListener != null) {
                cropStateListener.onPictureCropped(position + 1, totalPositions);
            }
            position++;

            temp = Bitmap.createBitmap(fromAssets, 0, 0, height, height);
            prevTime = currentTime;
            currentTime = System.currentTimeMillis();
            Log.d(TAG, "time for creating last cover: " + (currentTime - prevTime));
            if (fromAssets != temp)
                fromAssets.recycle();
            FileUtil.saveJpeg90(temp, albumId + File.separator + "preview" + File.separator + "_last" + PREVIEW_ENDING);

            prevTime = currentTime;
            currentTime = System.currentTimeMillis();
            Log.d(TAG, "time for saving cover: " + (currentTime - prevTime));
            temp.recycle();

            for (int spreadPosition = 0, spreadsSize = spreads.size(); spreadPosition < spreadsSize; spreadPosition++) {
                Spread spread = spreads.get(spreadPosition);
                Page left = spread.getLeft();
                position = cropAndSavePagePreview(false, context, albumId, left, position, totalPositions, cropStateListener, spreadsSize);
                currentTime = System.currentTimeMillis();
                Page right = spread.getRight();
                position = cropAndSavePagePreview(true, context, albumId, right, position, totalPositions, cropStateListener, spreadsSize);
                currentTime = System.currentTimeMillis();
            }
            if (cropStateListener != null) {
                cropStateListener.onPictureCropping(position + 1, totalPositions);
            }
             FileUtil.renameAlbumFile(albumId + File.separator + "preview" + File.separator + "_last" + PREVIEW_ENDING, previewFileNameForPageImage(albumId, position));
            if (cropStateListener != null) {
                cropStateListener.onPictureCropped(position + 1, totalPositions);
            }
        }
        if (cropStateListener != null) {
            cropStateListener.onCroppingFinished();
        }
        System.gc();
        return true;
    }

    public static int cropAndSavePagePreview(boolean isRight, Context context, String albumId, Page page, int currentProgressPosition, int totalPositions, CropStateListener cropStateListener, int spreadsSize) {
        long pageCurrentTime = System.currentTimeMillis();
        long pagePrevTime;
        if (page != null && page.getPictures() != null) {
            Picture[] pictures = page.getPictures();
            int startPosition = currentProgressPosition;
            for (int imagePosition = 0, imagesSize = pictures.length; (cropStateListener == null || !cropStateListener.isCancelled()) && imagePosition < imagesSize; imagePosition++) {
                if (cropStateListener != null) {
                    cropStateListener.onPictureCropping(currentProgressPosition + 1, totalPositions);
                }
                Picture picture = pictures[imagePosition];

                //   updateForOriginalState(picture, state, settings);
                PictureViewState viewState = new PictureViewState();
                viewState.setImageH(picture.getViewState().getImageH()*2);
                viewState.setImageW(picture.getViewState().getImageW()*2);
                viewState.setViewportH(picture.getViewState().getViewportH()*2);
                viewState.setViewportW(picture.getViewState().getViewportW()*2);
                State state = picture.getMatrixState().toState();
                State state1 = new State();
                state1.set(state.getX()*2,state.getY()*2,state.getZoom(),state.getRotation());
                /*load original downsampled image*/
                Bitmap downSampledBitmap = PictureUtils.decodeSampledBitmapFromFile(picture.getPath(), viewState.getImageW(), viewState.getImageH());
                pagePrevTime = pageCurrentTime;
                pageCurrentTime = System.currentTimeMillis();
                Log.d(TAG, "time for loading current picture: " + (pageCurrentTime - pagePrevTime));
                /*scaling*/
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(downSampledBitmap,viewState.getImageW(), viewState.getImageH(), false);
                pagePrevTime = pageCurrentTime;
                pageCurrentTime = System.currentTimeMillis();
                Log.d(TAG, "time for loading scaling current picture: " + (pageCurrentTime - pagePrevTime));
                if (downSampledBitmap != scaledBitmap)
                    downSampledBitmap.recycle();
                downSampledBitmap = null;
                /*cropping*/
                Bitmap croppedBitmap = CropUtils.cropOrig(scaledBitmap,state1, viewState.toSettings());
                pagePrevTime = pageCurrentTime;
                pageCurrentTime = System.currentTimeMillis();
                Log.d(TAG, "time for loading cropping current picture: " + (pageCurrentTime - pagePrevTime));
                if (croppedBitmap != scaledBitmap)
                    scaledBitmap.recycle();
                scaledBitmap = null;
                //resizing, if template isn't PageTemplate.SINGLE
                if (imagesSize > 1) {
                    try {
                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(
                                croppedBitmap,
                                viewState.getViewportW() ,
                                viewState.getViewportH() ,
                                false);
                        croppedBitmap.recycle();
                        croppedBitmap = resizedBitmap;
                    } catch (OutOfMemoryError e) {
                        Log.e(TAG, e.getMessage());
                    }
                }

                if (croppedBitmap != null) {
                    if (picture.getFilter() != null) {
                        Bitmap bitmap = FilterUtils.createFiltered(croppedBitmap, FilterUtils.filterForClass(context, picture.getFilter()));
                        if (bitmap != croppedBitmap)
                            croppedBitmap.recycle();
                        croppedBitmap = bitmap;
                    }
                    if (pictures.length == 1 && currentProgressPosition != 0 && currentProgressPosition != spreadsSize - 1) {
                        int width = pictures[0].getViewState().getViewportW() * mSize, height = pictures[0].getViewState().getViewportH() * mSize;
                        Bitmap tempBitmap = Bitmap.createScaledBitmap(croppedBitmap, width * page.getTemplate().getWidthCount() - PdfConverter.PAGE_MARGIN_IMAGE / 2, height * page.getTemplate().getHeightCount(), false);
                        if (tempBitmap != croppedBitmap)
                            croppedBitmap.recycle();
                        Bitmap result = Bitmap.createBitmap(width * page.getTemplate().getWidthCount(), height * page.getTemplate().getHeightCount(), Bitmap.Config.ARGB_8888);
                        Canvas canvas = new Canvas(result);
                        canvas.drawColor(Color.WHITE);
                        canvas.drawBitmap(tempBitmap, isRight ? PdfConverter.PAGE_MARGIN_IMAGE / 2 : 0, 0, null);
                        tempBitmap.recycle();
                        croppedBitmap = result;
                    }
                    FileUtil.saveJpeg90(croppedBitmap, previewFileNameForPageImage(albumId, currentProgressPosition));
                    pagePrevTime = pageCurrentTime;
                    pageCurrentTime = System.currentTimeMillis();
                    Log.d(TAG, "time for saving picture: " + (pageCurrentTime - pagePrevTime));
                    croppedBitmap.recycle();
                    croppedBitmap = null;
                    if (cropStateListener != null) {
                        cropStateListener.onPictureCropped(currentProgressPosition + 1, totalPositions);
                    }
                    currentProgressPosition++;
                }
            }
            if (page.getPictures().length > 1) {
                Bitmap bitmap = combinePagePreview(startPosition, albumId, page.getTemplate(), page.getPictures());
                for (int i = startPosition; i < currentProgressPosition; i++) {
                    FileUtil.deleteAlbumFile(previewFileNameForPageImage(albumId, i));
                }
                if (currentProgressPosition != 0 && currentProgressPosition != spreadsSize - 1) {
                    Bitmap tempBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth() - PdfConverter.PAGE_MARGIN_IMAGE / 2, bitmap.getHeight(), false);
                    int width = pictures[0].getViewState().getViewportW() * mSize, height = pictures[0].getViewState().getViewportH() * mSize;
                    Bitmap result = Bitmap.createBitmap(width * page.getTemplate().getWidthCount(), height * page.getTemplate().getHeightCount(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(result);
                    canvas.drawColor(Color.WHITE);
                    canvas.drawBitmap(tempBitmap, isRight ? PdfConverter.PAGE_MARGIN_IMAGE / 2 : 0, 0, null);
                    tempBitmap.recycle();
                    bitmap.recycle();
                    bitmap = result;
                }
                FileUtil.saveJpeg90(bitmap, previewFileNameForPageImage(albumId, startPosition));
                bitmap.recycle();
                currentProgressPosition = startPosition + 1;
            }
        }
        return currentProgressPosition;
    }

    private static Bitmap combinePagePreview(int startPosition, String albumId, PageTemplate template, Picture[] pictures) {
        int width = pictures[0].getViewState().getViewportW() * mSize, height = pictures[0].getViewState().getViewportH() * mSize;
        Bitmap result = Bitmap.createBitmap(width * template.getWidthCount(), height * template.getHeightCount(), Bitmap.Config.ARGB_8888);
        Canvas combined = new Canvas(result);
        combined.drawColor(Color.WHITE);
        int position = startPosition;
        for (int h = 0; h < template.getHeightCount(); h++) {
            for (int w = 0; w < template.getWidthCount(); w++) {
                Bitmap current = FileUtil.getBitmapByFile(new File(FileUtil.getAlbumsFolder(), previewFileNameForPageImage(albumId, position)).getAbsolutePath());
                combined.drawBitmap(current, width * w + (PdfConverter.PAGE_MARGIN_IMAGE * w), height * h + (PdfConverter.PAGE_MARGIN_IMAGE * h), null);
                current.recycle();
                position++;
            }
        }
        return result;
    }

    private static Bitmap combinePageTemp(int spreadPosition, int pagePosition, String albumId, PageTemplate template, Picture[] pictures, boolean isThumbnail) {
        int width = PdfConverter.NORMAL_IMAGE_SIDE_SIZE, height = PdfConverter.NORMAL_IMAGE_SIDE_SIZE;
        Bitmap result = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        width /= template.getWidthCount();
        height /= template.getHeightCount();
        Canvas combined = new Canvas(result);
        combined.drawColor(Color.WHITE);
        int position = 0;
        for (int h = 0; h < template.getHeightCount(); h++) {
            for (int w = 0; w < template.getWidthCount(); w++) {
                Bitmap current = FileUtil.getBitmapByFile(new File(FileUtil.getAlbumsFolder(), fileNameForPageImage(albumId, spreadPosition, pagePosition, position, isThumbnail)).getAbsolutePath());
                //  current.setHeight(100);
                Log.d("beforeMarginSizeCombine", current.getWidth() + "x" + current.getHeight());
                combined.drawBitmap(current, width * w + (PdfConverter.PAGE_MARGIN_IMAGE * 2 * w), height * h + (PdfConverter.PAGE_MARGIN_IMAGE * 2 * h), null);
                current.recycle();
                position++;
            }
        }
        return result;
    }

    private static void createNoMediaInFolder(File folder) {
        File noMedia = new File(folder, ".nomedia");
        try {
            noMedia.createNewFile();
        } catch (IOException ignored) {
        }
    }

    public static boolean isSpreadPicturesCreated(String albumId) {
        File folder = new File(FileUtil.getAlbumsFolder(), albumId + File.separator + "temp" + File.separator);
        if (folder.exists() && folder.isDirectory()) {
            String[] list = folder.list();
            return list != null && list.length > 0;
        }
        return false;
    }

    public static void clearAlbumFolder(String albumId) {
        removePreviewAlbumFolder(albumId);
        removeTempAlbumFolder(albumId);

        PdfConverter.deletePdf(albumId, true);
        PdfConverter.deletePdf(albumId, false);
    }

    public static void removePreviewAlbumFolder(String albumId) {
        File folder = new File(FileUtil.getAlbumsFolder(), albumId + File.separator + "preview" + File.separator);
        if (folder.exists() && folder.isDirectory()) {
            deleteRecursive(folder);
        }
    }

    public static void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        Log.d("deleted", "" + fileOrDirectory.delete());
    }

    public static void removeTempAlbumFolder(String albumId) {
        File folder = new File(FileUtil.getAlbumsFolder(), albumId + File.separator + "temp" + File.separator);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
            folder.delete();
        }
    }

    public static boolean cropSpreadsAndSave(Context context, String albumId, List<Spread> spreads, String albumCover, CropStateListener cropStateListener) {
        long currentTime = System.currentTimeMillis();
        long prevTime;
        if (cropStateListener != null) {
            cropStateListener.onCroppingStarted();
        }
        File folder = new File(FileUtil.getAlbumsFolder(), albumId + File.separator + "temp" + File.separator);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile()) {
                        file.delete();
                    }
                }
            }
        } else {
            folder.mkdirs();
        }
        createNoMediaInFolder(folder);
        prevTime = currentTime;
        currentTime = System.currentTimeMillis();
        Log.d(TAG, "time for deleting: " + (currentTime - prevTime));
        if (spreads != null) {
            int totalPositions = 1;//first and last cover together
            for (Spread spread : spreads) {
                totalPositions += spread.getTotalImages();
            }
            int position = 1;//index starts from 1
            if (cropStateListener != null) {
                cropStateListener.onPictureCropping(position, totalPositions);
            }
            Bitmap fromAssets = FileUtil.getBitmapByFile(albumCover);

            prevTime = currentTime;
            currentTime = System.currentTimeMillis();
            Log.d(TAG, "time for loading cover from asset: " + (currentTime - prevTime));
            FileUtil.saveJpeg50(fromAssets, String.format(COVER_FILE_NAME, albumId) + THUMB_ENDING);
            prevTime = currentTime;
            currentTime = System.currentTimeMillis();
            Log.d(TAG, "time for saving jpg50 cover: " + (currentTime - prevTime));
            FileUtil.saveJpeg90(fromAssets, String.format(COVER_FILE_NAME, albumId) + FULL_SIZE_ENDING);
            if (cropStateListener != null) {
                cropStateListener.onPictureCropped(position, totalPositions);
            }
            position++;
            prevTime = currentTime;
            currentTime = System.currentTimeMillis();
            Log.d(TAG, "time for saving jpg 85 cover: " + (currentTime - prevTime));
            fromAssets.recycle();

            for (int spreadPosition = 0, spreadsSize = spreads.size(); (cropStateListener == null || !cropStateListener.isCancelled()) && spreadPosition < spreadsSize; spreadPosition++) {
                Spread spread = spreads.get(spreadPosition);
                position = cropAndSavePage(context, albumId, spreadPosition, spread.getLeft(), 0, position, totalPositions, cropStateListener);
                position = cropAndSavePage(context, albumId, spreadPosition, spread.getRight(), 1, position, totalPositions, cropStateListener);

            }
        }
        if (cropStateListener != null) {
            cropStateListener.onCroppingFinished();
        }
        System.gc();
        return true;
    }

    public static int cropAndSavePage(Context context, String albumId, int spreadPosition, Page page, int pagePosition, int currentProgressPosition, int totalPositions, CropStateListener cropStateListener) {
        long pageCurrentTime = System.currentTimeMillis();
        long pagePrevTime;
        if (page != null && page.getPictures() != null) {
            Picture[] pictures = page.getPictures();
            int startPosition = currentProgressPosition;
            for (int imagePosition = 0, imagesSize = pictures.length; (cropStateListener == null || !cropStateListener.isCancelled()) && imagePosition < imagesSize; imagePosition++) {
                if (cropStateListener != null) {
                    cropStateListener.onPictureCropping(currentProgressPosition, totalPositions);
                }
                Picture picture = pictures[imagePosition];
                State state = picture.getMatrixState().toState();
                Settings settings = picture.getViewState().toSettings();
                updateForOriginalState(picture, state, settings);

                /*load original image*/
                Bitmap fullSizedBitmap = FileUtil.getBitmapByFile(picture.getPath());
                pagePrevTime = pageCurrentTime;
                pageCurrentTime = System.currentTimeMillis();
                Log.d(TAG, "time for loading fullsized bitmap: " + (pageCurrentTime - pagePrevTime));
                /*scaling*/
                Bitmap scaledBitmap = CropUtils.cropOrig(fullSizedBitmap, state, settings);
                pagePrevTime = pageCurrentTime;
                pageCurrentTime = System.currentTimeMillis();
                Log.d(TAG, "time for cropping: " + (pageCurrentTime - pagePrevTime));
                if (scaledBitmap != fullSizedBitmap) {
                    fullSizedBitmap.recycle();
                }

                if (cropStateListener != null && cropStateListener.isCancelled() && scaledBitmap != fullSizedBitmap) {
                    scaledBitmap.recycle();
                    Log.d("recycleMy", "recycleMy");
                    break;
                }

                //resizing, if template isn't PageTemplate.SINGLE
                if (imagesSize > 1) {
                    try {
                        int w = PdfConverter.NORMAL_IMAGE_SIDE_SIZE / page.getTemplate().getWidthCount();
                        int h = PdfConverter.NORMAL_IMAGE_SIDE_SIZE / page.getTemplate().getHeightCount();
                        Log.d("MarginAfter", w + " x " + h);
                        /*if (page.getTemplate() == PageTemplate.FOUR) {
                            w -= PdfConverter.PAGE_MARGIN_IMAGE * 2;
                            h -= PdfConverter.PAGE_MARGIN_IMAGE * 2;
                            Log.d("Margin", "four " + w + " x " + h);
                        } else if (page.getTemplate() == PageTemplate.TWO_HORIZONTAL) {
                            w -= PdfConverter.PAGE_MARGIN_IMAGE * 2;
                            Log.d("Margin", "horizontal " + w + " x " + h);
                        } else {*/
                            h -= PdfConverter.PAGE_MARGIN_IMAGE * 2;
                            Log.d("Margin", "vertical " + w + " x " + h);

                        Log.d(TAG, "cropAndSavePage: " + scaledBitmap.getHeight() + " x " + scaledBitmap.getWidth());
                        Log.d(TAG, "cropAndSavePage2: " + h + " x " + w);
                        Bitmap resizedBitmap = null;
                        if (scaledBitmap.getWidth() == w && scaledBitmap.getHeight() == h)
                            scaledBitmap = Bitmap.createBitmap(scaledBitmap);
                        else
                            resizedBitmap = Bitmap.createScaledBitmap(scaledBitmap,
                                    w,
                                    h, false);
                     /*   Bitmap bmOverlay = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

                        Paint p = new Paint();
                        p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
                        Canvas c = new Canvas(bmOverlay);

                        c.drawBitmap(scaledBitmap, 0, 0, null);*/
                        if (scaledBitmap != resizedBitmap) {
                            scaledBitmap.recycle();
                            scaledBitmap = resizedBitmap;
                        }
                        Log.d(TAG, "cropAndSavePage3: " + scaledBitmap.getHeight() + " x " + scaledBitmap.getWidth());
                        Log.d("beforeMarginSize", scaledBitmap.getWidth() + " x " + scaledBitmap.getHeight());
                    } catch (OutOfMemoryError e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                /*filtering*/
                if (picture.getFilter() != null) {
                    try {
                        scaledBitmap = FilterUtils.createFiltered(scaledBitmap, FilterUtils.filterForClass(context, picture.getFilter()));
                    } catch (OutOfMemoryError e) {
                        Log.e(TAG, e.getMessage());
                    }
                }
                /*brightness+5*//*
                try {
                    Bitmap brighterBitmap= Bitmap.createBitmap(scaledBitmap.getWidth(),scaledBitmap.getHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas=new Canvas(brighterBitmap);
                    Paint paint=new Paint();
                    paint.setColorFilter(new PorterDuffColorFilter(Color.argb(5, 255, 255, 255), PorterDuff.Mode.SRC_OVER));
                    canvas.drawBitmap(scaledBitmap,0f,0f,paint);
                    scaledBitmap.recycle();
                    scaledBitmap=brighterBitmap;
                }catch (OutOfMemoryError e){
                    Log.e(TAG,e.getProgress());
                }*/

                FileUtil.saveJpeg50(scaledBitmap, fileNameForPageImage(albumId, spreadPosition, pagePosition, imagePosition, true));
                pagePrevTime = pageCurrentTime;
                pageCurrentTime = System.currentTimeMillis();
                Log.d(TAG, "time for saving jpg50: " + (pageCurrentTime - pagePrevTime));
                if (cropStateListener != null && cropStateListener.isCancelled()) {
                    scaledBitmap.recycle();
                    break;
                }
                FileUtil.saveJpeg90(scaledBitmap, fileNameForPageImage(albumId, spreadPosition, pagePosition, imagePosition, false));
                pagePrevTime = pageCurrentTime;
                pageCurrentTime = System.currentTimeMillis();
                Log.d(TAG, "time for saving jpg100: " + (pageCurrentTime - pagePrevTime));

                scaledBitmap.recycle();
                Log.w(TAG, ("thumbnail and original") + spreadPosition + ":" + imagePosition + " saved");
                if (cropStateListener != null) {
                    cropStateListener.onPictureCropped(currentProgressPosition, totalPositions);
                }
                currentProgressPosition++;
            }
            if (page.getPictures().length > 1) {
                Bitmap bitmap = combinePageTemp(spreadPosition, pagePosition, albumId, page.getTemplate(), page.getPictures(), false);
                for (int i = 0; i < currentProgressPosition; i++) {
                    FileUtil.deleteAlbumFile(fileNameForPageImage(albumId, spreadPosition, pagePosition, i, false));
                }
                FileUtil.saveJpeg90(bitmap, fileNameForPageImage(albumId, spreadPosition, pagePosition, 0, false));
                bitmap.recycle();

                bitmap = combinePageTemp(spreadPosition, pagePosition, albumId, page.getTemplate(), page.getPictures(), true);
                for (int i = 0; i < currentProgressPosition; i++) {
                    FileUtil.deleteAlbumFile(fileNameForPageImage(albumId, spreadPosition, pagePosition, i, true));
                }
                FileUtil.saveJpeg50(bitmap, fileNameForPageImage(albumId, spreadPosition, pagePosition, 0, true));
                bitmap.recycle();
                currentProgressPosition = startPosition + 1;
            }
        }
        return currentProgressPosition;
    }

    public static void updateForOriginalState(Picture picture, State state, Settings settings) {
        int thumbnailWidth = settings.getImageW();
        int thumbnailHeight = settings.getImageH();
        Matrix matrix = new Matrix();
        state.get(matrix);

        float[] beforeValues = new float[9];
        matrix.getValues(beforeValues);

        float scaleX = beforeValues[Matrix.MSCALE_X];
        float scaleY = beforeValues[Matrix.MSCALE_Y];

        matrix.postScale(1 / scaleX, 1 / scaleY);

        int width = picture.getOrigWidth();
        int height = picture.getOrigHeight();
        float origScaleX = thumbnailWidth / (float) width;
        float origScaleY = thumbnailHeight / (float) height;
        float[] afterValues = new float[9];
        matrix.getValues(afterValues);
        //тут вытаскиваем сдвиг.
        afterValues[Matrix.MTRANS_X] /= origScaleX;
        afterValues[Matrix.MTRANS_Y] /= origScaleY;
        matrix.setValues(afterValues);

        matrix.postScale(scaleX, scaleY);
        //тут у нас матрица сформирована уже нормальная, которая должна подходить для сдвига оригинального изображения

        float[] finalValues = new float[9];
        matrix.getValues(finalValues);
        state.set(matrix);

        settings.setViewport((int) (settings.getViewportW() / origScaleX), (int) (settings.getViewportH() / origScaleY));
    }


    public interface CropStateListener {

        boolean isCancelled();

        void onCroppingStarted();

        void onPictureCropping(int position, int totalCount);

        void onPictureCropped(int position, int totalCount);

        void onCroppingFinished();
    }
}
