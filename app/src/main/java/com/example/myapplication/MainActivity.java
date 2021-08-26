package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.number.Scale;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.myapplication.Database.DatabaseInterface;

import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    TimePicker timePicker;
    EditText txtName;
    TextView txtdate1;
    DatePicker datePicker;
    Button btnDate;
    PendingIntent pendingIntent, pendingIntent2;
    public static final String CHANNEL_ID = "AlarmApp2";
    Integer flagNotif=0;
    AlarmManager alarmManager;
    Button btnToggle;
    private CreateDatabase createDb;
    private EventData eventData;
    private DatabaseInterface dao;
    private DatePicker picker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //createChannel();
        picker=(DatePicker)findViewById(R.id.datePicker);
        txtName = findViewById(R.id.txteventName);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        datePicker = (DatePicker) findViewById(R.id.datePicker);
        txtdate1 =(TextView) findViewById(R.id.txtdate1);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        btnToggle = (Button) findViewById(R.id.btnToggle);

        setListener();

    }

    public String getCurrentDate(){
        StringBuilder builder=new StringBuilder();;
        builder.append((picker.getMonth() + 1)+"/");//month is 0 based
        builder.append(picker.getDayOfMonth()+"/");
        builder.append(picker.getYear());
        return builder.toString();
    }

    public void setListener() {

        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(txtName.getText())) {
                    txtName.setError("Event name is required!");
                } else {

                    long time;
                    Calendar calendar = Calendar.getInstance();

                    calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour());
                    calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute());
                    calendar.set(Calendar.DAY_OF_MONTH, datePicker.getDayOfMonth());
                    calendar.set(Calendar.MONTH, datePicker.getMonth());
                    calendar.set(Calendar.YEAR, datePicker.getYear());

                    Toast.makeText(MainActivity.this, "Alarm is set", Toast.LENGTH_SHORT).show();

                    time = (calendar.getTimeInMillis() - (calendar.getTimeInMillis() % 60000));

                    if (System.currentTimeMillis() > time) {
                        if (calendar.AM_PM == 0)
                            time = time + (1000 * 60 * 60 * 12);
                        else
                            time = time + (1000 * 60 * 60 * 24);
                    }


                    insertData(time, txtName.getText().toString(), true);

                    setAlarm(eventData.getId(), flagNotif, time, txtName.getText().toString());
                }
            }
        });

    }

    private void setAlarm(int uid, int isnotify, long time, String eventName)
    {
        long timeNotif = 0;
        if((time-900000)>0)
        {
            timeNotif = time-900000;
            flagNotif = 1;
            Intent intent2 = new Intent(MainActivity.this, AlarmRingClass.class);
            intent2.putExtra("type","n");
            intent2.putExtra("desc",eventName);
            pendingIntent2 = PendingIntent.getBroadcast(MainActivity.this, uid+10000, intent2, 0);
            //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 10000, pendingIntent2);
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeNotif, pendingIntent2);
        }

        Intent intent = new Intent(MainActivity.this, AlarmRingClass.class);
        intent.putExtra("type","a");
        intent.putExtra("desc",eventName);
        //intent2.putExtra("type","n");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(MainActivity.this, uid, intent, 0);
        //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, time, 10000, pendingIntent);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
        //if(isnotify)
        txtName.setText("");
    }

    public void insertData(Long time, String eventName, Boolean isNotify)
    {
        EventData myDataList = new EventData(time, eventName, isNotify);
        createDb = CreateDatabase.getInstance(this);
        dao = createDb.Dao();
        dao.insert(myDataList);
        eventData = new EventData((long) 0, eventName, false);
        eventData = dao.getAlarm(time);

        Toast.makeText(this,"Added to db!",Toast.LENGTH_LONG).show();
    }

}