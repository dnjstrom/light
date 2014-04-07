package se.nielstrom.light.fragments;

import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import se.nielstrom.light.app.R;

/**
 * Created by Daniel on 2014-04-06.
 */
public class FlashLight extends ActiveFragment {
    private Camera camera;
    private Parameters parameters;
    private FlashSafeGuard timer;
    private int deactivationDelay = 60 * 1000; // 1 min
    private View view;
    private ProgressBar progressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(container == null) {
            return null;
        }
        view = inflater.inflate(R.layout.flashlight, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setMax(deactivationDelay);

        return view;
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

        timer = new FlashSafeGuard();
        timer.execute();

        view.setKeepScreenOn(true);
    }

    @Override
    protected void onDeactivate() {
        timer.cancel(true);
        parameters.setFlashMode(Parameters.FLASH_MODE_OFF);
        camera.setParameters(parameters);
        camera.stopPreview();
        view.setKeepScreenOn(false);
    }

    private class FlashSafeGuard extends AsyncTask<Void, Void, Void> {

        long startMillis;
        private int lightTimeLeft;

        public FlashSafeGuard() {
            startMillis = SystemClock.elapsedRealtime();
            lightTimeLeft = deactivationDelay;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            while(lightTimeLeft > 0) {
                lightTimeLeft = (int) (deactivationDelay - (SystemClock.elapsedRealtime() - startMillis));
                publishProgress();

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    return null;
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... v) {
            progressBar.setProgress(lightTimeLeft);
        }

        @Override
        protected void onPostExecute(Void v) {
            cancel();
        }

        @Override
        protected void onCancelled(Void v) {
            cancel();
        }

        private void cancel() {
            lightTimeLeft = 0;
            publishProgress();
            FlashLight.this.deactivate();
        }
    }
}