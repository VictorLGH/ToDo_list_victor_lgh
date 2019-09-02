package com.isep.simov.todo.fragment;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.isep.simov.todo.activity.MainActivity;
import com.isep.simov.todo.R;
import com.isep.simov.todo.activity.AddTaskActivity;
import com.isep.simov.todo.activity.OnboardingActivity;
import com.isep.simov.todo.activity.ProfileActivity;
import com.isep.simov.todo.adapter.TaskAdapter;
import com.isep.simov.todo.adapter.swipe.SwipeController;
import com.isep.simov.todo.adapter.swipe.SwipeControllerActions;
import com.isep.simov.todo.model.Task;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;


public class TaskFragment extends Fragment {

    private static final String TAG = TaskFragment.class.toString();
    @BindView(R.id.recycler)
    RecyclerView recyclerView;
    @BindView(R.id.fab)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.fab2)
    FloatingActionButton floatingActionButton2;

    TaskAdapter taskAdapter;
    List<Task> tasks;
    SwipeController swipeController;

    FirebaseFirestore firebaseDB;
    FirebaseAuth auth;

    public TaskFragment() {
    }

    public static TaskFragment newInstance() {
        TaskFragment fragment = new TaskFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }



    /*private View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (mOnItemLongClickListener != null) {
                RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(v);
                return mOnItemLongClickListener.onItemLongClick(recyclerView, holder.getAdapterPosition(), v);
            }
            return false;
        }
    };*/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_task, container, false);
        ButterKnife.bind(this, view);
        firebaseDB = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();


//        taskAdapter.setOnClickListner();

        floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(TaskFragment.this.getActivity(), AddTaskActivity.class);
            ActivityOptions options = null;


            options = ActivityOptions.makeSceneTransitionAnimation(
                    TaskFragment.this.getActivity(),
                    android.util.Pair.create(floatingActionButton, "bg"));
            TaskFragment.this.startActivityForResult(intent, MainActivity.ADD_TASK_REQUEST, options.toBundle());
        });

        floatingActionButton2.setOnClickListener(v -> {
            //Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
            showPopup(floatingActionButton2);

            PopupMenu popup = new PopupMenu(TaskFragment.this.getActivity(), v);
            popup.getMenuInflater().inflate(R.menu.menu_popup,popup.getMenu());
            //MenuInflater inflater = popup.getMenuInflater();
            //inflater.inflate(R.menu.menu_popup, popup.getMenu());

            popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if (item.getItemId() == R.id.popup1) {
                        Toast.makeText(getContext(), "1", Toast.LENGTH_SHORT).show();
                        initializeLayout();
                        getTasksOrderByEndDate(Query.Direction.ASCENDING);
                        fetchList();
                        popup.dismiss();
                    }
                    else if (item.getItemId() == R.id.popup2) {
                        Toast.makeText(getContext(), "2", Toast.LENGTH_SHORT).show();
                        initializeLayout();
                        getTasksOrderByEndDate(Query.Direction.DESCENDING);
                        fetchList();
                    }
                    else if (item.getItemId() == R.id.popup3) {
                        initializeLayout();
                        getTasksOrderByPriority();
                        fetchList();
                        Toast.makeText(getContext(), "3", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(getContext(), "fail", Toast.LENGTH_SHORT).show();
                    }


                    //Toast.makeText(getContext(), item.getItemId(), Toast.LENGTH_SHORT).show();
                    /*switch (item.getItemId()) {
                        case R.id.popup1:
                            //Toast.makeText(getContext(), "Filter is selected", Toast.LENGTH_SHORT).show();
                        case R.id.popup2:
                            //Toast.makeText(getContext(), "Date is selected", Toast.LENGTH_SHORT).show();
                        case R.id.popup3:
                            //Toast.makeText(getContext(), "Hodqddur is selected", Toast.LENGTH_SHORT).show();
                        default:
                            break;
                    }*/
                    return true;
                }
            });
            popup.show();
        });


        //   initTasks();
        initializeLayout();
        getTasks();
        fetchList();

        return view;


    }



    private void initializeLayout() {
        tasks = new ArrayList<>();
        taskAdapter = new TaskAdapter(tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(taskAdapter);
        setSwipeController();

    }


    public void fetchList() {
        taskAdapter.setTasks(tasks);
        taskAdapter.notifyDataSetChanged();

    }

    public synchronized void getTasks() {
        firebaseDB.collection("Task")
                .whereEqualTo("userId", auth.getCurrentUser().getUid())
                // .orderBy("endDate", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(documentSnapshots -> {
                    //Toast.makeText(getContext(), "There is smthg here", Toast.LENGTH_SHORT).show();
                    if (documentSnapshots.isEmpty()) {
                        Log.d(TAG, "onSuccess: empty list");
                    } else {
                        Log.d(TAG, "onSuccess: full list");
                        tasks.clear();

                        for (QueryDocumentSnapshot documentSnapshot : documentSnapshots) {
                            Task dbTask = documentSnapshot.toObject(Task.class);
                            dbTask.setTaskId(documentSnapshot.getId());
                            tasks.add(dbTask);
                        }

                        fetchList();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Fail here", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onError: failed to fetch data: " + e.getMessage());
                });
    }

    public synchronized void getTasksOrderByEndDate(Query.Direction direction) {
        firebaseDB.collection("Task")
                .whereEqualTo("userId", auth.getCurrentUser().getUid())
                .orderBy("dateTimeStamp", direction)
                .get()
                .addOnSuccessListener(documentSnapshots -> {
                    //Toast.makeText(getContext(), "There is smthg here", Toast.LENGTH_SHORT).show();
                    if (documentSnapshots.isEmpty()) {
                        Log.d(TAG, "onSuccess: empty list");
                    } else {
                        Log.d(TAG, "onSuccess: full list");
                        tasks.clear();

                        for (QueryDocumentSnapshot documentSnapshot : documentSnapshots) {
                            Task dbTask = documentSnapshot.toObject(Task.class);
                            dbTask.setTaskId(documentSnapshot.getId());
                            tasks.add(dbTask);
                        }

                        fetchList();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Fail here", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onError: failed to fetch data: " + e.getMessage());
                });
    }

    public synchronized void getTasksOrderByPriority() {
        firebaseDB.collection("Task")
                .whereEqualTo("userId", auth.getCurrentUser().getUid())
                .whereEqualTo("priority", true)
                .get()
                .addOnSuccessListener(documentSnapshots -> {
                    //Toast.makeText(getContext(), "There is smthg here", Toast.LENGTH_SHORT).show();
                    if (documentSnapshots.isEmpty()) {
                        Log.d(TAG, "onSuccess: empty list");
                    } else {
                        Log.d(TAG, "onSuccess: full list");
                        tasks.clear();

                        for (QueryDocumentSnapshot documentSnapshot : documentSnapshots) {
                            Task dbTask = documentSnapshot.toObject(Task.class);
                            dbTask.setTaskId(documentSnapshot.getId());
                            tasks.add(dbTask);
                        }

                        fetchList();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Fail here", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onError: failed to fetch data: " + e.getMessage());
                });
    }

    public void deleteTask(Task task) {
        String taskId = task.getTaskId();
        if (taskId != null) {
            firebaseDB.collection("Task").document(taskId)
                    .delete()
                    .addOnSuccessListener(v -> {
                        Toast.makeText(getContext(), "Task deleted: " + task.getTaskName(), Toast.LENGTH_SHORT).show();
                    }).addOnFailureListener(e -> Log.e(TAG, e.getMessage()));
            getTasks();
            fetchList();
        } else {
            Toast.makeText(getContext(), "task id is null", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MainActivity.ADD_TASK_REQUEST) {
            if (resultCode == RESULT_OK) {
                getTasks();
                fetchList();
            }

        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.popup1:
                Toast.makeText(TaskFragment.this.getActivity(), "Filter is selected", Toast.LENGTH_SHORT).show();
            case R.id.popup2:
                Toast.makeText(TaskFragment.this.getActivity(), "Date is selected", Toast.LENGTH_SHORT).show();
            case R.id.popup3:
                Toast.makeText(TaskFragment.this.getActivity(), "Hour is selected", Toast.LENGTH_SHORT).show();
            default:
                break;
        }
        return true;
    }

    private void setSwipeController() {
        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                Toast.makeText(getContext(), "Delete action will be here...", Toast.LENGTH_SHORT).show();
                deleteTask(tasks.get(position));
                tasks.remove(tasks.get(position));
                fetchList();

            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeController);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
    }

    public void showPopup(View v){
        PopupMenu popup = new PopupMenu(TaskFragment.this.getActivity(), v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_popup, popup.getMenu());
        popup.show();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (taskAdapter != null) {
            //   adapter.stopListening();
        }
    }


}


