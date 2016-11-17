package edu.gatech.vedant.segnudge;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.wearable.view.BoxInsetLayout;
import android.support.wearable.view.DismissOverlayView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.Arrays;
import java.util.Map;

public class LauncherActivity extends Activity {

    private TextView mTextView;
    private ViewFlipper mViewFlipper;
    private static final String TAG = "MainActivity";


    private DismissOverlayView mDismissOverlay;
    private GestureDetector mDetector;

    private Map<String,String> mMap;

    private final String YES="y";
    private final String NO="n";

    private String initProbe="pb";
    private String currProbe;

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

        //Plastic tree
        mMap =Common.getPbMap(this);
        currProbe=initProbe;

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

        mViewFlipper.addView(getNewView(mMap.get("pb")));
//        mViewFlipper.addView(getNewView("End World"));
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

    public void onSwipeLeft() {
        Log.d(TAG, "Swipe Left");
        if (!isTerminal(currProbe))
            ifYes(currProbe+YES);
    }


    public void onSwipeRight() {
        Log.d(TAG, "Swipe Right");
        if (!isTerminal(currProbe))
            ifNo(currProbe+NO);
    }

    public void ifYes(String probe){
        currProbe=probe;
        View probeView = getNewView(mMap.get(probe));
        if (isTerminal(probe))
            probeView=finalLeaf(probe);
        mViewFlipper.addView(probeView,0);
        animNext();
    }

    public void ifNo(String probe){
        currProbe=probe;
        View probeView = getNewView(mMap.get(probe));
        if (isTerminal(probe))
            probeView=finalLeaf(probe);
        mViewFlipper.addView(probeView);
        animPrev();
    }


    public void animNext(){
        mViewFlipper.setInAnimation(this, R.anim.transition_in_left);
        mViewFlipper.setOutAnimation(this, R.anim.transition_out_left);
        //inverted scrolling
        mViewFlipper.showNext();
        mViewFlipper.removeViewAt(1);
    }

    public void animPrev(){
        mViewFlipper.setInAnimation(this, R.anim.transition_in_right);
        mViewFlipper.setOutAnimation(this, R.anim.transition_out_right);
        //inverted scrolling
        mViewFlipper.showPrevious();
        mViewFlipper.removeViewAt(0);
    }

    public boolean isTerminal(String probe){
       return Arrays.asList(Common.leafProbes).contains(probe);
    }

    public View finalLeaf(String probe){
        BoxInsetLayout bil = (BoxInsetLayout) findViewById(R.id.watch_view_stub);
        bil.setBackgroundColor(getResources().getColor(R.color.colorSuccess));

        View rectNo = findViewById(R.id.rectangleNo);
        View rectYes = findViewById(R.id.rectangleYes);

        rectNo.setVisibility(View.INVISIBLE);
        rectYes.setVisibility(View.INVISIBLE);

        RelativeLayout rl = new RelativeLayout(this);
        RelativeLayout.LayoutParams rlParams =
                new RelativeLayout.LayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        rl.setLayoutParams(rlParams);

        TextView textView = new TextView(this);
        textView.setText(mMap.get(probe));
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        textView.setLayoutParams(layoutParams);

        textView.setTextColor(getResources().getColor(R.color.colorTextSuccess));

        rl.addView(textView);

        return rl;
    }
}
