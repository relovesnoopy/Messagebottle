package jp.ac.hal.messagebottle;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.List;


/**
 * Created by naoi on 2017/04/25.
 */

public class CasarealRecycleViewAdapter extends RecyclerView.Adapter<CasarealRecycleViewAdapter.MyViewHolder> {

    private List<ImageEntity> list;
    private OnItemClickListener listener;

    public CasarealRecycleViewAdapter(List<ImageEntity> list) {
        this.list = list;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.customlist, parent,false);
        return new MyViewHolder(inflate);
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        holder.titleView.setText(list.get(position).getTextdata());
        holder.thumbnail.setImageBitmap(list.get(position).getThumbnail());
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(v, list.get(holder.getAdapterPosition()));
            }
        });

    }
    public void setOnItemClickListener(CasarealRecycleViewAdapter.OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onClick(View view, ImageEntity position);
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        final TextView titleView;
        final ImageView thumbnail;
        final LinearLayout layout ;

        public MyViewHolder(View itemView) {
            super(itemView);
            layout = (LinearLayout)itemView.findViewById(R.id.holder_list);
            titleView = (TextView) itemView.findViewById(R.id.filter_name);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        }
    }
}





