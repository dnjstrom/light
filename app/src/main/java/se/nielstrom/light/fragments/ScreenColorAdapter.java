package se.nielstrom.light.fragments;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Daniel on 2014-04-07.
 */
public class ScreenColorAdapter extends FragmentStatePagerAdapter {
    String[] colors;
    FragmentManager fm;

    public ScreenColorAdapter(FragmentManager fm, String[] colors) {
        super(fm);
        this.fm = fm;
        this.colors = colors;
    }

    @Override
    public Fragment getItem(int position) {
        int c = Color.parseColor(colors[position % colors.length]);
        ColorFragment fragment = new ColorFragment(c);
        return fragment;
    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }
}
