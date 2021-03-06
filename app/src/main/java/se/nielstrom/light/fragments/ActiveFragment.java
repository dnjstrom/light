package se.nielstrom.light.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;

/**
 * A fragment with side effects which can be activated or deactivated.
 *
 * Created by Daniel on 2014-04-06.
 */
public abstract class ActiveFragment extends Fragment {
    private boolean isResumed = false;
    protected boolean isActive = false;
    private boolean isToBeActivated = false;
    protected boolean isFirstUse = true;
    private boolean isLocked = false;

    protected abstract void onActivate();
    protected abstract void onDeactivate();

    protected SharedPreferences settings;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settings = getActivity().getSharedPreferences("ActiveFragment_" + getClass().getSimpleName(), 0);
        isFirstUse = settings.getBoolean("isFirstUse", true);
    }


    /**
     * Activates the fragment by calling onActivate. If the fragment is not yet resumed, it will
     * defer activation until the fragment is.
     */
    public void activate() {
        if (!isResumed) {
            isToBeActivated = true;
            return;
        }

        if (isLocked) {
            return;
        }

        if (!isActive) {
            isActive = true;
            onActivate();
        }

        if (isFirstUse) {
            isFirstUse = false;
            settings.edit()
                    .putBoolean("isFirstUse", false)
                    .commit();
            onFirstUse();
        }
    }

    /**
     * Method stub meant to be overridden by implementor.
     */
    protected void onFirstUse() {}

    public void deactivate() {
        if (isLocked) {
            return;
        }

        if (isActive) {
            isActive = false;
            onDeactivate();
        }
    }

    public void lockActivationState(boolean locked) {
        this.isLocked = locked;
    }

    @Override
    public void onResume() {
        super.onResume();
        isResumed = true;

        if (isToBeActivated) {
            isToBeActivated = false;
            activate();
        }
    }

    public void onPause() {
        super.onPause();
        deactivate();
        isResumed = false;
    }
}
