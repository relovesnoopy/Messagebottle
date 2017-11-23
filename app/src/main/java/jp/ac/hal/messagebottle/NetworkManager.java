package jp.ac.hal.messagebottle;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by muto.masakazu on 2017/09/15.
 */

public class NetworkManager {

    /**
     * ネットワーク接続が確立されているか
     * @param context
     * @return
     */
    public static boolean isConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if(networkInfo != null ){
            return connectivityManager.getActiveNetworkInfo().isConnected();
        }

        return false;
    }
}
