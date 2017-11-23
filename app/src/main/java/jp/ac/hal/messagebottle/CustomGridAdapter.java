package jp.ac.hal.messagebottle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import java.util.List;

/**
 * Created by muto.masakazu on 2017/11/16.
 */



public class CustomGridAdapter extends ArrayAdapter<ImageEntity>{
    private int mResource;
    private List<ImageEntity> mItems;
    private LayoutInflater mInflater;
    //gridviewで画像表示するためのレイアウト
    private static final int RESOURCE_ID = R.layout.grid_item;

    public CustomGridAdapter(Context context,List<ImageEntity> objects) {
        super(context, RESOURCE_ID, objects);
        mItems = objects;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
        ImageEntity item = mItems.get(position);
        viewHolder.image.setImageBitmap(item.getThumbnail());
        return convertView;
    }
    // GridView一コマの内部の参照を保持する
    static class ViewHolder {
        ImageView image;
    }
}


