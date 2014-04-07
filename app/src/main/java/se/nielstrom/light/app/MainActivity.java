package se.nielstrom.light.app;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;

import java.util.ArrayList;
import java.util.List;

import se.nielstrom.light.fragments.ActiveFragment;
import se.nielstrom.light.fragments.FlashLight;
import se.nielstrom.light.fragments.ScreenLight;

public class MainActivity extends FragmentActivity {

    private FragmentStatePagerAdapter adapter;
    private ViewPager pager;

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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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

                if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                    fragment.deactivate();
                } else {
                    fragment.activate();
                }
            }
        });
    }
}
