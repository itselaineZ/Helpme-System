package com.example.begin.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.begin.activity.R;
import com.example.begin.activity.RecievedetailActivity;
import com.example.begin.activity.TaskdetailActivity;
import com.example.begin.bean.Tasksource;

import java.util.List;

public class RecievedSearchAdapter extends RecyclerView.Adapter<RecievedSearchAdapter.MyViewHolder> {

    private List<Tasksource> taskList;

    //接收参数
    public RecievedSearchAdapter(List<Tasksource> taskList){
        this.taskList = taskList;
    }

    @NonNull
    @Override
    public RecievedSearchAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 填充布局
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searchtask, parent, false);
        return new RecievedSearchAdapter.MyViewHolder(view);
    }
    /**
     * 创建 ViewHolder
     */
    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTvTasktitle;
        TextView mTvTaskPublisherId;
        TextView mTvPersonTitle;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvTasktitle= itemView.findViewById(R.id.tv_tasktitle);
            mTvTaskPublisherId = itemView.findViewById(R.id.tv_taskperson);
            mTvPersonTitle = itemView.findViewById(R.id.tv_persontitle);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecievedSearchAdapter.MyViewHolder holder, int position) {
        Tasksource task = taskList.get(position);

        String title = task.getTitle();
        String person = task.getPublisherName();
        long taskId = task.getId();
        String description = task.getDescription();


        holder.mTvTasktitle.setText(title);
        holder.mTvTaskPublisherId.setText(person);
        holder.mTvPersonTitle.setText("发布者: ");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("点击了任务项");
                Bundle bundle = new Bundle();
                bundle.putString("taskId", String.valueOf(taskId));
                bundle.putString("description", description);
                bundle.putString("title", title);
                bundle.putString("publisherId", person);
                Intent intent = new Intent(v.getContext(), RecievedetailActivity.class);
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }
}
