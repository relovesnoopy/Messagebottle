package jp.ac.hal.messagebottle;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Log;

import static jp.ac.hal.messagebottle.MainActivity.view_height;
import static jp.ac.hal.messagebottle.MainActivity.view_width;

/**
 * Created by muto.masakazu on 2017/12/06.
 */

public class ImageManage {

    public Bitmap scaleBitmap(String strbitmap){
        Bitmap ret = null;
        //画像
        BitmapFactory.Options option = new BitmapFactory.Options();
        Bitmap src;
        int sample_size;

        //実際に読み込まないで情報だけ取得しスケールを決める
        option.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(strbitmap, option);

        if((option.outWidth * option.outHeight) > 1048576){
            //１Mピクセル超えてる
            double out_area = (double)(option.outWidth * option.outHeight) / 1048576.0;
            sample_size = (int) (Math.sqrt(out_area) + 1);
            Log.d("debug" ,"1MOver");
        }else{
            //小さいのでそのまま
            sample_size = 1;
        }

        //実際に読み込むモード
        option.inJustDecodeBounds = false;
        //スケーリングする係数
        option.inSampleSize = sample_size;
        //画像を読み込む
        src = BitmapFactory.decodeFile(strbitmap,option);

        if(src == null){
        }else{
            int src_width = src.getWidth();
            int src_height = src.getHeight();
            //表示利用域に合わせたサイズを計算
            float scale = getFitScale(view_width, view_height, src_width, src_height);
            //リサイズマトリクス
            Matrix matrix = new Matrix();
            matrix.postScale(scale, scale);

            //ビットマップ作成
            ret = Bitmap.createBitmap(src, 0, 0, src_width, src_height, matrix, true);
        }
        return ret;
    }
    /**
     *
     * @param dest_width 目的のサイズ（幅）
     * @param dest_height 目的のサイズ（高さ）
     * @param src_width 元のサイズ（幅）
     * @param src_height 元のサイズ（高さ)
     * @return
     */
    public float getFitScale(int dest_width, int dest_height, int src_width, int src_height){
        float ret = 0;
        if(dest_width < dest_height){
            //縦が長い
            if(src_width < src_height){
                //縦が長い
                ret = (float)dest_height / (float)src_height;

                if((src_width * ret) > dest_width){
                    //縦に合わせると横がはみ出る
                    ret = (float)dest_width / (float)src_width;
                }
            }else{
                //横が長い
                ret = (float)dest_width / (float)src_width;
            }
        }else{
            //横が長い
            if(src_width < src_height){
                //縦が長い
                ret = (float)dest_height / (float)src_height;
            }else{
                //横が長い
                ret = (float)dest_width / (float)src_width;

                if((src_height * ret) > dest_height){
                    //横に合わせると縦がはみ出る
                    ret = (float)dest_height / (float)src_height;
                }
            }
        }

        return ret;
    }
}