package edu.gatech.vedant.segnudge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.DismissOverlayView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class LauncherActivity extends Activity {

    private TextView mTextView;
    private ViewFlipper mViewFlipper;
    private static final String TAG = "MainActivity";


    private DismissOverlayView mDismissOverlay;
    private GestureDetector mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
//        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
//        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
//            @Override
//            public void onLayoutInflated(WatchViewStub stub) {
//                mTextView = (TextView) stub.findViewById(R.id.text);
//            }
//        });

        startService(new Intent(this, DataService.class));

        // Configure a gesture detector
        configDetector();

    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Configure the viewflipper
        configFlipper();
    }

    private void configFlipper() {
        mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        mViewFlipper.addView(getNewView("Hello World"));
        mViewFlipper.addView(getNewView("End World"));
    }

    public View getNewView(String question){
        RelativeLayout rl = new RelativeLayout(this);
        RelativeLayout.LayoutParams rlParams =
                new RelativeLayout.LayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        rl.setLayoutParams(rlParams);

        TextView textView = new TextView(this);
        textView.setText(question);
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        textView.setLayoutParams(layoutParams);

        rl.addView(textView);

        return rl;
    }

    public void configDetector(){
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
                        if (Math.abs(diffX) > Common.SWIPE_THRESHOLD && Math.abs(velocityX) > Common.SWIPE_VELOCITY_THRESHOLD) {
                            if (diffX > 0) {
                                onSwipeRight();
                            } else {
                                onSwipeLeft();
                            }
                        }
                        result = true;
                    }

                    result = true;

                } catch (Exception exception) {
                    exception.printStackTrace();
                }
                return result;
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        Log.d(TAG,"Touch event");
        return mDetector.onTouchEvent(ev) || super.onTouchEvent(ev);
    }

    public void onSwipeRight() {
        Log.d(TAG, "Swipe Right");
        mViewFlipper.setInAnimation(this, R.anim.transition_in_right);
        mViewFlipper.setOutAnimation(this, R.anim.transition_out_right);
        mViewFlipper.showPrevious();
    }

    public void onSwipeLeft() {
        Log.d(TAG, "Swipe Left");
        mViewFlipper.setInAnimation(this, R.anim.transition_in_left);
        mViewFlipper.setOutAnimation(this, R.anim.transition_out_left);
        mViewFlipper.showNext();
    }


}
