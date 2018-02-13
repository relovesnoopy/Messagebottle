package jp.ac.hal.messagebottle

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import jp.ac.hal.messagebottle.MainActivity.Companion.view_height
import jp.ac.hal.messagebottle.MainActivity.Companion.view_width


/**
 * Created by muto.masakazu on 2017/12/06.
 */

class ImageManage {

    fun scaleBitmap(strbitmap: String): Bitmap? {
        var ret: Bitmap? = null
        //画像
        val option = BitmapFactory.Options()
        val src: Bitmap?
        val sample_size: Int

        //実際に読み込まないで情報だけ取得しスケールを決める
        option.inJustDecodeBounds = true
        BitmapFactory.decodeFile(strbitmap, option)

        if (option.outWidth * option.outHeight > 1048576) {
            //１Mピクセル超えてる
            val out_area = (option.outWidth * option.outHeight).toDouble() / 1048576.0
            sample_size = (Math.sqrt(out_area) + 1).toInt()
            Log.d("debug", "1MOver")
        } else {
            //小さいのでそのまま
            sample_size = 1
        }

        //実際に読み込むモード
        option.inJustDecodeBounds = false
        //スケーリングする係数
        option.inSampleSize = sample_size
        //画像を読み込む
        src = BitmapFactory.decodeFile(strbitmap, option)

        if (src != null) {
            val src_width = src.width
            val src_height = src.height
            //表示利用域に合わせたサイズを計算
            val scale = getFitScale(view_width, view_height, src_width, src_height)
            //リサイズマトリクス
            val matrix = Matrix()
            matrix.postScale(scale, scale)

            //ビットマップ作成
            ret = Bitmap.createBitmap(src, 0, 0, src_width, src_height, matrix, true)
        }
        return ret
    }

    /**
     *
     * @param dest_width 目的のサイズ（幅）
     * @param dest_height 目的のサイズ（高さ）
     * @param src_width 元のサイズ（幅）
     * @param src_height 元のサイズ（高さ)
     * @return
     */
    fun getFitScale(dest_width: Int, dest_height: Int, src_width: Int, src_height: Int): Float {
        var ret = 0f
        if (dest_width < dest_height) {
            //縦が長い
            if (src_width < src_height) {
                //縦が長い
                ret = dest_height.toFloat() / src_height.toFloat()

                if (src_width * ret > dest_width) {
                    //縦に合わせると横がはみ出る
                    ret = dest_width.toFloat() / src_width.toFloat()
                }
            } else {
                //横が長い
                ret = dest_width.toFloat() / src_width.toFloat()
            }
        } else {
            //横が長い
            if (src_width < src_height) {
                //縦が長い
                ret = dest_height.toFloat() / src_height.toFloat()
            } else {
                //横が長い
                ret = dest_width.toFloat() / src_width.toFloat()

                if (src_height * ret > dest_height) {
                    //横に合わせると縦がはみ出る
                    ret = dest_height.toFloat() / src_height.toFloat()
                }
            }
        }

        return ret
    }
}
