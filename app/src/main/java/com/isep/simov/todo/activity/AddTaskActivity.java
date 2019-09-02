package com.isep.simov.todo.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.transition.Transition;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.util.Strings;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.isep.simov.todo.R;
import com.isep.simov.todo.model.Geofencing;
import com.isep.simov.todo.model.Task;
import com.isep.simov.todo.service.NotificationScheduler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.blackbox_vision.datetimepickeredittext.view.DatePickerEditText;
import io.blackbox_vision.datetimepickeredittext.view.TimePickerEditText;
import mabbas007.tagsedittext.TagsEditText;

import static java.util.Calendar.getInstance;


public class AddTaskActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final String TAG = AddTaskActivity.class.toString();
    int height;
    int width;
    FloatingActionButton mFab;
    ConstraintLayout mConstraintLayout;
    int duration = 300;
    Transition sharedElementEnterTransition;
    Transition.TransitionListener mTransitionListener;

    @BindView(R.id.add_task_title)
    EditText taskTitle;
    @BindView(R.id.add_task_description)
    EditText taskDescription;
    @BindView(R.id.add_task_end_date_picker)
    DatePickerEditText taskEndDate;
    @BindView(R.id.add_task_end_time_picker)
    TimePickerEditText taskEndTime;
    @BindView(R.id.add_task_notification_date_picker)
    DatePickerEditText taskNotificationDate;
    @BindView(R.id.add_task_notification_time_picker)
    TimePickerEditText taskNotificationTime;

    @BindView(R.id.add_task_button)
    Button createTaskButton;
    @BindView(R.id.add_task_priority)
    CheckBox priorityCheckBox;
    @BindView(R.id.tagsEditText)
    TagsEditText tagsEditText;
    @BindView(R.id.place_picker_text)
    EditText placePickerText;
    @BindView(R.id.placer_picker_button)
    Button placePickerButton;

    FirebaseFirestore firebaseDB;
    FirebaseAuth auth;
    private Place taskPlace;

    private static final int PERMISSIONS_REQUEST_FINE_LOCATION = 111;
    private GoogleApiClient mClient;
    private Geofencing mGeofencing;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        ButterKnife.bind(this);
        taskEndDate.setManager(getSupportFragmentManager());
        taskNotificationTime.setManager(getSupportFragmentManager());
        taskNotificationDate.setManager(getSupportFragmentManager());
        taskEndTime.setManager(getSupportFragmentManager());

        firebaseDB = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        mClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, this)
                .build();

        mGeofencing = new Geofencing(this, mClient);

        getWindow().setEnterTransition(null);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        height = displayMetrics.heightPixels;
        width = displayMetrics.widthPixels;
        mFab = findViewById(R.id.next_fab);

        mConstraintLayout = findViewById(R.id.bg);
        sharedElementEnterTransition = getWindow().getSharedElementEnterTransition();
        mTransitionListener = new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                setAnim(mConstraintLayout, true);
                setFab(mFab, false);
            }

            @Override
            public void onTransitionCancel(Transition transition) {
            }

            @Override
            public void onTransitionPause(Transition transition) {
            }

            @Override
            public void onTransitionResume(Transition transition) {
            }
        };
        sharedElementEnterTransition.addListener(mTransitionListener);
        placePickerButton.setOnClickListener(v -> {
            // Toast.makeText(this, "Place picker will be added here", Toast.LENGTH_SHORT).show();
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }


        });




        //TASK CREATION
        createTaskButton.setOnClickListener(v -> {
            boolean ok = true;
            String title = taskTitle.getText().toString();
            String description = taskDescription.getText().toString();
            boolean priority = priorityCheckBox.isChecked();

            Calendar endDate = taskEndDate.getDate();
            Calendar endTime = taskEndTime.getTime();
            long date_millis = endDate.getTimeInMillis()/1000;


            if (endTime != null) {
                endDate.set(Calendar.HOUR_OF_DAY, endTime.getTime().getHours());
                endDate.set(Calendar.MINUTE, endTime.getTime().getMinutes());
            }

            boolean isToBeNotified = true;
            Calendar notificationDate = taskNotificationDate.getDate();
            Calendar notificationTime = taskNotificationTime.getTime();
            if (notificationDate != null) {
                if (notificationTime != null) {
                    notificationDate.set(Calendar.HOUR_OF_DAY, notificationTime.getTime().getHours());
                    notificationDate.set(Calendar.MINUTE, notificationTime.getTime().getMinutes());
                }
            } else {
                isToBeNotified = false;
                Toast.makeText(this, "wont be notified...", Toast.LENGTH_SHORT).show();
            }


            Date date = endDate.getTime();

            List<String> tags = (tagsEditText.getTags() != null ? tagsEditText.getTags() : new ArrayList<>());
            String tagsVal = "";
            if (!tags.isEmpty()) {
                tagsVal = tags.stream().map(Object::toString)
                        .collect(Collectors.joining(";"));
            }

            if (validation(title, description, date)) {

                Task task = new Task(title, description, priority, tagsVal, getInstance().getTime(), date, auth.getCurrentUser().getUid(), taskPlace,date_millis);
                insertTask(task);

                if (taskPlace != null) {
                    if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Need location permissions to create geofence", Toast.LENGTH_LONG).show();
                        // onLocationPermissionClicked(v);
                    } else {

                        PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(mClient, taskPlace.getId());
                        placeResult.setResultCallback(places -> {
                            mGeofencing.updateGeofencesList(places);
                            mGeofencing.registerAllGeofences();
                        });
                    }
                }

                sharedElementEnterTransition.removeListener(mTransitionListener);
                setAnim(mConstraintLayout, true);
                setFab(mFab, false);
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                if (isToBeNotified) {
                    scheduleNotification(getNotification(task), notificationDate);
                }

                finish();
            }
        });
    }

    public boolean validation(String title, String description, Date date) {
        if (title.isEmpty()) {
            taskTitle.setError(getString(R.string.add_title_error));
            return false;
        }
        if (description.isEmpty()) {
            taskDescription.setError(getString(R.string.add_description_error));
            return false;
        }
        if (date.compareTo(getInstance().getTime()) < 0) {
            taskEndDate.setError(getString(R.string.add_date_error));
            taskEndTime.setError(getString(R.string.add_date_error));
            if (taskEndTime.getTime() == null) {
                Calendar c = Calendar.getInstance();
                c.set(Calendar.HOUR, 0);
                c.set(Calendar.SECOND, 0);
                taskEndTime.setTime(c);
            }
            return false;
        }
        return true;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
    }

    public void insertTask(Task task) {
        firebaseDB.collection("Task")
                .add(task)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == RESULT_OK) {
                taskPlace = PlacePicker.getPlace(this, data);
                if (taskPlace != null) {
                    if (!Strings.isEmptyOrWhitespace(taskPlace.getName().toString())) {
                        placePickerText.setText(taskPlace.getName());
                    } else {
                        placePickerText.setText(taskPlace.getAddress());
                    }
                }
            }
        }
    }

    @Override
    public void onBackPressed() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sharedElementEnterTransition.removeListener(mTransitionListener);
            setAnim(mConstraintLayout, false);
            setFab(mFab, true);
        } else {
            super.onBackPressed();
        }
    }


    private void scheduleNotification(Notification notification, Calendar notificationDate) {
        Intent notificationIntent = new Intent(this, NotificationScheduler.class);
        notificationIntent.putExtra(NotificationScheduler.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationScheduler.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + 10000;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
       // alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
      //  Log.d(TAG, notificationDate.getTime().toString());
          alarmManager.set(AlarmManager.RTC, notificationDate.getTimeInMillis(), pendingIntent);
        Log.d(TAG, "Alarm manager was set for: " + notificationDate.getTime().toString());
    }

    private Notification getNotification(Task task) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault());
        Notification.Builder builder = new Notification.Builder(this);
        builder.setContentTitle("Reminder: " + task.getTaskName());
        builder.setContentText("You have incoming to-do at " + dateFormat.format(task.getEndDate()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId("taskChannel");
        }

        builder.setSmallIcon(R.drawable.ic_launcher_foreground);
        return builder.build();
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setAnim(final View myView, boolean isShow) {
        // previously invisible view

// get the center for the clipping circle
        int cx = mFab.getWidth() / 2;
        int cy = mFab.getHeight() / 2;

// get the final radius for the clipping circle
        float finalRadius = (float) Math.hypot(width, height);

        int[] startingLocation = new int[2];
        mFab.getLocationInWindow(startingLocation);

// create the animator for this view (the start radius is zero)
        Animator anim;
        if (isShow) {
            anim =
                    ViewAnimationUtils.createCircularReveal(myView, (int) (mFab.getX() + cx), (int) (mFab.getY() + cy), 0, finalRadius);
            // make the view visible and start the animation
            myView.setVisibility(View.VISIBLE);
        } else {
            anim =
                    ViewAnimationUtils.createCircularReveal(myView, (int) (mFab.getX() + cx), (int) (mFab.getY() + cy), finalRadius, 0);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animator) {
                }

                @Override
                public void onAnimationEnd(Animator animator) {
                    myView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animator) {
                }

                @Override
                public void onAnimationRepeat(Animator animator) {
                }
            });
        }

        anim.setDuration(duration);
        anim.start();
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    void setFab(final View myView, boolean isShow) {

// get the center for the clipping circle
        int cx = myView.getWidth() / 2;
        int cy = myView.getHeight() / 2;

// get the initial radius for the clipping circle
        float initialRadius = (float) Math.hypot(cx, cy);
        Animator anim;
        if (isShow) {
// create the animation (the final radius is zero)
            anim =
                    ViewAnimationUtils.createCircularReveal(myView, cx, cy, 0, initialRadius);
// make the view invisible when the animation is done
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.VISIBLE);
                    finishAfterTransition();
                }
            });
            anim.setDuration(duration);
        } else {
            anim =
                    ViewAnimationUtils.createCircularReveal(myView, cx, cy, initialRadius, 0);
// make the view invisible when the animation is done
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    myView.setVisibility(View.INVISIBLE);
                }
            });
        }
// start the animation
        anim.start();
    }
}