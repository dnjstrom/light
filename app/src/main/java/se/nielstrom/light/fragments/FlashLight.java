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
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.IOException;

import se.nielstrom.light.app.R;

/**
 * Created by Daniel on 2014-04-06.
 */
public class FlashLight extends ActiveFragment implements SurfaceHolder.Callback {
    private Camera camera;
    private Parameters parameters;
    private FlashSafeGuard timer;
    private int deactivationDelay = 60 * 1000; // 1 min
    private View view;
    private ProgressBar progressBar;
    private boolean isLoadingCamera = false;
    private SurfaceHolder holder;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(container == null) {
            return null;
        }
        view = inflater.inflate(R.layout.flashlight, container, false);

        SurfaceView surfaceView = (SurfaceView) view.findViewById(R.id.surfaceView);
        holder = surfaceView.getHolder();

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

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        holder = surfaceHolder;

        if (camera == null) {
            return;
        }

        try {
            camera.setPreviewDisplay(holder);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (camera != null) {
            camera.stopPreview();
        }
        holder = null;
    }

    private class AsyncActivation extends AsyncTask<Void, Camera, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                publishProgress(Camera.open(0));
            } catch (RuntimeException e) {
                cancel(false);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Camera... cs) {
            camera = cs[0];

            if (camera != null) {
                holder.addCallback(FlashLight.this);

                try {
                    camera.setPreviewDisplay(holder);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                parameters = camera.getParameters();
                turnOnFlash();
            }

            isLoadingCamera = false;
        }

        @Override
        protected void onCancelled(Void v) {
            Toast.makeText(getActivity(), R.string.camera_busy, Toast.LENGTH_LONG);
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

        ValueAnimator animator = ValueAnimator.ofInt(0, -100, -100, -100, 0);
        animator.setInterpolator(new LinearInterpolator());
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