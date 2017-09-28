package com.example.matteo.mmackinn_countbook;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;

import static com.example.matteo.mmackinn_countbook.MainActivity.EXTRA_MESSAGE;
import static com.example.matteo.mmackinn_countbook.MainActivity.recData;
import static com.example.matteo.mmackinn_countbook.MainActivity.FILE_NAME;
import static com.example.matteo.mmackinn_countbook.R.id.countEdit;
import static com.example.matteo.mmackinn_countbook.R.id.defaultEdit;
import static com.example.matteo.mmackinn_countbook.R.id.nameEdit;
import static com.example.matteo.mmackinn_countbook.R.id.nameText;


public class RecActivity extends AppCompatActivity {

    public Record cRec;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rec);

        loadData();

        // Get position clicked that started this activity
        Intent intent = getIntent();
        int pos = intent.getIntExtra(EXTRA_MESSAGE, 0);

        // Gets textviews so they can be edited
        TextView nameText = (TextView) findViewById(R.id.nameText);
        TextView commentText = (TextView) findViewById(R.id.commentText);
        TextView defaultText = (TextView) findViewById(R.id.defaultText);
        TextView countText = (TextView) findViewById(R.id.countText);
        TextView dateText = (TextView) findViewById(R.id.dateText);

        // Set textviews based on records
        cRec = recData.get(pos);
        nameText.setText(cRec.name);
        commentText.setText(cRec.comment);
        String initStr = String.valueOf(cRec.init);
        String countStr = String.valueOf(cRec.count);
        defaultText.setText(initStr);
        countText.setText(countStr);
        dateText.setText("Date: " + cRec.fDate);

    }

    // Sets name when clicked
    public void nameClick(View view) {
        // Get widgets to interact with
        EditText nameEdit = (EditText) findViewById(R.id.nameEdit);
        TextView nameText = (TextView) findViewById(R.id.nameText);
        TextView mess = (TextView) findViewById(R.id.messageText);

        // Get new name and check if valid
        String nameSet = nameEdit.getText().toString();
        if (!isValidStr(nameSet)) {
            mess.setText("Name must be unique and not empty");
            return;
        }
        // Set new name
        cRec.name = nameSet;
        nameText.setText(nameSet);
        saveData();

    }

    // Sets comment when clicked
    public void commentClick(View view) {
        // Get widgets to interact with
        EditText commentEdit = (EditText) findViewById(R.id.commentEdit);
        TextView commentText = (TextView) findViewById(R.id.commentText);

        // Set new comment
        String commentSet = commentEdit.getText().toString();
        cRec.comment = commentSet;
        commentText.setText(commentSet);
        saveData();
    }

    // Sets default int when clicked
    public void defaultClick(View view) {
        // Get widgets to interact with
        EditText defaultEdit = (EditText) findViewById(R.id.defaultEdit);
        TextView defaultText = (TextView) findViewById(R.id.defaultText);
        TextView mess = (TextView) findViewById(R.id.messageText);

        // Set new default value
        String defaultSet = defaultEdit.getText().toString();
        if (!isValidInt(defaultSet)) {
            mess.setText("NO NEGATIVE INTEGERS!");
            return;
        }
        // Get absolute value of defaultSet
        int initSet = Integer.parseInt(defaultSet);
        initSet = Math.abs(initSet);
        cRec.init = initSet;

        defaultSet = Integer.toString(initSet);
        defaultText.setText(defaultSet);
        saveData();
    }

    // Sets current value when clicked
    public void countClick(View view) {
        // Get widgets to interact with
        EditText countEdit = (EditText) findViewById(R.id.countEdit);
        TextView countText = (TextView) findViewById(R.id.countText);
        TextView mess = (TextView) findViewById(R.id.messageText);

        // Check if new current integer is a number; sets message and stops otherwise
        String countSet = countEdit.getText().toString();
        if (!isValidInt(countSet)) {
            mess.setText("NO NEGATIVE INTEGERS!!");
            return;
        }

        // Get absolute value
        int countInt = Integer.parseInt(countSet);
        countInt = Math.abs(countInt);
        cRec.count = countInt;

        // Set correct value, update date, and save data
        countSet = Integer.toString(countInt);
        countText.setText(countSet);
        cRec.upDate();
        saveData();
    }

    // Increase current count by 1 on click
    public void plusClick(View view) {
        // Get widgets to interact with
        TextView countText = (TextView) findViewById(R.id.countText);

        // Update count
        cRec.count += 1;
        String countSet = Integer.toString(cRec.count);

        // Update appropriate values
        countText.setText(countSet);
        cRec.upDate();
        saveData();

    }

    // Decrease current count by 1 when clicked
    public void minusClick(View view) {
        //Get widgets to interact with
        TextView countText = (TextView) findViewById(R.id.countText);
        TextView mess = (TextView) findViewById(R.id.messageText);

        // Stop if current number is zero
        if (cRec.count == 0) {
            mess.setText("COUNTER CANNOT GO UNDER ZERO!");
            return;
        }

        // Update count
        cRec.count -= 1;
        String countSet = Integer.toString(cRec.count);

        // Update appropriate values
        countText.setText(countSet);
        cRec.upDate();
        saveData();

    }

    // Set current count to initial count when clicked
    public void resetClick(View view) {
        // Get widgets to interact with
        TextView countText = (TextView) findViewById(R.id.countText);

        // Set current value to initial value
        cRec.count = cRec.init;
        String countSet = Integer.toString(cRec.count);

        // Update apropriate values
        countText.setText(countSet);
        cRec.upDate();
        saveData();

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

    // Saves Records from recData to Json -- Based on code from lab 3
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

    // Returns true if String parameter is not empty and unique from Record names; false otherwise
    public boolean isValidStr(String str){
        if (str.length() == 0) {
            return false;
        }
        for (Record r: recData) {
            String cr = r.name;
            if (cr.equals(str) ) {
                return false;
            }
        }
        return true;
    }

    // Returns true if String parameter can be converted into Integer; false otherwise
    public boolean isValidInt(String s) {
        try {
            Integer i = Integer.valueOf(s);
        }
        catch (NumberFormatException e) {
            return false;
        }
        return true;
    }

}
