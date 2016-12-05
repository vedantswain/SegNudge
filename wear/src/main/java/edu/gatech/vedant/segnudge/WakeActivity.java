package edu.gatech.vedant.segnudge;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.BoxInsetLayout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.Arrays;
import java.util.Map;

public class WakeActivity extends WearableActivity {

    private static final String
            LOG_RECORD_CAPABILITY_NAME = "log_record";

    public static final String LOG_RECORD_MESSAGE_PATH = "/log_record";
    private static final long KILL_TIME = 2000; //milliseconds to kill app after terminal

    private GoogleApiClient mGoogleApiClient;

    private ViewFlipper mViewFlipper;
    private static final String TAG = "WakeActivity";

    private GestureDetector mDetector;

    private Map<String,String> mMap;

    private final String YES="y";
    private final String NO="n";

    private String initProbe;
    private String currProbe;
    private long startTime;
    private long stopTime;

    private BoxInsetLayout mContainerView;
    private int noId, yesId;
    private Map<String,String> tipMap;
    private String transcriptionNodeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wake);
        setAmbientEnabled();

        startTime=System.currentTimeMillis();
        mContainerView = (BoxInsetLayout) findViewById(R.id.container);

        Intent intent = getIntent();
        String dataTxt = intent.getStringExtra(Common.KEY_BIN_TYPE);

        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] vibrationPattern = {0, 500, 50, 300};
        //-1 - don't repeat
        final int indexInPatternToRepeat = -1;
        vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);

        getInitProbe(dataTxt);

        currProbe=initProbe;

        Log.d(TAG, "Activity started");

        showInteractive();

        initGoogleApiClient();

        configDetector();
    }

    public void getInitProbe(String dataTxt){
        //Plastic tree
        if(dataTxt.contains("plastic")){
            mMap =Common.getPbMap(this);
            initProbe="pb";
        }
        //Aluminum tree
        else if(dataTxt.contains("aluminum")) {
            mMap = Common.getAcMap(this);
            initProbe = "ac";
        }
        //Paper tree
        else if(dataTxt.contains("paper")) {
            mMap = Common.getMpMap(this);
            initProbe = "mp";
        }
        //Landfill tree
        else {
            mMap = Common.getLfMap(this);
            initProbe = "lf";
        }
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
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Configure the viewflipper
        configFlipper();
    }

    private void configFlipper() {
        mViewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        mViewFlipper.addView(getRootView(mMap.get(currProbe)));
//        requestLog("Test log");
//        mViewFlipper.addView(getNewView("End World"));
    }

    @Override
    public void onEnterAmbient(Bundle ambientDetails) {
        super.onEnterAmbient(ambientDetails);
        updateDisplay();
    }

    @Override
    public void onUpdateAmbient() {
        super.onUpdateAmbient();
        updateDisplay();
    }

    @Override
    public void onExitAmbient() {
        updateDisplay();
        super.onExitAmbient();
    }

    private void updateDisplay() {
        if (isAmbient()) {
            Log.d(TAG,"Is Ambient");
            mContainerView.setBackgroundColor(getResources().getColor(R.color.colorAmbient));

        } else {
            showInteractive();
        }
    }

    public void showInteractive(){
        Log.d(TAG,"Is Interactive");
        if(isTerminal(currProbe)) {
            Log.d(TAG,"Is Success");
            mContainerView.setBackground(getResources().getDrawable(R.drawable.rect_interactive_bg_success));
        }
        else {
            Log.d(TAG,"Is Tree");
            mContainerView.setBackground(getResources().getDrawable(R.drawable.rect_interactive_bg));
        }

        View rectNo = findViewById(R.id.rectangleNo);
        View rectYes = findViewById(R.id.rectangleYes);
        TextView textNo = (TextView) findViewById(noId);
        TextView textYes = (TextView) findViewById(yesId);

        try {
            if(!isTerminal(currProbe)) {
                rectNo.setVisibility(View.VISIBLE);
                rectYes.setVisibility(View.VISIBLE);
                textNo.setVisibility(View.VISIBLE);
                textYes.setVisibility(View.VISIBLE);
            }
        }
        catch (NullPointerException e){
            Log.d(TAG,e.toString());
        }
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

        textView.setTextAppearance(this,android.R.style.TextAppearance_DeviceDefault_Large);
        textView.setTextColor(getResources().getColor(R.color.colorTextTree));

        rl.addView(textView);

        return rl;
    }

    public View getRootView(String question){
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

        textView.setTextAppearance(this,android.R.style.TextAppearance_DeviceDefault_Large);
        textView.setTextColor(getResources().getColor(R.color.colorTextTree));

        rl.addView(textView);

        TextView noView = new TextView(this);
        noView.setText("No");
        RelativeLayout.LayoutParams noParams =
                new RelativeLayout.LayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        noParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        noParams.addRule(RelativeLayout.ALIGN_PARENT_START);
        noParams.setMargins(10,0,0,0);
        noView.setLayoutParams(noParams);
        noView.setTextColor(getResources().getColor(R.color.colorTextTree));
        noView.setVisibility(View.INVISIBLE);

        noId = View.generateViewId();
        noView.setId(noId);

        rl.addView(noView);

        TextView yesView = new TextView(this);
        yesView.setText("Yes");
        RelativeLayout.LayoutParams yesParams =
                new RelativeLayout.LayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        yesParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        yesParams.addRule(RelativeLayout.ALIGN_PARENT_END);
        yesParams.setMargins(0,0,10,0);
        yesView.setLayoutParams(yesParams);
        yesView.setTextColor(getResources().getColor(R.color.colorTextTree));
        yesView.setVisibility(View.INVISIBLE);

        yesId = View.generateViewId();
        yesView.setId(yesId);

        rl.addView(yesView);

        ImageView swipeRight = new ImageView(this);
        swipeRight.setImageDrawable(getResources().getDrawable(R.drawable.ic_right_swipe));
        RelativeLayout.LayoutParams rightParams =
                new RelativeLayout.LayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        rightParams.addRule(RelativeLayout.END_OF,noId);
        rightParams.addRule(RelativeLayout.ALIGN_TOP,noId);
        swipeRight.setLayoutParams(rightParams);

        rl.addView(swipeRight);

        ImageView swipeLeft = new ImageView(this);
        swipeLeft.setImageDrawable(getResources().getDrawable(R.drawable.ic_left_swipe));
        RelativeLayout.LayoutParams leftParams =
                new RelativeLayout.LayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        leftParams.addRule(RelativeLayout.START_OF,yesId);
        leftParams.addRule(RelativeLayout.ALIGN_TOP,yesId);
        swipeLeft.setLayoutParams(leftParams);

        rl.addView(swipeLeft);

        return rl;
    }


    public View finalLeaf(String probe){
        mContainerView.setBackground(getResources().getDrawable(R.drawable.rect_interactive_bg_success));

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

        textView.setTextAppearance(this,android.R.style.TextAppearance_DeviceDefault_Large);
//        textView.setTextColor(getResources().getColor(R.color.colorTextSuccess));

        rl.addView(textView);

        TextView emojiView = new TextView(this);
        emojiView.setText(getEmoji(probe));
        RelativeLayout.LayoutParams emojiParams =
                new RelativeLayout.LayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        emojiParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        emojiParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        emojiView.setLayoutParams(emojiParams);

        emojiView.setTextAppearance(this,android.R.style.TextAppearance_DeviceDefault_Large);
        emojiView.setTextSize(40);

        rl.addView(emojiView);

        stopTime= System.currentTimeMillis();

        requestLog(startTime+","+initProbe+","+currProbe+","+stopTime);
        setFinishTimer();

        return rl;
    }

    private void setFinishTimer() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, KILL_TIME);
    }

    public View tipLeaf(String tip){
        mContainerView.setBackground(getResources().getDrawable(R.drawable.rect_interactive_bg_success));

        RelativeLayout rl = new RelativeLayout(this);
        RelativeLayout.LayoutParams rlParams =
                new RelativeLayout.LayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        rl.setLayoutParams(rlParams);

        TextView textView = new TextView(this);
        textView.setText(tipMap.get(tip));
        RelativeLayout.LayoutParams layoutParams =
                new RelativeLayout.LayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL);
        textView.setLayoutParams(layoutParams);

        textView.setTextAppearance(this,android.R.style.TextAppearance_DeviceDefault_Large);
//        textView.setTextColor(getResources().getColor(R.color.colorTextSuccess));

        rl.addView(textView);

        TextView emojiView = new TextView(this);
        emojiView.setText(new String(Character.toChars(getResources().getInteger(R.integer.tip))));
        RelativeLayout.LayoutParams emojiParams =
                new RelativeLayout.LayoutParams(new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        emojiParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        emojiParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        emojiView.setLayoutParams(emojiParams);

        emojiView.setTextAppearance(this,android.R.style.TextAppearance_DeviceDefault_Large);
        emojiView.setTextSize(40);

        rl.addView(emojiView);

        return rl;
    }

    public String getEmoji(String probe){
        int emojiInt;
        String probeText=mMap.get(probe);
        if(probeText.contains("another"))
            emojiInt=R.integer.another;
        else if(probeText.contains("Landfill"))
            emojiInt=R.integer.landfill;
        else if (probe.contains("pb"))
            emojiInt=R.integer.plastic;
        else if (probe.contains("ac"))
            emojiInt=R.integer.aluminum;
        else if (probe.contains("mp"))
            emojiInt=R.integer.paper;
        else
            emojiInt=R.integer.landfill;

        return new String(Character.toChars(getResources().getInteger(emojiInt)));
    }

    private void initGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .build();

        mGoogleApiClient.connect();
    }


    private void requestLog(final String logText) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mGoogleApiClient ).await();
                for(Node node : nodes.getNodes()) {
                    Wearable.MessageApi.sendMessage(
                            mGoogleApiClient, node.getId(), LOG_RECORD_MESSAGE_PATH, logText.getBytes()).setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {
                        @Override
                        public void onResult(@NonNull MessageApi.SendMessageResult result) {
                            if (!result.getStatus().isSuccess()) {
                                // Failed to send message
                                Log.d(TAG, "Failed to send log");
                            }
                        }
                    });

                }
            }
        }).start();
    }

}
