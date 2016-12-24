package com.droidloft.appthingy;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    private String version = "0.1", buildDate = "12-19-2016";
    private EditText nameET, phoneET;
    private TextView dateTV;
    private Button saveB;
    private SQLiteDatabase sqlDB;
    private String date, name, phone, mMonth, mDay, mYear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        idViews();
        getTheDate();

        //openDB();
        
        saveB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEntryValues();
                if(name.equals("")||phone.equals("")){
                    Toast.makeText(MainActivity.this, "Missing Data", Toast.LENGTH_SHORT).show();
                } else{
                    placeDataInDB();
                }

                //dateET.setText("");
                nameET.setText("");
                phoneET.setText("");
                nameET.requestFocus();

            }
        });


        dateTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCalendar();
            }
        });
        
        
        
        
        
    }

    private void showCalendar() {
        final Dialog calendarDialog = new Dialog(this);
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View calLayout = inflater.inflate(R.layout.calendar_layout, (ViewGroup)findViewById(R.id.calendar_layout));
        calendarDialog.setContentView(calLayout);
        calendarDialog.show();

        CalendarView calendarView = (CalendarView)calLayout.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int year, int month, int dayOfMonth) {
                month = month + 1;

                if(dayOfMonth < 10) {
                    mDay = ("0" + dayOfMonth);
                } else {
                    mDay = Integer.toString(dayOfMonth);
                }

                if(month < 10) {
                    mMonth = ("0" + month);
                } else {
                    mMonth = Integer.toString(month);
                }

                if(year < 10) {
                    mYear = ("0" + year);
                } else {
                    mYear = Integer.toString(year);
                }

                String changedDate = String.valueOf(mYear + "-" + mMonth + "-" + mDay);
                dateTV.setText(changedDate);
                calendarDialog.cancel();
            }
        });
    }

    private void getTheDate() {

        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strDate = sdf.format(c.getTime());
        dateTV.setText("" + strDate);



    }

    @Override
    protected void onStart() {
        super.onStart();
        openDB();
    }

    private void openDB() {
        try{
           sqlDB = this.openOrCreateDatabase("SqlDB", MODE_PRIVATE, null);
            sqlDB.execSQL("CREATE TABLE IF NOT EXISTS mytable " + "(id integer primary key, date text, name text, phone text);");
            File database = getApplicationContext().getDatabasePath("SqlDB.db");
        } catch (Exception e){
            Log.e("DATABASE ERROR", "Error Creating Database");
        }
    }

    private void placeDataInDB() {
        sqlDB.execSQL("INSERT INTO mytable (date, name, phone) VALUES('" + date + "','" + name + "','" + phone + "');");
        Toast.makeText(MainActivity.this, "Data Added", Toast.LENGTH_SHORT).show();
    }

    private void getEntryValues() {
        date = dateTV.getText().toString();
        name = nameET.getText().toString();
        phone = phoneET.getText().toString();
    }

    private void idViews() {
        dateTV = (TextView)findViewById(R.id.dateTextView);
        nameET = (EditText)findViewById(R.id.nameEditText);
        phoneET = (EditText)findViewById(R.id.phoneEditText);
        saveB = (Button)findViewById(R.id.saveButton);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.list_screen) {
            startActivity(new Intent(MainActivity.this, ListScreen.class));
            sqlDB.close();
        }

        if(item.getItemId() == R.id.about){
            AlertDialog.Builder aboutAlert = new AlertDialog.Builder(this);
            aboutAlert.setTitle("AppThingy v" + version);
            aboutAlert.setMessage("Build Date: " + buildDate + "\n" + "by Michael May" + "\n" + "DroidLoft");
            aboutAlert.setIcon(R.mipmap.ic_launcher);
            aboutAlert.setCancelable(true);
            aboutAlert.show();
        }

        return super.onOptionsItemSelected(item);
    }
}
