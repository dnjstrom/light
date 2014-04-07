package se.nielstrom.flashlight.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import se.nielstrom.flashlight.app.R;

/**
 * Created by Daniel on 2014-04-07.
 */
public class ColorFragment extends Fragment {

    View view;
    int color;

    public ColorFragment(int color) {
        this.color = color;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.color_fragment, container, false);
        view.setBackgroundColor(color);

        return view;
    }
}
