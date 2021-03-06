package edu.gatech.vedant.segnudge;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.concurrent.TimeUnit;

public class DataService extends WearableListenerService{
    private static final String
            LOG_RECORD_CAPABILITY_NAME = "log_record";

    private GoogleApiClient mGoogleApiClient;

    public DataService() {
    }

    public static final String TAG = "DataService" ;

    public void onCreate(){
        super.onCreate();
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onDataChanged: " + dataEvents);
        }

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();

        ConnectionResult connectionResult =
                googleApiClient.blockingConnect(30, TimeUnit.SECONDS);

        if (!connectionResult.isSuccess()) {
            Log.e(TAG, "Failed to connect to GoogleApiClient.");
            return;
        }

        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED) {
                // DataItem changed
                DataItem item = event.getDataItem();
                if (item.getUri().getPath().compareTo(Common.PATH_BIN_TYPE) == 0) {
                    DataMap dataMap = DataMapItem.fromDataItem(item).getDataMap();
                    Log.d(TAG, dataMap.getString(Common.KEY_BIN_TYPE));

                    Intent mIntent = new Intent(this, WakeActivity.class);
                    mIntent.putExtra(Common.KEY_BIN_TYPE,dataMap.getString(Common.KEY_BIN_TYPE));
                    mIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(mIntent);
                }
            } else if (event.getType() == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }



}

