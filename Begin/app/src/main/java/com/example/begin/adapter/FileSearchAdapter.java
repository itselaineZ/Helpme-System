package com.example.begin.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import com.example.begin.activity.FileActivity;
import com.example.begin.activity.FiledetailActivity;
import com.example.begin.activity.R;
import com.example.begin.bean.Course;
import com.example.begin.bean.Filesource;


public class FileSearchAdapter extends RecyclerView.Adapter<FileSearchAdapter.MyViewHolder>{

    private List<Filesource> fileList;

    //接收参数
    public FileSearchAdapter(List<Filesource> fileList){
        this.fileList = fileList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 填充布局
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searchfile, parent, false);
        return new MyViewHolder(view);
    }
    /**
     * 创建 ViewHolder
     */
    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTvFilename;
        TextView mTvFilescore;
        TextView mTvFiledownloads;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvFilename = itemView.findViewById(R.id.tv_filename);
            mTvFilescore = itemView.findViewById(R.id.tv_filescore);
            mTvFiledownloads = itemView.findViewById(R.id.tv_filedownloads);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Filesource file = fileList.get(position);

        String filename = file.getCourseMaterialName();
        double filescore = file.getScore();
        int filedownloads = file.getDownloads();
        // 设置 课程名
        holder.mTvFilename.setText(filename);
        holder.mTvFilescore.setText(String.valueOf(filescore));//转成字符串才能放进TextView
        holder.mTvFiledownloads.setText(String.valueOf(filedownloads));


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("fileName", filename);
                Intent intent = new Intent(v.getContext(), FiledetailActivity.class);
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

}
