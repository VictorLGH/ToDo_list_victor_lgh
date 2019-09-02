package com.isep.simov.todo.fragment;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.PopupWindow;
import android.widget.Toast;

import android.app.Dialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.isep.simov.todo.R;
import com.isep.simov.todo.activity.MainActivity;
import com.isep.simov.todo.adapter.UserAdapter;
import com.isep.simov.todo.adapter.swipe.SwipeController;
import com.isep.simov.todo.adapter.swipe.SwipeControllerActions;
import com.isep.simov.todo.adapter.swipe.SwipeControllerActionsUser;
import com.isep.simov.todo.adapter.swipe.SwipeControllerUser;
import com.isep.simov.todo.model.Task;
import com.isep.simov.todo.model.User;
import com.isep.simov.todo.model.Usershare;

import java.util.ArrayList;
import java.util.List;


import butterknife.BindView;
import butterknife.ButterKnife;

import static android.app.Activity.RESULT_OK;
import static java.util.Calendar.getInstance;


public class MapFragment extends Fragment {

    private static final String TAG = MapFragment.class.toString();
    @BindView(R.id.recycler2)
    RecyclerView recyclerView;

    FirebaseFirestore firebaseDB;
    FirebaseAuth auth;
    UserAdapter userAdapter;
    List<User> users;
    List<Task> tasks;
    SwipeControllerUser swipeControllerUser;
    SwipeControllerUser swipeControllerUser_bis;
    PopupWindow popup;
    LinearLayout layout;
    Dialog mydialog;




    public MapFragment() {
        // Required empty public constructor
    }

    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        View view_popup = inflater.inflate(R.layout.taskpopup, container, false);
        //mydialog = new Dialog(view_popup);
        popup = new PopupWindow(view_popup);
        ButterKnife.bind(this, view);
        firebaseDB = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        /*floatingActionButton.setOnClickListener(v -> {
            Intent intent = new Intent(TaskFragment.this.getActivity(), AddTaskActivity.class);
            ActivityOptions options = null;


            options = ActivityOptions.makeSceneTransitionAnimation(
                    TaskFragment.this.getActivity(),
                    android.util.Pair.create(floatingActionButton, "bg"));
            TaskFragment.this.startActivityForResult(intent, MainActivity.ADD_TASK_REQUEST, options.toBundle());
        });*/


        //   initTasks();
        initializeLayout();
        getUsers();
        fetchList();

        return view;


    }



    private void initializeLayout() {
        users = new ArrayList<>();
        userAdapter = new UserAdapter(users);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(userAdapter);
        setSwipeController();
        setSwipeController_bis();
    }


    public void fetchList() {
        userAdapter.setUsers(users);
        userAdapter.notifyDataSetChanged();

    }

    public synchronized void getUsers() {
        firebaseDB.collection("users")
                //.whereEqualTo("userId", auth.getCurrentUser().getUid())
                // .orderBy("endDate", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(documentSnapshots -> {
                    //Toast.makeText(getContext(), "There is smthg here", Toast.LENGTH_SHORT).show();
                    if (documentSnapshots.isEmpty()) {
                        Log.d(TAG, "onSuccess: empty list");
                    } else {
                        Log.d(TAG, "onSuccess: full list");
                        users.clear();

                        for (QueryDocumentSnapshot documentSnapshot : documentSnapshots) {
                            User dbUser = documentSnapshot.toObject(User.class);
                            dbUser.setUserId(documentSnapshot.getId());
                            users.add(dbUser);
                        }

                        fetchList();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Fail here", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "onError: failed to fetch data: " + e.getMessage());
                });


    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MainActivity.ADD_TASK_REQUEST) {
            if (resultCode == RESULT_OK) {
                getUsers();
                fetchList();
            }

        }

    }

    /*private void setSwipeController() {
        swipeControllerUser = new SwipeControllerUser(new SwipeControllerActionsUser() {
            @Override
            public void onRightClicked(int position) {
                Toast.makeText(getContext(), "Delete action will be here...", Toast.LENGTH_SHORT).show();
            }

            swipeControllerUser = new SwipeControllerUser(new SwipeControllerActionsUser() {
                @Override
                public void onRightClicked(int position) {
                    Toast.makeText(getContext(), "Delete action will be here...", Toast.LENGTH_SHORT).show();
                }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeControllerUser);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeController.onDraw(c);
            }
        });
    }*/
    private void setSwipeController() {
        swipeControllerUser = new SwipeControllerUser(new SwipeControllerActionsUser() {
            @Override
            public void onRightClicked(int position) {
                Toast.makeText(getContext(), "Contact", Toast.LENGTH_SHORT).show();
                //deleteUser(users.get(position));
                //users.remove(users.get(position));
               // fetchList();
            }
        });



        /*swipeControllerUser = new SwipeControllerUser(new SwipeControllerActionsUser() {
            @Override
            public void onLeftClicked(int position) {
               //deleteUser(users.get(position),tasks.get(position));
                Toast.makeText(getContext(), "Share", Toast.LENGTH_SHORT).show();
               // users.remove(users.get(position));
                //fetchList();
            }
        });*/

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeControllerUser);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeControllerUser.onDraw(c);
            }
        });
    }

    private void setSwipeController_bis() {
        /*swipeControllerUser = new SwipeControllerUser(new SwipeControllerActionsUser() {
            @Override
            public void onRightClicked(int position) {
                Toast.makeText(getContext(), "Contact", Toast.LENGTH_SHORT).show();
                //deleteUser(users.get(position));
                //users.remove(users.get(position));
                // fetchList();
            }
        });*/



        swipeControllerUser_bis = new SwipeControllerUser(new SwipeControllerActionsUser() {
            @Override
            public void onLeftClicked(int position) {
               //deleteUser(users.get(position),tasks.get(position));
                Toast.makeText(getContext(), "Share", Toast.LENGTH_SHORT).show();
               // users.remove(users.get(position));
                //fetchList();
            }
        });

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeControllerUser_bis);
        itemTouchhelper.attachToRecyclerView(recyclerView);

        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
                swipeControllerUser_bis.onDraw(c);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (userAdapter != null) {
            //   adapter.stopListening();
        }
    }


}
