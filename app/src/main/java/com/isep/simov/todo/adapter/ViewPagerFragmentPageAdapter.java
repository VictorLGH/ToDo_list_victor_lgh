package com.isep.simov.todo.adapter;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.isep.simov.todo.fragment.MapFragment;
import com.isep.simov.todo.fragment.CalendarFragment;
import com.isep.simov.todo.fragment.TaskFragment;

public class ViewPagerFragmentPageAdapter extends FragmentPagerAdapter {
    final int PAGE_COUNT = 3;
    private String tabTitles[] = new String[] { "Tasks", "Calendar", "Users" };
    private Context context;

    public ViewPagerFragmentPageAdapter(FragmentManager supportFragmentManager, Context context) {
        super(supportFragmentManager);
        this.context = context;
    }

    @Override
    public int getCount() {
        return PAGE_COUNT;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0: // Fragment # 0 - This will show FirstFragment
                return TaskFragment.newInstance();
            case 1:
                return CalendarFragment.newInstance("1","2");
            case 2:
                return MapFragment.newInstance("3","4");
            default:
                return null;
        }
    }

    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
}
