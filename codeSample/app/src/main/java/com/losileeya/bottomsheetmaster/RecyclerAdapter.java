package com.losileeya.bottomsheetmaster;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;



/**
 * User: Losileeya (847457332@qq.com)
 * Date: 2016-09-21
 * Time: 14:01
 * 类描述：
 *
 * @version :
 */
public class RecyclerAdapter  extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>{
    private List<AppInfo> datas;
    private Context mContext;
    private OnItemClickListener onItemClickListener;

    public RecyclerAdapter(List<AppInfo> datas, Context mContext) {
        this.datas = datas;
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item, parent, false);
        return new MyViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder,final int position) {
        holder.tv.setText(datas.get(position).getAppLabel());
        holder.icon.setImageDrawable(datas.get(position).getAppIcon());
        holder.tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onItemClickListener!=null){
                    onItemClickListener.onItemClickListener(v,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size()>0?datas.size():0;
    }

    public static class  MyViewHolder extends RecyclerView.ViewHolder{
         TextView tv;
         ImageView icon;
         public MyViewHolder(View itemView) {
             super(itemView);
             tv= (TextView) itemView.findViewById(R.id.tv);
             icon= (ImageView) itemView.findViewById(R.id.icon);
         }
     }
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener{
        void onItemClickListener(View item, int position);
    }
}
