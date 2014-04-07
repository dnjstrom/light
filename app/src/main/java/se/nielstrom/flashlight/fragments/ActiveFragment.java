package se.nielstrom.flashlight.fragments;

import android.support.v4.app.Fragment;

/**
 * The onActivate and onDeactivate methods are called by the FragmentAdapter to tell the fragment
 * whether it is visible.
 *
 * Created by Daniel on 2014-04-06.
 */
public abstract class ActiveFragment extends Fragment {
    private boolean isActive = false;

    protected abstract void onActivate();
    protected abstract void onDeactivate();

    public void activate() {
        if (!isActive) {
            isActive = true;
            onActivate();
        }
    }

    public void deactivate() {
        if (isActive) {
            isActive = false;
            onDeactivate();
        }
    }

    public void onPause() {
        super.onPause();
        deactivate();
    }
}
