package ru.lucky_book.pdf;

import android.util.Log;

import com.example.luckybookpreview.utils.FileUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Utilities;
import com.itextpdf.text.pdf.PdfName;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import ru.lucky_book.database.RealmAlbum;
import ru.lucky_book.entities.spread.Spread;
import ru.lucky_book.utils.AlbumUtils;
import ru.lucky_book.utils.PageUtils;

/**
 * Created by histler
 * on 25.08.16 14:27.
 */
public final class PdfConverter {
    private static final String TAG = "PdfConverter";
    public static final int COVER_WIDTH_WITH_FOLD = (int) Utilities.millimetersToPoints(450);
    public static final int COVER_HEIGHT_WITH_FOLD = (int) Utilities.millimetersToPoints(236);

    public static final int PAGE_WIDTH_WITH_FOLD = (int) Utilities.millimetersToPoints(406);
    public static final int PAGE_HEIGHT_WITH_FOLD = (int) Utilities.millimetersToPoints(206);

    public static final int PAGE_MARGIN_IMAGE = (int) Utilities.millimetersToPoints(1);
    public static final int PAGE_MARGIN_PAGE = (int) Utilities.millimetersToPoints(1);

    //  public static final int PAGE_MARGIN_IMAGE = (int) Utilities.millimetersToPoints(3);//we take margin as 1 mm, so after real cropping, we still has normal image

    public static final Rectangle COVER = new Rectangle(COVER_WIDTH_WITH_FOLD, COVER_HEIGHT_WITH_FOLD);
    public static final Rectangle PAGE = new Rectangle(PAGE_WIDTH_WITH_FOLD, PAGE_HEIGHT_WITH_FOLD);

    public static final Rectangle HALF_PAGE = new Rectangle(PAGE_WIDTH_WITH_FOLD / 2 - (PAGE_MARGIN_PAGE) / 2, PAGE_HEIGHT_WITH_FOLD);
    /*to set the correct dpi, You must divide points by (72/your_dpi) */
    public static final int MIN_IMAGE_SIDE_SIZE = 800;
    public static final int NORMAL_IMAGE_SIDE_SIZE = (int) ((float) PAGE_HEIGHT_WITH_FOLD / 0.24f);

    public static boolean isPdfExists(String albumId, boolean isThumbnails) {
        return getPdfFile(albumId, isThumbnails).exists();
    }

    public static File getPdfFile(String albumId, boolean isThumbnails) {
        return getPdfFile(albumId, (isThumbnails ? "thumbnail" : "fullsized"));
    }

    public static File getPdfFile(String albumId, String pdfName) {
        return new File(AlbumUtils.getAlbumFolder(albumId), pdfName + ".pdf");
    }

    public static void deletePdf(String albumId, boolean isThumbnail) {
        File pdf = getPdfFile(albumId, isThumbnail);
        if (pdf.exists()) {
            pdf.delete();
        }
    }

    public static String generatePdf(String albumId, List<Spread> spreads, boolean isThumbnails, PdfGenerationListener listener) throws IOException, DocumentException {
        Document document = new Document(PAGE, 0, 0, 0, 0);

        if (listener != null) {
            listener.onPdfGenerationStarted();
        }
        deletePdf(albumId, isThumbnails);
        File pdfFile = getPdfFile(albumId, isThumbnails);
        FileOutputStream output = new FileOutputStream(pdfFile);
        PdfWriter pdfWriter = PdfWriter.getInstance(document, output);
        //pdfWriter.setCompressionLevel(9);
        pdfWriter.setDefaultColorspace(PdfName.COLORSPACE, PdfName.DEFAULTCMYK);
        document.open();


            /*adding pages*/
        document.setPageSize(COVER);
        document.newPage();
        int spreadsSize = spreads.size();
        if (listener != null) {
            listener.onPdfPageGenerating(1, spreadsSize + 1);
        }
        String coverImagePath = new File(FileUtil.getAlbumsFolder(), String.format(PageUtils.COVER_FILE_NAME, albumId) + (isThumbnails ? PageUtils.THUMB_ENDING : PageUtils.FULL_SIZE_ENDING)).getAbsolutePath();
        if (listener != null && listener.isCancelled()) {
            document.close();
            deletePdf(albumId, isThumbnails);
            return null;
        }
        Image coverImage = Image.getInstance(coverImagePath);
        coverImage.scaleToFit(COVER);
        document.add(coverImage);

        document.setPageSize(PAGE);

        for (int spreadPosition = 0; (listener == null || !listener.isCancelled()) && spreadPosition < spreadsSize; spreadPosition++) {
            if (listener != null) {
                listener.onPdfPageGenerating(spreadPosition + 2, spreadsSize + 1);
            }
            document.newPage();

            for (int pagePosition = 0, pagesSize = 2; pagePosition < pagesSize; pagePosition++) {
                String croppedImagePath = new File(FileUtil.getAlbumsFolder(), (PageUtils.fileNameForPageImage(albumId, spreadPosition, pagePosition, 0, isThumbnails))).getAbsolutePath();
                if (listener != null && listener.isCancelled()) {
                    document.close();
                    deletePdf(albumId, isThumbnails);
                    return null;
                }

                Image croppedImage = Image.getInstance(croppedImagePath);
                croppedImage.scaleToFit(HALF_PAGE);
                croppedImage.scaleAbsolute(HALF_PAGE.getWidth(), HALF_PAGE.getHeight());
                if (pagePosition == 0)
                    croppedImage.setAbsolutePosition(PAGE_WIDTH_WITH_FOLD * pagePosition / pagesSize, 0);
                else {
                    croppedImage.setAbsolutePosition(PAGE_WIDTH_WITH_FOLD * pagePosition / pagesSize + PAGE_MARGIN_PAGE / 2, 0);
                }
                document.add(croppedImage);
            }
            Log.w(TAG, "leaf " + spreadPosition + " created");
        }
        document.close();
        if (listener != null) {
            listener.onPdfGenerationFinished();
        }
        return pdfFile.getAbsolutePath();
    }

    public interface PdfGenerationListener {
        boolean isCancelled();

        void onPdfGenerationStarted();

        void onPdfPageGenerating(int position, int totalCount);

        void onPdfGenerationFinished();
    }

    public static boolean isCorrectPdf(RealmAlbum realmAlbum, boolean isThumb) {
        if (isThumb) {
            return realmAlbum.getThumbnailPath() != null;
        }
        return realmAlbum.getFullSizePath() != null;
    }
}
