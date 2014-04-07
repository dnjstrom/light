package se.nielstrom.flashlight.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import fr.castorflex.android.verticalviewpager.VerticalViewPager;
import se.nielstrom.flashlight.app.R;

/**
 * Created by Daniel on 2014-04-06.
 */
public class ScreenLight extends ActiveFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(container == null) {
            return null;
        }

        View view = inflater.inflate(R.layout.screenlight, container, false);
        String[] colors = getResources().getStringArray(R.array.screenColors);
        VerticalViewPager pager = (VerticalViewPager) view.findViewById(R.id.screenpager);
        ScreenColorAdapter adapter = new ScreenColorAdapter(getActivity().getSupportFragmentManager(), colors);
        pager.setAdapter(adapter);

        return view;
    }

    @Override
    protected void onActivate() {
        WindowManager.LayoutParams layout = getActivity().getWindow().getAttributes();
        layout.screenBrightness = 1F; // Max brightness
        getActivity().getWindow().setAttributes(layout);
    }

    @Override
    protected void onDeactivate() {
        WindowManager.LayoutParams layout = getActivity().getWindow().getAttributes();
        layout.screenBrightness = -1F; // Use user setting
        getActivity().getWindow().setAttributes(layout);
    }
}