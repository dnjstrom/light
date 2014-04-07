package se.nielstrom.flashlight.fragments;

import android.support.v4.app.Fragment;

/**
 * A fragment with side effects which can be activated or deactivated.
 *
 * Created by Daniel on 2014-04-06.
 */
public abstract class ActiveFragment extends Fragment {
    private boolean isResumed = false;
    private boolean isActive = false;
    private boolean isToBeActivated = false;

    protected abstract void onActivate();
    protected abstract void onDeactivate();

    /**
     * Activates the fragment by calling onActivate. If the fragment is not yet resumed, it will
     * defer activation until the fragment is.
     */
    public void activate() {
        if (!isResumed) {
            isToBeActivated = true;
            return;
        }

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

    @Override
    public void onResume() {
        super.onResume();
        isResumed = true;

        if (isToBeActivated) {
            activate();
        }
    }

    public void onPause() {
        super.onPause();
        deactivate();
        isResumed = false;
    }
}
