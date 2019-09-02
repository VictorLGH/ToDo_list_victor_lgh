package com.isep.simov.todo.fragment;


import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.isep.simov.todo.adapter.swipe.SwipeController;
import com.isep.simov.todo.adapter.swipe.SwipeControllerActions;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.isep.simov.todo.R;
import com.isep.simov.todo.activity.AddTaskActivity;
import com.isep.simov.todo.activity.MainActivity;
import com.isep.simov.todo.adapter.TaskAdapter;
import com.isep.simov.todo.model.Task;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CalendarFragment extends Fragment implements OnDateSelectedListener {


    @BindView(R.id.buttonCal)
    public
    Button button;

    @BindView(R.id.calendarView)
    public
    MaterialCalendarView calendarView;



    @BindView(R.id.recyclerCalendar)
    public
    RecyclerView recyclerView;

    SwipeController swipeController;
    TaskAdapter taskAdapter;

    private static final String TAG = CalendarFragment.class.toString();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    List<Task> tasks;
    FirebaseFirestore firebaseDB;
    FirebaseAuth auth;
    List<CalendarDay> dates;
    private List<Task> tasksCopy;

    //CalendarDay currentday = CalendarDay.from(new Date());


    public static CalendarFragment newInstance(String param1, String param2) {

        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendar, container, false);
        ButterKnife.bind(this, view);
        calendarView.setOnDateChangedListener(this);
        firebaseDB = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();
        tasks = new ArrayList<>();
        tasksCopy = new ArrayList<>();
        dates = new ArrayList<>();

        button.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarFragment.this.getActivity(), AddTaskActivity.class);
            ActivityOptions options = null;


            options = ActivityOptions.makeSceneTransitionAnimation(
                    CalendarFragment.this.getActivity(),
                    android.util.Pair.create(button, "bg"));
            CalendarFragment.this.startActivityForResult(intent, MainActivity.ADD_TASK_REQUEST, options.toBundle());
        });

        initializeLayout();
        getTasks();
        return view;
    }

    public synchronized void getTasks() {
        firebaseDB.collection("Task")
                .whereEqualTo("userId", auth.getCurrentUser().getUid())
               // .orderBy("endDate", Query.Direction.ASCENDING)
                .get()
                .addOnSuccessListener(documentSnapshots -> {
                    if (documentSnapshots.isEmpty()) {
                        Log.d(TAG, "onSuccess: empty list");
                    } else {
                        Log.d(TAG, "onSuccess: full list in calendar");
                        tasks.clear();
                        for (QueryDocumentSnapshot documentSnapshot : documentSnapshots) {
                            Task dbTask = documentSnapshot.toObject(Task.class);
                            dbTask.setTaskId(documentSnapshot.getId());
                            tasks.add(dbTask);
                            dates.add(CalendarDay.from(dbTask.getEndDate()));
                        }
                        tasksCopy = tasks;
                        addEventsDecorators();


                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "onError: failed to fetch data: " + e.getMessage());
                });
    }

    private void initializeLayout() {
        taskAdapter = new TaskAdapter(tasks);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(taskAdapter);
        setSwipeController();


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

    public void fetchList() {
        taskAdapter.setTasks(tasks);
        taskAdapter.notifyDataSetChanged();

    }
    private void setSwipeController() {
        swipeController = new SwipeController(new SwipeControllerActions() {
            @Override
            public void onRightClicked(int position) {
                // Toast.makeText(getContext(), "Delete action will be here...", Toast.LENGTH_SHORT).show();
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

    private void addEventsDecorators() {

        calendarView.addDecorator(new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay day) {
                return dates.contains(day);
            }

            @Override
            public void decorate(DayViewFacade view) {
                view.addSpan(new DotSpan(10, R.color.colorPrimary));
            }
        });
    }


    public void onButtonPressed(Uri uri) {

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
        SimpleDateFormat newdate = new SimpleDateFormat("yyyy-MM-dd");
        String date1 = newdate.format(date.getDate());


        List<Task> templist = new ArrayList();
        for (Task t : tasksCopy) {
            if (CalendarDay.from(t.getEndDate()).equals(date)) {
                templist.add(t);
            }
        }
        tasks = templist;
        fetchList();

    }
}
