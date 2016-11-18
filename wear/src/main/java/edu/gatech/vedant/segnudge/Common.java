package edu.gatech.vedant.segnudge;

import android.content.Context;
import android.content.res.Resources;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by vedantdasswain on 14/11/16.
 */

public class Common {
    public static String transcriptionNodeId;
    public static final String KEY_BIN_TYPE = "Bin Type";
    public static final String PATH_BIN_TYPE = "/bin-type";
    public static final int SWIPE_THRESHOLD = 20;
    public static final int SWIPE_VELOCITY_THRESHOLD = 10;

    public static final String[] leafProbes = new String []
            {"pbyy","pbyn","pbn","acyy","acyn","acn","mpyy","mpyny","mpynn","mpn","lfy","lfn"};

    public static Map<String,String> getPbMap(Context ctx){
        Resources res=ctx.getResources();
        Map<String, String> mMap = new HashMap<String, String>();
        mMap.put("pb",res.getString(R.string.pb));
        mMap.put("pby",res.getString(R.string.pby));
        mMap.put("pbyy",res.getString(R.string.pbyy));
        mMap.put("pbyn",res.getString(R.string.pbyn));
        mMap.put("pbn",res.getString(R.string.pbn));

        return mMap;
    }


    public static Map<String,String> getAcMap(Context ctx){
        Resources res=ctx.getResources();
        Map<String, String> mMap = new HashMap<String, String>();
        mMap.put("ac",res.getString(R.string.ac));
        mMap.put("acy",res.getString(R.string.acy));
        mMap.put("acyy",res.getString(R.string.acyy));
        mMap.put("acyn",res.getString(R.string.acyn));
        mMap.put("acn",res.getString(R.string.acn));

        return mMap;
    }

    public static Map<String,String> getMpMap(Context ctx){
        Resources res=ctx.getResources();
        Map<String, String> mMap = new HashMap<String, String>();
        mMap.put("mp",res.getString(R.string.mp));
        mMap.put("mpy",res.getString(R.string.mpy));
        mMap.put("mpyy",res.getString(R.string.mpyy));
        mMap.put("mpyn",res.getString(R.string.mpyn));
        mMap.put("mpyny",res.getString(R.string.mpyny));
        mMap.put("mpynn",res.getString(R.string.mpynn));
        mMap.put("mpn",res.getString(R.string.mpn));

        return mMap;
    }

    public static Map<String,String> getLfMap(Context ctx){
        Resources res=ctx.getResources();
        Map<String, String> mMap = new HashMap<String, String>();
        mMap.put("lf",res.getString(R.string.lf));
        mMap.put("lfn",res.getString(R.string.lfy));
        mMap.put("lfy",res.getString(R.string.lfn));

        return mMap;
    }

    public static void setTranscriptionId(String id){
        transcriptionNodeId=id;
    }
}
