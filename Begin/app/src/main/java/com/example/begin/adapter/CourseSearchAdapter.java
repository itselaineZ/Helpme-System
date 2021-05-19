package com.example.begin.adapter;

import android.content.Intent;
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
import com.example.begin.activity.R;
import com.example.begin.bean.Course;


public class CourseSearchAdapter extends RecyclerView.Adapter<CourseSearchAdapter.MyViewHolder>{

    private List<Course> courseList;

    //接收参数
    public CourseSearchAdapter(List<Course> courseList){
        this.courseList = courseList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // 填充布局
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searchcourse, parent, false);
        return new MyViewHolder(view);
    }
    /**
     * 创建 ViewHolder
     */
    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView mTvCoursename;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            mTvCoursename = itemView.findViewById(R.id.tv_coursename);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Course course = courseList.get(position);

        String coursename = course.getCoursename();
        // 设置 课程名
        holder.mTvCoursename.setText(coursename);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), FileActivity.class);
                intent.putExtra("course", course);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return courseList.size();
    }

}
