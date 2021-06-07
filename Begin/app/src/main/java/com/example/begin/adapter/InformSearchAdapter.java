package com.example.begin.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.begin.activity.R;
import com.example.begin.bean.Information;
import com.example.begin.bean.Tasksource;

import java.util.List;

public class InformSearchAdapter extends RecyclerView.Adapter<InformSearchAdapter.MyViewHolder> {

    private List<Information> infoList;

    //接收参数
    public InformSearchAdapter(List<Information> taskList){ this.infoList = infoList; }

    @NonNull
    @Override
    public InformSearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // 填充布局
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searchinfo, parent, false);
            return new InformSearchAdapter.MyViewHolder(view);
            }
    /**
     * 创建 ViewHolder
     */
    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTvInformTitle;
        TextView mTvInformStatus;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvInformTitle= itemView.findViewById(R.id.tv_infotitle);
            mTvInformStatus = itemView.findViewById(R.id.tv_taskstatus);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull InformSearchAdapter.MyViewHolder holder, int position) {
        Information info = infoList.get(position);

        String title = info.getInformTitle();
        String status = info.getInformStatus();

        holder.mTvInformTitle.setText(title);
        holder.mTvInformStatus.setText(status);

    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }
}