package jp.ac.hal.messagebottle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.Date;

/**
 * Created by muto.masakazu on 2017/09/04.
 * NCMBで使用するファイル情報
 */

public class FileEntity {
    private String Object_id;
    private String file;
    private String file_genre;
    private Date TimeStamp;
    private byte[] bytes = null;

    public String getObject_id() {
        return Object_id;
    }

    public void setObject_id(String object_id) {
        Object_id = object_id;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getFile_genre() {
        return file_genre;
    }

    public void setFile_genre(String file_genre) {
        this.file_genre = file_genre;
    }

    public Date getTimeStamp() {
        return TimeStamp;
    }

    public void setTimeStamp(Date timeStamp) {
        TimeStamp = timeStamp;
    }

    public void setDetailImage(Bitmap thumbnail) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        bytes = baos.toByteArray();
    }


    public Bitmap getDetailImage() {
        Bitmap bmp = null;
        if (bytes != null) {
            bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return bmp;
    }
}
