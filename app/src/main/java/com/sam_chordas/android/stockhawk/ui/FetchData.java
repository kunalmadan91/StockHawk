package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by KUNAL on 28-05-2016.
 */
public class FetchData extends AsyncTask<String, Void, String> {

    public FetchData(Context mcontext) {

    }
    @Override
    protected String doInBackground(String... params) {
        String url = "http://chartapi.finance.yahoo.com/instrument/1.0/AAPL/chartdata;type=quote;range=5y/json";

        OkHttpClient client = new OkHttpClient();


        Request request = new Request.Builder()
                .url(url)
                .build();


        Response response = null;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String res = response.body().toString();

        Log.v("response","response"+res);
        return res;
    }

    @Override
    protected void onPostExecute(String aVoid) {
        super.onPostExecute(aVoid);
    }
}
