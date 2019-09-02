package com.isep.simov.todo.adapter;


import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.isep.simov.todo.R;
import com.isep.simov.todo.activity.LoginActivity;
import com.isep.simov.todo.activity.MainActivity;
import com.isep.simov.todo.holder.TaskHolder;
import com.isep.simov.todo.model.Task;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskHolder> {

    private List<Task> tasks;

    OnClickListner onClickListner ;

    public TaskAdapter(List<Task> tasks) {
        this.tasks = tasks;
    }

    @NonNull
    @Override
    public TaskHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View v = inflater.inflate(R.layout.item_task, viewGroup, false);
        return new TaskHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull TaskHolder taskHolder,int position) {
        taskHolder.bindTask(tasks.get(position));

       /* taskHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListner .setOnItemClick(position,v);
            }
        });*/
        /*taskHolder.itemView.setOnClickListener(v -> {
            //Toast.makeText(viewGroup.getContext(), tasks.get(position).toString(), Toast.LENGTH_SHORT).show();
            Log.d("wesh", "TEST CLICK TASK" + tasks.get(position));
        });*/
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public interface OnClickListner {
        void setOnItemClick(int postion, View view);
    }
    public void setOnClickListner(OnClickListner onClickListner ) {
        this.onClickListner = onClickListner ;
    }

    public List<Task> getTasks() {
        return tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public interface OnNoteListener{
        void onNoteClick(int position);
    }
}
