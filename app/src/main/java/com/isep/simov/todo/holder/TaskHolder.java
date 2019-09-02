package com.isep.simov.todo.holder;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.isep.simov.todo.R;
import com.isep.simov.todo.model.Task;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import mabbas007.tagsedittext.TagsEditText;

import static android.support.constraint.Constraints.TAG;

public class TaskHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.task_name)
    TextView taskName;
    @BindView(R.id.task_description)
    TextView taskDescription;
    @BindView(R.id.task_end_date)
    TextView endDate;
    @BindView(R.id.task_end_time)
    TextView endTime;
    @BindView(R.id.task_location_text)
    TextView localizationTextView;
    @BindView(R.id.icon_for_location)
    ImageView locationIcon;
    @BindView(R.id.priority_line)
    View priorityLine;
    @BindView(R.id.tagsEditText)
    TagsEditText tagsEditText;
    @BindView(R.id.task_done)
    TextView task_done;
    @BindView(R.id.instruction)
    TextView instruction;


    DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    //DateFormat dateFormat = new SimpleDateFormat("SSSSSSSSS", Locale.getDefault());
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

    List<Task> tasks;

    FirebaseFirestore firebaseDB;
    FirebaseAuth auth;



    public TaskHolder(@NonNull View itemView) {

        super(itemView);
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(v -> {
            if(task_done.getText() == "DONE") {
                task_done.setText("PENDING");
                instruction.setText("Tap on task to mark completion");
                task_done.setTextColor(Color.rgb(255,0,0));
            }
            else {
                task_done.setText("DONE");
                instruction.setText("Tap on task to mark as pending");
                task_done.setTextColor(Color.rgb(0,255,0));
            }

           /* String taskId = tasks.get(postion).getTaskId();

            firebaseDB.collection("Task").document(taskId)
                    .update("done",true);*/




        });
    }



    public void bindTask(Task task) {
       if (task.isDone()==true) {
            Log.d(TAG, "isDONE");
           task_done.setText("DONE");
           task_done.setTextColor(Color.rgb(0,255,0));
        }
        else {
            Log.d(TAG, "isNOTDONE");
            task_done.setText("PENDING");
            task_done.setTextColor(Color.rgb(255,0,0));
        }

       // task_done.setText(task.getTaskName());
        taskName.setText(task.getTaskName());
        taskDescription.setText(task.getTaskDescription());
        endDate.setText(dateFormat.format(task.getEndDate()));
        endTime.setText(timeFormat.format(task.getEndDate()));
        if (task.isPriority()) {
            priorityLine.setVisibility(View.VISIBLE);
        }
        if (!task.getTags().isEmpty()) {
            String[] tags = task.getTags().split(";");
            tagsEditText.setTags(tags);
            tagsEditText.setVisibility(View.VISIBLE);

//            ViewGroup.LayoutParams lp = priorityLine.getLayoutParams();
//            lp.height = lp.height * 2;
//            priorityLine.setLayoutParams(lp);
        } else {
            tagsEditText.setVisibility(View.GONE);
        }
        if (task.getLocalization() != null) {
            if (!task.getLocalization().isEmpty()) {
                String placeName = (String) task.getLocalization().get("placeName");
                if (!Strings.isNullOrEmpty(placeName)) {
                    localizationTextView.setText(placeName);
                    locationIcon.setVisibility(View.VISIBLE);
                    localizationTextView.setVisibility(View.VISIBLE);
                }
            }
        } else {
            locationIcon.setVisibility(View.GONE);
            localizationTextView.setVisibility(View.GONE);
        }

    }
}
