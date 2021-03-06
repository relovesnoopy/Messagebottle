package jp.ac.hal.messagebottle;

import android.content.Context;
import android.graphics.Bitmap;
import java.util.ArrayList;
import java.util.List;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageEmbossFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageExposureFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageMonochromeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImagePosterizeFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSepiaFilter;

/**
 * Created by muto.masakazu on 2017/11/26.
 */

class ColorFilter {
    private GPUImage gpuImage;
    ColorFilter(){
        Context context = MainActivity.Companion.getContext();
        assert context != null;
        gpuImage = new GPUImage(context);
    }
    private ImageEntity None_filter(Bitmap bp){
        ImageEntity entity = new ImageEntity();
        entity.setTextdata("none");
        entity.setThumbnail(bp);
        return entity;
    }
    private ImageEntity Sepia_filter(GPUImage gp){
        gp.setFilter(new GPUImageSepiaFilter());
        ImageEntity entity = new ImageEntity();
        entity.setTextdata("sepia");
        entity.setThumbnail(gp.getBitmapWithFilterApplied());
        return entity;
    }
    private ImageEntity Mono_filter(GPUImage gp){
        gp.setFilter(new GPUImageMonochromeFilter());
        ImageEntity entity = new ImageEntity();
        entity.setTextdata("monochrome");
        entity.setThumbnail(gp.getBitmapWithFilterApplied());
        return entity;
    }
    private ImageEntity Emboss_filter(GPUImage gp){
        gpuImage.setFilter(new GPUImageEmbossFilter());
        ImageEntity entity = new ImageEntity();
        entity.setTextdata("Emboss");
        entity.setThumbnail(gp.getBitmapWithFilterApplied());
        return entity;
    }
    private ImageEntity Posterize_filter(GPUImage gp){
        gpuImage.setFilter(new GPUImagePosterizeFilter());
        ImageEntity entity = new ImageEntity();
        entity.setTextdata("posterize");
        entity.setThumbnail(gp.getBitmapWithFilterApplied());
        return entity;
    }
    private ImageEntity Exposure_filter(GPUImage gp){
        gpuImage.setFilter(new GPUImageExposureFilter());
        ImageEntity entity = new ImageEntity();
        entity.setTextdata("Exposure");
        entity.setThumbnail(gp.getBitmapWithFilterApplied());
        return entity;
    }

    List<ImageEntity> getFilterList(Bitmap bp){
        gpuImage.setImage(bp);
        List<ImageEntity> entityList = new ArrayList<>();
        entityList.add(this.None_filter(bp));
        entityList.add(this.Sepia_filter(gpuImage));
        entityList.add(this.Mono_filter(gpuImage));
        entityList.add(this.Emboss_filter(gpuImage));
        entityList.add(this.Posterize_filter(gpuImage));
        entityList.add(this.Exposure_filter(gpuImage));

        return entityList;
    }
}
