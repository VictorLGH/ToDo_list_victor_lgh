package com.isep.simov.todo.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.isep.simov.todo.R;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    ImageView imageView;
    TextView heading;
    TextView description;

    private String[] headings;
    private String[] descriptions;

    public SliderAdapter(Context context) {
        this.context = context;
        setOnboardingTexts();
    }

    void setOnboardingTexts() {
        headings = context.getResources().getStringArray(R.array.start_headings);
        descriptions = context.getResources().getStringArray(R.array.start_descriptions);
    }

    public int[] images = {
            R.drawable.icon1,
            R.drawable.icon2,
            R.drawable.icon3
    };


    @Override
    public int getCount() {
        return headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == (LinearLayout) o;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);

        imageView = view.findViewById(R.id.imageView);
        heading = view.findViewById(R.id.heading);
        description = view.findViewById(R.id.description);

        imageView.setImageResource(images[position]);
        heading.setText(headings[position]);
        description.setText(descriptions[position]);
        container.addView(view);
        return view;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);

    }
}

