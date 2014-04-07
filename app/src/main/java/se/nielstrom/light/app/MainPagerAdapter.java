package se.nielstrom.light.app;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import se.nielstrom.light.fragments.ActiveFragment;


/**
 * Created by Daniel on 2014-04-06.
 */
public class MainPagerAdapter extends FragmentStatePagerAdapter {

    private List<ActiveFragment> fragments;

    public MainPagerAdapter(FragmentManager fm, List<ActiveFragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public ActiveFragment getItem(int position) {
        return this.fragments.get(position);
    }

    @Override
    public int getCount() {
        return this.fragments.size();
    }
}