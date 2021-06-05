package com.example.begin.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.begin.activity.FiledetailActivity;
import com.example.begin.activity.R;
import com.example.begin.bean.Filesource;
import com.example.begin.bean.Tasksource;

import java.util.List;

public class TaskSearchAdapter extends RecyclerView.Adapter<TaskSearchAdapter.MyViewHolder> {

    private List<Tasksource> taskList;

    //接收参数
    public TaskSearchAdapter(List<Tasksource> taskList){
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public TaskSearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 填充布局
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searchtask, parent, false);
        return new TaskSearchAdapter.MyViewHolder(view);
    }
    /**
     * 创建 ViewHolder
     */
    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTvTasktitle;
        TextView mTvTaskPublisherId;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvTasktitle= itemView.findViewById(R.id.tv_tasktitle);
            mTvTaskPublisherId = itemView.findViewById(R.id.tv_taskpublisher);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull TaskSearchAdapter.MyViewHolder holder, int position) {
        Tasksource task = taskList.get(position);

        String title = task.getTitle();
        String publisherId = task.getPublisherId();
        String Id = task.getId();

        holder.mTvTasktitle.setText(title);
        holder.mTvTaskPublisherId.setText(publisherId);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("taskId", Id);
                //Intent intent = new Intent(v.getContext(), TaskdetailActivity.class);
                //intent.putExtras(bundle);
                //v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
