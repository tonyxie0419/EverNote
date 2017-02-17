package com.jikexueyuan.evernote.view;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jikexueyuan.evernote.model.Entity;
import com.jikexueyuan.evernote.R;
import com.yanzhenjie.recyclerview.swipe.SwipeMenuAdapter;

import java.util.List;

/**
 * Created by admin on 2017/1/12.
 */

public class HomeAdapter extends SwipeMenuAdapter<HomeAdapter.MyViewHolder> {

    private Context mContext;
    private List<Entity> list;
    private OnItemClickListener mOnItemClickListener;

    public HomeAdapter(Context mContext, List<Entity> list) {
        this.mContext = mContext;
        this.list = list;
    }

    @Override
    public View onCreateContentView(ViewGroup parent, int viewType) {
        return LayoutInflater.from(mContext).inflate(R.layout.listview_item, parent, false);
    }

    @Override
    public MyViewHolder onCompatCreateViewHolder(View realContentView, int viewType) {
        return new MyViewHolder(realContentView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.tvTitle.setText(list.get(position).getTitle());
        holder.tvDate.setText((list.get(position).getDate()));
        holder.tvContent.setText(list.get(position).getContent());

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = holder.getLayoutPosition();
                    mOnItemClickListener.onItemClick(holder.itemView, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvDate;
        TextView tvContent;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.lv_tvTitle);
            tvDate = (TextView) itemView.findViewById(R.id.lv_tvDate);
            tvContent = (TextView) itemView.findViewById(R.id.lv_tvContent);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }
}
