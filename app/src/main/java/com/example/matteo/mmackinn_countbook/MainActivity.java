package com.example.matteo.mmackinn_countbook;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.CountBookmmackinn";
    public static final String FILE_NAME = "data.sav";

    public boolean delMode = false;
    public boolean intentSignal = false;

    public static ArrayList<Record> recData = new ArrayList<>();
    public static ArrayList<String> nameData = new ArrayList<>();
    public ArrayAdapter adapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Sets up recData and nameData
        loadData();
        bufferString();
        ListView list = (ListView) findViewById(R.id.myListView);

        // Simple_list_item 1 based on https://www.youtube.com/watch?v=A-_hKWMA7mk September 25, 2017
        // Set adapter for List View
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, nameData);

        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (delMode) {
                    // If program in delete mode, remove item from list, and update
                    recData.remove(position);
                    saveData();

                    bufferString();
                    adapter.notifyDataSetChanged();


                }
                else {
                    // If program in select mode, call function to access record
                    switchAct(position);
                }
            }


        });
    }

    // Loads Records from Json to recData -- Based on code from lab 3
    public void loadData() {
        try {
            FileInputStream fis = openFileInput(FILE_NAME);
            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<Record>>() {}.getType();

            recData = gson.fromJson(br, listType);

        }
        catch (FileNotFoundException e) {
            recData = new ArrayList<Record>();
        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }

    // Saves Records to Json from recData -- Based on code from lab 3
    public void saveData() {
        try {
            FileOutputStream fos = openFileOutput(FILE_NAME, Context.MODE_PRIVATE);
            OutputStreamWriter ow = new OutputStreamWriter(fos);
            Gson gson = new Gson();
            gson.toJson(recData, ow);
            ow.flush();
            fos.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    // This method called when recData changes or activity loaded
    // Resets recNames based on recData. Useful for updating listView after updates
    public void bufferString() {

        nameData.clear();

        for(Record r: recData) {

            String entry1 = r.name;
            String entry2 = r.fDate;
            int e3 = r.count;
            String entry3 = String.valueOf(e3);
            String entry = entry1 + " / " + entry2 + " /" + entry3;
            nameData.add(entry);

        }
    }

    // Check if String paremeter is unique from names and not empty
    public boolean isValidStr(String str){
        // If string is empty, return false
        if (str.length() == 0) {
            return false;
        }
        // Iterate through Records to see if name matches
        // if one does, return false
        for (Record r: recData) {
            String cr = r.name;
            if (cr.equals(str) ) {
                return false;
            }
        }
        // return true if String valid
        return true;
    }

    // Switches activity to recActivity based on item clicked
    public void switchAct(int pos) {
        Intent intent = new Intent(this, RecActivity.class);
        intent.putExtra(EXTRA_MESSAGE, pos);
        startActivity(intent);
    }


    public void addClick(View view) {
        EditText editText = (EditText) findViewById(R.id.nameBar);
        String s = editText.getText().toString();

        if (!isValidStr(s)) {
            return;
        }

        Record rec = new Record(s);
        recData.add(rec);

        bufferString();
        adapter.notifyDataSetChanged();
        saveData();
    }

    // Change to select mode when clicked
    public void selectClick(View view) {
        TextView textView = (TextView) findViewById(R.id.modeText);
        textView.setText("select mode");
        delMode = false;
    }

    // Change to delete mode when clicked
    public void deleteClick(View view) {
        TextView textView = (TextView) findViewById(R.id.modeText);
        textView.setText("delete mode");
        delMode = true;
    }
}

