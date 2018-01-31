package jp.ac.hal.messagebottle

import android.content.Context

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

/**
 * Created by muto.masakazu on 2017/08/14.
 */

class CustomListAdapter(context: Context, private val mResource: Int, private val mItems: List<ImageEntity>) : ArrayAdapter<ImageEntity>(context, mResource, mItems) {
    private val mInflater: LayoutInflater

    init {
        mInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = if (convertView != null) convertView else mInflater.inflate(mResource, null)

        // リストビューに表示する要素を取得
        val item = mItems[position]

        // サムネイル画像を設定
        val thumbnail = view.findViewById(R.id.thumbnail) as ImageView
        val textView = view.findViewById(R.id.filter_name) as TextView
        thumbnail.setImageBitmap(item.thumbnail)
        textView.text = item.textdata

        //コメント
        return view
    }

}
