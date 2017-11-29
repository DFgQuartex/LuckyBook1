package ru.lucky_book.features.preview_screen.utils;

import android.content.Context;
import android.webkit.MimeTypeMap;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.FileContent;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.Permission;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import ru.lucky_book.R;
import ru.lucky_book.utils.listeners.DriveFileUploadProgressListener;

public class GoogleDriveHelper {

    private Context mContext;
    private Drive mDrive;

    private GoogleDriveHelper(Context context, String token) {
        mContext = context;
        initCredential(token);
    }

    public static GoogleDriveHelper newInstance(Context context, String token) {
        return new GoogleDriveHelper(context, token);
    }

    public String uploadFile(File file, DriveFileUploadProgressListener progressListener) {
        if (mDrive != null) {
            String extension = MimeTypeMap.getFileExtensionFromUrl(file.getAbsolutePath());
            if (extension != null) {
                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
                com.google.api.services.drive.model.File body = new com.google.api.services.drive.model.File();
                body.setTitle(file.getName());
                FileContent content = new FileContent(mimeType, file);
                try {
                    Drive.Files.Insert insert=mDrive.files().insert(body, content);
                    if(progressListener!=null) {
                        progressListener.onStreamSet(content.getInputStream());
                        insert.getMediaHttpUploader().setProgressListener(progressListener);
                    }
                    com.google.api.services.drive.model.File result = insert.execute();
                    if (result != null) {
                        return share(result);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    private String share(com.google.api.services.drive.model.File file) {
        Permission permission = new Permission()
                .setType("anyone")
                .setRole("reader");
        try {
            Permission responsePermission = mDrive.permissions().insert(file.getId(), permission).execute();
            if (responsePermission != null) {
                return file.getAlternateLink();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initCredential(String token) {
        try {
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            GoogleClientSecrets secrets = loadClientSecretsResource(mContext, jsonFactory);
            GoogleCredential credential = new GoogleCredential.Builder()
                    .setClientSecrets(secrets)
                    .setJsonFactory(jsonFactory)
                    .setTransport(transport)
                    .build();
            credential.setAccessToken(token);
            mDrive = new Drive.Builder(transport, jsonFactory, credential)
                    .setApplicationName(mContext.getString(R.string.app_name))
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static GoogleClientSecrets loadClientSecretsResource(Context context, JsonFactory jsonFactory) throws IOException {
        return GoogleClientSecrets.load(
                jsonFactory,
                new InputStreamReader(
                        context.getAssets().open("client_id.json"), "UTF-8"));
    }

}
