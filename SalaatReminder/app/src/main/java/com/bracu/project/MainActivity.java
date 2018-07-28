package com.bracu.project;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.azan.PrayerTimes;
import com.azan.TimeCalculator;
import com.azan.types.PrayersType;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import static com.azan.types.AngleCalculationType.EGYPT;
import static com.azan.types.AngleCalculationType.MWL;

public class MainActivity extends AppCompatActivity {

    private Date fajr, zuhr, asr, magrib, isha, tahajjut;
    private TextView fajrAzan, zuhrAzan, asrAzan, maghribAzan, ishaAzan;
    private TextView fajrSalat, zuhrSalat, asrSalat, maghribSalat, ishaSalat, tahajjutSalat;
    private Button setAlarm;
    private int fajrTime, zuhrTime, asrTime, maghribTime, ishaTime;
    private PrayerTimes prayerTimes;

    private static final int REQUEST_LOCATION = 1;
    LocationManager locationManager;
    private Double lattitude, longitude, latti = 23.8103, longi = 90.4125;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initialize the textfield
        fajrAzan = findViewById(R.id.fajr);
        zuhrAzan = findViewById(R.id.zuhr);
        asrAzan = findViewById(R.id.asr);
        maghribAzan = findViewById(R.id.maghrib);
        ishaAzan = findViewById(R.id.isha);
        fajrSalat = findViewById(R.id.fajrSalat);
        zuhrSalat = findViewById(R.id.zuhrSalat);
        asrSalat = findViewById(R.id.asrSalat);
        maghribSalat = findViewById(R.id.maghribSalat);
        ishaSalat = findViewById(R.id.ishaSalat);
        tahajjutSalat = findViewById(R.id.tahajjutSalat);
        setAlarm = findViewById(R.id.setAlarm);

        Intent intent =getIntent();
        if (intent.hasExtra("check"))
            startActivity(new Intent(this,AlarmActivity.class));

        //location permission checking
        ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        //creating a timezone object
        //In PrayerTimes Library the timezone will be auto set
        TimeZone tzone = TimeZone.getTimeZone("");
        tzone.setDefault(tzone);
        /*GregorianCalendar date = new GregorianCalendar();
        date.set(Calendar.HOUR_OF_DAY, 18);*/
        Date date1 = new Date();

        //start the locationService
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //checking if Location service is enabled in the device
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //if not then go to below method for permission to start the location service of device
            buildAlertMessageNoGps();
            //PrayerTimes Library will now calculate the prayerTimes of that location
            //Default is Dhaka
            //But someHow the library is problematic so time which i get is not correct time
            //the time is corrected as per required time less/more from local time
            prayerTimes = new TimeCalculator().date(date1, tzone).location(latti, longi, 17, 6).timeCalculationMethod(MWL).calculateTimes();
            prayerTimes.setUseSecond(false);


        } else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            //if location Enabled then get the location
            getLocation();
            prayerTimes = new TimeCalculator().date(date1, tzone).location(lattitude, longitude, 17, 6).timeCalculationMethod(MWL).calculateTimes();
            prayerTimes.setUseSecond(false);
        }

        //getting the preyer times for fajr,zuhr,asr,maghrib,Isha & Tahajjut
        fajr = prayerTimes.getPrayTime(PrayersType.FAJR);
        tahajjut = prayerTimes.getPrayTime(PrayersType.FAJR);
        //prayerTimes.getPrayTime(PrayersType.SUNRISE);
        zuhr = prayerTimes.getPrayTime(PrayersType.ZUHR);
        asr = prayerTimes.getPrayTime(PrayersType.ASR);
        magrib = prayerTimes.getPrayTime(PrayersType.MAGHRIB);
        isha = prayerTimes.getPrayTime(PrayersType.ISHA);



        /*if (day.equalsIgnoreCase("sat")) {
            zuhr.setTime(zuhr.getTime() + ((3600 * 1000) + (5 * 60 * 1000)));
        }*/



        //setting the time in textField of UI
        //time will get in 24Hours
        //AM/PM setting in the below if else conditions
        if (fajr.getHours() > 12) {
            //if time is getting more than 13 then is will coverted to PM and start from 1
            //fajr azan time is being fixed
            fajrTime = fajr.getHours() - 12;
            fajrAzan.setText(fajrTime + ":" + fajr.getMinutes() + " PM");
            //salat will be started after 10 minute of azan
            //so the salt time is being calculated and fixed
            fajr.setTime(fajr.getTime() + (10 * 60 * 1000));
            fajrSalat.setText(fajrTime + ":" + fajr.getMinutes() + " PM");
        } else {
            fajrAzan.setText(fajr.getHours() + ":" + fajr.getMinutes() + " AM");
            fajr.setTime(fajr.getTime() + (10 * 60 * 1000));
            fajrSalat.setText(fajr.getHours() + ":" + fajr.getMinutes() + " AM");
        }
        if (zuhr.getHours() > 12) {
            zuhrTime = zuhr.getHours() - 12;
            zuhrAzan.setText(zuhrTime + ":" + zuhr.getMinutes() + " PM");
            zuhr.setTime(zuhr.getTime() + (15 * 60 * 1000));
            zuhrSalat.setText(zuhr.getHours() + ":" + zuhr.getMinutes() + " PM");
        } else {
            zuhrAzan.setText(zuhr.getHours() + ":" + zuhr.getMinutes() + " AM");
            zuhr.setTime(zuhr.getTime() + (15 * 60 * 1000));
            zuhrSalat.setText(zuhr.getHours() + ":" + zuhr.getMinutes() + " AM");
        }
        if (asr.getHours() > 12) {
            asrTime = asr.getHours() - 12;
            asrAzan.setText(asrTime + ":" + asr.getMinutes() + " PM");
            asr.setTime(asr.getTime() + (10 * 60 * 1000));
            asrSalat.setText(asrTime + ":" + asr.getMinutes() + " PM");
        } else {
            asrAzan.setText(asr.getHours() + ":" + asr.getMinutes() + " AM");
            asr.setTime(asr.getTime() + (10 * 60 * 1000));
            asrSalat.setText(asr.getHours() + ":" + asr.getMinutes() + " AM");
        }
        if (magrib.getHours() > 12) {
            maghribTime = magrib.getHours() - 12;
            maghribAzan.setText(maghribTime + ":" + magrib.getMinutes() + " PM");
            magrib.setTime(magrib.getTime() + (5 * 60 * 1000));
            maghribSalat.setText(maghribTime + ":" + magrib.getMinutes() + " PM");
        } else {
            maghribAzan.setText(magrib.getHours() + ":" + magrib.getMinutes() + " AM");
            magrib.setTime(magrib.getTime() + (5 * 60 * 1000));
            maghribSalat.setText(magrib.getHours() + ":" + magrib.getMinutes() + " AM");
        }
        if (isha.getHours() > 12) {
            ishaTime = isha.getHours() - 12;
            ishaAzan.setText(ishaTime + ":" + isha.getMinutes() + " PM");
            isha.setTime(isha.getTime() + (15 * 60 * 1000));
            ishaSalat.setText(ishaTime + ":" + isha.getMinutes() + " PM");
        } else {
            ishaAzan.setText(isha.getHours() + ":" + isha.getMinutes() + " AM");
            isha.setTime(isha.getTime() + (15 * 60 * 1000));
            ishaSalat.setText(isha.getHours() + ":" + isha.getMinutes() + " AM");
        }

        tahajjut.setTime(fajr.getTime() - (76 * 60 * 1000));
        tahajjutSalat.setText(tahajjut.getHours() + ":" + tahajjut.getMinutes() + " AM");

        //starting the alarmSetingActivity
        setAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AlarmActivity.class)
                        .putExtra("isha", isha).putExtra("fajr", fajr).putExtra("zuhr", zuhr)
                        .putExtra("asr", asr).putExtra("maghrib", magrib).putExtra("tahajjut", tahajjut));
                //finish();
            }
        });
    }

    //getting the location
    private void getLocation() {
        //checking for accessiong network provided location & GPS provided location
        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission
                (MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {
            //getting network provider location
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

            //getting GPS tracking location
            Location location1 = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

            //getting passive location
            Location location2 = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);

            //checking for location and getting the location
            if (location != null) {
                lattitude = location.getLatitude();
                longitude = location.getLongitude();

            } else if (location1 != null) {
                lattitude = location1.getLatitude();
                longitude = location1.getLongitude();

            } else if (location2 != null) {
                lattitude = location2.getLatitude();
                longitude = location2.getLongitude();

            } else {
                //default value set if location is Untraceable
                //default lattitude and logitude is of Dhaka,Bangladesh
                lattitude = 23.8103;
                longitude = 90.4125;
                //Toast.makeText(this,"Unble to Trace your location",Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void buildAlertMessageNoGps() {

        //dialog for start if user want to start the location service of the device
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Please Turn ON your GPS Connection")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }
}
