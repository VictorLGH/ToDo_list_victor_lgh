package com.isep.simov.todo.adapter;


import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.isep.simov.todo.R;
import com.isep.simov.todo.holder.UserHolder;
import com.isep.simov.todo.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserHolder> {

    private List<User> users;

    public UserAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public UserHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View v = inflater.inflate(R.layout.item_user, viewGroup, false);
        return new UserHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull UserHolder userHolder, int position) {
        userHolder.bindUser(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }


    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users= users;
    }
}
