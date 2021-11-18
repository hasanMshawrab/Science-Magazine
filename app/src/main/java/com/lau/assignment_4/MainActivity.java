package com.lau.assignment_4;

/**
 * Name: Hasan Mshawrab
 * Course: CSC498X - Mobile Development
 * Assignment_4: Tech News App
 *
 * In the below code you just have to Uncomment the two _TODO_ comments
 * to run the code for the first time.
 * After that re-comment them.
 *
 * NB: Some urls do not work. (i.e. if you click on one of the news it will take
 * to the web view page, but it won't reload the page or it would take long time
 * because the source code will be too long to load).
 *
 */

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayList<ArrayList<String>> newsInfoList;
    ArrayList<String> titlesList;
    ArrayAdapter<String> adapter;
    Intent intent;
    ListView listView;
    SQLiteDatabase db;


    boolean bool;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            intent = new Intent(MainActivity.this, WebView_Activity.class);
            newsInfoList = new ArrayList<>();
            bool = false;

            //Components - Start
            listView = findViewById(R.id.listView);
            //Components - End

            //Background Functions - Fetching Data using API - Start
            /**TODO - Uncomment the below Function for onc **/
            /**
            DownloadInfoFromAPI downloadInfoFromAPI = new DownloadInfoFromAPI();
            bool = downloadInfoFromAPI.execute("https://hacker-news.firebaseio.com/v0/topstories.json").get();
            while (!bool){ // I used while loop to make sure that all data were fetched
                if (bool){
                    break;
                }
            }
            */
            //Background Functions - Fetching Data using API - End

            //SQLiteDataBase - Start
            db = this.openOrCreateDatabase("assignmentdb", MODE_PRIVATE, null);
            /**TODO - Uncomment Table Creation code for once **/
            /** ---Table Creation---
             db.execSQL("Create Table IF NOT EXISTS news (id TEXT, title TEXT, url TEXT)");
             String sql = "INSERT INTO news (id, title, url) VALUES(?,?,?)";
             SQLiteStatement statement = db.compileStatement(sql);
             for (int i=0; i<newsInfoList.size(); i++) {
             String id = newsInfoList.get(i).get(0);
             String title = newsInfoList.get(i).get(1);
             String url = newsInfoList.get(i).get(2);
             System.out.println(id + " - " + title + " - " + url);
             statement.clearBindings();
             statement.bindString(1, id);
             statement.bindString(2, title);
             statement.bindString(3, url);
             statement.executeInsert();
             }
             */

            /** ---Fetch titles from the DB--- */
            Cursor cursorTitles = db.rawQuery("Select title from news", null);
            int title_index = cursorTitles.getColumnIndex("title");
            cursorTitles.moveToFirst();
            cursorTitles.moveToFirst();

            //SQLiteDataBase - End

            //List View Section - Start
            titlesList = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                String title = cursorTitles.getString(title_index);
                titlesList.add(title);
                cursorTitles.moveToNext();
            }
            adapter = new ArrayAdapter<>(listView.getContext(), R.layout.row, titlesList);
            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    String title = ((TextView) view).getText().toString();
                    intent.putExtra("title", title);
                    intent.putExtra("url", fetchURLFromDB(title));
                    startActivity(intent);
                }
            });
            //List View Section - End
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String fetchURLFromDB(String title){
        Cursor cursorURL = db.rawQuery("Select url from news where title=?", new String[]{title});
        int url_index = cursorURL.getColumnIndex("url");
        cursorURL.moveToFirst();
        Log.i("Fetching: ", "Title: " + title + " URl_Index: " + url_index);
        return cursorURL.getString(url_index);
    }


    public class DownloadInfoFromAPI extends AsyncTask<String, Void, Boolean> {
        @Override
        protected Boolean doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                String result = "";
                int data = reader.read();
                while (data != -1) {
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                result = "{\"id\":" + result + "}";
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("id");

                int counter = 0;
                boolean counted20 = false;
                for (int i=0; !counted20; i++){
                    String id = jsonArray.getString(i);
                    String api = "https://hacker-news.firebaseio.com/v0/item/" + id + ".json?print=pretty";
                    URL url1 = new URL(api);
                    HttpURLConnection connection1 = (HttpURLConnection) url1.openConnection();
                    InputStream in1 = connection1.getInputStream();
                    InputStreamReader reader1 = new InputStreamReader(in1);
                    String result1 = "";
                    int data1 = reader1.read();
                    while (data1 != -1){
                        char current1 = (char) data1;
                        result1+=current1;
                        data1 = reader1.read();
                    }

                    JSONObject jsonObject1 = new JSONObject(result1);
                    if(jsonObject1.has("url")){
                        counter++;
                        String newsID = jsonObject1.getString("id");
                        String newsTitle = jsonObject1.getString("title");
                        String newsUrl = jsonObject1.getString("url");
                        ArrayList<String > list = new ArrayList<>();
                        list.add(newsID);
                        list.add(newsTitle);
                        list.add(newsUrl);
                        newsInfoList.add(list);
                    }
                    if(counter == 20){
                        counted20 = true;
                    }
                }

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
    }

}
