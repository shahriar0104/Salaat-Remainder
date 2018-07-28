package com.bracu.project;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class AlarmActivity extends AppCompatActivity {

    private ArrayList<PendingIntent> intentArray;
    private PendingIntent newPendingIntent;
    Calendar calendar;

    private int intentRunning = 0;
    private int hour, minute, hourIsha;
    private Date fajr, zuhr, asr, maghrib, isha, tahajjut;
    public static  String item;
    private TimePicker timePicker;
    private AlarmManager[] am;
    private Intent[] intent1;
    private static final int ALARM_REQUEST_CODE = 133;
    private CheckBox fajrCheck, zuhrCheck, asrCheck, maghribCheck, ishaCheck, tahajjutCheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        initCustomSpinner();
        timePicker = (TimePicker) findViewById(R.id.timePicker);

        fajrCheck = findViewById(R.id.fajrCheck);
        zuhrCheck = findViewById(R.id.zuhrCheck);
        asrCheck = findViewById(R.id.asrCheck);
        maghribCheck = findViewById(R.id.maghribCheck);
        ishaCheck = findViewById(R.id.ishaCheck);
        //tahajjutCheck = findViewById(R.id.tahajjutCheck);

        //Intent alarmIntent = new Intent(AlarmActivity.this, AlarmReceiver.class);
        //intentArray = new ArrayList<PendingIntent>();
        //pendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, ALARM_REQUEST_CODE, alarmIntent, 0);

        findViewById(R.id.start_alarm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //method for initialize and start the alarm for azan & salat for 5 waqt & tahajjut
                triggerAzan(0, getApplicationContext());
                /*String getInterval = editText.getText().toString().trim();
                if (!getInterval.equals("") && !getInterval.equals("0"))

                    /*calendar = Calendar.getInstance();
                    calendar.set(Calendar.HOUR_OF_DAY,23);
                    calendar.set(Calendar.MINUTE,33);
                    long interVal = (calendar.getTimeInMillis()-System.currentTimeMillis())/1000;
                    long interTrigger =System.currentTimeMillis()/(60*1000)+interVal;
                    triggerAlarmManager(0,getApplicationContext());*/

            }
        });

        //start the alarm By Clicking below button
        findViewById(R.id.set_alarm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    calendar.set(Calendar.HOUR_OF_DAY,timePicker.getHour());
                    calendar.set(Calendar.MINUTE,timePicker.getMinute());
                    calendar.set(Calendar.SECOND,0);
                } else {
                    calendar.set(Calendar.HOUR_OF_DAY,timePicker.getCurrentHour());
                    calendar.set(Calendar.MINUTE,timePicker.getCurrentMinute());
                    calendar.set(Calendar.SECOND,0);
                }
                if (Calendar.getInstance().after(calendar))
                    calendar.add(Calendar.DAY_OF_MONTH,1);
                //method for initialize and start the alarm for azan & salat for 5 waqt & tahajjut
                if (Calendar.HOUR_OF_DAY < 6)
                    triggerAlarmManager(0, (calendar.getTimeInMillis()+(18*60*60*1000)));
                else
                    triggerAlarmManager(0, (calendar.getTimeInMillis()-(6*60*60*1000)));
            }
        });

        //testing button for mutialarm setting
        findViewById(R.id.repeater_alarm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //testing for mutialarm is working or not
                triggerMultiAlarm();
            }
        });

        //stopping the running alarm
        findViewById(R.id.stop_alarm_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarmManager();
            }
        });

    }

    //// for testing the multiAlarm method is written
    public void triggerMultiAlarm() {

        AlarmManager[] manager = new AlarmManager[2];
        Intent alarmIntent[] = new Intent[2];
        List<Integer> minutes = new ArrayList<>();
        minutes.add(31);
        minutes.add(32);
        intentArray=new ArrayList<>();
        for (int i = 0; i < 2; i++) {

            alarmIntent[i] = new Intent(AlarmActivity.this, AlarmReceiver.class);
            newPendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, i, alarmIntent[i], 0);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MINUTE, minutes.get(i));
            manager[i] = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            //alarm will be played after every 30 sec
            manager[i].set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+30000*(i+1),  newPendingIntent);
            intentArray.add(newPendingIntent);
        }
        Toast.makeText(this, "Reapeter Alarm Set", Toast.LENGTH_SHORT).show();
    }


    public void triggerAzan(int alarmTriggerTime, Context context) {

        //initialize Calendar for getting time
        Calendar cal = Calendar.getInstance();
        //cal.add(Calendar.SECOND, alarmTriggerTime);


        //getting the time of fajr,zuhr,asar,maghrib,isha and tahajjut from MainActivity
        Intent intent = getIntent();
        fajr = (Date) intent.getSerializableExtra("fajr");
        zuhr = (Date) intent.getSerializableExtra("zuhr");
        asr = (Date) intent.getSerializableExtra("asr");
        maghrib = (Date) intent.getSerializableExtra("maghrib");
        isha = (Date) intent.getSerializableExtra("isha");
        tahajjut = (Date) intent.getSerializableExtra("tahajjut");

        //initialize two arrayList for inserting the value hour & minute of azan & salat
        ArrayList<Integer> hours = new ArrayList<>();
        ArrayList<Integer> minutes = new ArrayList<>();

        hour = 0;
        minute = 0;

        //time not getting correct time so it is corrected as per required hour & minute
        //the time actually is 6 hours less
        //so the time is corrected in below
        if (fajr.getHours() < 6) {
            //as time is 6 hours less so if the time is 0-5 am then is added with 18 hours
            //then the time is getting the more 18 hours and setting the right timw for the location
            /*hours.add(fajr.getHours() + 18);
            minutes.add(fajr.getMinutes());*/
            //above time is for salat time
            // so fajr azan time will be 10 minute less
            //in below fajr azan time is setted
            fajr.setTime(fajr.getTime() - (10 * 60 * 1000));
            hours.add(fajr.getHours() + 18);
            minutes.add(fajr.getMinutes());
        } else {
            //if the time more tha 6 means 6AM then the time is will be deducted by 6
            //and the right time for the locatio will be getted
            /*hours.add(fajr.getHours() - 6);
            minutes.add(fajr.getMinutes());*/
            fajr.setTime(fajr.getTime() - (10 * 60 * 1000));
            hours.add(fajr.getHours() - 6);
            minutes.add(fajr.getMinutes());
        }
        if (zuhr.getHours() < 6) {
            /*hours.add(zuhr.getHours() + 18);
            minutes.add(zuhr.getMinutes());*/
            zuhr.setTime(zuhr.getTime() - (15 * 60 * 1000));
            hours.add(zuhr.getHours() + 18);
            minutes.add(zuhr.getMinutes());
        } else {
            /*hours.add(asr.getHours() - 6);
            minutes.add(zuhr.getMinutes());*/
            zuhr.setTime(zuhr.getTime() - (15 * 60 * 1000));
            hours.add(zuhr.getHours() - 6);
            minutes.add(zuhr.getMinutes());
        }
        if (asr.getHours() < 6) {
            /*hours.add(asr.getHours() + 18);
            minutes.add(asr.getMinutes());*/
            asr.setTime(asr.getTime() - (10 * 60 * 1000));
            hours.add(asr.getHours() + 18);
            minutes.add(asr.getMinutes());
        } else {
            /*hours.add(asr.getHours() - 6);
            minutes.add(asr.getMinutes());*/
            asr.setTime(asr.getTime() - (10 * 60 * 1000));
            hours.add(asr.getHours() - 6);
            minutes.add(asr.getMinutes());
        }
        if (maghrib.getHours() < 6) {
            /*hours.add(maghrib.getHours() + 18);
            minutes.add(maghrib.getMinutes());*/
            maghrib.setTime(maghrib.getTime() - (5 * 60 * 1000));
            hours.add(maghrib.getHours() + 18);
            minutes.add(maghrib.getMinutes());
        } else {
            /*hours.add(maghrib.getHours() - 6);
            minutes.add(maghrib.getMinutes());*/
            maghrib.setTime(maghrib.getTime() - (5 * 60 * 1000));
            hours.add(maghrib.getHours() - 6);
            minutes.add(maghrib.getMinutes());
        }
        if (isha.getHours() < 6) {
            /*hours.add(isha.getHours() + 18);
            minutes.add(isha.getMinutes());*/
            isha.setTime(isha.getTime() - (15 * 60 * 1000));
            hours.add(isha.getHours() + 18);
            minutes.add(isha.getMinutes());
        } else {
            /*hours.add(isha.getHours() - 6);
            minutes.add(isha.getMinutes());*/
            isha.setTime(isha.getTime() - (15 * 60 * 1000));
            hours.add(isha.getHours() - 6);
            minutes.add(isha.getMinutes());
        }
        /*if (tahajjut.getHours() < 6) {
            hours.add(tahajjut.getHours() + 18);
            minutes.add(tahajjut.getMinutes());
        } else {
            hours.add(tahajjut.getHours() - 6);
            minutes.add(tahajjut.getMinutes());
        }*/


        try {

            //AlarmManager is initilized
            //5*2=10 of 5 waqt azan and salat time + 1 waqt tahajjut time
            AlarmManager[] manager = new AlarmManager[5];
            //initialize 11 alarm for sending via pending intent
            Intent alarmIntent[] = new Intent[5];
            intentArray=new ArrayList<>();
            //starting the loop for setting the 11 times
            for (int i = 0; i < 5; i++) {
                //initialize calendar for geiing the times of salat and azan
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, (hours.get(i)));
                calendar.set(Calendar.MINUTE, (minutes.get(i)));
                if (Calendar.getInstance().after(calendar)) {
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                }

                //there is 5 waqt azan & salat and tahajjut checkBox for setting for which waqt salat and azan we want to set
                //in the below i check for which checkBox is marked
                //Suppose if only Maghrib is checked then only maghrib azan and salat will be broadCasted,other's will not be broadcasted
                if ((i == 0) && fajrCheck.isChecked()) {
                    //I fixed the value of index 0,1 for Fajr salat and azan and if that is marked then alarm will be set for fajr time

                    //setting alarmIntent for triggering to AlarmReceiver class
                    alarmIntent[i] = new Intent(AlarmActivity.this, AlarmReceiver.class);
                    //setting pendingIntent for triggering in that fixed time of fajr
                    newPendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, i, alarmIntent[i], 0);
                    //start alarm service
                    manager[i] = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    //setReating is used so that every day with INTERVAL_DAY the alarm will be trigged in that time
                    manager[i].setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, newPendingIntent);
                    intentArray.add(newPendingIntent);
                }
                if ((i == 1) && zuhrCheck.isChecked()) {
                    alarmIntent[i] = new Intent(AlarmActivity.this, AlarmReceiver.class);
                    newPendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, i, alarmIntent[i], 0);
                    manager[i] = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    manager[i].setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, newPendingIntent);
                    intentArray.add(newPendingIntent);
                }
                if ((i == 2) && asrCheck.isChecked()) {
                    alarmIntent[i] = new Intent(AlarmActivity.this, AlarmReceiver.class);
                    newPendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, i, alarmIntent[i], 0);
                    manager[i] = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    manager[i].setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, newPendingIntent);
                    intentArray.add(newPendingIntent);
                }
                if ((i == 3) && maghribCheck.isChecked()) {
                    alarmIntent[i] = new Intent(AlarmActivity.this, AlarmReceiver.class);
                    newPendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, i, alarmIntent[i], 0);
                    manager[i] = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    manager[i].setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, newPendingIntent);
                    intentArray.add(newPendingIntent);
                }
                if ((i == 4) && ishaCheck.isChecked()) {
                    alarmIntent[i] = new Intent(AlarmActivity.this, AlarmReceiver.class);
                    newPendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, i, alarmIntent[i], 0);
                    manager[i] = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    manager[i].setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, newPendingIntent);
                    intentArray.add(newPendingIntent);
                }
                /*if (i == 10 && tahajjutCheck.isChecked()) {
                    alarmIntent[i] = new Intent(AlarmActivity.this, AlarmReceiver.class);
                    newPendingIntent = PendingIntent.getBroadcast(AlarmActivity.this, i, alarmIntent[i], 0);
                    manager[i] = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    manager[i].setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, newPendingIntent);
                    intentArray.add(newPendingIntent);
                }*/
            }

        } catch (Exception e) {
            LogException(e);
            throw e;
        }
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    private void LogException(Exception e) {
        Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
    }

    //5 waqt & tahajjut azan & salat alarm setting method
    public void triggerAlarmManager(int alarmTriggerTime, long time) {

        am = new AlarmManager[6];
        intent1 = new Intent[6];
        if (item.equalsIgnoreCase("fajr")){
            intent1[0] = new Intent(AlarmActivity.this, AlarmReceiver.class);
            newPendingIntent=PendingIntent.getBroadcast(this,6,intent1[0],0);
            am[0]=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
            am[0].set(AlarmManager.RTC_WAKEUP, time, newPendingIntent);
        }else if (item.equalsIgnoreCase("zuhr")){
            intent1[1] = new Intent(AlarmActivity.this, AlarmReceiver.class);
            newPendingIntent=PendingIntent.getBroadcast(this,7,intent1[1],0);
            am[1]=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
            am[1].set(AlarmManager.RTC_WAKEUP, time, newPendingIntent);
        }else if (item.equalsIgnoreCase("asr")){
            intent1[2] = new Intent(AlarmActivity.this, AlarmReceiver.class);
            newPendingIntent=PendingIntent.getBroadcast(this,8,intent1[2],0);
            am[2]=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
            am[2].set(AlarmManager.RTC_WAKEUP, time, newPendingIntent);
        }else if (item.equalsIgnoreCase("maghrib")){
            intent1[3] = new Intent(AlarmActivity.this, AlarmReceiver.class);
            newPendingIntent=PendingIntent.getBroadcast(this,9,intent1[3],0);
            am[3]=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
            am[3].set(AlarmManager.RTC_WAKEUP, time, newPendingIntent);
        }else if (item.equalsIgnoreCase("isha")){
            intent1[4] = new Intent(AlarmActivity.this, AlarmReceiver.class);
            newPendingIntent=PendingIntent.getBroadcast(this,10,intent1[4],0);
            am[4]=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
            am[4].set(AlarmManager.RTC_WAKEUP, time, newPendingIntent);
        }else if (item.equalsIgnoreCase("tahajjut")){
            intent1[5] = new Intent(AlarmActivity.this, AlarmReceiver.class);
            newPendingIntent=PendingIntent.getBroadcast(this,11,intent1[5],0);
            am[5]=(AlarmManager) getSystemService(Context.ALARM_SERVICE);
            am[5].set(AlarmManager.RTC_WAKEUP, time, newPendingIntent);
        }
        Toast.makeText(this, "Alarm is set", Toast.LENGTH_SHORT).show();
    }

    public void stopAlarmManager() {

        //start alarmManager for stop the running alarm
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //manager.cancel(pendingIntent);

        //stop the alarm
        manager.cancel(newPendingIntent);
        //Log.v("IGA",""+stopFlag+"-"+intentArray.size());
        //Toast.makeText(this, ""+intentArray.size()+"-"+stopFlag, Toast.LENGTH_SHORT).show();
        //intentArray.clear();

        //after stop the AlarmSoundservice is called for stopping the alarm sound
        stopService(new Intent(AlarmActivity.this, AlarmSoundService.class));
        //Notification which is started is being terminated after alarm has stopped
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(AlarmNotificationService.NOTIFICATION_ID);
        Toast.makeText(this, "Alarm Canceled/Stop by User.", Toast.LENGTH_SHORT).show();
    }

    private void initCustomSpinner() {

        Spinner spinnerCustom= findViewById(R.id.spinnerCustom);
        // Spinner Drop down elements
        ArrayList<String> namazTime = new ArrayList<String>();
        namazTime.add("fajr");
        namazTime.add("zuhr");
        namazTime.add("asr");
        namazTime.add("maghrib");
        namazTime.add("isha");
        namazTime.add("tahajjut");
        CustomSpinnerAdapter customSpinnerAdapter=new CustomSpinnerAdapter(AlarmActivity.this,namazTime);
        spinnerCustom.setAdapter(customSpinnerAdapter);
        spinnerCustom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                item = parent.getItemAtPosition(position).toString();

                //Toast.makeText(parent.getContext(), "Android Custom Spinner Example Output..." + item, Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    public class CustomSpinnerAdapter extends BaseAdapter implements SpinnerAdapter {

        private final Context activity;
        private ArrayList<String> salatArrayList;

        public CustomSpinnerAdapter(Context context,ArrayList<String> salatArrayList) {
            this.salatArrayList=salatArrayList;
            activity = context;
        }



        public int getCount()
        {
            return salatArrayList.size();
        }

        public Object getItem(int i)
        {
            return salatArrayList.get(i);
        }

        public long getItemId(int i)
        {
            return (long)i;
        }



        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextView txt = new TextView(AlarmActivity.this);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(18);
            txt.setGravity(Gravity.CENTER_VERTICAL);
            txt.setText(salatArrayList.get(position));
            txt.setTextColor(Color.parseColor("#000000"));
            return  txt;
        }

        public View getView(int i, View view, ViewGroup viewgroup) {
            TextView txt = new TextView(AlarmActivity.this);
            txt.setGravity(Gravity.CENTER);
            txt.setPadding(16, 16, 16, 16);
            txt.setTextSize(16);
            txt.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_down, 0);
            txt.setText(salatArrayList.get(i));
            txt.setTextColor(Color.parseColor("#000000"));
            return  txt;
        }
    }
}
