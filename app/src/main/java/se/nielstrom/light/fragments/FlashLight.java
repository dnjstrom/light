package se.nielstrom.light.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
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
    private boolean isLoadingCamera = false;

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
        if (isLoadingCamera) {
            return;
        } else if (camera == null) {
            // If no camera, load it asynchronously
            isLoadingCamera = true;
            new AsyncActivation().execute();
        } else {
            turnOnFlash();
        }
    }

    private class AsyncActivation extends AsyncTask<Void, Camera, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            publishProgress(Camera.open());
            return null;
        }

        @Override
        protected void onProgressUpdate(Camera... cs) {
            camera = cs[0];
            parameters = camera.getParameters();
            turnOnFlash();
            isLoadingCamera = false;
        }
    }

    private void turnOnFlash() {
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

    @Override
    protected void onFirstUse() {
        final ViewPager pager = (ViewPager) getActivity().findViewById(R.id.mainpager);

        ValueAnimator animator = ValueAnimator.ofInt(0, -100, 0);
        animator.setInterpolator(new BounceInterpolator());
        animator.setDuration(1000);

        animator.addListener( new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                lockActivationState(true);
                pager.beginFakeDrag();
            }
            @Override
            public void onAnimationEnd(Animator animation) {
                lockActivationState(false);
                pager.endFakeDrag();
            }
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