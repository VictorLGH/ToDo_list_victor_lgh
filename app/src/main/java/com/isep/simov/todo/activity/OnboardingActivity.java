package com.isep.simov.todo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.isep.simov.todo.R;
import com.isep.simov.todo.adapter.SliderAdapter;

public class OnboardingActivity extends AppCompatActivity {

    private ViewPager mSlidenewPager;
    private LinearLayout mDotLayout;
    private SliderAdapter sliderAdapter;
    private TextView[] mDots;
    private Button b_left;
    private Button b_right;
    private int current_page;
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int i, float v, int i1) {

        }

        @Override
        public void onPageSelected(int i) {
            addDotsIndicators(i);
            current_page = i;
            //even tho we are not using this, i'll keep this code if we want to change. button left is invisible but not deleted
            if (i == 0) {
                b_right.setEnabled(true);
                b_left.setEnabled(false);
                b_left.setVisibility(View.INVISIBLE);
                b_right.setText("Go to App");
                b_left.setText("");
            } else if (i == mDots.length - 1) {
                b_right.setEnabled(true);
                b_left.setEnabled(false);
                b_left.setVisibility(View.INVISIBLE);
                b_right.setText("Go to App");
                b_left.setText("Back");
            } else {
                b_right.setEnabled(true);
                b_left.setEnabled(false);
                b_left.setVisibility(View.INVISIBLE);
                b_right.setText("Go to App");
                b_left.setText("Back");

            }
        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        mSlidenewPager = findViewById(R.id.viewPager);
        mDotLayout = findViewById(R.id.dotsLayout);

        b_left = findViewById(R.id.btn_left);
        b_right = findViewById(R.id.btn_right);
        b_right.setOnClickListener(v -> {

            Intent i = new Intent(OnboardingActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        });
        sliderAdapter = new SliderAdapter(this);

        mSlidenewPager.setAdapter(sliderAdapter);
        addDotsIndicators(0);
        mSlidenewPager.addOnPageChangeListener(viewListener);
    }

    public void addDotsIndicators(int pos) {
        mDots = new TextView[3];
        mDotLayout.removeAllViews();
        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorTransparentwhite));
            mDotLayout.addView(mDots[i]);
        }
        if (mDots.length > 0) {
            mDots[pos].setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }
}
