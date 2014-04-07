package se.nielstrom.flashlight.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import se.nielstrom.flashlight.app.R;

/**
 * Created by Daniel on 2014-04-06.
 */
public class ScreenLight extends ActiveFragment {

    private View view;

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

        return view;
    }

    @Override
    protected void onActivate() {
        setBrightness(1F); // max brightness
        view.setKeepScreenOn(true);
    }

    @Override
    protected void onDeactivate() {
        setBrightness(-1F); // user setting
        view.setKeepScreenOn(false);
    }

    private void setBrightness(float brightness) {
        Window window = getActivity().getWindow();
        WindowManager.LayoutParams layout = window.getAttributes();
        layout.screenBrightness = brightness;
        window.setAttributes(layout);
    }
}