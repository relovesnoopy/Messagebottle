package jp.ac.hal.messagebottle

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 * Created by muto.masakazu on 2017/09/15.
 */

object NetworkManager {

    /**
     * ネットワーク接続が確立されているか
     * @param context
     * @return
     */
    fun isConnected(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return if (networkInfo != null) {
            connectivityManager.activeNetworkInfo.isConnected
        } else false

    }
}
