package com.isep.simov.todo.activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.isep.simov.todo.R;
import com.isep.simov.todo.adapter.ViewPagerFragmentPageAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    public static final int ADD_TASK_REQUEST = 1;


    public ViewPager getViewPager() {
        return viewPager;
    }

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @BindView(R.id.sliding_tabs)
    public
    TabLayout tabLayout;

    @BindView(R.id.toolbar)
    public Toolbar toolbar;

    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseAuth auth;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();

        //get current user
        user = FirebaseAuth.getInstance().getCurrentUser();

        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user auth state is changed - user is null
                    // launch login activity
                    MainActivity.this.startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    MainActivity.this.finish();
                }
            }
        };

        user = auth.getCurrentUser();
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        viewPager.setAdapter(new ViewPagerFragmentPageAdapter(getSupportFragmentManager(),
                MainActivity.this));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                viewPager.getAdapter().notifyDataSetChanged();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
        getSupportActionBar().setTitle(getResources().getString(R.string.todo_title));


        createNotificationChannel();


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about_menu_item:
                //showPopup(viewPager);
                Intent intent = new Intent(this, OnboardingActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.logout_menu_item:
                Toast.makeText(this, "User is signed out", Toast.LENGTH_SHORT).show();
                signOut();
                break;
            case R.id.profile_menu_item:
                Intent intent1 = new Intent(this, ProfileActivity.class);
                startActivity(intent1);
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void signOut() {
        auth.signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void showPopup(View v){
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_popup, popup.getMenu());
        popup.show();
    }
    public void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            String id = "taskChannel";
            if (notificationManager.getNotificationChannel(id) == null) {
                // The user-visible name of the channel.
                CharSequence name = "Task";
                // The user-visible description of the channel.
                String description = "Notifications regarding tasks";
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel mChannel = new NotificationChannel(id, name, importance);
                // Configure the notification channel.
                mChannel.setDescription(description);
                mChannel.enableLights(true);
                mChannel.setLightColor(Color.BLUE);
                notificationManager.createNotificationChannel(mChannel);
            }
        }

    }
}