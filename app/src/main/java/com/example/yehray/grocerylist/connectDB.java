package com.example.yehray.grocerylist;

import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class connectDB extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... params) {
        URL urlCould;
        HttpURLConnection connection;
        InputStream inputStream = null;
        try {
            //parse data from input
            String cmd = URLEncoder.encode(params[1], "UTF-8");
            String url = params[0] + "?cmd=" + cmd;
            Log.e("myapp", url);
            urlCould = new URL(url);
            connection = (HttpURLConnection) urlCould.openConnection();
            connection.setConnectTimeout(30000);
            connection.setReadTimeout(30000);
            connection.setRequestMethod("GET");
            connection.connect();

            inputStream = connection.getInputStream();

        } catch (MalformedURLException MEx){

        } catch (IOException IOEx){
            Log.e("Utils", "HTTP failed to fetch data");
            IOEx.printStackTrace();
            return null;
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder sb = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    @Override
    protected void onPostExecute(String rs) {
        super.onPostExecute(rs);
    }
}
