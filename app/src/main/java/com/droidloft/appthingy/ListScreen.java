package com.droidloft.appthingy;

import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by DroidLoft2 on 12/19/2016.
 */

public class ListScreen extends AppCompatActivity {

    private TextView dateColTV, nameColTV, phoneColTV;
    private GridView gridView;
    private SQLiteDatabase sqlDB;
    private ArrayList<String> mainArrayList;
    private  ArrayList<String>rowList;
    private ArrayAdapter<String> mainArrayAdapter;
    private Cursor cursor;
    private List retrieveList;
    boolean isRestoreList = false;
    private String date2, name2, phone2;

    DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference listRef = rootRef.child("listref");


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_screen);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        idViews();
        openDB();
        getTheData();



    }

    private void getTheData() {

        mainArrayList = new ArrayList<>();
        rowList = new ArrayList<>();
        mainArrayAdapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.list_item_item, mainArrayList);
        cursor = sqlDB.rawQuery("SELECT * FROM mytable ORDER BY date ", null);
        int idColumn = cursor.getColumnIndex("id");
        int dateColumn = cursor.getColumnIndex("date");
        int nameColumn = cursor.getColumnIndex("name");
        int phoneColumn = cursor.getColumnIndex("phone");
        cursor.moveToFirst();

        /*if(cursor == null && cursor.getCount() == 0) {
            mainArrayAdapter.notifyDataSetChanged();
        }*/


        if(cursor != null && cursor.getCount() > 0) {
            do{
                String id = cursor.getString(idColumn);
                String date = cursor.getString(dateColumn);
                String name = cursor.getString(nameColumn);
                String phone = cursor.getString(phoneColumn);

                mainArrayList.add(date);
                mainArrayList.add(name);
                mainArrayList.add(phone);
                rowList.add(id);
            } while (cursor.moveToNext());



            gridView.setAdapter(mainArrayAdapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    int position = i;
                    int row = position / 3;
                    String idx = rowList.get(row);
                    sqlDB.execSQL("DELETE FROM mytable WHERE id = '" + idx + "';");
                    Toast.makeText(ListScreen.this, "Item Deleted", Toast.LENGTH_SHORT).show();
                    mainArrayList.clear();
                    mainArrayAdapter.notifyDataSetChanged();
                    rowList.clear();
                    getTheData();
                }
            });
        }
    }

    private void openDB() {
        sqlDB = this.openOrCreateDatabase("SqlDB", MODE_PRIVATE, null);
    }

    private void idViews() {
        dateColTV = (TextView)findViewById(R.id.dateColTextView);
        nameColTV = (TextView)findViewById(R.id.nameColTextView);
        phoneColTV = (TextView)findViewById(R.id.phoneColTextView);
        gridView = (GridView)findViewById(R.id.grid_view);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.backup) {
            backupDataToCloud();
        }

        if(item.getItemId() == R.id.restore) {
            restoreDataFromCloud();
        }

        if(item.getItemId() == R.id.gen_data) {

            generateData();

        }
        return super.onOptionsItemSelected(item);
    }

    private void restoreDataFromCloud() {
        listRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                sqlDB.execSQL("DELETE FROM mytable");
                retrieveList = new ArrayList();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    retrieveList.add((String.valueOf(dsp.getValue())));

                }
                Log.d("Retrieved Data: " , "DATA: " + retrieveList);

                int totalCount = 0;
                int count = 0;
                for(int i = 0; i < retrieveList.size(); i++){

                    if(count < 4) {
                        String text = (String) retrieveList.get(i);
                        if(count == 0){
                            date2 = text;
                        }

                        if(count == 1) {
                            name2 = text;
                        }

                        if(count == 2) {
                            phone2 = text;
                            count = -1;
                            Log.d("DATA BLOCKS: ", "DATA BLOCKS " + date2 + "" + "\n" + name2 + "\n" + phone2);
                            sqlDB.execSQL("INSERT INTO mytable (date, name, phone) VALUES('" + date2 + "','" + name2 + "','" + phone2 + "');");
                        }

                        count++;
                    }

                }

                getTheData();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void backupDataToCloud() {
        listRef.removeValue();
        for(int i = 0; i < mainArrayList.size(); i++) {
            String item = mainArrayList.get(i);
            listRef.push().setValue(item);
        }
    }
    
    
    //Remove This Later
    private void generateData() {
        String date1 = "12/22/2016";
        String name1 = "Michael";
        String phone1 = "954-655-3839";
        sqlDB.execSQL("INSERT INTO mytable (date, name, phone) VALUES('" + date1 + "','" + name1 + "','" + phone1 + "');");
        date1 = "12/23/2016";
        name1 = "Cate";
        phone1 = "954-555-2148";
        sqlDB.execSQL("INSERT INTO mytable (date, name, phone) VALUES('" + date1 + "','" + name1 + "','" + phone1 + "');");
        date1 = "12/24/2016";
        name1 = "Steve";
        phone1 = "415-756-3448";
        sqlDB.execSQL("INSERT INTO mytable (date, name, phone) VALUES('" + date1 + "','" + name1 + "','" + phone1 + "');");

        Toast.makeText(ListScreen.this, "Data Added", Toast.LENGTH_SHORT).show();
        getTheData();

    }
}
