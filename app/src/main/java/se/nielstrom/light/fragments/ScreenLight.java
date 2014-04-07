package se.nielstrom.light.fragments;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.util.Timer;
import java.util.TimerTask;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import se.nielstrom.light.app.R;

/**
 * Created by Daniel on 2014-04-06.
 */
public class ScreenLight extends ActiveFragment implements View.OnTouchListener, View.OnSystemUiVisibilityChangeListener {

    private View view;
    private HideUiTask timer;
    private int hiddenUiState = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN;

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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            view.setOnSystemUiVisibilityChangeListener(this);
            pager.setOnTouchListener(this);
        }

        return view;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onActivate() {
        setBrightness(1F); // max brightness
        view.setKeepScreenOn(true);
        restartUiTimer();
    }

    @Override
    protected void onDeactivate() {
        setBrightness(-1F); // user setting
        view.setKeepScreenOn(false);
        if (timer != null) {
            timer.cancel(true);
        }
    }

    private void setBrightness(float brightness) {
        Window window = getActivity().getWindow();
        WindowManager.LayoutParams layout = window.getAttributes();
        layout.screenBrightness = brightness;
        window.setAttributes(layout);
    }

    private void restartUiTimer() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return;
        }

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
        restartUiTimer();
        return false;
    }


    private class HideUiTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                Thread.sleep(3*1000);
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