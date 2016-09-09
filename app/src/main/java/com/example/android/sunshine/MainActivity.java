package com.example.android.sunshine;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.GregorianCalendar;
import java.util.List;

public class MainActivity extends AppCompatActivity implements AsyncResponse {

    public ArrayAdapter<String> adapter;
    public ArrayList<String> forecastArray;

    @Override
    public void processFinish(String[] output) {
        if (output != null) {
            ListView listView = (ListView) findViewById(R.id.listview_forecast);
//            String[] forecastArray = {"Today - Windy - 12 / 6",
//                    "Tomorrow - Rainy - 10 / 3",
//                    "Thursday - Foggy - 15 / 9",
//                    "Friday - Sunny - 18 / 10",
//                    "Saturday - Cloudy - 12 / 6"};
            List<String> listForecast = new ArrayList<>();
            for (String s : output) {
                listForecast.add(s);
            }
            adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, listForecast);
            listView.setAdapter(adapter);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView) findViewById(R.id.listview_forecast);
        forecastArray = new ArrayList<String>();
        forecastArray.add("Today - Windy - 12 / 6");
        forecastArray.add("Tomorrow - Rainy - 10 / 3");
        forecastArray.add("Thursday - Foggy - 15 / 9");
        forecastArray.add("Friday - Sunny - 18 / 10");
        forecastArray.add("Saturday - Cloudy - 12 / 6");
        adapter = new ArrayAdapter<>(getBaseContext(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, forecastArray);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String texto = parent.getItemAtPosition(position).toString();
                Toast.makeText(getBaseContext(),texto,Toast.LENGTH_LONG).show();
            }
        });
//        listView.setOnClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                String texto = parent.getItemAtPosition(position).toString();
//                Intent intent = new Intent(,DetailActivity.class)
//                        .putExtra(Intent.EXTRA_TEXT,texto);
//                startActivity(intent);
//            }
//        });
//        Button button = (Button) findViewById(R.id.clic_btn);
//        button.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Toast.makeText(getBaseContext(),"SOLTAAA",Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.forecastfragment, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh) {
            Log.i("MainActivity", "Refresh button pushed");
            new FetchWeatherTaskPostalCode().execute("94043");
//            List<String> listForecast = new ArrayList<>(Arrays.asList(r));
//            ListView listView = (ListView) findViewById(R.id.listview_forecast);
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.list_item_forecast, R.id.list_item_forecast_textview, listForecast);
//            listView.setAdapter(adapter);
            //ListView listView = (ListView) findViewById(R.id.listview_forecast);

        }
        return super.onOptionsItemSelected(item);
    }

    public class FetchWeatherTaskPostalCode extends AsyncTask<String, Void, String[]> {
        // These two need to be declared outside the try/catch
        // so that they can be closed in the finally block.
        HttpURLConnection urlConnection;
        BufferedReader reader;
        // Tag to add to the log insertions
        String LOG_TAG;
        // Will contain the raw JSON response as a string.
        String forecastJsonStr;
        final String API_KEY = "7496dbe4b8ee7c3f2f7014116891de10";
        // Params names for the construction of the url
        final String QUERY_PARAM = "q";
        final String MODE_PARAM = "mode";
        final String CANT_PARAM = "cnt";
        final String UNITS_PARAM = "units";
        final String API_PARAM = "appid";
        final int numDays = 7;

        @Override
        protected void onPreExecute() {
            urlConnection = null;
            reader = null;
            LOG_TAG = FetchWeatherTaskPostalCode.class.getSimpleName();
            forecastJsonStr = null;

        }

        @Override
        protected String[] doInBackground(String... params) {
            try {
                if (params.length == 0)
                    return null;

                // Params for the construction of the url
                String zipCode = params[0];
                String format = "json";
                String unit = "metric";

                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are available at OWM's forecast API page, at
                //URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7&appid=7496dbe4b8ee7c3f2f7014116891de10");
                Uri.Builder builder = new Uri.Builder();
                builder.scheme("http")
                        .authority("api.openweathermap.org")
                        .appendPath("data")
                        .appendPath("2.5")
                        .appendPath("forecast")
                        .appendPath("daily")
                        .appendQueryParameter(QUERY_PARAM, zipCode)
                        .appendQueryParameter(MODE_PARAM, format)
                        .appendQueryParameter(UNITS_PARAM, unit)
                        .appendQueryParameter(CANT_PARAM, Integer.toString(numDays))
                        .appendQueryParameter(API_PARAM, API_KEY);
//            String myUrl = builder.build().toString();
                // Create the request to OpenWeatherMap, and open the connection
                String query = builder.build().toString();
                Log.v(LOG_TAG, "BUILD QUERY: " + query);
                URL url = new URL(query);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();

//            Log.e(LOG_TAG, forecastJsonStr);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
                forecastJsonStr = null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getWeatherDataFromJson(forecastJsonStr, numDays);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            // This will only happen if there was an error getting or parsing the forecast.
            return null;
//        return null;

        }

        /* The date/time conversion code is going to be moved outside the asynctask later,
             * so for convenience we're breaking it out into its own method now.
             */
        private String getReadableDateString(long time) {
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }

        @Override
        protected void onPostExecute(String[] strings) {
            if (strings != null) {
                //adapter.clear();
                forecastArray.clear();
                for (String dayForecastStr : strings) {
                    forecastArray.add(dayForecastStr);
                }
                adapter.notifyDataSetChanged();
            }
        }

        /**
         * Take the String representing the complete forecast in JSON Format and
         * pull out the data we need to construct the Strings needed for the wireframes.
         * <p/>
         * Fortunately parsing is easy:  constructor takes the JSON string and converts it
         * into an Object hierarchy for us.
         */
        private String[] getWeatherDataFromJson(String forecastJsonStr, int numDays)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_LIST = "list";
            final String OWM_WEATHER = "weather";
            final String OWM_TEMPERATURE = "temp";
            final String OWM_MAX = "max";
            final String OWM_MIN = "min";
            final String OWM_DESCRIPTION = "main";
            final String OWM_DT = "dt";
            JSONObject forecastJson = new JSONObject(forecastJsonStr);
            JSONArray weatherArray = forecastJson.getJSONArray(OWM_LIST);

            // OWM returns daily forecasts based upon the local time of the city that is being
            // asked for, which means that we need to know the GMT offset to translate this data
            // properly.

            // Since this data is also sent in-order and the first day is always the
            // current day, we're going to take advantage of that to get a nice
            // normalized UTC date for all of our weather.

//        Time dayTime = new Time();
//        Time dayTime = new GregorianCalendar(TimeZone.getTimeZone("UTC-03:00"));

            // we start at the day returned by local time. Otherwise this is a mess.
//        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

            // now we work exclusively in UTC
            GregorianCalendar calendar = new GregorianCalendar();

            String[] resultStrs = new String[numDays];
            for (int i = 0; i < weatherArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"
                String day;
                String description;
                String highAndLow;

                // Get the JSON object representing the day
                JSONObject dayForecast = weatherArray.getJSONObject(i);


                // description is in a child array called "weather", which is 1 element long.
                JSONObject weatherObject = dayForecast.getJSONArray(OWM_WEATHER).getJSONObject(0);
                description = weatherObject.getString(OWM_DESCRIPTION);

                long dateTime = dayForecast.getLong(OWM_DT);
                //the value from dt is in seconds... multiply by 1000 to get the milliseconds in order to transform it to date
                day = getReadableDateString(dateTime * 1000);

                // Temperatures are in a child object called "temp".  Try not to name variables
                // "temp" when working with temperature.  It confuses everybody.
                JSONObject temperatureObject = dayForecast.getJSONObject(OWM_TEMPERATURE);
                double high = temperatureObject.getDouble(OWM_MAX);
                double low = temperatureObject.getDouble(OWM_MIN);

                highAndLow = formatHighLows(high, low);
                resultStrs[i] = day + " - " + description + " - " + highAndLow;
            }

            for (String s : resultStrs) {
                Log.v(LOG_TAG, "Forecast entry: " + s);
            }
            return resultStrs;

        }
    }
}
