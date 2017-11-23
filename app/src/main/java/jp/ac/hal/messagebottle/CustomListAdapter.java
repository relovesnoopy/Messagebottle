package jp.ac.hal.messagebottle;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;


import java.util.List;
/**
 * Created by muto.masakazu on 2017/08/14.
 */

public class CustomListAdapter extends ArrayAdapter<ImageEntity> {
    private int mResource;
    private List<ImageEntity> mItems;
    private LayoutInflater mInflater;



    public CustomListAdapter( Context context, int resource, List<ImageEntity> objects) {
        super(context, resource,  objects);
        mResource = resource;
        mItems = objects;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView != null) {
            view = convertView;
        }
        else {
            view = mInflater.inflate(mResource, null);
        }

        // リストビューに表示する要素を取得
        ImageEntity item = mItems.get(position);

        // サムネイル画像を設定
        ImageView thumbnail = (ImageView)view.findViewById(R.id.thumbnail);
        thumbnail.setImageBitmap(item.getThumbnail());

        //コメント
        return view;
    }

}
