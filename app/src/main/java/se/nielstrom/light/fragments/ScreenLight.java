package se.nielstrom.light.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.BounceInterpolator;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import se.nielstrom.light.app.R;

/**
 * Created by Daniel on 2014-04-06.
 */
public class ScreenLight extends ActiveFragment implements View.OnTouchListener, View.OnSystemUiVisibilityChangeListener {

    private View view;
    private HideUiTask timer;
    private int hiddenUiState = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;
    private int extendedLayout = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
    private int hideDelay = 2 * 1000;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(container == null) {
            return null;
        }

        view = inflater.inflate(R.layout.screenlight, container, false);
        String[] colors = getResources().getStringArray(R.array.screenColors);
        VerticalViewPager pager = (VerticalViewPager) view.findViewById(R.id.screenpager);
        ScreenColorAdapter adapter = new ScreenColorAdapter(getActivity().getSupportFragmentManager(), colors);
        pager.setAdapter(adapter);
        pager.setCurrentItem(colors.length * 1000);

        view.setOnSystemUiVisibilityChangeListener(this);
        pager.setOnTouchListener(this);

        return view;
    }


    @Override
    protected void onActivate() {
        //view.setSystemUiVisibility(extendedLayout);
        setBrightness(1F); // max brightness
        view.setKeepScreenOn(true);
        restartUiTimer();
    }

    @Override
    protected void onDeactivate() {
        //view.setSystemUiVisibility(view.getSystemUiVisibility() & ~extendedLayout);
        setBrightness(-1F); // user setting
        view.setKeepScreenOn(false);
        if (timer != null) {
            timer.cancel(true);
        }
    }

    @Override
    protected void onFirstUse() {
        final VerticalViewPager pager = (VerticalViewPager) getActivity().findViewById(R.id.screenpager);

        ValueAnimator animator = ValueAnimator.ofInt(0, -200, 0);
        animator.setInterpolator(new BounceInterpolator());
        animator.setDuration(1000);

        animator.addListener( new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) { pager.beginFakeDrag(); }
            @Override
            public void onAnimationEnd(Animator animation) { pager.endFakeDrag(); }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            int oldValue;
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int value = (Integer) valueAnimator.getAnimatedValue();
                if (pager.isFakeDragging()) {
                    pager.fakeDragBy(value - oldValue);
                }
                oldValue = value;
            }
        });

        animator.setStartDelay(200);
        animator.start();
    }

    private void setBrightness(float brightness) {
        Window window = getActivity().getWindow();
        WindowManager.LayoutParams layout = window.getAttributes();
        layout.screenBrightness = brightness;
        window.setAttributes(layout);
    }

    private void restartUiTimer() {
        if (timer != null) {
            timer.cancel(true);
        }

        timer = new HideUiTask();
        timer.execute();
    }

    @Override
    public void onSystemUiVisibilityChange(int i) {
        restartUiTimer();
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if (isActive) {
            restartUiTimer();
        }
        return false;
    }


    private class HideUiTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(hideDelay);
            } catch (InterruptedException e) {}
            return null;
        }

        @SuppressLint("NewApi")
        @Override
        protected void onPostExecute(Void v) {
            view.setSystemUiVisibility(hiddenUiState);
        }
    }
}