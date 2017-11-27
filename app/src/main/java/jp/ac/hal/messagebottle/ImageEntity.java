package jp.ac.hal.messagebottle;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import static android.R.attr.bitmap;

/**
 * Created by muto.masakazu on 2017/08/14.
 */

public class ImageEntity implements Serializable {
    private byte[] bytes = null;
    private String ncmbImage;
    private String textdata;


    public String getTextdada() {
        return textdata;
    }

    public void setTextdada(String textdata) {
        this.textdata = textdata;
    }


    public String getNcmbImage() {
        return ncmbImage;
    }

    public void setNcmbImage(String ncmbImage) {
        this.ncmbImage = ncmbImage;
    }


   // private Bitmap bp;

    public void setThumbnail(Bitmap thumbnail) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        bytes = baos.toByteArray();
    }


    public Bitmap getThumbnail() {
        Bitmap bmp = null;
        if (bytes != null) {
            bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return bmp;
    }

    public byte[] getbyte(){
        return this.bytes;
    }





}
