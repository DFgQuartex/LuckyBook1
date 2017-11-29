package ru.lucky_book.database;

import android.util.Log;

import com.example.luckybookpreview.utils.FileUtil;

import java.io.File;
import java.util.Date;

import io.realm.DynamicRealm;
import io.realm.FieldAttribute;
import io.realm.RealmMigration;
import io.realm.RealmSchema;

/**
 * Created by histler
 * on 02.09.16 17:21.
 */
public class DbMigration implements RealmMigration {
    public static final String TAG = DbMigration.class.getSimpleName();

    @Override
    public void migrate(DynamicRealm realm, long oldVersion, long newVersion) {
        RealmSchema schema = realm.getSchema();
        if (oldVersion == 0) {
            schema
                    .get(RealmAlbum.class.getSimpleName())
                    .addField("thumbnailPath", String.class)
                    .addField("fullSizePath", String.class);
            oldVersion++;
        }
        if (oldVersion == 1) {
            schema.get(RealmAlbum.class.getSimpleName())
                    .addField("updateTime", Date.class);
            oldVersion++;
        }
        if (oldVersion == 2) {
            realm.delete(RealmAlbum.class.getSimpleName());
            schema.get(RealmAlbum.class.getSimpleName())
                    .addPrimaryKey("id");
            oldVersion++;
        }
        if (oldVersion == 3) {
            schema.get(RealmPicture.class.getSimpleName())
                    .addField("filter", String.class);
            oldVersion++;
        }
        if (oldVersion == 4) {
            File albumsFolder = FileUtil.getAppFolder();
            if (albumsFolder.exists() && albumsFolder.isDirectory()) {
                File[] albumFolders = albumsFolder.listFiles();
                if (albumFolders != null) {
                    albumsFolder = FileUtil.getAlbumsFolder();
                    for (File albumFolder : albumFolders) {
                        boolean success = albumFolder.renameTo(new File(albumsFolder, File.separator + albumFolder.getName()));
                        Log.d(TAG, "moving album " + albumFolder.getName() + " folder:" + (success ? "success" : "error"));
                    }
                }
            }
            oldVersion++;
        }
        if (oldVersion == 5) {
            schema.get(RealmAlbum.class.getSimpleName())
                    .addField("coverId", int.class)
                    .addField("promoCode", String.class);
            oldVersion++;
        }
        if (oldVersion == 6) {
            schema.get(RealmAlbum.class.getSimpleName())
                    .addField("fullSizePathLocal", String.class);
            oldVersion++;
        }
        if (oldVersion == 7) {
            schema.get(RealmAlbum.class.getSimpleName())
                    .addField("statusUpload", String.class);
            oldVersion++;
        }
        if (oldVersion == 8) {
            schema.get(RealmAlbum.class.getSimpleName())
                    .addField("statusPayment", String.class)
                    .addField("payTransaction", String.class);
            oldVersion++;
        }
        if (oldVersion == 9) {
            schema.get(RealmAlbum.class.getSimpleName())
                    .addField("maxSize", int.class);
            oldVersion++;
        }
        if (oldVersion == 10) {
            schema.create("RealmSubCoverAlbum")
                    .addField("id", int.class, FieldAttribute.PRIMARY_KEY)
                    .addField("pathThumb", String.class)
                    .addField("pathOriginal", String.class)
                    .addField("priority", int.class);

            schema.create("RealmCoverAlbum")
                    .addField("id", int.class, FieldAttribute.PRIMARY_KEY)
                    .addField("name", String.class)
                    .addField("pathThumb", String.class)
                    .addField("url", String.class)
                    .addField("priority", int.class)
                    .addRealmListField("mSubcoverAlbumList", schema.get("RealmSubCoverAlbum"));
            oldVersion++;
        }
        if(oldVersion==11){
            schema.get(RealmAlbum.class.getSimpleName())
                    .addField("coverPromo",boolean.class);

        }
    }
}
