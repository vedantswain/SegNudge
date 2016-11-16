package edu.gatech.vedant.segnudge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.DismissOverlayView;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

public class MainActivity extends Activity {

    private TextView mTextView;
    private static final String TAG = "MainActivity";

    private static final int SWIPE_THRESHOLD = 100;
    private static final int SWIPE_VELOCITY_THRESHOLD = 50;

    private DismissOverlayView mDismissOverlay;
    private GestureDetector mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mTextView = (TextView) stub.findViewById(R.id.text);
            }
        });

        startService(new Intent(this, DataService.class));

        // Configure a gesture detector
        mDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent event) {
                Log.d(TAG, " onLongPress: " + event.toString());
                finish();
            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                boolean result = false;
                try {
                    float diffY = e2.getY() - e1.getY();
                    float diffX = e2.getX() - e1.getX();
                    if (Math.abs(diffX) > Math.abs(diffY)) {
                        if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                        }
                        result = true;
                    }
                    else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                        if (diffY > 0) {
                            onSwipeBottom();
                        } else {
                            onSwipeTop();
                        }
                    }
                    result = true;

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        });

    }

    // Capture long presses
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        Log.d(TAG,"Touch event");
        return mDetector.onTouchEvent(ev) || super.onTouchEvent(ev);
    }

    public void onSwipeRight() {
        Log.d(TAG, "Swipe Right");
    }

    public void onSwipeLeft() {
        Log.d(TAG, "Swipe Left");
    }

    public void onSwipeTop() {
        Log.d(TAG, "Swipe Top");
    }

    public void onSwipeBottom() {
        Log.d(TAG, "Swipe Bottom");
    }
}
