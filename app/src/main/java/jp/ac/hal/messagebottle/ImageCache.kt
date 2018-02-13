package jp.ac.hal.messagebottle

import android.graphics.Bitmap
import android.util.Log

import java.util.HashMap

/**
 * Created by muto.masakazu on 2017/12/28.
 * 読み込んだ画像をキャッシュとして一時保存する
 */

class ImageCache {

    companion object {
        //読み取り専用でないためMutableMap
        private var cache: MutableMap<String, Bitmap>? = HashMap()
        //キャッシュより画像データを取得
        @JvmStatic fun getImage(key: String): Bitmap? {
            return if (cache!!.containsKey(key)) {
                cache!![key]
            } else null
            //存在しない場合はNULLを返す
        }

        //キャッシュに画像データを設定
        @JvmStatic fun setImage(key: String, image: Bitmap) {
            Log.v("setcache", "OK")
            cache?.put(key, image)
        }

        //キャッシュの初期化（リスト選択終了時に呼び出し、キャッシュで使用していたメモリを解放する）
        @JvmStatic fun clearCache() {
            cache = null
            cache = HashMap()
        }
    }
}
