package jp.ac.hal.messagebottle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

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
    /*
    static class ViewHolder {
        ImageView image;
    }
    */

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        //ViewHolder viewHolder;
        View view;

        if (convertView == null) {
            view = mInflater.inflate(RESOURCE_ID, parent, false);
            //grid内のview生成
            //convertView = mInflater.inflate(RESOURCE_ID, null);
            // GridView一コマの中のそれぞれのViewの参照を保持するクラスを生成
            //viewHolder = new ViewHolder();
            //viewHolder.image = (ImageView)convertView.findViewById(R.id.gridimageView);

            //convertView.setTag(viewHolder);

        }else {
            view = convertView;
            // TagからGridViewの1コマの中に設定されたViewの参照を取得
            //viewHolder = (ViewHolder)convertView.getTag();
        }
        ImageView imageView = (ImageView) view.findViewById(R.id.gridimageView);

        // リストビューに表示する要素を取得
        FileEntity entity = mItems.get(position);
        String NCMBPath = entity.getFile();
        //String object_id = entity.getObject_id();
        //viewHolder.image.setTag(NCMBPath);

        //View非表示
        //viewHolder.image.setVisibility(View.GONE);
        //Picasso.with(context).load(NCMBPath).error(R.drawable.noimage).into(viewHolder.image);
        Picasso.with(context).load(NCMBPath).error(R.drawable.noimage).into(imageView);

        //viewHolder.image.setVisibility(View.VISIBLE);
        //return convertView;
        return  view;
    }



}


