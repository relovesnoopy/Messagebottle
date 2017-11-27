package jp.ac.hal.messagebottle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;

import static jp.ac.hal.messagebottle.MainActivity.getContext;

/**
 * Created by muto.masakazu on 2017/11/26.
 */

public class ColorFilter {
    private Context context;
    private String filter_text;
    public ColorFilter(){
        this.context = getContext();
    }
    public Bitmap Sepia_filter(Bitmap bp){
        GPUImage gpuImage = new GPUImage(this.context);
        gpuImage.setImage(bp);
        gpuImage.setFilter(new GPUImageSepiaFilter());
        return gpuImage.getBitmapWithFilterApplied();
    }
}
