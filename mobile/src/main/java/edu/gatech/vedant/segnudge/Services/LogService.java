package edu.gatech.vedant.segnudge.Services;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import edu.gatech.vedant.segnudge.Logger;

public class LogService extends WearableListenerService implements MessageApi.MessageListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static final String LOG_RECORD_MESSAGE_PATH = "/log_record";

    public static final String TAG = "LogService" ;

    GoogleApiClient mGoogleApiClient;

    public LogService() {
    }

    public void onCreate(){
        super.onCreate();
        // Build a new GoogleApiClient for the Wearable API
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        mGoogleApiClient.connect();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if (messageEvent.getPath().equals(LOG_RECORD_MESSAGE_PATH)) {
            String recMsg=new String(messageEvent.getData());
            Log.d(TAG,recMsg);
            Logger.logWrite(recMsg);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG,"On Connected");
        Wearable.MessageApi.addListener( mGoogleApiClient, this );
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG,"Connection Suspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG,"Connection Failed");
    }
}
