package jp.ac.hal.messagebottle

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView


/**
 * Created by naoi on 2017/04/25.
 */

class CasarealRecycleViewAdapter(private val list: List<ImageEntity>) : RecyclerView.Adapter<CasarealRecycleViewAdapter.MyViewHolder>() {
    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val inflate = LayoutInflater.from(parent.context).inflate(R.layout.customlist, parent, false)
        return MyViewHolder(inflate)
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.titleView.text = list[position].textdata
        holder.thumbnail.setImageBitmap(list[position].thumbnail)
        holder.layout.setOnClickListener { v -> listener!!.onClick(v, list[holder.adapterPosition]) }
    }

    fun setOnItemClickListener(listener: CasarealRecycleViewAdapter.OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onClick(view: View, position: ImageEntity)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleView: TextView
        val thumbnail: ImageView
        val layout: LinearLayout

        init {
            layout = itemView.findViewById(R.id.holder_list) as LinearLayout
            titleView = itemView.findViewById(R.id.filter_name) as TextView
            thumbnail = itemView.findViewById(R.id.thumbnail) as ImageView
        }
    }
}





