package ru.lucky_book.features.order_screen.dropbox_integration;

import android.content.Context;

import com.dropbox.core.DbxException;
import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.NetworkIOException;
import com.dropbox.core.RetryException;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.CommitInfo;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.UploadErrorException;
import com.dropbox.core.v2.files.UploadSessionCursor;
import com.dropbox.core.v2.files.UploadSessionFinishErrorException;
import com.dropbox.core.v2.files.UploadSessionLookupErrorException;
import com.dropbox.core.v2.files.WriteMode;
import com.dropbox.core.v2.sharing.SharedLinkMetadata;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ru.lucky_book.R;
import ru.lucky_book.features.order_screen.dropbox_integration.exception.ChunkSizeSmallException;

public class DropboxHelper {

    private static final String APP_KEY = "pfvkh7c4nv20gph";
    private static final String APP_SECRET = "qm5xelxwlr6dvej";
    private static final String SAMPLE_TOKEN = "zuiNbIsdfAAAAAAAAAAADlt0XB2DFfHv-jzV8RyJAsiQHDrGiIWBFelQ5_tW1uUC";

    private static final long CHUNKED_UPLOAD_CHUNK_SIZE = 2L << 20; // 2MiB
    private static final int CHUNKED_UPLOAD_MAX_ATTEMPTS = 5;

    private Context mContext;
    private DbxClientV2 mClient;
    private Date mCreateDate;
    private String mUserFullName;

    private DropboxHelper(Context context, Date createDate, String userFullName) {
        mContext = context;
        mCreateDate = createDate;
        mUserFullName = userFullName;
        initClient();
    }

    public static DropboxHelper newInstance(Context context, Date createDate, String userFullName) {
        return new DropboxHelper(context, createDate, userFullName);
    }

    private void initClient() {
        if (mClient == null) {
            String userLocale = Locale.getDefault().toString();
            DbxRequestConfig config = DbxRequestConfig.newBuilder(mContext.getResources().getString(R.string.app_name))
                    .withUserLocale(userLocale)
                    .withAutoRetryEnabled(5)
                    .build();
            mClient = new DbxClientV2(config, SAMPLE_TOKEN);
        }
    }

    public String uploadFile(File file) {
        if (mClient != null) {
            String folderName = createFolder();
            String fileName = String.format("%s_%s.pdf", mUserFullName, System.currentTimeMillis());
            try (InputStream stream = new FileInputStream(file)) {
                String remoteName = String.format("/%s/%s", folderName, fileName);
                FileMetadata metadata = mClient.files().uploadBuilder(remoteName).withMode(WriteMode.OVERWRITE).uploadAndFinish(stream);
                return createShareLink(metadata);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UploadErrorException e) {
                e.printStackTrace();
            } catch (DbxException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public String chunkUpload(File file, ProgressListener listener) throws ChunkSizeSmallException, DbxException {
        if (mClient != null) {
            long size = file.length();

            if (size < CHUNKED_UPLOAD_CHUNK_SIZE) {
                throw new ChunkSizeSmallException("File too small. Use upload instead");
            }
            String folderName = createFolder();
            String fileName = String.format("%s_%s.pdf", mUserFullName, System.currentTimeMillis());
            String remoteName = String.format("/%s/%s", folderName, fileName);
            long uploaded = 0L;
            DbxException thrown = null;
            String sessionId = null;
            for (int i = 0; i < CHUNKED_UPLOAD_MAX_ATTEMPTS; i++) {
                try (InputStream in = new FileInputStream(file)) {
                    in.skip(uploaded);

                    if (sessionId == null) {
                        sessionId = mClient.files().uploadSessionStart()
                                .uploadAndFinish(in, CHUNKED_UPLOAD_CHUNK_SIZE)
                                .getSessionId();
                        uploaded += CHUNKED_UPLOAD_CHUNK_SIZE;

                        if (listener != null) {
                            listener.onStreamSet(in);
                            listener.onProgress((double) uploaded);
                        }

                    }
                    UploadSessionCursor cursor = new UploadSessionCursor(sessionId, uploaded);
                    while ((size - uploaded) > CHUNKED_UPLOAD_CHUNK_SIZE) {
                        mClient.files().uploadSessionAppendV2(cursor)
                                .uploadAndFinish(in, CHUNKED_UPLOAD_CHUNK_SIZE);
                        uploaded += CHUNKED_UPLOAD_CHUNK_SIZE;

                        if (listener != null) {
                            listener.onProgress((double) uploaded);
                        }
                        cursor = new UploadSessionCursor(sessionId, uploaded);
                    }

                    long remaining = size - uploaded;
                    CommitInfo commitInfo = CommitInfo.newBuilder(remoteName)
                            .withMode(WriteMode.OVERWRITE)
                            .withClientModified(new Date(file.lastModified()))
                            .build();
                    FileMetadata fileMetadata = mClient.files().uploadSessionFinish(cursor, commitInfo).uploadAndFinish(in, remaining);
                    return createShareLink(fileMetadata);
                } catch (RetryException e) {
                    thrown = e;
                    continue;
                } catch (NetworkIOException e) {
                    thrown = e;
                    continue;
                } catch (UploadSessionLookupErrorException e) {
                    if (e.errorValue.isIncorrectOffset()) {
                        thrown = e;
                        uploaded = e.errorValue
                                .getIncorrectOffsetValue()
                                .getCorrectOffset();
                        continue;
                    } else {
                        return null;
                    }
                } catch (UploadSessionFinishErrorException e) {
                    if (e.errorValue.isLookupFailed() && e.errorValue.getLookupFailedValue().isIncorrectOffset()) {
                        thrown = e;
                        uploaded = e.errorValue
                                .getLookupFailedValue()
                                .getIncorrectOffsetValue()
                                .getCorrectOffset();
                        continue;
                    } else {
                        return null;
                    }
                } catch (DbxException e) {
                    return null;
                } catch (IOException e) {
                    return null;
                }
            }
            if (thrown != null) {
                throw thrown;
            }
        }
        return null;

    }

    private String createShareLink(FileMetadata fileMetadata) throws DbxException {
        if (fileMetadata != null) {
            SharedLinkMetadata link = mClient.sharing().createSharedLinkWithSettings(fileMetadata.getPathLower());
            if (link != null) {
                return link.getUrl();
            }
        }
        return null;
    }

    private String createFolder() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String folderName = sdf.format(mCreateDate);
        try {
            mClient.files().createFolder(String.format("/%s", folderName));
        } catch (DbxException e) {
            e.printStackTrace();
        }
        return folderName;
    }

    public interface ProgressListener {
        void onStreamSet(InputStream inputStream);
        void onProgress(double progress);
    }

}
