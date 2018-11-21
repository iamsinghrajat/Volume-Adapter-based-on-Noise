package com.rajatsingh.hppc.sounddetector;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import android.widget.TextView;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import android.media.MediaRecorder;
/**
 * Created by hp pc on 08-10-2017.
 */

public class SoundService extends Service {
    int bufferSize;
    AudioRecord audio;


    TextView displayLabel;

    // Projection array. Creating indices for this array instead of doing
// dynamic lookups improves performance.
    public static final String[] EVENT_PROJECTION = new String[]{
            CalendarContract.Calendars._ID,                           // 0
            CalendarContract.Calendars.ACCOUNT_NAME,                  // 1
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,         // 2
            CalendarContract.Calendars.OWNER_ACCOUNT                  // 3
    };

    // The indices for the projection array above.
    private static final int PROJECTION_ID_INDEX = 0;
    private static final int PROJECTION_ACCOUNT_NAME_INDEX = 1;
    private static final int PROJECTION_DISPLAY_NAME_INDEX = 2;
    private static final int PROJECTION_OWNER_ACCOUNT_INDEX = 3;
    Thread thread;
    double lastLevel;
    private MediaRecorder mRecorder = null;

    AudioManager audioManager;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

            try {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mRecorder.setOutputFile("/dev/null");
                mRecorder.prepare();
            mRecorder.start();
            }
            catch (IOException e) {
                e.printStackTrace();
            }


      //  ere();
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        int sampleRate = 8000;
        final int a = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        Log.e("A", a + "");
        try {
            bufferSize = AudioRecord.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            audio = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, bufferSize);


            audio.startRecording();
            Timer t = new Timer();
//Set the schedule function and rate
            t.scheduleAtFixedRate(new TimerTask() {


                                      @Override
                                      public void run() {try {
                                          readAudioBuffer();
                                          double ampl=mRecorder.getMaxAmplitude()/2700.00;
                                          sendMessage((int)Math.round(ampl));
                                          int a1=audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL);
                                          int a2=audioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
                                          int a3=audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
                                          int a4=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                                          Log.e("max",a1+" "+a2+" "+a3+ " "+a4);
                                         Log.e("level1", ampl+"");
                                         // Log.e("level2", lastLevel + "");

                                          Log.e("Volume",""+(int)  Math.round(3*ampl+2));
                                          Log.e("Volume",""+(int) Math.round(6*ampl+2));
                                          if(ampl>4.0)
                                              audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                                          else
                                              audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);



                                          audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL,(int)  Math.round(3*ampl+2), 0);
                                          // audioManager.setStreamVolume(AudioManager.USE_DEFAULT_STREAM_TYPE, audioManager.getStreamMaxVolume(AudioManager.USE_DEFAULT_STREAM_TYPE), 0);
                                          audioManager.setStreamVolume(AudioManager.STREAM_RING, (int) Math.round(3*ampl+2), 0);
                                          audioManager.setStreamVolume(AudioManager.STREAM_ALARM, (int)  Math.round(3*ampl+2), 0);
                                          audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int)  Math.round(6*ampl+2), 0);

                                         /* if (lastLevel > 50) {
                                              audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL), 0);
                                              // audioManager.setStreamVolume(AudioManager.USE_DEFAULT_STREAM_TYPE, audioManager.getStreamMaxVolume(AudioManager.USE_DEFAULT_STREAM_TYPE), 0);
                                              audioManager.setStreamVolume(AudioManager.STREAM_RING, audioManager.getStreamMaxVolume(AudioManager.STREAM_RING), 0);
                                              audioManager.setStreamVolume(AudioManager.STREAM_ALARM, audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM), 0);
                                              //  audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);

                                          } else
                                              audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC) / 2, 0);

*/
                                         /* if(lastLevel > 0 && lastLevel <= 50){
                                              Log.e("Volume","Low");
                                              // Toast.makeText(getApplicationContext(),"Low Sound",Toast.LENGTH_SHORT).show();
                                              audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)-9, 0);
                                              //mouthImage.setImageResource(R.drawable.mouth4);
                                          }else
                                          if(lastLevel > 50 && lastLevel <= 120){
                                              Log.e("Volume","Mid");
                                              // Toast.makeText(getApplicationContext(),"Mediumd Sound",Toast.LENGTH_SHORT).show();
                                              audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)-6, 0);
                                              //mouthImage.setImageResource(R.drawable.mouth3);
                                          }else
                                          if(lastLevel > 100 && lastLevel <= 200){
                                              Log.e("Volume","High");
                                              //  Toast.makeText(getApplicationContext(),"Loud Sound",Toast.LENGTH_SHORT).show();
                                              audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)-3, 0);
                                              // mouthImage.setImageResource(R.drawable.mouth2);
                                          }
                                          if(lastLevel >200 ){
                                              Log.e("Volume","Very High");
                                              //     Toast.makeText(getApplicationContext(),"Very Loud Sound",Toast.LENGTH_SHORT).show();
                                              audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);

                                              //mouthImage.setImageResource(R.drawable.mouth1);
                                          }*/
                                      }
                                      catch (Exception e)
                                      {
                                          Log.e("error",e.toString());
                                      }

                                      }

                                  },
                    0,
                    500);
        } catch (Exception e) {
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void sendMessage(int data) {
        // The string "my-integer" will be used to filer the intent
        Intent intent = new Intent("my-integer");
        // Adding some data
        intent.putExtra("message", data);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
    public void ere() {
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(Uri.parse("content://com.android.calendar/events"), new String[]{"calendar_id", "title", "description", "dtstart", "dtend", "eventLocation"}, null, null, null);
        //Cursor cursor = cr.query(Uri.parse("content://calendar/calendars"), new String[]{ "_id", "name" }, null, null, null);
        String add = null;
        cursor.moveToFirst();


        add = "Event" + cursor.getInt(0) + ": \nTitle: " + cursor.getString(1) + "\nDescription: " + cursor.getString(2) + "\nStart Date: " + new Date(cursor.getLong(3)) + "\nEnd Date : " + new Date(cursor.getLong(4)) + "\nLocation : " + cursor.getString(5);

        Log.e("kk", add);
        // ((TextView)findViewById(R.id.calendars)).setText(add);
        cursor.close();


    }


    // Run query



    @Override
    public void onDestroy()
    {
        Log.e("Destroyed","f");
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private void readAudioBuffer() {

        try {
            short[] buffer = new short[bufferSize];

            int bufferReadResult = 1;

            if (audio != null) {

// Sense the voice…
                bufferReadResult = audio.read(buffer, 0, bufferSize);
                double sumLevel = 0;
                for (int i = 0; i < bufferReadResult; i++) { sumLevel += buffer[i];
                } lastLevel = Math.abs((sumLevel / bufferReadResult));
            }
        }

        catch (Exception e) {
            e.printStackTrace();
        } }
}
