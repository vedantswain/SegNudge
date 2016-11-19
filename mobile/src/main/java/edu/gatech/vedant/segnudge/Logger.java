package edu.gatech.vedant.segnudge;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Created by vedantdasswain on 19/11/16.
 */

public class Logger {
    private static String TAG="LogWriter";

    public static String HEADER="start time"+","+"initial probe"+","+"final bin"+","+"stop_time";
    public static File logFile;
    public static File SegNudgeDir=new File(Environment.getExternalStorageDirectory()+File.separator+"SegNudge");

    public static void PathCheck(File logFile,String header){
        if(!SegNudgeDir.exists()){
            try{
                SegNudgeDir.mkdir();
                Log.v(TAG,"directory made at: "+SegNudgeDir.getPath());
            }
            catch(Exception e){
                Log.v(TAG,e.toString());
            }
        }

        if(!logFile.exists()){
            BufferedWriter buf;
            try {
                Log.v(TAG,"file created at: "+logFile.getPath());
                logFile.createNewFile();
                buf = new BufferedWriter(new FileWriter(logFile, true));
                buf.append(header);
                buf.newLine();
                buf.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                System.out.print(e.toString());
            }
        }
    }

    public static void LogWrite(File logFile,String logstring,String header){
        synchronized(logFile){

            PathCheck(logFile,header);

            BufferedWriter buf;
            try {
                buf = new BufferedWriter(new FileWriter(logFile, true));

                buf.append(logstring);
                buf.newLine();
                buf.close();
//						Log.v("ELSERVICES", logstring+" written  into"+logFile.toString() );
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Log.v(TAG,e.toString());
            }

        }
    }

    public static void logWrite(String logstring){
        logFile=new File(Environment.getExternalStorageDirectory()+File.separator+"SegNudge"+"log"+".csv");
        LogWrite(logFile,logstring, HEADER);
    }



}
