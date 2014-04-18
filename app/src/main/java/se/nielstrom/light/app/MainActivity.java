package se.nielstrom.light.app;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import se.nielstrom.light.fragments.ActiveFragment;
import se.nielstrom.light.fragments.FlashLight;
import se.nielstrom.light.fragments.ScreenLight;

public class MainActivity extends FragmentActivity {

    private FragmentStatePagerAdapter adapter;
    private ViewPager pager;
    private int pager_state;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        initialisePaging();
    }

    public void onResume() {
        super.onResume();
        ActiveFragment f = ((ActiveFragment) adapter.getItem(pager.getCurrentItem()));
        f.activate();
    }

    private void initialisePaging() {
        List<ActiveFragment> fragments = new ArrayList<ActiveFragment>();

        PackageManager pm = this.getPackageManager();

        if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            fragments.add( (ActiveFragment) Fragment.instantiate(this, FlashLight.class.getName()));
        }
        fragments.add( (ActiveFragment) Fragment.instantiate(this, ScreenLight.class.getName()));

        pager = (ViewPager) super.findViewById(R.id.mainpager);
        adapter = new MainPagerAdapter(super.getSupportFragmentManager(), fragments);
        pager.setAdapter(adapter);

        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageScrollStateChanged(int state) {
                ActiveFragment fragment = (ActiveFragment) adapter.getItem(pager.getCurrentItem());
                pager_state = state;

                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    fragment.deactivate();
                } else {
                    fragment.activate();
                }
            }
        });

        pager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (pager_state != ViewPager.SCROLL_STATE_IDLE) {
                    return false;
                }

                ActiveFragment fragment = ((ActiveFragment) adapter.getItem(pager.getCurrentItem()));

                switch (motionEvent.getActionMasked()) {
                    case MotionEvent.ACTION_DOWN:
                        fragment.deactivate();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        fragment.activate();
                        break;
                }
                return false;
            }
        });
    }
}
