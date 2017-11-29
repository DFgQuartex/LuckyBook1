package ru.lucky_book.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.alexvasilkov.gestures.Settings;
import com.alexvasilkov.gestures.State;
import com.example.luckybookpreview.utils.PictureUtils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import me.nereo.multi_image_selector.bean.Image;
import ru.lucky_book.R;
import ru.lucky_book.entities.spread.Page;
import ru.lucky_book.entities.spread.PageTemplate;
import ru.lucky_book.entities.spread.Picture;
import ru.lucky_book.entities.spread.PictureMatrixState;
import ru.lucky_book.entities.spread.PictureViewState;
import ru.lucky_book.entities.spread.Spread;
import ru.lucky_book.entities.spread.template.TemplatePicture;
import ru.lucky_book.features.spreads.PictureClickListener;
import ru.lucky_book.network.utils.DownloadUtil;
import ru.lucky_book.utils.transformation.CropTransformation;
import ru.lucky_book.utils.transformation.FilterTransformation;

/**
 * Created by histler
 * on 29.08.16 16:04.
 * generate spreads for view by pages with templates
 */
public final class SpreadUtils {
    public static final int PREVIEW_SCALE = 2;

    private volatile static List<Target> STATIC_TARGETS = new ArrayList<>();

    public static void bindPage(Page page, ViewGroup pageView, final boolean isPreview, View.OnTouchListener onTouchListener) {
        bindPage(page, pageView, isPreview, null, onTouchListener);
    }

    public static void bindPage(Page page, ViewGroup pageView, final boolean isPreview, View.OnTouchListener onTouchListener, int pictureNull) {
        bindPage(page, pageView, isPreview, null, onTouchListener, pictureNull);
    }

    public static void bindPage(Page page, ViewGroup pageView, boolean isPreview, PictureClickListener onPictureClickListener, View.OnTouchListener onTouchListener) {
        pageView.setActivated(false);
        bindPage(page, pageView, isPreview, onPictureClickListener, onTouchListener, R.drawable.plus_img2);
    }

    public static void bindPage(Page page, ViewGroup pageView, boolean isPreview, PictureClickListener onPictureClickListener, View.OnTouchListener onTouchListener, int pictureNull) {
        pageView.removeAllViews();
        /*if(page==null){
            return;
        }*/
        PageTemplate template = page != null ? page.getTemplate() : PageTemplate.SINGLE;
        Context context = pageView.getContext();
        ViewGroup innerView = (ViewGroup) View.inflate(context, template.getLayoutResId(), pageView).findViewById(R.id.images_holder);

        for (int i = 0, size = template.getImagesCount(); i < size; i++) {
            ImageView view = (ImageView) innerView.getChildAt(i).findViewById(R.id.image);
            view.setOnTouchListener(onTouchListener);
            view.setPadding(1, 1, 1, 1);
            bindPicture(view, page, i, isPreview, onPictureClickListener, pictureNull);
        }
    }

    public static void bindPicture(final ImageView view, final Page page, final int position, final boolean isPreview, final PictureClickListener onPictureClickListener) {
        bindPicture(view, page, position, isPreview, onPictureClickListener, R.drawable.plus_img2);
    }

    public static void bindPicture(final ImageView view, final Page page, final int position, final boolean isPreview, final PictureClickListener onPictureClickListener, int pictureNull) {
        final Picture picture = page != null ? page.getPictures()[position] : null;
        if (picture != null) {
            if (picture instanceof TemplatePicture) {
                Picasso.with(view.getContext())
                        .load(R.drawable.template_img2)
                        .fit()
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .centerCrop()
                        .into(view);
            } else {
                if (picture.getOrigHeight() == 0 || picture.getOrigWidth() == 0) {
                    int[] sizes = BitmapUtils.getBitmapSizes(picture.getPath());
                    picture.setOrigWidth(sizes[0]);
                    picture.setOrigHeight(sizes[1]);
                }

                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        STATIC_TARGETS.remove(this);
                        view.setImageBitmap(bitmap);
                        if (!isPreview) {
                            if (onPictureClickListener != null) {
                                view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        onPictureClickListener.onPictureClick(view, page, position);
                                    }
                                });
                            }
                        } else {
                            if (picture.getMatrixState() != null) {
                                State copy = picture.getMatrixState().toState();
                                float translateX = copy.getX();
                                float translateY = copy.getY();
                                copy.translateTo(translateX / PREVIEW_SCALE, translateY / PREVIEW_SCALE);
                            }
                        }
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
                        STATIC_TARGETS.remove(this);
                        view.setImageDrawable(errorDrawable);
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                        view.setImageDrawable(placeHolderDrawable);
                    }
                };
                STATIC_TARGETS.add(target);

                int scale = (isPreview ? PREVIEW_SCALE : 1);

                int resultWidth = picture.getViewState().getImageW() / scale;
                int resultHeight = picture.getViewState().getImageH() / scale;
                int orientation = PictureUtils.getBitmapOrientation(picture.getPath());
                boolean isRotated = orientation == ExifInterface.ORIENTATION_ROTATE_90 || orientation == ExifInterface.ORIENTATION_ROTATE_270;


                State copy = picture.getMatrixState().toState();
                float translateX = copy.getX();
                float translateY = copy.getY();
                copy.translateTo(translateX / scale, translateY / scale);

                Settings settingsCopy = picture.getViewState().toSettings();
                settingsCopy.setViewport(settingsCopy.getViewportW() / scale, settingsCopy.getViewportH() / scale);

                RequestCreator creator = Picasso.with(view.getContext())
                        .load(new File(picture.getPath()))
                        .error(R.drawable.template_img2)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .resize(isRotated ? resultHeight : resultWidth, isRotated ? resultWidth : resultHeight)
                        .transform(new CropTransformation(copy, settingsCopy));
                if (picture.getFilter() != null) {
                    creator.transform(new FilterTransformation(view.getContext(), picture.getFilter()));
                }
                creator.into(target);
            }
        } else {
            if (!isPreview) {
                if (onPictureClickListener != null) {
                    view.setOnClickListener(v -> onPictureClickListener.onPictureClick(view, page, position));
                }
            }
            Picasso.with(view.getContext())
                    .load(pictureNull)
                    .fit()
                    .centerCrop()
                    .into(view);
        }
    }

    public static void initSpreads(List<Spread> spreads, int viewWidth, int viewHeight) {
        if (spreads != null) {
            for (Spread spread : spreads) {
                initSpread(spread, viewWidth, viewHeight);
            }
        }
    }

    public static void initSpread(Spread spread, int viewWidth, int viewHeight) {
        if (spread.getLeft() != null && spread.getLeft().getPictures() != null) {
            Page left = spread.getLeft();
            initPage(left, viewWidth, viewHeight);
        }
        if (spread.getRight() != null && spread.getRight().getPictures() != null) {
            Page right = spread.getRight();
            initPage(right, viewWidth, viewHeight);
        }
    }

    public static void initPage(Page page, int viewWidth, int viewHeight) {
        for (Picture picture : page.getPictures()) {
            if (picture != null) {
                centerCropPicture(picture, page.getTemplate(), viewWidth, viewHeight);
            }
        }
    }

    public static void centerCropPicture(Picture picture, PageTemplate pageTemplate, int viewWidth, int viewHeight) {
        float pictureScale = Math.min((float) picture.getOrigWidth() / viewWidth, (float) picture.getOrigHeight() / viewHeight);
        pictureScale *= Math.min(pageTemplate.getWidthCount(), pageTemplate.getHeightCount());
        int width = (int) (picture.getOrigWidth() / (pictureScale));
        int height = (int) (picture.getOrigHeight() / (pictureScale));

        int vWidth = viewWidth / pageTemplate.getWidthCount();
        int vHeight = viewHeight / pageTemplate.getHeightCount();

        float scale;
        float dx = 0, dy = 0;

        if (width * vHeight > vWidth * height) {
            scale = (float) vHeight / (float) height;
            dx = (vWidth - width * scale) * 0.5f;
        } else {
            scale = (float) vWidth / (float) width;
            dy = (vHeight - height * scale) * 0.5f;
        }
        PictureMatrixState state = new PictureMatrixState();
        state.set(Math.round(dx), Math.round(dy), scale, 0f);
        picture.setMatrixState(state);
        Settings settings = new Settings();
        settings.setImage(width, height);
        settings.setViewport(vWidth, vHeight);
        PictureViewState viewState = new PictureViewState();
        viewState.fromSettings(settings);
        picture.setViewState(viewState);
    }

    public static List<Spread> makeSpreads(List<String> imagePaths) {
        List<Spread> spreads = new ArrayList<>();
        if (imagePaths != null) {
            for (int i = 0, size = imagePaths.size(); i < size; ) {
                Spread spread = new Spread();

                Page left = new Page();
                int picturesSize = left.getTemplate().getImagesCount();
                left.setPictures(new Picture[picturesSize]);
                int position = 0;
                for (picturesSize = picturesSize + i; i < size && i < picturesSize; i++) {
                    String path = imagePaths.get(i);
                    left.getPictures()[position] = initPicture(path);
                    position++;
                }
                spread.setLeft(left);

                Page right = new Page();
                picturesSize = right.getTemplate().getImagesCount();
                right.setPictures(new Picture[picturesSize]);
                position = 0;
                for (picturesSize = picturesSize + i; i < size && i < picturesSize; i++) {
                    String path = imagePaths.get(i);
                    right.getPictures()[position] = initPicture(path);
                    position++;
                }
                spread.setRight(right);

                spreads.add(spread);
            }
        }
        return spreads;
    }

    private static Picture initPicture(String path) {
        Picture picture = new Picture();
        picture.setPath(path);
        int[] sizes = BitmapUtils.getBitmapSizes(picture.getPath());
        picture.setOrigWidth(sizes[0]);
        picture.setOrigHeight(sizes[1]);
        return picture;
    }

    public static Picture initPicture(Image image) {
        return initPicture(image.isLocal ? image.path : DownloadUtil.getLocalPath(image));
    }

    public static Spread getFirstNotFilled(List<Spread> spreads) {
        for (Spread spread : spreads) {
            if (spread.getLeft() != null) {
                Page left = spread.getLeft();
                for (Picture picture : left.getPictures()) {
                    if (picture == null) {
                        return spread;
                    }
                }
            } else {
                return spread;
            }
            if (spread.getRight() != null) {
                Page right = spread.getRight();
                for (Picture picture : right.getPictures()) {
                    if (picture == null) {
                        return spread;
                    }
                }
            } else {
                return spread;
            }
        }
        return null;
    }

    public static Spread createEmptySpread() {
        Spread spread = new Spread();
        Page page = new Page();
        page.setTemplate(PageTemplate.SINGLE);
        spread.setLeft(page);
        page = new Page();
        page.setTemplate(PageTemplate.SINGLE);
        spread.setRight(page);
        return spread;
    }
}
