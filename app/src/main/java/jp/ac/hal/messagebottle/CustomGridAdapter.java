package jp.ac.hal.messagebottle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by muto.masakazu on 2017/11/16.
 */

public class CustomGridAdapter extends ArrayAdapter<FileEntity>{
    private int mResource;
    private List<FileEntity> mItems;
    private LayoutInflater mInflater;
    private Context context;
    //gridviewで画像表示するためのレイアウト
    private static final int RESOURCE_ID = R.layout.grid_item;

    public CustomGridAdapter(Context context,List<FileEntity> objects) {
        super(context, RESOURCE_ID, objects);
        mItems = objects;
        Collections.reverse(mItems);
        this.context = context;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // GridView一コマの内部の参照を保持する
    static class ViewHolder {
        ImageView image;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            //grid内のview生成
            convertView = mInflater.inflate(RESOURCE_ID, null);
            // GridView一コマの中のそれぞれのViewの参照を保持するクラスを生成
            viewHolder = new ViewHolder();
            viewHolder.image = (ImageView)convertView.findViewById(R.id.gridimageView);
            convertView.setTag(viewHolder);

        }else {
            // TagからGridViewの1コマの中に設定されたViewの参照を取得
            viewHolder = (ViewHolder)convertView.getTag();
        }
        // リストビューに表示する要素を取得
        FileEntity entity = mItems.get(position);
        String NCMBPath = entity.getFile();
        String object_id = entity.getObject_id();
        viewHolder.image.setTag(NCMBPath);

        //View非表示
        viewHolder.image.setVisibility(View.GONE);
        //画像読込
        try{
            viewHolder.image.setTag(object_id);
            // AsyncTaskは１回しか実行できない為、毎回インスタンスを生成
            ImageGetTask task = new ImageGetTask(viewHolder.image);
            //画像を設定
            //Bitmap bitmap = ((BitmapDrawable)viewHolder.image.getDrawable()).getBitmap();
            task.setOnCallBack(entity::setDetailImage);
            task.execute(NCMBPath, object_id);
        }
        catch(Exception e){
            viewHolder.image.setImageDrawable(context.getResources().getDrawable(R.drawable.noimage));
            viewHolder.image.setVisibility(View.VISIBLE);
        }

        //非同期画像取得

        /*
            Single.create((SingleOnSubscribe<Bitmap>) emitter -> {
                // 一個emitして完了
                //emitter.onSuccess(downloadImage(entity.getFile()));
                emitter.onSuccess(downloadImage(NCMBPath));
            }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bitmap -> {
                    // 一回呼ばれる
                    viewHolder.image.setImageBitmap(bitmap);
                    entity.setDetailImage(bitmap);
                    viewHolder.image.setVisibility(View.VISIBLE);

                },throwable ->
                        viewHolder.image.setImageResource(R.drawable.noimage)
                );
        */

        return convertView;
    }



    private Bitmap downloadImage(String address) {
        Bitmap bmp = null;
        try {
            URL url = new URL(address);
            // HttpURLConnection インスタンス生成
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            // タイムアウト設定
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(20000);
            // リクエストメソッド
            urlConnection.setRequestMethod("GET");
            // リダイレクトを自動で許可しない設定
            urlConnection.setInstanceFollowRedirects(false);
            // ヘッダーの設定(複数設定可能)
            urlConnection.setRequestProperty("Accept-Language", "jp");
            // 接続
            urlConnection.connect();

            BitmapFactory.Options option = new BitmapFactory.Options();
            option.inPreferredConfig = Bitmap.Config.ARGB_4444;
            option.inPurgeable = true;
            option.inSampleSize = 2;

            int resp = urlConnection.getResponseCode();
            switch (resp){
                case HttpURLConnection.HTTP_OK:
                    InputStream is = urlConnection.getInputStream();
                    //bmp = BitmapFactory.decodeStream(is);
                    bmp = BitmapFactory.decodeStream(is, null, option);
                    is.close();
                    break;
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            Log.d("grid", "画像のダウンロードに失敗しました"
                    + address);
            e.printStackTrace();
            e.getMessage();
        }

        return bmp;
    }

    class ImageGetTask extends AsyncTask<String,Void,Bitmap> {
        private ImageView image;
        private String tag;
        private CallBackTask callbacktask;


        public ImageGetTask(ImageView image) {
            //対象の項目を保持
            this.image = image;
            tag = image.getTag().toString();
        }

        @Override
        protected Bitmap doInBackground(String... strings) {
            // ここでHttp経由で画像を取得します。取得後Bitmapで返します。
            synchronized (context) {
                try {
                    //キャッシュより画像データを取得
                    Bitmap image = ImageCache.getImage(strings[1]);
                    if (image == null) {
                        //キャッシュにデータが存在しない場合はwebより画像データを取得
                        Log.v("download", "OK");
                        image = downloadImage(strings[0]);
                        //取得した画像データをキャッシュに保持
                        ImageCache.setImage(strings[1], image);
                    }
                    return image;
                } catch (Exception e) {
                    Log.v("load", "err");
                    return null;
                }
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);
            // Tagが同じものか確認して、同じであれば画像を設定する
            // （Tagの設定をしないと別の行に画像が表示されてしまう）
            if (tag.equals(image.getTag())) {
                if (result != null) {
                    //画像の設定
                    image.setImageBitmap(result);
                } else {
                    Log.v("DownloadImage", tag + "&" + image.getTag());
                    image.setImageDrawable(context.getResources().getDrawable(R.drawable.noimage));
                }
                //取得した画像を表示
                image.setVisibility(View.VISIBLE);
            }
            callbacktask.CallBack(result);
        }
        public void setOnCallBack(CallBackTask _cbj) {
            callbacktask = _cbj;
        }

    }
    /**
     * コールバック用のinterface
     */
    public interface CallBackTask {
        void CallBack(Bitmap result);
    }
}


