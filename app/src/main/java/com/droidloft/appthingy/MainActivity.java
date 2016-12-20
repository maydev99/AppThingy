package com.droidloft.appthingy;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private String version = "0.1", buildDate = "12-19-2016";
    private EditText dateET, nameET, phoneET;
    private Button saveB;
    private SQLiteDatabase sqlDB;
    private String date, name, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        idViews();
        openDB();
        
        saveB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getEntryValues();
                if(date.equals("")||name.equals("")||phone.equals("")){
                    Toast.makeText(MainActivity.this, "Missing Data", Toast.LENGTH_SHORT).show();
                } else{
                    placeDataInDB();
                }

                dateET.setText("");
                nameET.setText("");
                phoneET.setText("");
                dateET.requestFocus();

            }
        });
        
        
        
        
        
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
        date = dateET.getText().toString();
        name = nameET.getText().toString();
        phone = phoneET.getText().toString();
    }

    private void idViews() {
        dateET = (EditText)findViewById(R.id.dateEditText);
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
