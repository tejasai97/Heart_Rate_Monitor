package com.example.tejag.heart_rate_monitor;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.tejag.heart_rate_monitor.R.id.BPM;

public class MainActivity extends AppCompatActivity {
   // public static int i=0;
    private TextView bpm;
    public void auto(){
        final long period = 10;
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                // do your task here
                new JSONTask().execute("https://thingspeak.com/channels/254941/feed.json");
            }
        }, 0, period);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bpm = (TextView)findViewById(R.id.BPM);
        auto();

    }
    class JSONTask extends AsyncTask<String,String,String>{
        @Override
        protected  String doInBackground(String... params ){
            StringBuffer buffer=new StringBuffer();
            HttpURLConnection connection=null;
            BufferedReader reader=null;
            URL url= null;
            try {
                url = new URL(params[0]);

                connection=(HttpURLConnection)url.openConnection();
                connection.connect();

                InputStream stream=connection.getInputStream();
                reader=new BufferedReader(new InputStreamReader(stream));
                String line="";
                buffer=new StringBuffer();
                while ((line=reader.readLine())!=null){
                    buffer.append(line);
                }
                String finalJson=buffer.toString();
                JSONObject parentobject=new JSONObject(finalJson);
                JSONArray parentarray=parentobject.getJSONArray("feeds");
                int i=parentarray.length()-1;

                JSONObject finalobject=  parentarray.getJSONObject(i);
                String val=finalobject.getString("field1");
                return val;
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(String result){
            super.onPostExecute(result);
            bpm.setText("BPM:"+result);

        }
    }
}
