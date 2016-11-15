package edu.gatech.vedant.segnudge.Services;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import edu.gatech.vedant.segnudge.Common;
import edu.gatech.vedant.segnudge.MainActivity;
import edu.gatech.vedant.segnudge.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener{

    public static final String INTENT_FILTER = "SegNudge_Message" ;
    GoogleApiClient mGoogleApiClient;

    public MyFirebaseMessagingService() {
    }

    private static final String TAG = "MyFirebaseMsgService";


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
    public void onDestroy() {
        if (null != mGoogleApiClient && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
        super.onDestroy();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            sendNotification(remoteMessage.getData());
        }

    }


    private void sendNotification(Map data) {
        int notificationId = 001;
        String binType=(String)data.get("bin_type");
// Build intent for notification content
        Intent viewIntent = new Intent(this, MainActivity.class);
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(this, 0, viewIntent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_media_play)
                        .setContentTitle("SegNudge")
                        .setContentText("Data synced to wearable "+binType)
                        .setContentIntent(viewPendingIntent);

// Get an instance of the NotificationManager service
        NotificationManagerCompat notificationManager =
                NotificationManagerCompat.from(this);

// Build the notification and issues it with notification manager.
        notificationManager.notify(notificationId, notificationBuilder.build());

        changeBin(binType);
    }

    private void changeBin(String binType) {
        Log.d(TAG,"Changing bin to: "+binType);

        PutDataMapRequest putDataMapReq = PutDataMapRequest.create(Common.PATH_BIN_TYPE);
        putDataMapReq.getDataMap().putString(Common.KEY_BIN_TYPE, binType +" "+ System.currentTimeMillis());
        PutDataRequest putDataReq = putDataMapReq.asPutDataRequest();
        putDataMapReq.setUrgent();
        PendingResult<DataApi.DataItemResult> pendingResult =
                Wearable.DataApi.putDataItem(mGoogleApiClient, putDataReq);

        DataApi.DataItemResult result = pendingResult.await();
        if(result.getStatus().isSuccess()) {
            Log.d(TAG, "Data item set: " + result.getDataItem().getUri());
        }
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG,"On Connected");
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
