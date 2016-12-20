package com.droidloft.appthingy;

import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

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

        if(cursor == null && cursor.getCount() == 0) {
            mainArrayAdapter.notifyDataSetChanged();
        }


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

        }

        if(item.getItemId() == R.id.restore) {

        }
        return super.onOptionsItemSelected(item);
    }
}
