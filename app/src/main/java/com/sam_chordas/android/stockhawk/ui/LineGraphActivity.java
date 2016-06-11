package com.sam_chordas.android.stockhawk.ui;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sam_chordas.android.stockhawk.R;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class LineGraphActivity extends AppCompatActivity {

    List<String> date = new ArrayList<>();
    List<Float> value = new ArrayList<>();
    private Context context;
    String companyName;
    private View progressCircle;
    boolean isConnected;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_line_graph);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TextView errror = (TextView) findViewById(R.id.error);
        progressCircle = findViewById(R.id.progress_circle);

        Intent intent = getIntent();

        String symbol = intent.getStringExtra("SYMBOL");

        //Toast.makeText(LineGraphActivity.this, "Value  " + symbol, Toast.LENGTH_SHORT).show();

        Log.v("check","check"+isConnected);
        if (isConnected) {

            FetchChartData fetchChartData = new FetchChartData();

            fetchChartData.execute(symbol);
        } else {
            errror.setVisibility(View.VISIBLE);
            progressCircle.setVisibility(View.INVISIBLE);
            setTitle("Error Loading!!");
        }


    }

    String fetchDataFromResponse(String resp) throws JSONException {

        String result = "";

        if (resp.startsWith("finance_charts_json_callback(")) {
            result = resp.substring(29, resp.length() - 2);
        }

        Log.v("result", "result" + result);

        JSONObject object = new JSONObject(result);

        JSONObject obj = object.getJSONObject("meta");

        companyName = obj.getString("Company-Name");

        JSONArray jsonArray = object.getJSONArray("series");

        Log.v("json", "json" + jsonArray);

        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject jsonObject = jsonArray.getJSONObject(i);

            String dateStr = jsonObject.getString("Date");
            SimpleDateFormat srcFormat = new SimpleDateFormat("yyyyMMdd");

            Date date1 = new Date();
            try {
                date1 = srcFormat.parse(dateStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String dateString = date1.toString();

            String finalDate = dateString.substring(0, 10);


            Log.v("jsonObj", "jsonObj" + date1);

            date.add(finalDate);

            String close = jsonObject.getString("close");

            value.add(Float.parseFloat(close));
        }

        return null;
    }

    public class FetchChartData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String url = "http://chartapi.finance.yahoo.com/instrument/1.0/"
                    + params[0] + "/chartdata;type=quote;range=5y/json";

            Log.v("URL", "URL" + url);

            OkHttpClient client = new OkHttpClient();


            Request request = new Request.Builder()
                    .url(url)
                    .build();


            Response response = null;
            String res = "";

            try {
                response = client.newCall(request).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                res = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.v("response", "response" + res);


            try {
                return fetchDataFromResponse(res);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String str) {
            super.onPostExecute(str);

            progressCircle.setVisibility(View.GONE);
            setTitle(companyName);
            ValueLineChart mCubicValueLineChart = (ValueLineChart) findViewById(R.id.cubiclinechart);

            ValueLineSeries series = new ValueLineSeries();
            series.setColor(0xFF56B7F1);

            for (int i = 0; i < date.size(); i++) {

                series.addPoint(new ValueLinePoint(date.get(i), value.get(i)));

            }

            mCubicValueLineChart.addSeries(series);
            mCubicValueLineChart.startAnimation();

        }
    }
}


