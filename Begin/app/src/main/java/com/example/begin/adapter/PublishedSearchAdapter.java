package com.example.begin.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.begin.activity.PublishdetailActivity;
import com.example.begin.activity.R;
import com.example.begin.bean.Tasksource;

import java.util.List;

public class PublishedSearchAdapter extends RecyclerView.Adapter<PublishedSearchAdapter.MyViewHolder> {

    private List<Tasksource> infoList;

    //接收参数
    public PublishedSearchAdapter(List<Tasksource> infoList){ this.infoList = infoList; }

    @NonNull
    @Override
    public PublishedSearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 填充布局
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searchtask, parent, false);
        return new PublishedSearchAdapter.MyViewHolder(view);
    }
    /**
     * 创建 ViewHolder
     */
    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTvTasktitle;
        TextView mTvTaskRecieverId;
        TextView mTvPersonTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvTasktitle = itemView.findViewById(R.id.tv_tasktitle);
            mTvTaskRecieverId = itemView.findViewById(R.id.tv_taskperson);
            mTvPersonTitle = itemView.findViewById(R.id.tv_persontitle);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PublishedSearchAdapter.MyViewHolder holder, int position) {
        Tasksource task = infoList.get(position);

        long taskId = task.getId();
        String title = task.getTitle();
        String person = task.getReceiverName();
        String description = task.getDescription();

        holder.mTvTasktitle.setText(title);
        holder.mTvTaskRecieverId.setText(person);
        holder.mTvPersonTitle.setText("接收者: ");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("taskId", String.valueOf(taskId));
                bundle.putString("description", description);
                bundle.putString("title", title);
                Intent intent = new Intent(v.getContext(), PublishdetailActivity.class);
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return infoList.size();
    }
}