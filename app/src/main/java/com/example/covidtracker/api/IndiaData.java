package com.example.covidtracker.api;


import android.os.AsyncTask;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.covidtracker.R;
import com.example.covidtracker.StatsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

import javax.net.ssl.HttpsURLConnection;


public class IndiaData extends AsyncTask<Object, String, String> {
    String data = "";
    public String active, confirmed, deaths, recovred, todayscases, todaysdeaths, todaysrecovered, lasttimeupdated, states, lasttimeupdatedforstates, migrated;


    public String day, month, year, timeinstring, monthinwords, finaldate, perfecttime, ago, curTime;
    long total;
    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;
    long oldMillis, nowMillis;


    @Override
    protected String doInBackground(Object... objects) {


        try {
            URL url = new URL("https://api.covid19india.org/data.json");
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            InputStream inputStream = httpsURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while (line != null) {
                line = bufferedReader.readLine();
                data = data + line;

            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }


    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            JSONObject parentObject = new JSONObject(data);
            JSONArray statewisearray = parentObject.getJSONArray("statewise");

            //Nation wise data
            for (int i = 0; i < 1; i++) {
                JSONObject jsonObject = statewisearray.getJSONObject(i);
                for (int j = 0; j < jsonObject.length(); j++) {
                    active = jsonObject.getString("active");
                    confirmed = jsonObject.getString("confirmed");
                    deaths = jsonObject.getString("deaths");
                    recovred = jsonObject.getString("recovered");
                    todayscases = jsonObject.getString("deltaconfirmed");
                    todaysdeaths = jsonObject.getString("deltadeaths");
                    todaysrecovered = jsonObject.getString("deltarecovered");
                    lasttimeupdated = jsonObject.getString("lastupdatedtime");
                    migrated = jsonObject.getString("migratedother");
                    states = jsonObject.getString("state");
                    getTimeforStates();
                    StatsFragment.mUpdatedDate.setText(ago);
                    StatsFragment.mStatPositiveCases.setText(String.format("%,d",Integer.parseInt(confirmed)));
                    StatsFragment.mStatDeathCases.setText(String.format("%,d",Integer.parseInt(deaths)));
                    StatsFragment.mStatCuredCases.setText(String.format("%,d",Integer.parseInt(recovred)));
                    StatsFragment.mStatMonitoringCases.setText(String.format("%,d",Integer.parseInt(active)));
                    StatsFragment.mStatPatientCases.setText(String.format("%,d",Integer.parseInt(migrated)));
                    StatsFragment.mStatAddedPositive.setText(String.format("%,d",Integer.parseInt(todayscases)));
                    StatsFragment.mStatAddedDeath.setText(String.format("%,d",Integer.parseInt(todaysdeaths)));
                    StatsFragment.mStatAddedCured.setText(String.format("%,d",Integer.parseInt(todaysrecovered)));

                }
            }
StatsFragment.hideRegularDataLoading();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getTimeforStates() {


        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        formatter.setLenient(false);

        Date curDate = new Date();
        long curMillis = curDate.getTime();
        curTime = formatter.format(curDate);

        String oldTime = lasttimeupdated;
        Date oldDate = null;
        Date currdates = null;
        try {
            oldDate = formatter.parse(oldTime);
            curDate = formatter.parse(curTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        nowMillis = curDate.getTime();
        oldMillis = oldDate.getTime();

        total = nowMillis - oldMillis;

        if (total < MINUTE_MILLIS) {
            ago = "just now";
        } else if (total < 2 * MINUTE_MILLIS) {
            ago = "a minute ago";
        } else if (total < 50 * MINUTE_MILLIS) {
            ago = total / MINUTE_MILLIS + " minutes ago";
        } else if (total < 90 * MINUTE_MILLIS) {
            ago = "an hour ago";
        } else if (total < 24 * HOUR_MILLIS) {
            ago = total / HOUR_MILLIS + " hours ago";
        } else if (total < 48 * HOUR_MILLIS) {
            ago = "yesterday";
        } else {
            ago = total / DAY_MILLIS + " days ago";
        }

    }

    public void getTimings() {

        day = lasttimeupdated.substring(0, 2);
        month = lasttimeupdated.substring(3, 5);
        year = lasttimeupdated.substring(6, 9);
        timeinstring = lasttimeupdated.substring(11, 19);

        if (month.equals("01")) {
            monthinwords = "Jan";
        }
        if (month.equals("02")) {
            monthinwords = "Feb";
        }
        if (month.equals("03")) {
            monthinwords = "Mar";
        }
        if (month.equals("04")) {
            monthinwords = "Apr";
        }
        if (month.equals("05")) {
            monthinwords = "May";
        }
        if (month.equals("06")) {
            monthinwords = "Jun";
        }
        if (month.equals("07")) {
            monthinwords = "Jul";
        }
        if (month.equals("08")) {
            monthinwords = "Aug";
        }
        if (month.equals("09")) {
            monthinwords = "Sep";
        }
        if (month.equals("10")) {
            monthinwords = "Oct";
        }
        if (month.equals("11")) {
            monthinwords = "Nov";
        }
        if (month.equals("12")) {
            monthinwords = "Dec";
        }

        perfecttime = timeinstring.substring(0, 5);
        try {
            final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
            final Date dateObj;
            dateObj = sdf.parse(perfecttime);
            perfecttime = new SimpleDateFormat("hh:mm aa").format(dateObj);


        } catch (ParseException e) {
            e.printStackTrace();
        }

        finaldate = day + " " + monthinwords + ", " + perfecttime;

    }


}
