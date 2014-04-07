package se.nielstrom.flashlight.fragments;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Timer;
import java.util.TimerTask;

import se.nielstrom.flashlight.app.R;

/**
 * Created by Daniel on 2014-04-06.
 */
public class FlashLight extends ActiveFragment {
    private Camera camera;
    private Parameters parameters;
    private Timer timer;
    private int deactivationDelay = 60 * 1000; // 1 min

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(container == null) {
            return null;
        }

        return inflater.inflate(R.layout.flashlight, container, false);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    @Override
    protected void onActivate() {
        if (camera == null) {
            camera = Camera.open();
            parameters = camera.getParameters();
        }

        parameters.setFlashMode(Parameters.FLASH_MODE_TORCH);
        camera.setParameters(parameters);
        camera.startPreview();

        timer = new Timer();
        timer.schedule(new FlashSafeGuard(), deactivationDelay);
    }

    @Override
    protected void onDeactivate() {
        timer.cancel();
        parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
        camera.setParameters(parameters);
        camera.stopPreview();
    }

    private class FlashSafeGuard extends TimerTask {
        @Override
        public void run() {
            FlashLight.this.deactivate();
            timer.cancel();
        }
    }
}